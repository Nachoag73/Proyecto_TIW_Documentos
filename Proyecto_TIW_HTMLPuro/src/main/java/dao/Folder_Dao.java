package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import models.Folder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class Folder_Dao {

	private Connection con;

	public Folder_Dao(Connection con) {
		super();
		this.con = con;
	}
	
	

	public void create_folder(String folder_name, int fatherFolder, int user) throws SQLException {
		
		 if (folderExistsInParent(folder_name)) {
		        throw new SQLException("Ya existe una carpeta con el mismo nombre en la carpeta padre especificada");
		    }
	    // Obtener la fecha actual
	    LocalDate fechaActual = LocalDate.now();
	    // Definir el formato deseado
	    DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	    // Formatear la fecha según el formato definido
	    String fechaFormateada = fechaActual.format(formato);
	    // Imprimir la fecha formateada
	    System.out.println("Fecha actual: " + fechaFormateada);

	    String query = "INSERT INTO folder (folder_name, folder_creation, fatherfolder_id, user_Id) VALUES (?, ?, ?, ?)";
	    
	    // Preparar la sentencia SQL
	    try (PreparedStatement pstatement = con.prepareStatement(query)) {
	        // Establecer los valores de los parámetros
	        pstatement.setString(1, folder_name);
	        pstatement.setString(2, fechaFormateada);
	        pstatement.setInt(3,fatherFolder);
	        pstatement.setInt(4, user);
	        
	        System.out.println(folder_name);
	        System.out.println(fechaFormateada);
	        System.out.println(fatherFolder);
	        System.out.println(user);
	        
	        // Ejecutar la consulta de inserción
	        pstatement.executeUpdate();
	    }
	}
	
	public List<Folder> findFolder(int user) throws SQLException {
		List<Folder> carpetas = new ArrayList<Folder>();

		String query = "SELECT * FROM folder WHERE user_Id = ? ";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, user);
			try (ResultSet resultSet = pstatement.executeQuery();) {
				while (resultSet.next()) {
					Folder carpeta = new Folder();
					carpeta.setFolder_id(resultSet.getInt("folder_id"));
					carpeta.setFolder_name(resultSet.getString("folder_name"));
					carpeta.setFolder_creation(resultSet.getString("folder_creation"));
					carpeta.setFatherfolder_id(resultSet.getInt("fatherfolder_id"));
					carpeta.setUser_id(resultSet.getInt("user_id"));
					carpetas.add(carpeta);

				
				}
			}
		}
		
		return carpetas;
	}
	
	public int findFolderIdByName(String folderName) throws SQLException {
	    int folderId = -1;
	    String sql = "SELECT folder_id FROM folder WHERE folder_name = ?";
	    try (PreparedStatement statement = con.prepareStatement(sql)) {
	        statement.setString(1, folderName);
	        try (ResultSet resultSet = statement.executeQuery()) {
	            if (resultSet.next()) {
	                folderId = resultSet.getInt("folder_id");
	            }
	        }
	    }
	    return folderId;
	}
	
	private boolean folderExistsInParent(String folderName) throws SQLException {
	    String query = "SELECT COUNT(*) AS count FROM folder WHERE folder_name = ?";
	    try (PreparedStatement statement = con.prepareStatement(query)) {
	        statement.setString(1, folderName);
	        try (ResultSet resultSet = statement.executeQuery()) {
	            if (resultSet.next()) {
	                int count = resultSet.getInt("count");
	                return count > 0;
	            }
	        }
	    }
	    return false;
	}

	
	public Folder finfFolderById(int folderid) throws SQLException {
		Folder carpeta = null;
		String query = "SELECT * FROM folder WHERE folder_id = ?";
		try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
			preparedStatement.setInt(1, folderid);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					
					carpeta = new Folder();
					carpeta.setFolder_id(resultSet.getInt("folder_id"));
					carpeta.setFolder_name(resultSet.getString("folder_name"));
					carpeta.setFolder_creation(resultSet.getString("folder_creation"));
					carpeta.setFatherfolder_id(resultSet.getInt("fatherfolder_id"));
					carpeta.setUser_id(resultSet.getInt("user_id"));

				}
			}
		}
		return carpeta;
	}
}

