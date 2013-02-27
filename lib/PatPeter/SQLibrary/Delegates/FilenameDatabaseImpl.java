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
	private String directory;
	private String filename;
	private File file;
	private String extension = ".db";
	
	public FilenameDatabaseImpl() {}
	
	@Override
	public String getDirectory() {
		return directory;
	}
	
	@Override
	public void setDirectory(String directory) {
		if (directory == null || directory.length() == 0)
			throw new DatabaseException("Directory cannot be null or empty.");
		else
			this.directory = directory;
	}
	
	@Override
	public String getFilename() {
		return filename;
	}
	
	@Override
	public void setFilename(String filename) {
		if (filename == null || filename.length() == 0)
			throw new DatabaseException("Filename cannot be null or empty.");
		else if (filename.contains("/") || filename.contains("\\") || filename.endsWith(".db"))
			throw new DatabaseException("The database filename cannot contain: /, \\, or .db.");
		else
			this.filename = filename;
	}
	
	@Override
	public String getExtension() {
		return extension;
	}
	
	@Override
	public void setExtension(String extension) {
		if (extension == null || extension.length() == 0)
			throw new DatabaseException("Extension cannot be null or empty.");
		if (extension.charAt(0) != '.')
			throw new DatabaseException("Extension must begin with a period");
	}
	
	@Override
	public File getFile() {
		return this.file;
	}
	
	@Override
	public void setFile(String directory, String filename) throws DatabaseException {
		setDirectory(directory);
		setFilename(filename);
		
		File folder = new File(getDirectory());
		if (!folder.exists())
			folder.mkdir();
		
		file = new File(folder.getAbsolutePath() + File.separator + getFilename() + getExtension());
	}
	
	@Override
	public void setFile(String directory, String filename, String extension) throws DatabaseException {
		setExtension(extension);
		this.setFile(directory, filename);
	}
}
