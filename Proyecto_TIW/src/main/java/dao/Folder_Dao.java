package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import models.Carpeta;
import models.User;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Folder_Dao {

	private Connection con;

	public Folder_Dao(Connection con) {
		super();
		this.con = con;
	}
	
	

	public void create_folder(String folder_name, Carpeta fatherFolder, User user) throws SQLException {
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
	        pstatement.setInt(3, fatherFolder.getFolder_id());
	        pstatement.setInt(4, user.getId());
	        
	        // Ejecutar la consulta de inserción
	        pstatement.executeUpdate();
	    }
	}


}
