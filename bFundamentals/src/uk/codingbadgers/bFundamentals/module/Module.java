package uk.codingbadgers.bFundamentals.module;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import uk.codingbadgers.bFundamentals.bFundamentals;
import uk.codingbadgers.bFundamentals.commands.ModuleCommand;
import uk.codingbadgers.bFundamentals.commands.ModuleCommandHandler;
import uk.codingbadgers.bFundamentals.module.loader.Loadable;
import uk.codingbadgers.bFundamentals.update.UpdateThread;
import uk.codingbadgers.bFundamentals.update.Updater;
import uk.thecodingbadgers.bDatabaseManager.Database.BukkitDatabase;

/**
 * The base Module class.
 */
public abstract class Module extends Loadable implements Listener {

	/** The base plugin. */
	protected final bFundamentals m_plugin;
	
	/** The logger for this module */
	private ModuleLogger m_log;
	
	/** The version of the module. */
	private String m_version;
	
	/** The name of the module. */
	private String m_name;
	
	/** The Update thread for this module */
	private UpdateThread m_updater;
	
	/** The config. */
	protected FileConfiguration m_config;
	
	/** The config file. */
	protected File m_configFile = null;
	
	/** The language map. */
	private HashMap<String, String> m_languageMap = new HashMap<String, String>();
	
	/** The commands registered to this module. */
	protected List<ModuleCommand> m_commands = new ArrayList<ModuleCommand>();

	/** All the listeners registered to this module */
	private List<Listener> m_listeners = new ArrayList<Listener>();
	
	/** The database registered to the modules. */
	protected static BukkitDatabase m_database = null;
	
	/** The Permissions instance. */
	private static Permission m_permissions = null;
	
	/** Whether this module is in debug mode */
	private boolean m_debug = false;

	/** Whether the language file is loaded. */
	private boolean loadedLanguageFile;
	
	/**
	 * Instantiates a new module.
	 *
	 * @param ldf the modules desciption file
	 */
	public Module() {
		super();
		m_plugin = bFundamentals.getInstance();
		m_database = bFundamentals.getBukkitDatabase();
		m_debug = bFundamentals.getConfigurationManager().getDebug();
		m_permissions = bFundamentals.getPermissions();
	}
	
	public void init() {
		m_version = getDesciption().getVersion();
		m_name = getDesciption().getName();
		m_log = new ModuleLogger(this);
	}
	
	protected void setUpdater(Updater updater) {
		m_updater = new UpdateThread(updater);
		log(Level.INFO, "Set new updater to " + m_updater.getUpdater().getUpdater());
	}
	
	public void update() {
		if (m_updater == null) {
			log(Level.INFO, "Updater is null, cannot check for updates");
			return;
		}
		
		m_updater.start();
	}
	
	/**
	 * Load language file.
	 */
	protected void loadLanguageFile() {
		File languageFile = new File(getDataFolder() + File.separator + m_name + "_" + bFundamentals.getConfigurationManager().getLanguage() + ".lang");
		
		if (!languageFile.exists()) {
			log(Level.SEVERE, "Missing language file '" + languageFile.getAbsolutePath() + "'!");
			
			boolean foundLangFile = false;
			InputStream stream = null;
			FileOutputStream fstream = null;
			
			try {
				stream = getClass().getResourceAsStream("/" + languageFile.getName());
				
				// if default file exists in jar, copy it out to the right directory
				if (stream != null) {
					fstream = new FileOutputStream(languageFile);
					
					foundLangFile = true;
					IOUtils.copy(stream, fstream);
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (stream != null) {
						stream.close();
					}
					
					if (fstream != null) {
						fstream.close();
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			
			if (foundLangFile) {
				log(Level.INFO, "Copied default language file from jar file");
			} else {
				return;
			}
		}
		
		log(Level.INFO, "Loading Language File: " + languageFile.getName());
		
		FileInputStream fstream = null;
		DataInputStream in = null;
		BufferedReader br = null;
		
		try {
			fstream = new FileInputStream(languageFile);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			
			String line = null;
			String key = null;
			while ((line = br.readLine()) != null)   {
				
				if (line.isEmpty() || line.startsWith("//"))
					continue;
				
				if (line.startsWith("#")) {
					key = line.substring(1);
					continue;
				}
				
				if (key == null) {
					log(Level.WARNING, "Trying to parse a language value, with no key set!");
					continue;
				}

				m_languageMap.put(key, line);				
			}
			
			loadedLanguageFile = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fstream != null) {
					fstream.close();		
				}
				if (in != null) {
					in.close();
				}
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
	}
	
	/**
	 * Log to console.
	 *
	 * @param level the Log level
	 * @param string the message
	 */
	public void log(Level level, String string) {
		m_log.log(Level.INFO, string);
	}
	
	/**
	 * Get the logger
	 * 
	 * @return this modules logger
	 */
	public Logger getLogger() {
		return m_log;
	}
	
	/**
	 * Register a event listener.
	 *
	 * @param listener the bukkit event listener
	 */
	public final void register(Listener listener) {
		m_plugin.getServer().getPluginManager().registerEvents(listener, m_plugin);
		m_listeners.add(listener);
	}
	
	/**
	 * Gets the permissions.
	 *
	 * @return the permissions
	 */
	public Permission getPermissions() {
		return m_permissions;
	}
	
	/**
	 * On enable.
	 */
	public abstract void onEnable();
	
	/**
	 * On disable.
	 */
	public abstract void onDisable();
	
	/**
	 * On Load.
	 */
	public void onLoad() {}
	
	/**
	 * On command.
	 *
	 * @param sender the sender
	 * @param label the label
	 * @param args the arguments
	 * @return true, if successful
	 */
	public boolean onCommand(CommandSender sender, String label, String[] args){
		return false;
	}
	
	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public String getVersion() {
		return m_version;
	}
	
	/**
	 * Checks for permission.
	 *
	 * @param player the player
	 * @param node the node
	 * @return true, if successful
	 */
	public static boolean hasPermission(final Player player, final String node) {
		if (m_permissions.has(player, node)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Send message to a player.
	 *
	 * @param name the name of the module
	 * @param player the player to send to
	 * @param message the message
	 */
	public static void sendMessage(String name, Player player, String message) {
		player.sendMessage(ChatColor.DARK_PURPLE + "[" + name + "] " + ChatColor.RESET + message);
	}
	

	/**
	 * Register command.
	 *
	 * @param command the command
	 */
	protected void registerCommand(ModuleCommand command) {
		ModuleCommandHandler.registerCommand(this, command);
	}
	
	/**
	 * Get all commands registered to this module
	 * 
	 * @return the commands
	 */
	public List<ModuleCommand> getCommands() {
		return m_commands;
	}
	
	/**
	 * Gets the language value.
	 *
	 * @param key the key
	 * @return the language value
	 */
	public String getLanguageValue(String key) {
		Validate.notNull(key, "Language key cannot be null");
		
		if (!loadedLanguageFile) {
			log(Level.SEVERE, "Cannot get language value before loading language file");
		}
		
		String value = m_languageMap.get(key);
		
		if (value == null) {
			value = key.toLowerCase().replace("-", " ");
		}
		
		return value;
	}
	

	/**
	 * Get all the listeners registered to this module, for cleaning up on disable
	 * 
	 * @return a list of all listeners
	 */
	public List<Listener> getListeners() {
		return m_listeners;
	}
	
	/**
	 * Is debug mode enabled on this module
	 * 
	 * @return if debug is enabled
	 */
	public boolean isDebug() {
		return m_debug;
	}
	
	/**
	 * Set the debug mode for this module
	 * 
	 * @param debug whether debug is on or not
	 */
	public void setDebug(boolean debug) {
		m_debug = debug;
	}
	
	/**
	 * Output a message to console if debug mode is on
	 * 
	 * @param message the message to output
	 */
	public void debugConsole(String message){ 
		if (!m_debug)
			return;
		
		log(Level.INFO, "[Debug] " + message);
	}
}