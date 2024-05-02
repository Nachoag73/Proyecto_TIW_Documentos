package models;

public class Folder {

	private int folder_id;
	private String folder_name;
	private String folder_creation;
	private int fatherfolder;
	private int user;

	public Folder() {
		
	}
	
	public Folder(int folder_id, String folder_name, String folder_creation) {
		super();
		this.folder_id = folder_id;
		this.folder_name = folder_name;
		this.folder_creation = folder_creation;
		
	}
	
	public Folder(int folder_id, String folder_name, String folder_creation, int fatherfolder, int user) {
		super();
		this.folder_id = folder_id;
		this.folder_name = folder_name;
		this.folder_creation = folder_creation;
		this.fatherfolder = fatherfolder;
		this.user = user;
	}

	public int getFolder_id() {
		return folder_id;
	}

	public void setFolder_id(int folder_id) {
		this.folder_id = folder_id;
	}

	public String getFolder_name() {
		return folder_name;
	}

	public void setFolder_name(String folder_name) {
		this.folder_name = folder_name;
	}

	public String getFolder_creation() {
		return folder_creation;
	}

	public void setFolder_creation(String folder_creation) {
		this.folder_creation = folder_creation;
	}

	public int getFatherfolder_id() {
		return fatherfolder;
	}

	public void setFatherfolder_id(int i) {
		this.fatherfolder = i;
	}

	public int getUser_id() {
		return user;
	}

	public void setUser_id(int user) {
		this.user = user;
	}
	

}
