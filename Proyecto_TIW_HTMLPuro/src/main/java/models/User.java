package models;

public class User {

	private int user_Id;
	private String username;
	private String password;
	private String email;


	public User() {
		
	}
	
	public User(int user_id, String username, String password, String mail) {
		super();
		this.user_Id = user_id;
		this.username = username;
		this.password = password;
		this.email = mail;
	}
	
	

	public int getId() {
		return user_Id; 
	}

	public void setId(int user_id) {
		this.user_Id = user_id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMail() {
		return email;
	}

	public void setMail(String mail) {
		this.email = mail;
	}

}
