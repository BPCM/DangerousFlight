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
        System.out.println("Player Damage Loop " + loopCounter);
        while (loopCounter > 0) {
            System.out.println(Integer.toString(loopCounter) + " " + playerSignedIn);
            sleep();
            loopCounter--;
        }
        done();
    }


    private void sleep() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException ignored) {
            System.out.println("Interrupted sleep");
        }
    }

    private void done() {
        System.out.println("canFly=true");
        canFly = true;
    }

    private void allowFlight() {
        System.out.println("allowFlight entered " + playerSignedIn);
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException ignore) {
                System.out.println("Interrupted allow flight");
            }
        }
    }

    @Override
    public void run() {
        Thread counter = new Thread(new Runnable() {
            @Override
            public void run() {
                while (playerSignedIn) {
                    System.out.println("run loop started " + playerSignedIn);
                    if (!canFly) {
                        playerDamagedLoop();
                    }
                    allowFlight();
                }
                System.out.println("THIS THREAD IS DED");
            }
        });
        counter.start();
    }

    public void playerDamaged() {
        System.out.println("Lock.notifyAll() called!");
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
        System.out.println("Called Player signed out!");
        playerSignedIn = false;
        canFly = false;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public void playerDeath() {
        System.out.println("Death called!");
        loopCounter = 0;
        canFly = true;
    }
}
