
from Config import *
import time
from DirClient import *
import math
import matplotlib.pyplot as plt

peerPort = getFreePort()[1]
obj = DirClient(DirServerIp, DirServerPort, "123")
SRTT=0.1

beta=0.25
alpha=0.125
DevRTT=0

rttList = []
eRttList = []
for x in xrange(0, 100):
    start = time.time()
    obj.informAndUpdate("/Users/yugarsi/git-local/cenProjects/project2/lib/sampleRTTFile/") #sends one segment to server
    end = time.time()
    RTT=end-start
    print("RTT \n", RTT)
    rttList.append(RTT)
    SRTT = alpha*RTT + (1 - alpha)*SRTT

    eRttList.append(SRTT)
    print("SRTT \n", SRTT)
    DevRTT = (1-beta)*DevRTT + abs(beta*(SRTT-RTT))
    print("DevRTT \n", DevRTT)


    #print("DevRTT \n", DevRTT)

plt.plot(rttList,label= "Sample RTT",color="red")
plt.plot(eRttList,label = "Estimated",color="blue")

plt.xlabel('Time(seconds)')
plt.ylabel('RTT(seconds)')
plt.legend(loc="upper left", bbox_to_anchor=[0, 1], ncol=2, shadow=True, title="Legend", fancybox=True)
plt.show()

timeoutInterval = RTT + 4 * DevRTT
print("timeoutInterval \n", timeoutInterval)