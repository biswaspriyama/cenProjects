#!/usr/bin/python
import socket
import sys
import os
import platform
from os import listdir
from os.path import isfile, join
import json
from Config import *
from httpClient import *
from httpServer import *
from threading import Thread

import yaml
import time

def createRequestMessage(method, hostname, address, data=""):
    messsage = {}
    messsage["method"] = method
    messsage["hostName"] = hostname
    messsage["address"] = address
    messsage["data"] = data


    packet = yaml.dump(messsage)
    packet.strip("\n")
    print "*****"
    print packet
    print "*****"
    return packet


def getFileAndSize(directory, osType = "ios"):
    fileHash = {}
    if osType == "ios":
        onlyfiles = [ file for file in listdir(directory) if isfile(join(directory,file)) ]
        for file in onlyfiles:
            fileHash[file] = os.path.getsize(directory + file)
    return fileHash


def getOsType():
    platform.system()

class createDataPacket:
    def __init__(self, infoData):
        self.idNum = 0
        self.fragmentFlag = 0
        self.dataLimit = pathMTU - 16
        self.data = infoData

    def createData(self):
        dataPck = {}
        size = sys.getsizeof(self.data)
        sizeInBytes = size/8
        if  sizeInBytes <= self.dataLimit:
            dataPck



def getFreePort():
    s = socket.socket()
    s.bind(("", 0))
    port = s.getsockname()
    s.close()
    return port



class DirClient:

    def __init__(self , host, port, peerPort):
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.serverHost = host
        self.serverPort = port
        self.sock.connect((self.serverHost, self.serverPort))

        #param releated to the peer client/server
        self.peerPort = str(peerPort)
        self.hostname = socket.gethostname()
        self.IP = socket.gethostbyname(self.hostname)




    def informAndUpdate(self , dirPath):
        files = getFileAndSize(dirPath)
        #print files
        # dataObj = createDataPacket(files)
        # dataObj.createData()


        message = createRequestMessage("InformAndUpdate", self.hostname, self.IP+":"+self.peerPort, files)
        self.sock.sendall(message + "\n")
        received = self.sock.recv(1024)
        print received


    def gracefulExit(self):
        message = createRequestMessage("Exit", self.hostname, self.IP+":"+self.peerPort)
        self.sock.sendall(message + "\n")
        received = self.sock.recv(1024)
        print received


    def queryForContent(self):
        message = createRequestMessage("QueryForContent", self.hostname, self.IP+":"+self.peerPort,"100")
        self.sock.sendall(message + "\n")
        received = self.sock.recv(1024)
        print received


    def __del__(self):
        # message = createRequestMessage("Exit", self.hostname, self.IP+":"+self.peerPort)
        # print message
        # self.sock.sendall(message + "\n")
        # received = self.sock.recv(1024)
        # print received

        print "closing client"
        self.sock.close()


def main():

    peerPort = getFreePort()[1]
    obj = DirClient(DirServerIp, DirServerPort, peerPort)
    obj.informAndUpdate(dirPath)
    hostname = socket.gethostname()
    IP = socket.gethostbyname(hostname)
    httpd = ThreadedHTTPServer((IP, peerPort), Handler)
    threading.Thread(target=httpd.serve_forever).start()

    obj.queryForContent()
    while True:
        i = raw_input("Enter y to download (or n to quit): ")
        if i != "y":
            obj.gracefulExit()
            httpd.shutdown()
            break
        file = raw_input("Enter file Name:")
        peer = raw_input("Enter Peer:")
        port = input("Enter port:")
        httpObj = lightShareHttpClient(peer, port)
        httpObj.downloadFile(file , "z.txt")




    httpd.shutdown()





if __name__ == "__main__":
    main()
