package uk.codingbadgers.bFundamentals;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.nodinchan.ncbukkit.loader.Loader;

import uk.codingbadgers.bFundamentals.module.Module;
import uk.codingbadgers.bFundamentals.module.ModuleHelpTopic;

/**
 * The ModuleLoader.
 */
public class ModuleLoader {
	
	/** The List of modules. */
	private final List<Module> m_modules;
	private Loader<Module> m_loader;

	/**
	 * Instantiates a new module loader.
	 */
	public ModuleLoader() {
		m_modules = new LinkedList<Module>();
		if (getModuleDir().mkdir())
			bFundamentals.log(Level.INFO, "Creating Module Directory...");
	}
	
	/**
	 * Gets the directory of the modules.
	 *
	 * @return the module dir
	 */
	public File getModuleDir() {
		return new File(bFundamentals.getInstance().getDataFolder(), "modules");
	}
	
	/**
	 * Loads the modules.
	 */
	public void load() {
		m_loader = new Loader<Module>(bFundamentals.getInstance(), getModuleDir());
		m_modules.addAll(m_loader.sort(m_loader.load()));
		
		for (Module module : m_modules) {
			module.onLoad();
			module.log(Level.INFO, module.getName() + " v:" + module.getVersion() + " has been loaded successfuly");
		}
		
		bFundamentals.log(Level.INFO, "Loaded " + m_modules.size() + " modules.");
	}
	
	/**
	 * Loads a module with a given name
	 * 
	 * @param fileName the files name
	 */
	public void load(String fileName) {
		File module = new File(getModuleDir() + File.separator + fileName + ".jar");
		load(module);
	}
	
	/**
	 * Loads a module with a given name
	 * 
	 * @param fileName the files name
	 */
	public void load(File file) {
		if (m_loader == null) 
			m_loader = new Loader<Module>(bFundamentals.getInstance(), getModuleDir());
		
		if (getModule(file) != null)
			throw new IllegalArgumentException("Module " + file.getName() + " is already loaded");
		
		m_modules.clear();
		m_modules.addAll(m_loader.sort(m_loader.load(file)));
		
		Module module = getModule(file);
		module.onLoad();
		module.log(Level.INFO, module.getName() + " v:" + module.getVersion() + " has been loaded successfuly");
	}
	
	/**
	 * Unloads the modules.
	 */
	public void unload() {
		disable();
		m_modules.clear();
	}
	
	/**
	 * Unload a specific module.
	 *
	 * @param module the module
	 */
	public void unload(Module module) {
		module.onDisable();
		m_modules.remove(module);
	}
	
	/**
	 * Run on enable in all modules.
	 */
	public void enable() {
		for (Module module : m_modules) {
			try {
				module.onEnable();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			bFundamentals.getInstance().getServer().getHelpMap().addTopic(new ModuleHelpTopic(module));
		}
	}
	
	/**
	 * run on disable in all modules.
	 */
	public void disable() {
		for (Module module : m_modules) {
			try {
				module.onDisable();
				for (Listener listener : module.getListeners()) {
					HandlerList.unregisterAll(listener);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Gets the modules.
	 *
	 * @return the modules
	 */
	public List<Module> getModules() {
		return m_modules;
	}

	public Module getModule(String string) {
		Iterator<Module> itr = m_modules.iterator();
		while(itr.hasNext()) {
			if (itr.next().getName().equalsIgnoreCase(string))
				return itr.next();
		}
		return null;		
	}
	
	public Module getModule(File file) {
		Iterator<Module> itr = m_modules.iterator();
		while(itr.hasNext()) {
			if (itr.next().getFile().getPath().equalsIgnoreCase(file.getPath()))
				return itr.next();
		}
		return null;
	}

}
