#!/usr/bin/python
import socket
import sys
import os
import platform
from os import listdir
from os.path import isfile, join
import json
import textwrap
import math
from Config import *
from httpClient import *
from httpServer import *
from threading import Thread
import pickle
import yaml
import time
import random
import threading
import timer

hash = {}

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

def getFreePort():
    s = socket.socket()
    s.bind(("", 0))
    port = s.getsockname()
    s.close()
    return port

class createAppMesage:
    def __init__(self, id):
        self.id = str(id)

    def createHeader(self, method, address, id, seqNum='000',flag='0'):
        header = []
        header.append(method)
        header.append(address)
        header.append(id)
        header.append(seqNum)
        header.append(flag)
        header = ";".join(header)
        header = header+";"
        return header

    def createMessage(self, method, address = "", hostname="", data=""):

        if method == "INUP":
            limit = messageLimit
            header = self.createHeader(method, address, self.id, '000', '0')
            headerLen = len(header)
            data = yaml.dump(data)
            data.strip("\n")
            totalData = hostname+"="+data
            message = header+totalData
            print message
            messageLen = len(message)
            print "messageLen = "+str(messageLen)

            if (messageLen > limit):
                numFrag = (messageLen - headerLen) / float(limit - headerLen)
                numFrag=int(math.ceil(numFrag))
                print "Number of Fragments of Message = "+str(numFrag)
                dataSize = limit-headerLen
                dataFragments = self.fragmentData(totalData,dataSize)
                messages = []
                fNum = 0
                flag = '1'
                for data in dataFragments:
                    seqNum = '%03d' % fNum
                    if fNum == len(dataFragments)-1:
                        flag = '0'
                    header = self.createHeader(method, address, self.id, seqNum , flag)

                    messages.append(header+data)
                    fNum = fNum+1
            else:
                messages = [message]

        elif method == "EXIT":
            header = self.createHeader(method, address, self.id, '000', '0')
            messages = [header]

        elif method == "QCON":
            type = data   #directory listing or specific file
            header = self.createHeader(method, address, self.id, '000', '0')
            messages = header+type

        return messages

    def fragmentData(self, data, packetLimit):
        dataFragments=[data[x:x+packetLimit] for x in range(0,len(data),packetLimit)]
        return dataFragments

class DirClient:

    def __init__(self , host, port, peerPort):
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.serverHost = host
        self.serverPort = port
        self.sock.connect((self.serverHost, self.serverPort))

        #params releated to the peer client/server
        self.peerPort = str(peerPort)
        self.hostname = socket.gethostname()
        self.IP = socket.gethostbyname(self.hostname)

    def retransmitIfTimedOut(self):
        try:
            sb=sendBases[0]
            self.sock.sendall(syncdata[sb] + "\n")
            print "retransmitting data"
            counter = False
        except:
            print "Not re-transmitting as all data sent already"

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

    def checkIfLastFragment(self, msg):
        if (msg["FragFlag"] == '0' and msg["SEQ"]!="000"):
            return self.receivedMsg["SEQ"]
        else:
            return False

    def reassembleData(self,buf):
        data = ""
        for key in sorted(buf.iterkeys()):
            try:
                int(key)
                data = data+buf[key]
            except:
                continue
        data = yaml.load(data)
        print data
        return data

    def receiveServerContent(self):

        lastSyn = -1
        responseFiles = {}
        while(1):
            received = self.sock.recv(messageLimit)
            #print "***"+received
            received = received.split(";")

            seq = received[1]
            flag = received[2]
            if (seq == "000" and flag == "0"):
                files = received[3]
                files = yaml.load(files)
                return files
            else:
                responseFiles[seq] = received[3]

            if (flag == '0' and seq!="000"):
                lastSyn = int(seq)

            counter = self.getExpectedSeqNum(responseFiles)
            if (counter == lastSyn+1):
                break

        files =  self.reassembleData(responseFiles)
        return files

    def reliableTransfer(self , messages, type = "default", shuffle = 0):

        if type == "QCON":
            print messages
            self.sock.sendall(messages + "\n")
            self.receiveServerContent()
            return

        global counter
        counter = True
        global syncdata
        syncdata = messages
        global sendBases
        sendBases = range(len(messages))
        sbCopy = sendBases

        if shuffle == 1:
            random.shuffle(messages)

        timeout = 0.1
        #reliable transfer until all messages has been sent and ack/cumulative acks has been received
        tim = timer.TimerReset(timeout, self.retransmitIfTimedOut)
        tim.start()
        while(len(sendBases)!= 0):
            print sendBases
            sb = sendBases[0]
            print(messages[sb])
            self.sock.sendall(messages[sb] + "\n")
            received = self.sock.recv(messageLimit)
            ack = received.split(":")[1]
            sb = int(ack)
            print int(ack)
            sendBases = sbCopy[sb:]
            if (counter == False):
                tim.reset()

    def informAndUpdate(self , dirPath):
        files = getFileAndSize(dirPath)
        msgObj = createAppMesage(200)
        address = self.IP+":"+self.peerPort
        messages = msgObj.createMessage("INUP", address, self.hostname, files)
        self.reliableTransfer(messages)


    def gracefulExit(self):
        msgObj = createAppMesage(202)
        address = self.IP+":"+self.peerPort
        message = msgObj.createMessage("EXIT", address, self.hostname)
        self.reliableTransfer(message)

    def queryForContent(self, count = "50", search = ""):
        msgObj = createAppMesage(201)
        address = self.IP+":"+self.peerPort
        query = count+"|"+search
        message = msgObj.createMessage("QCON", address, self.hostname , query)
        self.reliableTransfer(message,"QCON")


    def __del__(self):
        print "closing client"
        self.sock.close()


def main():

    peerPort = getFreePort()[1]
    obj = DirClient(DirServerIp, DirServerPort, peerPort)
    obj.informAndUpdate(dirPath)
    # time.sleep(10)
    # obj.gracefulExit()
    obj.queryForContent()

    # hostname = socket.gethostname()
    # IP = socket.gethostbyname(hostname)
    # httpd = ThreadedHTTPServer((IP, peerPort), Handler)
    # threading.Thread(target=httpd.serve_forever).start()
    #
    # obj.queryForContent()
    # while True:
    #     i = raw_input("Enter y to download (or n to quit): ")
    #     if i != "y":
    #         obj.gracefulExit()
    #         httpd.shutdown()
    #         break
    #     file = raw_input("Enter file Name:")
    #     peer = raw_input("Enter Peer:")
    #     port = input("Enter port:")
    #     httpObj = lightShareHttpClient(peer, port)
    #     httpObj.downloadFile(file , "z.txt")
    #
    #
    #
    #
    # httpd.shutdown()





if __name__ == "__main__":
    main()
