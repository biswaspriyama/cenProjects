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
from DirClient import *

def getFreePort():
    s = socket.socket()
    s.bind(("", 0))
    port = s.getsockname()
    s.close()
    return port

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
        httpObj.downloadFile(file , "tx.txt")




    httpd.shutdown()

if __name__ == '__main__':
    main()