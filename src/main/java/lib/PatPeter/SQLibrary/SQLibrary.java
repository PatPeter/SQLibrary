/*
 * Date Created: 2012-03-11 14:20
 * @author <a href="http://forums.bukkit.org/members/jertocvil.18552/">jertocvil</a>
 */
package lib.PatPeter.SQLibrary;
 
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Class for running the library as a plugin.<br>
 * Date Created: 2012-03-11 14:20.
 * 
 * @author Nicholas Solin, a.k.a. PatPeter
 */
public class SQLibrary extends JavaPlugin {
	/**
	 * Logger for the Minecraft server.
	 */
	public static final Logger logger = Logger.getLogger("Minecraft");
	
	/**
	 * onEnable() method for the plugin.
	 */
	@Override
	public void onEnable() {
	    SQLibrary.logger.log(Level.INFO, "SQLibrary loaded.");
	}
	
	/**
	 * onDisable method for the plugin.
	 */
	@Override
	public void onDisable() {
	    SQLibrary.logger.log(Level.INFO, "SQLibrary stopped.");
	}
} 