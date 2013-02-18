package cx.ath.fota.dangerousFlight.plugin;

import cx.ath.fota.dangerousFlight.listener.PlayerListener;
import cx.ath.fota.dangerousFlight.model.DFlier;
import cx.ath.fota.dangerousFlight.persistance.Persistence;
import cx.ath.fota.dangerousFlight.persistance.PersistenceDatabase;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;
import java.util.List;


public class DangerousFlight extends JavaPlugin {
    private Persistence persistence;
    private final float normalFlightSpeed, fastFlightSpeed;

    public DangerousFlight() {
        normalFlightSpeed = (float) .1;
        fastFlightSpeed = (float) .5;
    }

    public void onDisable() {
    }

    public void onEnable() {
        FileConfiguration fileConfiguration = getConfig();
        fileConfiguration.options().copyDefaults(true);
        saveConfig();
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        persistence = new PersistenceDatabase(this);
      /*  todo flat file persistence
      if ("FlatFile".equalsIgnoreCase(getConfig().getString("Storage"))) {
            persistence = new PersistenceFlatFile(this);
        } else {
            persistence = new PersistenceDatabase(this);
        }*/
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (command.getName().equals("fly")) {
            if (player.getAllowFlight() && player.getFlySpeed() == normalFlightSpeed)
                toggleFlight(player, false);
            else if (player.getAllowFlight() && player.getFlySpeed() == fastFlightSpeed) {
                setFlySpeed(player,normalFlightSpeed);
            } else {
                player.setFlySpeed(normalFlightSpeed);
                toggleFlight(player, true);
            }
            this.getPersistence().saveOrUpdate(new DFlier(player));
            return true;
        } else if (command.getName().equals("ff")) {
            if (player.getAllowFlight() && player.getFlySpeed() == fastFlightSpeed)
                toggleFlight(player, false);
            else if (player.getAllowFlight() && player.getFlySpeed() == normalFlightSpeed) {
                setFlySpeed(player, fastFlightSpeed);
            } else {
                player.setFlySpeed(fastFlightSpeed);
                toggleFlight(player, true);
            }
            this.getPersistence().saveOrUpdate(new DFlier(player));
            return true;
        }
        return false;

    }

    private void toggleFlight(Player player, boolean flying) {
        if (flying)
            sendFlightMessage(player);
        else sendPlayerMessage(player, "Flight disabled");
        player.setAllowFlight(flying);
    }

    private void setFlySpeed(Player player, float flySpeed) {
        player.setFlySpeed(flySpeed);
        sendFlightMessage(player);

    }

    private void sendFlightMessage(Player player) {
        if (player.getFlySpeed() == fastFlightSpeed)
            sendPlayerMessage(player, "Fast Flight enabled");
        else sendPlayerMessage(player, "Flight enabled");
    }

    private void sendPlayerMessage(Player player, String message) {
        player.sendMessage(String.format("%s%s", ChatColor.YELLOW, message));
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> classes = new LinkedList<Class<?>>();
        classes.add(DFlier.class);
        return classes;
    }

    @Override
    public void installDDL() {
        super.installDDL();
    }

    public Persistence getPersistence() {
        return persistence;
    }
}

