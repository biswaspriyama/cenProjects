import httplib
from Config import *

class lightShareHttpClient:

    def __init__(self , httpServer, port):
        self.httpServ = httplib.HTTPConnection(httpServer, port)
        self. httpServ.connect()

    def downloadFile(self, remoteFile, localFile = ""):
        self.httpServ.request('GET', remoteFile)
        response = self.httpServ.getresponse()

        if localFile == "":
            filePath = dirPath + remoteFile
        else:
            filePath = dirPath + localFile
        if response.status == httplib.OK:
            print "File transfered"
            f = open(filePath, 'wb')
            f.write(response.read())
            f.close()
        elif response.status == httplib.NOT_FOUND:
            print "HTTP 404 : File not found"

    def __del__(self):
        self.httpServ.close()


def main():
    obj = lightShareHttpClient("localhost", 9999)
    obj.downloadFile("a.txt" , "b.txt")


if __name__ == '__main__':
    main()
