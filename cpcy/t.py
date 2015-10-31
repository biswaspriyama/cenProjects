import time
now = time.time()
future = now + 0.1
while time.time() < future:
    print "hi"

    
