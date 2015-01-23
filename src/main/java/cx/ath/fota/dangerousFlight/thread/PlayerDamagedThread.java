package cx.ath.fota.dangerousFlight.thread;

import org.bukkit.scheduler.BukkitRunnable;

public class PlayerDamagedThread extends BukkitRunnable {
    private final Object lock = new Object();
    private final int crippleInSeconds;
    private volatile boolean canFly = true;
    private volatile boolean playerSignedIn = true;
    private volatile int loopCounter;

    public PlayerDamagedThread(int crippleInSeconds) {
        this.crippleInSeconds = crippleInSeconds;
    }

    private void playerDamagedLoop() {
        loopCounter = crippleInSeconds;
        while (loopCounter > 0) {
            sleep();
            loopCounter--;
        }
        done();
    }

    private void sleep() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException ignored) {
        }
    }

    private void done() {
        canFly = true;
    }

    private void allowFlight() {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException ignore) {
            }
        }
    }

    @Override
    public void run() {
        Thread counter = new Thread(new Runnable() {
            @Override
            public void run() {
                while (playerSignedIn) {
                    if (!canFly) {
                        playerDamagedLoop();
                    }
                    allowFlight();
                }
            }
        });
        counter.start();
    }

    public void playerDamaged() {
        canFly = false;
        loopCounter = crippleInSeconds;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public boolean canFly() {
        return canFly;
    }

    public void playerSignedOut() {
        playerSignedIn = false;
        canFly = false;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public void playerDeath() {
        loopCounter = 0;
        canFly = true;

    }
}
