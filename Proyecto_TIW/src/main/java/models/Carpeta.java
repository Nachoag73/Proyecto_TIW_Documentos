package models;

public class Carpeta {

	private int folder_id;
	private String folder_name;
	private String folder_creation;
	private Carpeta fatherfolder;
	private User user;

	
	

	public Carpeta() {
		
	}
	
	public Carpeta(int folder_id, String folder_name, String folder_creation) {
		super();
		this.folder_id = folder_id;
		this.folder_name = folder_name;
		this.folder_creation = folder_creation;
		
	}
	
	public Carpeta(int folder_id, String folder_name, String folder_creation, Carpeta fatherfolder, User user) {
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

	public Carpeta getFatherfolder_id() {
		return fatherfolder;
	}

	public void setFatherfolder_id(Carpeta fatherfolder) {
		this.fatherfolder = fatherfolder;
	}

	public User getUser_id() {
		return user;
	}

	public void setUser_id(User user) {
		this.user = user;
	}
	
	

}
