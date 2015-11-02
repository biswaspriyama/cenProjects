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
            parsed_path = urlparse.urlparse(path)
            file = open(path, "rb").read()
            self.send_response(200)
            self.end_headers()
            message =  threading.currentThread().getName()
            print message
            self.wfile.write(file) 
            self.wfile.write('\n')
            return
        except Exception:
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
    print "hi"
    httpd.shutdown()
    print "bye"



