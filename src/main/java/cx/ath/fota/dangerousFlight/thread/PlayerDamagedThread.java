package cx.ath.fota.dangerousFlight.thread;

import cx.ath.fota.dangerousFlight.logger.DangerousLogger;
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
        DangerousLogger.debug("Player Damage Loop " + loopCounter);
        while (loopCounter > 0) {
            DangerousLogger.debug(Integer.toString(loopCounter));
            sleep();
            loopCounter--;
        }
        done();
    }

    private void allowFlight() {
        DangerousLogger.debug("Allow flight entered");
        synchronized (lock) {
            while (canFly) {
                try {
                    lock.wait();
                } catch (InterruptedException ignore) {
                    DangerousLogger.debug("Interrupted allow flight");
                }
            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep(crippleInSeconds * 1000L);
        } catch (InterruptedException ignored) {
            DangerousLogger.debug("Interrupted sleep");
        }
    }

    private void done() {
        DangerousLogger.debug("canFly=true");
        canFly = true;
    }

    @Override
    public void run() {
        Thread counter = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    allowFlight();
                    if (!canFly) {
                        playerDamagedLoop();
                    }
                } while (playerSignedIn);
                DangerousLogger.debug("THE WHILE LOOP IS OVER");
            }
        });
        counter.start();
    }

    public void playerDamaged() {
        DangerousLogger.debug("Lock.notifyAll() called!");
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
        synchronized (lock) {

            lock.notifyAll();
        }
    }

    public void playerDeath() {
        DangerousLogger.debug("Death called!");
        loopCounter = 0;
        canFly = true;
    }
}
