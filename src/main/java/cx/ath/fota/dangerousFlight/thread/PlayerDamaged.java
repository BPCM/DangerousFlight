package cx.ath.fota.dangerousFlight.thread;

public class PlayerDamaged extends Thread {

    public PlayerDamaged(String playerName, int ticksPerHit) {
        this.ticksPerHit = ticksPerHit;
        super.getName();
    }

    private final int ticksPerHit;
    private volatile int ticks = ticksPerHit;


    public void run() {
        while (ticks > 0) {
            //      System.out.println(ticks + " " + getName());
            ticks--;
            try {
                sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //    System.out.println("DONE! " + getName());
    }

    public void playerHit() {
        ticks = ticksPerHit;
    }
}