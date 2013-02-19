package cx.ath.fota.dangerousFlight.thread;

import org.bukkit.entity.Player;

public class PlayerDamaged extends Thread {

    private final int ticksPerHit;
    private volatile int ticks;

    public PlayerDamaged(Player player, int crippleDuration) {
        super(player.getName());
        this.ticksPerHit = crippleDuration;
        this.ticks = ticksPerHit;
    }

    public void run() {
        while (ticks > 0) {
//            System.out.println(ticks + " " + getName());
            ticks--;
            try {
                sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        System.out.println("DONE! " + getName());
    }

    public void playerHit() {
        ticks = ticksPerHit;
    }
}