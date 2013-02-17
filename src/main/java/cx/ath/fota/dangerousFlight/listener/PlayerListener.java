package cx.ath.fota.dangerousFlight.listener;

import cx.ath.fota.dangerousFlight.model.DFlier;
import cx.ath.fota.dangerousFlight.plugin.DangerousFlight;
import cx.ath.fota.dangerousFlight.thread.PlayerDamaged;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public class PlayerListener implements Listener {

    private volatile HashMap<Player, PlayerDamaged> playerDamagedHashMap = new HashMap<Player, PlayerDamaged>();
    private final DangerousFlight dangerousFlight;
    private int effectDurationInSeconds;
    private final PotionEffect potionSlowEffect;
    private final PotionEffect potionBlindEffect;

    public PlayerListener(DangerousFlight dangerousFlight) {
        this.dangerousFlight = dangerousFlight;
        int defaultEffectDuration = 3;
        int defaultStrength = 1;
        try {
            this.effectDurationInSeconds = Integer.parseInt(dangerousFlight.getConfig().get("CrippleDuration").toString(), defaultEffectDuration);
        } catch (NumberFormatException e) {
            this.effectDurationInSeconds = 3;
            System.out.println("An integer was not entered for 'CrippleDuration', using defaults");
        }

        try {
            defaultStrength = Integer.parseInt(dangerousFlight.getConfig().get("CrippleStrength", defaultStrength).toString());
        } catch (NumberFormatException e) {
            System.out.println("An integer was not entered for 'CrippleStrength', using defaults");
        }
        potionSlowEffect = new PotionEffect(PotionEffectType.SLOW, effectDurationInSeconds * 20, defaultStrength);
        potionBlindEffect = new PotionEffect(PotionEffectType.BLINDNESS, effectDurationInSeconds * 20, defaultStrength);
    }

    @EventHandler
    public void playerDamageEvent(EntityDamageEvent entityDamageEvent) {
        if (entityDamageEvent.getEntity() instanceof Player) {
            Player player = (Player) entityDamageEvent.getEntity();
            //   System.out.println("Player Hit!: " + player.getName());
            player.setFlying(false);
            player.addPotionEffect(potionSlowEffect, true);
            if (playerDamagedHashMap.containsKey(player)) {
                if (playerDamagedHashMap.get(player).isAlive()) {
                    //  System.out.println("Threads Alive!: " + player.getName());
                    playerDamagedHashMap.get(player).playerHit();
                } else {
                    //    System.out.println("Threads Dead!: " + player.getName());
                    playerDamagedHashMap.remove(player);
                    newPlayerHit(player);
                }
            } else {
                //  System.out.println("Brand new Player Hit!!: " + player.getName());
                newPlayerHit(player);
            }
        }
    }

    private void newPlayerHit(Player player) {
        PlayerDamaged playerDamagedThread = new PlayerDamaged(player.getName(), effectDurationInSeconds);
        this.playerDamagedHashMap.put(player, playerDamagedThread);
        playerDamagedThread.start();
    }

    @EventHandler
    public void setPlayerJoinEvent(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        DFlier dFlier;
        if ((dFlier = dangerousFlight.getPersistence().findByName(player.getName())) != null) {
            player.setAllowFlight(dFlier.getFlightEnabled());
            player.setFlying(dFlier.getFlying());
        } else {
            player.setAllowFlight(false);
        }
    }


    @EventHandler
    public void PlayerToggleFlight(PlayerToggleFlightEvent playerToggleFlightEvent) {
        Player player = playerToggleFlightEvent.getPlayer();
        if (playerDamagedHashMap.containsKey(player) && playerDamagedHashMap.get(player).isAlive())
            playerToggleFlightEvent.setCancelled(true);
        else {
            player.setFlying(playerToggleFlightEvent.isFlying());
            dangerousFlight.getPersistence().saveOrUpdate(new DFlier(player));

        }
    }
}

