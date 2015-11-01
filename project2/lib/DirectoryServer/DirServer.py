import threading
#import SocketServer
import yaml
from MysqlConnect import *
from Config import *
import time
import Queue
import SocketServer
from collections import OrderedDict
import math
import random
import timer

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
    def retransmitIfTimedOut(self):
        try:
            index  = globals()[clientAddr+":"+str(portG)+"_sendbases"][0]
            skt.sendto(syncdata[index], clientAddr)
            print "retransmitting data"
            counter = False
        except:
            print "Not re-transmitting as all data sent already"

    def reliableTransfer(self, socket, clientAddr, port, messages, shuffle = 0):

        global counter
        counter = True
        global syncdata
        syncdata = messages

        if shuffle == 1:
            random.shuffle(messages)

        port = clientAddr[1]
        clAd = clientAddr[0]


        futureTime = time.time()+MaxRetry

        timeout = 0.1

        #reliable transfer until all messages has been sent and ack/cumulative acks has been received
        tim = timer.TimerReset(timeout, self.retransmitIfTimedOut)
        tim.start()

        while(len(globals()[clAd+":"+str(port)+"_sendbases"])!= 0  and time.time()<futureTime):

            sendBases = globals()[clAd+":"+str(port)+"_sendbases"]
            print sendBases
            if sendBases != []:
                sb = sendBases[0]
                print(messages[sb])
                socket.sendto(messages[sb], clientAddr)
                #time.sleep(timeout)
                if (counter == False):
                    tim.reset()


class ThreadedUDPRequestHandler(SocketServer.BaseRequestHandler):

    def handle(self):
        data = self.request[0].strip()
        port = self.client_address[1] ### get port number (a variable of socket server class
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
            globals()[client_address+":"+str(port)+"_sendbases"] = range(len(fragments))
            globals()[client_address+":"+str(port)+"_sbCopy"] = range(len(fragments))




            global clientAddr
            clientAddr = self.client_address
            global skt
            skt = socket
            global portG
            portG = port

            obj.reliableTransfer(socket, self.client_address, port, fragments)
            # for fragment in fragments:
            #     print fragment
            #     socket.sendto(fragment, self.client_address)
            return
        elif method == "QRES":


            seq = int(obj.receivedMsg["SEQ"])
            globals()[client_address+":"+str(port)+"_sendbases"] = globals()[client_address+":"+str(port)+"_sbCopy"][seq:]
            return

        time.sleep(serverDelay)
        socket.sendto(resp, self.client_address)



class ThreadedUDPServer(SocketServer.ThreadingMixIn,SocketServer.UDPServer):
    pass


def main():
    HOST, PORT = "localhost", ServerPort

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


