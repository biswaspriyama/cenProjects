import threading
import SocketServer
import yaml
from MysqlConnect import *
from Config import *

class ServerPacketProcessing:
    def __init__(self , packetData):
         self.packet = yaml.load(packetData)

    def processPacket(self):
        # try:
        method = self.packet["method"]

        if method == "InformAndUpdate":
            stat = self.informAndUpdate()

        if method == "Exit":
            stat = self.removePeer()

        if method == "QueryForContent":
            fileHash = self.QueryForContent()
            resp = self.createResponseMessage("200","OK",fileHash)
            return resp

        resp = self.createResponseMessage("200","OK")
        # except:
        #     resp = self.createResponseMessage("400","ERROR")
        return resp

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


class ThreadedUDPRequestHandler(SocketServer.BaseRequestHandler):
    def handle(self):
        data = self.request[0].strip()
        port = self.client_address[1] ### get port number
        socket = self.request[1]  ### get the communicate socket


        client_address = (self.client_address[0])
        cur_thread = threading.current_thread()


        obj = ServerPacketProcessing(data)
        resp = obj.processPacket()
        socket.sendto(resp, self.client_address)

class ThreadedUDPServer(SocketServer.ThreadingMixIn,SocketServer.UDPServer):
    pass

if __name__ == "__main__":
    HOST, PORT = "localhost", 8888

    server = ThreadedUDPServer((HOST, PORT),ThreadedUDPRequestHandler)
    ip, port = server.server_address
    server.serve_forever()
    # Start a thread with the server --
	# that thread will then start one
    # more thread for each request
    server_thread = threading.Thread(target=server.serve_forever)
    # Exit the server thread when the main thread terminates
    server_thread.daemon = True
    server_thread.start()
    server.shutdown()