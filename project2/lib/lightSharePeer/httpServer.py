from BaseHTTPServer import HTTPServer, BaseHTTPRequestHandler
from SocketServer import ThreadingMixIn
import threading
import urlparse
from Config import *


class Handler(BaseHTTPRequestHandler):
    def do_GET(self):
        try:
            path = dirPath+self.path
            print path
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
    server = ThreadedHTTPServer(('192.168.0.11', 52129), Handler)
    print 'Starting server, use <Ctrl-C> to stop'
    server.serve_forever()
