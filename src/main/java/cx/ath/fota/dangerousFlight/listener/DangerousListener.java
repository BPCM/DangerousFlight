package cx.ath.fota.dangerousFlight.listener;

import cx.ath.fota.dangerousFlight.model.DFlier;
import cx.ath.fota.dangerousFlight.plugin.DangerousFlight;
import cx.ath.fota.dangerousFlight.thread.PlayerDamagedThread;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class DangerousListener implements Listener {
    private final Map<Player, PlayerDamagedThread> playerThreadMap = new HashMap<Player, PlayerDamagedThread>();
    private final DangerousFlight dangerousFlight;
    private final int crippleDurationInSeconds;
    private final PotionEffect potionSlowEffect;

    public DangerousListener(DangerousFlight dangerousFlight) {
        int defaultCrippleDuration = 3;
        int defaultCrippleStrength = 80;
        this.dangerousFlight = dangerousFlight;
        int crippleStrength = dangerousFlight.getNodeData("CrippleStrength", defaultCrippleStrength);
        this.crippleDurationInSeconds = dangerousFlight.getNodeData("CrippleDuration", defaultCrippleDuration);
        potionSlowEffect = new PotionEffect(PotionEffectType.SLOW, crippleDurationInSeconds * 20, crippleStrength);
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        PlayerDamagedThread playerDamagedThread = new PlayerDamagedThread(crippleDurationInSeconds);
        playerDamagedThread.run();
        playerThreadMap.put(player, playerDamagedThread);
        DFlier dFlier;
        if ((dFlier = dangerousFlight.getPersistence().findBuUUID(player.getUniqueId())) != null) {
            player.setAllowFlight(dFlier.getFlightEnabled());
            player.setFlying(dFlier.getFlying());
            dangerousFlight.logger.fine(String.format("Loaded Player: %s %s", player.getName(), dFlier.toString()));
        } else {
            player.setAllowFlight(false);
            dangerousFlight.logger.fine(String.format("Failed to load Player: %s %s", player.getDisplayName(), player.getUniqueId()));
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent playerDeathEvent) {
        playerThreadMap.get(playerDeathEvent.getEntity()).playerDeath();
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent playerQuitEvent) {
        PlayerDamagedThread playerDamagedThread = playerThreadMap.get(playerQuitEvent.getPlayer());
        playerDamagedThread.playerSignedOut();
        playerThreadMap.remove(playerQuitEvent.getPlayer());
    }

    @EventHandler
    public void playerDamageEvent(EntityDamageEvent entityDamageEvent) {
        if (entityDamageEvent.getEntity() instanceof Player) {
            Player player = (Player) entityDamageEvent.getEntity();
            PlayerDamagedThread playerDamagedThread = playerThreadMap.get(player);
            if (player.isFlying() || !playerDamagedThread.canFly()) {
                player.setFlying(false);
                player.addPotionEffect(potionSlowEffect, true);
                dangerousFlight.logger.fine(player.getDisplayName() + " has been knocked out of the air");
                playerDamagedThread.playerDamaged();
            }
        }
    }

    @EventHandler
    public void PlayerToggleFlight(PlayerToggleFlightEvent playerToggleFlightEvent) {
        Player player = playerToggleFlightEvent.getPlayer();
        PlayerDamagedThread playerDamagedThread = playerThreadMap.get(player);
        playerToggleFlightEvent.setCancelled(!playerDamagedThread.canFly());
        if (!playerDamagedThread.canFly())
            player.setFlying(false);
        DFlier dFlier = new DFlier(player);
        dFlier.setFlying(playerToggleFlightEvent.isFlying());
        dangerousFlight.getPersistence().saveOrUpdate(dFlier);
        dangerousFlight.logger.fine(player.getPlayerListName() + " " + dFlier.toString());
    }
}
