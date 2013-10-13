package lib.PatPeter.SQLibrary;

import java.util.logging.Level;
import java.util.logging.Logger;

//import org.spout.api.plugin.Plugin;

public class SQLibrarySpout /*extends Plugin*/ {
	public static final Logger logger = Logger.getLogger("Minecraft");
	
	//@Override 
	public void onEnable() {
		SQLibrary.logger.log(Level.INFO, "SQLibrary loaded.");
	}
	
	//@Override
	public void onDisable() {
		SQLibrary.logger.log(Level.INFO, "SQLibrary stopped.");
	}
} 
