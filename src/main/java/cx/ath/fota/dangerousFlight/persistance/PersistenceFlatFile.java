/*
todo implement flat file saving
package cx.ath.fota.dangerousFlight.persistance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

import cx.ath.fota.dangerousFlight.plugin.DangerousFlight;
import org.me.bukkit.TestPlugin;

*/
/**
 * This class is a very, very, very basic flat-file implementation of the
 * {@link IPersistence} interface.
 * <p>
 * Do not use this implementation on any CraftBukkit server. It is not flexible
 * and not error prone. It only serves as an illustration how to start a
 * flat-file persistence implementation.
 * </p>
 * 
 * @author cryxli
 *//*

public class PersistenceFlatFile implements Persistence {

	*/
/** Reference to plugin's main class. *//*

	public final DangerousFlight plugin;

	*/
/** Keep the data in memory until the plugin shuts down. *//*

	private final Properties data = new Properties();

	public PersistenceFlatFile(final DangerousFlight plugin) {
		this.plugin = plugin;

		// load persisted data from last run
		loadProperties();
	}

	*/
/** Get the properties file containing plugin data. *//*

	private File getPropertyFile() {
		// bukkit already create the data folder for us
		File dataFolder = plugin.getDataFolder();
		// create/read from a file within that folder
		return new File(dataFolder, "data.properties");
	}

	*/
/**
	 * Load persisted plugin data.
	 * 
	 * @see #getPropertyFile()
	 *//*

	private void loadProperties() {
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(getPropertyFile());
			data.load(stream);
		} catch (FileNotFoundException e) {
			// the file does not exist which means there is no persisted data,
			// but that's just fine
		} catch (IOException e) {
			// an error while reading the data. not good

			// log error
			plugin.getLogger()
					.log(Level.SEVERE, "Error loading plugin data", e);
		} finally {
			if (stream != null) {
				try {
					// silently close the input stream
					stream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@Override
	public String findByName(final String playerName) {
		// a very simple key look-up into a properties file
		return data.getProperty(playerName);
	}

	*/
/**
	 * Persist plugin data to disk.
	 * 
	 * @see #getPropertyFile()
	 *//*

	private void saveProperties() {
		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(getPropertyFile());
			data.store(stream, "TestPlugin's data");
		} catch (IOException e) {
			// an error while reading the data. not good

			// log error
			plugin.getLogger().log(Level.SEVERE, "Error saving plugin data", e);
		} finally {
			if (stream != null) {
				try {
					// silently close the output stream
					stream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@Override
	public void save(final String key, final String value) {
		// put the value into the properties file, store it later
		data.setProperty(key, value);
	}

	@Override
	public void shutdown() {
		// persist any data kept in memory
		saveProperties();
	}

}
*/
