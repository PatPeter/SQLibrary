package me.PatPeter.SQLTestSuite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Logger;

public class SettingsHandler {
	public File file;
	public String name;
	public String saveFile;
	private HashMap<String,String> FileContents = new HashMap<String,String>();
	
	public SettingsHandler(String resourcePath, String saveFile) {
		this.saveFile = saveFile;
		this.name = resourcePath;
	}
	
	private void create(String name) {
		if (getClass().getResource(name) == null) {
			Logger log = Logger.getLogger("Minecraft");
			log.severe("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			log.severe("asdddddddddddddddddddddddddddddddddddddddddddd");
			return;
		}
	      InputStream input = getClass().getResourceAsStream(name);
	      if (input != null) {
	        FileOutputStream output = null;
	        try
	        {
	          output = new FileOutputStream(file);
	          byte[] buf = new byte[8192];
	          int length = 0;

	          while ((length = input.read(buf)) > 0) {
	            output.write(buf, 0, length);
	          }

	        } catch (Exception e) {
	          e.printStackTrace();
	        } finally {
	          try {
	            if (input != null)
	              input.close();
	          } catch (Exception e) {
	          }
	          try {
	            if (output != null)
	              output.close();
	          }
	          catch (Exception e)
	          {
	          }
	        }
	      }
	     
	    }
	
	public Boolean load() { //If force is true than it will delete the file and recreate it
		
		if (file == null) {
			this.file = new File(saveFile);
			
		}
		
		if (!file.exists()) {
			
			// create(); \\
			this.create(name);
			
		}
		
		this.FileContents = loadFileContents();
		return true;
	}
	
	public String getPropertyString(String property){
		
		try {
			if (FileContents.containsKey(property)) {
				return FileContents.get(property);
			}
			
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	public Boolean getPropertyBoolean(String property) {
		
		try {
			String result = FileContents.get(property);
			if (result.equalsIgnoreCase("true") || result.equalsIgnoreCase("false")) {
				return Boolean.valueOf(result);
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
		
	}

	public Integer getPropertyInteger(String property) {
		
		try {
			String result = FileContents.get(property);
			return Integer.valueOf(result);
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 10;
	}
	
	public Double getPropertyDouble(String property) {
		try {
			String result = FileContents.get(property);
			if (!result.contains(".")) result = result + ".0";
			
			return Double.valueOf(result);
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -10.0;
	}
	
	public boolean isValidProperty(String property) {
		if (this.FileContents.containsKey(property))
			return true;
		else
			return false;
	}
	
	private HashMap<String,String> loadFileContents(){
		HashMap<String,String> result = new HashMap<String,String>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String word = null;
			
			while((word = br.readLine()) != null) {
				if ((word.isEmpty()) || (word.startsWith("#")) || (!word.contains(":"))) continue;
				String[] args = word.split(":");
				result.put(args[0], args[1]);
			}
			
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		
		return result;
	}
}
