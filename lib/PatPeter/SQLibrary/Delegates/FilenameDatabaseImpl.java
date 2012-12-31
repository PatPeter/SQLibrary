package lib.PatPeter.SQLibrary.Delegates;

import java.io.File;

import lib.PatPeter.SQLibrary.DatabaseException;

/**
 * Implementation of databases that handle a single file.<br>
 * Date Created: 2012-12-18 04:45.
 * 
 * @author Nicholas Solin, a.k.a. PatPeter
 */
public class FilenameDatabaseImpl implements FilenameDatabase {
	private File file;
	
	@Override
	public File getFile() {
		return this.file;
	}

	@Override
	public void setFile(File file) throws DatabaseException {
		if (file == null)
			throw new DatabaseException("File cannot be null.");
		this.file = file;
	}

}
