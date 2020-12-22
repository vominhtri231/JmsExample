package jms.testing;

class PerpetualRunnable implements Runnable {
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

    synchronized void stop() {
        isStop = true;
    }

    private synchronized boolean isStop() {
        return isStop;
    }
}
