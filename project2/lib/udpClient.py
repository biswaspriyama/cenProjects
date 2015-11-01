'''
    udp socket client
    Silver Moon
'''
 
import socket   #for sockets
import sys  #for exit
import struct
# create dgram udp socket
try:
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
except socket.error:
    print 'Failed to create socket'
    sys.exit()
 
host = 'localhost'
port = 8888
 
while(1) :
    msg = raw_input('Enter message to send : ')

    msg = 1
    msg =struct.pack('=H', 4566)
    try :
        #Set the whole string
        s.sendto(msg, (host, port))
         
        # receive data from client (data, addr)
        s.settimeout(2)
    	d = s.recvfrom(2)
        reply = d[0]
        addr = d[1]
        print 'Server reply : ' + reply

    except socket.timeout,msg:
        #print 'Error Code : ' + str(msg[0]) + ' Message ' + msg[1]
        print msg
	sys.exit()
    	print("closing socket")
    	s.close()
