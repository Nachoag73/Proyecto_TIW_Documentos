package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import models.Carpeta;
import models.Documento;
import models.User;

public class Document_Dao {

    private Connection con;

    public Document_Dao(Connection con) {
        super();
        this.con = con;
    }

    public void create_doc(String document_name, String resumen, String type, Carpeta folder, User user)
            throws SQLException {

        // Obtener la fecha actual
        LocalDate fechaActual = LocalDate.now();

        // Definir el formato deseado
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Formatear la fecha según el formato definido
        String fechaFormateada = fechaActual.format(formato);

        // Imprimir la fecha formateada
        System.out.println("Fecha actual: " + fechaFormateada);

        String query = "INSERT INTO document (document_name, document_creation, resumen, type, folder_id, user_Id) VALUES (?, ?, ?, ?, ?, ?)";

        // Prepara la sentencia SQL
        try (PreparedStatement pstatement = con.prepareStatement(query);) {
            // Establecer los valores de los parámetros
            pstatement.setString(1, document_name);
            pstatement.setString(2, fechaFormateada);
            pstatement.setString(3, resumen);
            pstatement.setString(4, type);
            pstatement.setInt(5, folder.getFolder_id());
            pstatement.setInt(6, user.getId());
            
            // Ejecutar la consulta de inserción
            pstatement.executeUpdate();
        }
    }

    public void update_doc(Carpeta folder_id, Documento document) throws SQLException {

        String query = "UPDATE document SET folder_id = ? WHERE document_id = ?";

        // Prepara la sentencia SQL
        try (PreparedStatement pstatement = con.prepareStatement(query);) {
            // Establecer los valores de los parámetros
            pstatement.setInt(1, folder_id.getFolder_id());
            pstatement.setInt(2, document.getDocumento_id());
            
            // Ejecutar la consulta de actualización
            pstatement.executeUpdate();
        }
    }

}

