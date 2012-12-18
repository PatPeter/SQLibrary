package lib.PatPeter.SQLibrary;

import java.io.File;

/**
 * 
 * Date Created: 2012-12-18 04:45
 * 
 * @author PatPeter
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
