package cx.ath.fota.dangerousFlight.plugin;

import cx.ath.fota.dangerousFlight.listener.DangerousListener;
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
        normalFlightSpeed = .1F;
        fastFlightSpeed = .5F;
    }

    public void onDisable() {
    }

    public void onEnable() {
        FileConfiguration fileConfiguration = getConfig();
        fileConfiguration.options().copyDefaults(true);
        saveConfig();
        getServer().getPluginManager().registerEvents(new DangerousListener(this), this);
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
        String commandName = command.getName();
        boolean b = false;
        if (commandName.equals("fly")) {
            if (sender.hasPermission("dangerousFlight.fly")) { //todo this string should not be static
                handleFlightCommand(player, normalFlightSpeed);
                b = true;
            }
        } else if (commandName.equals("ff")) {
            if (sender.hasPermission("dangerousFlight.fastFly")) {
                handleFlightCommand(player, fastFlightSpeed);
                b = true;
            }
        }
        return b;
    }

    private void handleFlightCommand(Player player, float flightSpeed) {//todo english is your friend

        if (player.getAllowFlight()) {
            if (player.getFlySpeed() == flightSpeed) {
                player.setAllowFlight(false);
                player.setFlySpeed(normalFlightSpeed);
                sendPlayerMessage(player, "Flight disabled");
            } else {
                player.setFlySpeed(flightSpeed);
                sendPlayerMessage(player, "Flight speed modified!");
            }
        } else {
            player.setAllowFlight(true);
            player.setFlySpeed(flightSpeed);
            sendPlayerMessage(player, "Flight Enabled");
        }
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

    public int getNodeData(String nodeName, int defaultData) {
        Integer integer = (Integer) getConfig().getDefaults().get(nodeName);
        try {
            return Integer.parseInt(getConfig().get(nodeName).toString());
        } catch (NumberFormatException e) {
            if (integer != null) defaultData = integer;
            System.out.println(String.format("Configuration error - An Integer was not properly entered for '%s', using default: %s", nodeName, defaultData));
            return defaultData;
        }
    }

    @Override
    public void installDDL() {
        super.installDDL();
    }

    public Persistence getPersistence() {
        return persistence;
    }
}

