import threading
#import SocketServer
import yaml
from MysqlConnect import *
from Config import *
import time
import Queue
import SocketServer1
from collections import OrderedDict
import math



class ServerPacketProcessing:
    def __init__(self , packetData):
         self.packet = yaml.load(packetData)

    def processPacket(self):
        # try:
        method = self.packet["method"]

        if method == "INUP":
            stat = self.informAndUpdate()
            resp = self.createResponseMessage("200","OK")
            return resp

        if method == "EXIT":
            stat = self.removePeer()
            resp = self.createResponseMessage("200","OK")
            return resp

        if method == "QFCN":
            fileHash = self.QueryForContent()
            resp = self.createResponseMessage("200","OK",fileHash)
            return resp


        # except:
        #     resp = self.createResponseMessage("400","ERROR")


    def informAndUpdate(self):
        hostAddress = self.packet["address"]
        hostName = self.packet["hostName"]
        files = str(self.packet["data"])
        files = yaml.load(files)

        obj= MysqlConnect(serverIp, myDb, mySqlUser, mySqlpwd)
        for file in files:
            size = str(files[file])
            obj.writeDataFileTable(tableName, file, hostAddress, size , hostName)
        print "done"

    def removePeer(self):
        hostAddress = self.packet["address"]
        obj= MysqlConnect(serverIp, myDb, mySqlUser, mySqlpwd)
        obj.deleteFromFileTable(tableName, hostAddress)
        print "deleted"


    def QueryForContent(self):
        count = self.packet["data"]
        obj= MysqlConnect(serverIp, myDb, mySqlUser, mySqlpwd)
        allFiles = obj.readFileTable(tableName, count)
        fileStr = yaml.dump(allFiles)
        fileStr = fileStr.strip("\n")
        return fileStr

    def createResponseMessage(self, status, phrase , files = "none"):
        messsage = {}
        messsage["status"] = status
        messsage["phrase"] = phrase
        if files != "none":
            messsage["header"] = files
        packet = yaml.dump(messsage)
        packet.strip("\n")
        return packet




class ProcessNew:
    def __init__(self , packetData):
        self.packet = packetData
        self.receivedMsg = self.extractData()
        print self.receivedMsg["Data"]

    def getHeader(self):
        msg = self.receivedMsg
        msg.pop("Data", None)
        return msg

    def checkIfFragmented(self):
        fragments = {}
        if (self.receivedMsg["FragFlag"] == '0' and self.receivedMsg["SEQ"]=="000"):   #if only single packet
            print self.receivedMsg["Data"]
            d = self.receivedMsg["Data"].split("=")
            fragments["Hostname"] = d[0]
            fragments["Files"] = d[1]
            fragments["stat"] = 0
            return fragments
        else:
            key = self.receivedMsg["SEQ"]
            value = self.receivedMsg["Data"]
            fragments["info"] = key+"|"+value
            fragments["stat"] = 1
            return fragments

    def checkIfLastFragment(self):
        if (self.receivedMsg["FragFlag"] == '0' and self.receivedMsg["SEQ"]!="000"):
            return self.receivedMsg["SEQ"]
        else:
            return False

    def extractData(self):
        packet = {}
        arr = self.packet.split(";")
        packet["Method"] = arr[0]
        packet["Address"] = arr[1]
        packet["ID"] = arr[2]
        packet["SEQ"] = arr[3]
        packet["FragFlag"] = arr[4]
        packet["Data"] = arr[5]
        return packet

    def reassembleData(self,buf):
        data = ""
        body = {}
        for key in sorted(buf.iterkeys()):
            try:
                int(key)
                data = data+buf[key]
            except:
                continue
        d = data.split("=")
        body["Hostname"] = d[0]
        body["Files"] = d[1]

        return body

    def getExpectedSeqNum(self , buf):
        keys = sorted(buf)
        counter = 0
        for key in keys:
            try:
                if(int(key) == counter):
                    counter = counter+1
                else:
                    break
            except:
                continue
        return counter

    def updateDatabase(self, buf):
        headers = self.getHeader()
        buf.update(headers)
        hostAddress = buf["Address"]
        hostname = buf["Hostname"]
        files = buf["Files"]
        files = yaml.load(files)

        obj= MysqlConnect(serverIp, myDb, mySqlUser, mySqlpwd)
        for file in files:
            size = str(files[file])
            obj.writeDataFileTable(tableName, file, hostAddress, size , hostname)
        print "done"

    def removePeerDatabase(self):
        hostAddress = self.receivedMsg["Address"]
        obj= MysqlConnect(serverIp, myDb, mySqlUser, mySqlpwd)
        obj.deleteFromFileTable(tableName, hostAddress)
        print "deleted"

    def queryForContent(self ):
        host = self.receivedMsg["Address"]
        print host

        data = self.receivedMsg["Data"].split("|")
        count = data[0]
        key = data[1]
        obj= MysqlConnect(serverIp, myDb, mySqlUser, mySqlpwd)
        allFiles = obj.readFileTable(tableName, host, count, key)
        fileStr = yaml.dump(allFiles)
        fileStr = fileStr.strip("\n")
        return fileStr

    def createResponseHeader(self, method="QCON", seqNum='000',flag='0'):
        header = []
        header.append(method)
        header.append(seqNum)
        header.append(flag)
        header = ";".join(header)
        header = header+";"
        return header

    def fragmentContent(self , totalData):
        limit = messageLimit
        header = self.createResponseHeader("QCON", '000', '0')
        headerLen = len(header)
        message = header+totalData
        messageLen = len(message)

        if (messageLen > limit):
            numFrag = (messageLen - headerLen) / float(limit - headerLen)
            numFrag=int(math.ceil(numFrag))
            print "Number of Fragments of Message = "+str(numFrag)
            dataSize = limit-headerLen
            dataFragments = self.fragmentString(totalData,dataSize)
            messages = []
            fNum = 0
            flag = '1'
            for data in dataFragments:
                seqNum = '%03d' % fNum
                if fNum == len(dataFragments)-1:
                    flag = '0'
                header = self.createResponseHeader("QCON", seqNum , flag)
                messages.append(header+data)
                fNum = fNum+1
        else:
            messages = [message]

        return messages

    def fragmentString(self, data, packetLimit):
        dataFragments=[data[x:x+packetLimit] for x in range(0,len(data),packetLimit)]
        return dataFragments



class ThreadedUDPRequestHandler(SocketServer1.BaseRequestHandler):

    def handle(self):
        data = self.request[0].strip()
        port = self.client_address[1] ### get port number
        socket = self.request[1]  ### get the communicate socket


        client_address = (self.client_address[0])
        cur_thread = threading.current_thread()


        #obj = ServerPacketProcessing(data)
        #resp = obj.processPacket()
        obj = ProcessNew(data)
        method = obj.receivedMsg["Method"]
        if method == "INUP":
            hash = obj.checkIfFragmented()
            if(hash["stat"] != 0):
                fin = obj.checkIfLastFragment()
                info = hash["info"]
                seq = info.split("|")[0]
                data = info.split("|")[1]

                try:
                    globals()[client_address+":"+str(port)][seq] = data
                    print "existing_client"
                    if(fin):
                        globals()[client_address+":"+str(port)]["last"] = int(fin)

                    buf = globals()[client_address+":"+str(port)]
                    counter = obj.getExpectedSeqNum(buf)


                except Exception,e:
                    print e
                    print "new_client"
                    globals()[client_address+":"+str(port)] = {}
                    globals()[client_address+":"+str(port)][seq] = data
                    if(fin):
                        globals()[client_address+":"+str(port)]["last"] = int(fin)


                    buf = globals()[client_address+":"+str(port)]
                    counter = obj.getExpectedSeqNum(buf)

                try:
                    lastSyn = globals()[client_address+":"+str(port)]["last"]
                    if (counter == lastSyn+1):
                        data = obj.reassembleData(buf)

                        obj.updateDatabase(data)
                        print "all packets received"

                except Exception,e:
                    if "Duplicate" in str(e):
                        print "Ignoring duplicate packets"
                    else:
                        print "Waiting for more packets"
                resp = "ACK:"+str(counter)

            else:
                try:
                    obj.updateDatabase(hash)
                except Exception,e:
                    print "ignoring Duplicates"
                resp = "ACK:1"

        elif method == "EXIT":
            obj.removePeerDatabase()
            resp = "ACK:1"

        elif method == "QCON":
            resp = obj.queryForContent()
            fragments = obj.fragmentContent(resp)
            for fragment in fragments:
                print fragment
                socket.sendto(fragment, self.client_address)
            return


        time.sleep(serverDelay)
        socket.sendto(resp, self.client_address)



class ThreadedUDPServer(SocketServer1.ThreadingMixIn,SocketServer1.UDPServer):
    pass



def main():
    HOST, PORT = "localhost", 8888

    server = ThreadedUDPServer((HOST, PORT),ThreadedUDPRequestHandler)
    ip, port = server.server_address
    server.serve_forever()



    server_thread = threading.Thread(target=server.serve_forever)
    # Exit the server thread when the main thread terminates
    server_thread.daemon = True
    server_thread.start()
    server.shutdown()

if __name__ == "__main__":
    print "hi"
    main()


