package lib.PatPeter.SQLibrary.Delegates;

import java.io.File;

/**
 * Interface for database delegates that handle a single file.<br>
 * Date Created: 2012-12-18 04:45
 * 
 * @author Nicholas Solin, a.k.a. PatPeter
 */
public interface FilenameDatabase {
	File getFile();
	
	void setFile(File file);
}