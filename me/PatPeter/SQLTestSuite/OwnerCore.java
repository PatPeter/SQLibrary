package me.PatPeter.SQLTestSuite;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import lib.PatPeter.SQLibrary.*;

public class OwnerCore extends JavaPlugin {
	public String logPrefix = "[OWNER] "; // Prefix to go in front of all log entries
	public Logger log = 
		Logger.getLogger("Minecraft"); // Minecraft log and console
	public File pFolder = 
		new File("plugins" + File.separator + "Owner"); // Folder to store plugin settings file and database
	
	// Handlers
	public MySQL mysql;
	public SQLite sqlite;
	protected OwnerPlayerListener opl;
	public SettingsHandler settings;
	public HashMap<String, Integer> commandUsers = new HashMap<String, Integer>(); //Stores info about people using commands
	
	// Settings Variables \\
	public boolean mySQL = false;
	public String dbHost = null;
	public int dbPort = 0;
	public String dbUser = null;
	public String dbPass = null;
	public String dbDatabase = null;
	
	@Override
	public void onDisable() { }

	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		this.log.info(this.logPrefix + "Owner is initializing"); // Sends to the console
		
		// Declare the settings handler
		this.opl = new OwnerPlayerListener(this);
		settings = new SettingsHandler("Settings.properties", pFolder.getPath() + File.separator + "mySQL.properties");
		
		// load the settings handler
		try {
			settings.load();
		} catch (IOException e) {
			log.severe("Could not load settings! Exiting plugin.");
			return;
		}
		
		// get variables from settings handler\\
		if (settings.file.exists() && settings.isValidProperty("mySQL")) {
			this.mySQL = this.settings.getPropertyBoolean("mySQL");
			if (this.settings.isValidProperty("host"))
				this.dbHost = this.settings.getPropertyString("host");
			
			if (this.settings.isValidProperty("username"))
				this.dbUser = this.settings.getPropertyString("username");
			
			if (this.settings.isValidProperty("password"))
				this.dbPass = this.settings.getPropertyString("password");
			
			if (this.settings.isValidProperty("database"))
				this.dbDatabase = this.settings.getPropertyString("database");
		} else {
			this.log.warning(this.logPrefix + "Check mySQL.properties");
		}
		
		// Check Settings \\
		if (this.mySQL) {
			if (this.dbHost.equals(null)) {
				this.mySQL = false;
				this.log.severe(this.logPrefix + "mySQL is on, but host is not defined, defaulting to SQLite");
			}
			if (this.dbUser.equals(null)) {
				this.mySQL = false;
				this.log.severe(this.logPrefix + "mySQL is on, but username is not defined, defaulting to SQLite");
			}
			if (this.dbPass.equals(null)) {
				this.mySQL = false;
				this.log.severe(this.logPrefix + "mySQL is on, but password is not defined, defaulting to SQLite");
			}
			if (this.dbDatabase.equals(null)) {
				this.mySQL = false;
				this.log.severe(this.logPrefix + "mySQL is on, but database is not defined, defaulting to SQLite");
			}
		}
		
		// Enabled SQL/mySQL
		if (this.mySQL) {
			// Declare mySQL Handler
			this.mysql = new MySQL(this.log, this.logPrefix, this.dbHost, this.dbPort, this.dbDatabase, this.dbUser, this.dbPass);
			
			this.log.info(this.logPrefix + "mySQL Initializing");
			// Initialize mySQL Handler
			this.mysql.open();
			
			if (this.mysql.checkConnection()) { // Check if the Connection was successful
				this.log.info(this.logPrefix + "mySQL connection successful");
				if (!this.mysql.tableExists("blocks")) { // Check if the table exists in the database if not create it
					this.log.info(this.logPrefix + "Creating table blocks");
					String query = "CREATE TABLE blocks (id INT, owner VARCHAR(255), x INT, y INT, z INT);";
					this.mysql.createTable(query);
				}
			} else {
				this.log.severe(this.logPrefix + "mySQL connection failed");
				this.mySQL = false;
			}
		} else {
			this.log.info(this.logPrefix + "SQLite Initializing");
			
			// Declare SQLite handler
			this.sqlite = new SQLite(this.log, this.logPrefix, "Owners", pFolder.getPath());
			
			// Initialize SQLite handler
			this.sqlite.open();
			
			// Check if the table exists, if it doesn't create it
			if (!this.sqlite.tableExists("blocks")) {
				this.log.info(this.logPrefix + "Creating table blocks");
				String query = "CREATE TABLE blocks (id INT AUTO_INCREMENT PRIMARY_KEY, owner VARCHAR(255), x INT, y INT, z INT);";
				this.sqlite.createTable(query); // Use SQLite.createTable(query) to create tables 
			}
			
		}
		
		// Register Listeners \\
		//PluginManager pm = this.getServer().getPluginManager();
		//pm.registerEvent(, new OwnerPlayerListener(this), EventPriority.NORMAL, new SQLEventExecutor(), this);
		
		this.log.info(this.logPrefix + "Owner is finished initializing");
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) { 
		Player player = (Player) sender;
		
		if (commandLabel.equalsIgnoreCase("create") && player != null) {
			
			if (this.commandUsers.containsKey(player.getName().toLowerCase()) && this.commandUsers.get(player.getName().toLowerCase()) == 1) {
				player.sendMessage(ChatColor.GREEN + "Create mode disabled");
				this.commandUsers.remove(player.getName().toLowerCase());
				return true;
			} else {
				if (this.commandUsers.containsKey(player.getName().toLowerCase()) && this.commandUsers.get(player.getName().toLowerCase()) == 2) {
					player.sendMessage(ChatColor.RED + "Can not toggle create mode, you are in info mode");
				} else {
					player.sendMessage(ChatColor.GREEN + "Create mode enabled");
					this.commandUsers.put(player.getName().toLowerCase(), 1);
				}
				return true;
			}
		} else if (commandLabel.equalsIgnoreCase("info") && player != null) {
			if (this.commandUsers.containsKey(player.getName().toLowerCase()) && this.commandUsers.get(player.getName().toLowerCase()) == 2) {
				player.sendMessage(ChatColor.GREEN + "Info mode disabled");
				this.commandUsers.remove(player.getName().toLowerCase());
			} else {
				if (this.commandUsers.containsKey(player.getName().toLowerCase()) && this.commandUsers.get(player.getName().toLowerCase()) == 1) {
					player.sendMessage(ChatColor.RED + "Can not toggle info mode, you are in create mode");
				} else {
					player.sendMessage(ChatColor.GREEN + "Info mode enabled");
					this.commandUsers.put(player.getName().toLowerCase(), 2);
				}
			}
			return true;
		}
		
		return false;
	}
	
	public void createPluginDirectory() {
		if (!this.pFolder.exists()) {
			this.pFolder.mkdir();
		}
	}
	
}
