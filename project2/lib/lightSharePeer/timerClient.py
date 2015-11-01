from threading import Thread, Event, Timer
import time

def TimerReset(*args, **kwargs):
    """ Global function for Timer """
    return _TimerReset(*args, **kwargs)


class _TimerReset(Thread):
    """Call a function after a specified number of seconds:

    t = TimerReset(30.0, f, args=[], kwargs={})
    t.start()
    t.cancel() # stop the timer's action if it's still waiting
    """

    def __init__(self, interval, function, args=[], kwargs={}):
        Thread.__init__(self)
        self.interval = interval
        self.function = function
        self.args = args
        self.kwargs = kwargs
        self.finished = Event()
        self.resetted = True

    def cancel(self):
        """Stop the timer if it hasn't finished yet"""
        self.finished.set()
        print "Timer Stopped!"

    def run(self):
        print "Timer started..."
        while self.resetted:
            print "Resend packet if ACK not received in %s seconds" %self.interval
            self.resetted = False
            self.finished.wait(self.interval)

        if not self.finished.isSet():
            self.function(*self.args, **self.kwargs)



        self.finished.set()
        #self.reset(self.interval)
        #print "Timer Expired"


    def reset(self, interval=None):
        """ Reset the timer """

        if interval:
            print "Time: %s - timer resetting to %.2f..." % (time.asctime(), interval)
            self.interval = interval
        else:
            print "Time: %s - timer resetting..." % time.asctime()

        self.resetted = True
        self.finished.set()
        self.finished.clear()


# def hello(x):
#     print "i am in function" + x
#
# tim = TimerReset(5, hello,"a")
# tim.start()
# print "i am outside"
# # print "Time: %s - sleeping for 10..." % time.asctime()
# # time.sleep (10)
# print "Time: %s - end..." % time.asctime()
#
# print "\n\n"
