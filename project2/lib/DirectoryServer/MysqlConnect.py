__author__ = 'yugarsi'
import MySQLdb
from Config import *

class MysqlConnect:
    def __init__(self, ip, database = "",  user= "" , password = ""):

        self.db = MySQLdb.connect(host=ip, user=user, passwd=password, db = database)
        self.cursor = self.db.cursor()
    def createDB(self):
        self.cursor.execute("SET sql_notes = 0; ")
        self.curor.execute('CREATE DATABASE IF NOT EXISTS DirServer;')

    def createPeerTable(self, tableName ):
        sql = "CREATE TABLE  "+tableName + "(hostAddress VARCHAR(100) not NULL, " + " hostname TEXT, " + " files LONGTEXT, " + " PRIMARY KEY ( hostAddress ))"
        self.cursor.execute(sql)

    def createFileTable(self, tableName):
        sql = "CREATE TABLE  "+tableName + "(files VARCHAR(100) not NULL, " + "host VARCHAR(100) not NULL, " + " fileSize VARCHAR(100), " + "hostName VARCHAR(100) not NULL, " + " PRIMARY KEY ( files,host ))"
        self.cursor.execute(sql)


    def writeDataPeerTable(self, tableName, hostAddress, hostname, files):
        sql = ''' INSERT INTO '''+tableName+''' (hostAddress, hostname, files) VALUES ("'''+hostAddress+'''", "'''+hostname+'''","'''+files+'''");'''
        self.cursor.execute (sql)
        self.db.commit ()

    def writeDataFileTable(self, tableName, file, host, filesize , hostName):
        sql = ''' INSERT INTO '''+tableName+''' (files, host, fileSize, hostName) VALUES ("'''+file+'''", "'''+host+'''","'''+ filesize +'''","'''+hostName+'''");'''
        self.cursor.execute (sql)
        self.db.commit ()

    def deleteFromFileTable(self, tableName, host):
        sql = "DELETE FROM  "+tableName+" WHERE host= '"+host+"';"
        self.cursor.execute (sql)
        self.db.commit ()

    def readFileTable(self , tableName, host, count = "all" , keyWord = ""):
        sql = ""
        if keyWord == "" and count == "all":
            sql = "SELECT * FROM "+tableName+" where host != '"+host+"';"


        elif count != "all" and keyWord == "":
            sql = "SELECT * FROM "+tableName+  " where host != '"+host+"' LIMIT "+str(count)

        elif keyWord != "":
            sql = "SELECT * FROM "+tableName+" WHERE files LIKE '"+str(count)+"%' and  host != '"+host+"'"
            print sql

        self.cursor.execute(sql)
        results = self.cursor.fetchall()

        resultHash = {}
        files = []
        for result in results:
            file = result[0]
            files.append(file)
        uniqueFiles = list(set(files))
        for file in uniqueFiles:
            resultHash[file]=[]

        for result in results:
            host = result[1]
            size = result[2]
            hostName = result[3]
            tup = [host,hostName,size]
            resultHash[result[0]].append(tup)
        return resultHash




    def __del__(self):
        self.cursor.close ()
        self.db.close ()
def main():

    hostad = "192.168.0.11:59227"
    name = "yugarsis-MacBook-Air.local"
    files = "{u'c.pdf': 0, u'a.txt': 9, u'b.txt': 0}"
    obj= MysqlConnect(serverIp, myDb, mySqlUser, mySqlpwd)
    # obj.createFileTable("fileStore")
    # obj.writeDataFileTable(tableName, hostad, name,files)
    # obj.deleteFromFileTable(tableName, "192.168.0.11:52151")
    count = "50"
    key = ""
    host = "192.168.0.11:55476"
    allFiles = obj.readFileTable(tableName, host, count, key)
    print allFiles
    print "done"

if __name__ == '__main__':
    main()