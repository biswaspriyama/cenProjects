from BaseHTTPServer import HTTPServer, BaseHTTPRequestHandler
from SocketServer import ThreadingMixIn
import threading
import urlparse
from Config import *
from threading import Thread

class Handler(BaseHTTPRequestHandler):
    def do_GET(self):
        try:
            path = dirPath+self.path
            #print path
            parsed_path = urlparse.urlparse(path)
            requestline = self.raw_requestline
            if requestline[-2:] == '\r\n':
                requestline = requestline[:-2]
            elif requestline[-1:] == '\n':
                requestline = requestline[:-1]
            #self.requestline = requestline
            words = requestline.split()
            if len(words) == 3:
                [command, dpath, version] = words
                #print('%s--%s--%s' %(command,dpath,version))
                if version[:5] != 'HTTP/':
                    self.send_error(400, "Bad request version (%r)" % version)
                    return 
                try:
                    base_version_number = version.split('/', 1)[1]
                    version_number = base_version_number.split(".")
                    if len(version_number) != 2:
                        raise ValueError
                    version_number = int(version_number[0]), int(version_number[1])
                except (ValueError, IndexError):
                    self.send_error(400, "Bad request version (%r)" % version)
                    return
                if version_number >= (1, 1) and self.protocol_version >= "HTTP/1.1":
                    self.close_connection = 0
                if version_number >= (2, 0):
                    self.send_error(505,
                              "Invalid HTTP Version (%s)" % base_version_number)
                    return
            elif len(words) == 2:
                [command, dpath] = words
                self.close_connection = 1
                if command != 'GET':
                    self.send_error(400,
                                    "Bad HTTP/0.9 request type (%r)" % command)
                    return 


            file = open(path, "rb").read()
            self.send_response(200)
            self.end_headers()
            message =  threading.currentThread().getName()
            self.wfile.write(file) 
            self.wfile.write('\n')
            return
        except IOError,e:
            print e
            self.send_response(404)
            self.end_headers()
            message =  threading.currentThread().getName()
            print message
            return

        

class ThreadedHTTPServer(ThreadingMixIn, HTTPServer):
    """Handle requests in a separate thread."""




if __name__ == '__main__':


    httpd = ThreadedHTTPServer(('localhost', 9999), Handler)
    threading.Thread(target=httpd.serve_forever).start()
    # print "hi"
    # httpd.shutdown()
    # print "bye"



