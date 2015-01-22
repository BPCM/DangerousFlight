package cx.ath.fota.dangerousFlight.listener;

import cx.ath.fota.dangerousFlight.logger.DangerousLogger;
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
    private final int crippleStrength;
    private final PotionEffect potionSlowEffect;

    public DangerousListener(DangerousFlight dangerousFlight) {
        int defaultCrippleDuration = 3;
        int defaultCrippleStrength = 1;
        this.dangerousFlight = dangerousFlight;
        this.crippleStrength = dangerousFlight.getNodeData("CrippleStrength", defaultCrippleStrength);
        this.crippleDurationInSeconds = dangerousFlight.getNodeData("CrippleDuration", defaultCrippleDuration);
        potionSlowEffect = new PotionEffect(PotionEffectType.SLOW, crippleDurationInSeconds * 20, crippleStrength);
    }


    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        DangerousLogger.debug(String.format("Player %s has joined the server!", player));
        //     if (containsPlayer(player)) {//todo logic is good. need to be able to remove that thread.....
        PlayerDamagedThread playerDamagedThread = new PlayerDamagedThread(crippleDurationInSeconds);
        playerDamagedThread.run();
        playerThreadMap.put(player, playerDamagedThread);
        //   }
        DFlier dFlier;
        if ((dFlier = dangerousFlight.getPersistence().findByName(player.getName())) != null) {
            player.setAllowFlight(dFlier.getFlightEnabled());
            player.setFlying(dFlier.getFlying());
            DangerousLogger.debug("Loaded Player: " + dFlier.toString());
        } else {
            player.setAllowFlight(false);
            DangerousLogger.debug("Failed to load player: " + player.getDisplayName());
        }

    }


    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent playerDeathEvent) {
        DangerousLogger.debug(playerDeathEvent.getEntity().getDisplayName() + " died.");
        playerThreadMap.get(playerDeathEvent.getEntity()).playerDeath();

    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent playerQuitEvent) {
        PlayerDamagedThread playerDamagedThread = playerThreadMap.get(playerQuitEvent.getPlayer());
        //playerThreadMap.remove(playerQuitEvent.getPlayer());

        playerDamagedThread.playerSignedOut();
        DangerousLogger.debug("QUIT");//todo is this thread killed ever?
    }


    @EventHandler
    public void playerDamageEvent(EntityDamageEvent entityDamageEvent) {
        if (entityDamageEvent.getEntity() instanceof Player) {
            Player player = (Player) entityDamageEvent.getEntity();
            DangerousLogger.debug(player.getDisplayName() + " took damage.");
            playerThreadMap.get(player).playerDamaged();

        }
    }

    @EventHandler
    public void PlayerToggleFlight(PlayerToggleFlightEvent playerToggleFlightEvent) {
        Player player = playerToggleFlightEvent.getPlayer();
        PlayerDamagedThread playerDamagedThread = playerThreadMap.get(player);
        DangerousLogger.debug("Player is flying " + player.isFlying());
        DangerousLogger.debug("Player flight allowed " + player.getAllowFlight());
        DangerousLogger.debug("\nThread can fly " + playerDamagedThread.canFly());
        DangerousLogger.debug("\nPlayerToggleEvent " + playerToggleFlightEvent.isFlying() + "\n");
        playerToggleFlightEvent.setCancelled(!playerDamagedThread.canFly());
        DangerousLogger.debug("Player attempted flight :" + !playerDamagedThread.canFly());
        if (!playerDamagedThread.canFly()) {
            player.setFlying(false);
        }
        DFlier dFlier = new DFlier(player);
        dFlier.setFlying(playerToggleFlightEvent.isFlying());
        DangerousLogger.debug(dFlier.toString());
        dangerousFlight.getPersistence().saveOrUpdate(dFlier);
    }

    private boolean containsPlayer(Player player) {
        for (Player player1 : playerThreadMap.keySet()) {
            if (player.getUniqueId().compareTo(player1.getUniqueId()) == 0) return true;
        }
        return false;
    }

}
