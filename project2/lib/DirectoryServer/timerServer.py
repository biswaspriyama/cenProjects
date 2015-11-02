from threading import Thread, Event, Timer
import time

def TimerReset(*args, **kwargs):
    return _TimerReset(*args, **kwargs)


class _TimerReset(Thread):


    def __init__(self, interval, function, args=[], kwargs={}):
        Thread.__init__(self)
        self.interval = interval
        self.function = function
        self.args = args
        self.kwargs = kwargs
        self.finished = Event()
        self.resetted = True

    def cancel(self):
        """Stop the timer if it not finished yet"""
        self.finished.set()

    def run(self):
        print "Time: %s - timer running..." % time.asctime()
        while self.resetted:
            print "Resend packet if ACK not received in %s seconds" %self.interval
            self.resetted = False
            self.finished.wait(self.interval)

        if not self.finished.isSet():
            self.function(*self.args, **self.kwargs)
        self.finished.set()
        print "Timer finished!"

    def reset(self, interval=None):
        """ Reset the timer """

        if interval:
            print "Time: %s - timer resetting to %.2f..." % (time.asctime(), interval)
            self.interval = interval
        else:
            print "Timer resetting..."

        self.resetted = True
        self.finished.set()
        self.finished.clear()

