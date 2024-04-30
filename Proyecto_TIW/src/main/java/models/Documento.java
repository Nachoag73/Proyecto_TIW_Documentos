package models;

public class Documento {

	private int document_id;
	private String document_name;
	private String document_creation;
	private String resumen;
	private String type;
	private Carpeta dfolder;
	private User duser;
	
	

	public Documento() {

	}
	
	public Documento(int documento_id, String documento_name, String documento_creation, String resumen, String type) {
		super();
		this.document_id = documento_id;
		this.document_name = documento_name;
		this.document_creation = documento_creation;
		this.setResumen(resumen);
		this.setType(type);

	}
	
	public Documento(int documento_id, String documento_name, String documento_creation,String resumen, String type, Carpeta dfolder, User duser) {
		super();
		this.document_id = documento_id;
		this.document_name = documento_name;
		this.document_creation = documento_creation;
		this.setResumen(resumen);
		this.setType(type);
		this.dfolder = dfolder;
		this.duser = duser;
	}

	public int getDocumento_id() {
		return document_id;
	}

	public void setDocumento_id(int documento_id) {
		this.document_id = documento_id;
	}

	public String getDocumento_creation() {
		return document_creation;
	}

	public void setDocumento_creation(String documento_creation) {
		this.document_creation = documento_creation;
	}

	public String getDocumento_name() {
		return document_name;
	}

	public void setDocumento_name(String documento_name) {
		this.document_name = documento_name;
	}

	public User getDuser() {
		return duser;
	}

	public void setDuser(User duser) {
		this.duser = duser;
	}

	public Carpeta getDfolder() {
		return dfolder;
	}

	public void setDfolder(Carpeta dfolder) {
		this.dfolder = dfolder;
	}

	public String getResumen() {
		return resumen;
	}

	public void setResumen(String resumen) {
		this.resumen = resumen;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}



}
