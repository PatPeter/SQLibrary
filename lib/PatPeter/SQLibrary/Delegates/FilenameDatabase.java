package lib.PatPeter.SQLibrary.Delegates;

import java.io.File;

/**
 * Interface for database delegates that handle a single file.<br>
 * Date Created: 2012-12-18 04:45
 * 
 * @author Nicholas Solin, a.k.a. PatPeter
 */
public interface FilenameDatabase {
	String getDirectory();
	
	void setDirectory(String directory);
	
	String getFilename();
	
	void setFilename(String filename);
	
	String getExtension();
	
	void setExtension(String extension);
	
	File getFile();
	
	void setFile(String directory, String filename);
	
	void setFile(String directory, String filename, String extension);
}