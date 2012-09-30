package uk.codingbadgers.bFundamentals.module.loader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/*     Copyright (C) 2012  Nodin Chan <nodinchan@live.com>
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Loadable - Base for loadable classes
 * 
 * @author NodinChan
 *
 */
public class Loadable implements Cloneable {
	
	private String name;
	
	private File configFile;
	private FileConfiguration config;
    private LoadableDescriptionFile description;
	
	private JarFile jar;
	private File dataFolder;
	private File file;
	
	public Loadable(String name) {
		this.name = name;
	}
        
    public Loadable() {
    }
	
	@Override
	public Loadable clone() {
		Loadable loadable = new Loadable(name);
		loadable.config = YamlConfiguration.loadConfiguration(configFile);
		loadable.configFile = configFile;
		loadable.dataFolder = dataFolder;
		loadable.file = file;
		loadable.jar = jar;
        loadable.description = description;
		return loadable;
	}
	
	public File datafolder(File dataFolder) {
		dataFolder.mkdirs();
		return this.dataFolder = dataFolder;
	}
	
	public File file(File file) {
		return this.file = file;
	}
	
	public JarFile jar(JarFile jar) {
		return this.jar = jar;
	}
	
	public LoadableDescriptionFile description(LoadableDescriptionFile ldf) {
		this.description = ldf;
        this.name = description.getName();
		return description;
	}
	
	public void init() {}

	/**
	 * Gets the config
	 * 
	 * @return The config
	 */
	public FileConfiguration getConfig() {
		if (config == null)
			reloadConfig();
		
		return config;
	}
	
	/**
	 * Gets the data folder of this
	 * 
	 * @return The directory of this
	 */
	public File getDataFolder() {
		return dataFolder;
	}
	
	/**
	 * Gets the file of the loadable
	 * 
	 * @return
	 */
	public File getFile() {
		return file;
	}
	
	/**
	 * Gets the name of the Loadable
	 * 
	 * @return The name
	 */
	public final String getName() {
		return name;
	}
	
	/**
	 * Gets an embedded resource in this plugin
	 * 
	 * @param name File name of the resource
	 * 
	 * @return InputStream of the file if found, otherwise null
	 */
	public InputStream getResource(String name) {
		ZipEntry entry = jar.getEntry(name);
		
		if (entry == null)
			return null;
		
		try { return jar.getInputStream(entry); } catch (IOException e) { return null; }
	}
	
	/**
	 * Reloads the config
	 */
	public void reloadConfig() {
		if (configFile == null)
			configFile = new File(getDataFolder(), "config.yml");
		
		config = YamlConfiguration.loadConfiguration(configFile);
		
		InputStream defConfigStream = getResource("config.yml");
		
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			config.setDefaults(defConfig);
		}
	}
	
	/**
	 * Saves the config
	 */
	public void saveConfig() {
		if (config == null || configFile == null)
			return;
		
		try { config.save(configFile); } catch (IOException e) {}
	}
	
	/**
	 * Called when the Loadable is unloaded
	 */
	public void unload() {}
	
        public LoadableDescriptionFile getDesciption() {
            return this.description;
        }
        
	public static final class LoadResult {
		
		private final Result result;
		
		private final String reason;
		
		public LoadResult() {
			this(Result.SUCCESS, "");
		}
		
		public LoadResult(String failReason) {
			this(Result.FAILURE, failReason);
		}
		
		public LoadResult(Result result, String reason) {
			this.result = result;
			this.reason = reason;
		}
		
		public String getReason() {
			return reason;
		}
		
		public Result getResult() {
			return result;
		}
		
		public enum Result { FAILURE, SUCCESS }
	}
}