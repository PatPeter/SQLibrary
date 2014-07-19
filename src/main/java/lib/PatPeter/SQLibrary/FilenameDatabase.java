package lib.PatPeter.SQLibrary;

import java.io.File;
import java.util.logging.Logger;


/**
 * Implementation of databases that handle a single file.<br>
 * Date Created: 2012-12-18 04:45.
 * 
 * @author Nicholas Solin, a.k.a. PatPeter
 */
public abstract class FilenameDatabase extends Database {
	private String directory;
	private String filename;
	private File file;
	private String extension = ".db";

	public FilenameDatabase(Logger log, 
			String prefix, 
			DBMS dbms) {
		super(log, prefix, dbms);
		setFile();
	}
	
	public FilenameDatabase(Logger log, 
			String prefix, 
			DBMS dbms,
			String directory,
			String filename) {
		super(log, prefix, dbms);
		setFile(directory, filename);
	}
	
	public FilenameDatabase(Logger log, 
			String prefix, 
			DBMS dbms,
			String directory,
			String filename,
			String extension) {
		super(log, prefix, dbms);
		setFile(directory, filename, extension);
	}
	
	public String getDirectory() {
		return directory;
	}
	
	public void setDirectory(String directory) {
		if (directory == null || directory.length() == 0)
			throw new DatabaseException("Directory cannot be null or empty.");
		else
			this.directory = directory;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public void setFilename(String filename) {
		if (filename == null || filename.length() == 0)
			throw new DatabaseException("Filename cannot be null or empty.");
		else if (filename.contains("/") || filename.contains("\\") || filename.endsWith(".db"))
			throw new DatabaseException("The database filename cannot contain: /, \\, or .db.");
		else
			this.filename = filename;
	}
	
	public String getExtension() {
		return extension;
	}
	
	public void setExtension(String extension) {
		if (extension == null || extension.length() == 0)
			throw new DatabaseException("Extension cannot be null or empty.");
		if (extension.charAt(0) != '.')
			throw new DatabaseException("Extension must begin with a period");
		this.extension = extension;
	}
	
	public File getFile() {
		return this.file;
	}
	
	public void setFile() {
		file = null;
	}
	
	public void setFile(String directory, String filename) throws DatabaseException {
		setDirectory(directory);
		setFilename(filename);
		
		File folder = new File(getDirectory());
		if (!folder.exists())
			folder.mkdir();
		
		file = new File(folder.getAbsolutePath() + File.separator + getFilename() + getExtension());
	}
	
	public void setFile(String directory, String filename, String extension) throws DatabaseException {
		setExtension(extension);
		this.setFile(directory, filename);
	}
}
