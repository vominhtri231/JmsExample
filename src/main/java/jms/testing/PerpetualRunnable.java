package jms.testing;

public class PerpetualRunnable implements Runnable {
    private Runnable runnable;

    private boolean isStop;

    PerpetualRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() {
        while (!isStop()) {
            runnable.run();
        }
    }

    public synchronized void stop() {
        isStop = true;
    }

    private synchronized boolean isStop() {
        return isStop;
    }
}
