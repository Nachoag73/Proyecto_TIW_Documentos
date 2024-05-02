package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import models.Folder;
import models.Document;

public class Document_Dao {

	private Connection con;

	public Document_Dao(Connection con) {
		super();
		this.con = con;
	}

	public void create_doc(String document_name, String resumen, String type, int folder, int user)
			throws SQLException {

		if (documentExistsInFolder(document_name, folder)) {
			throw new SQLException("Ya existe un documento con el mismo nombre en la carpeta especificada");
		}

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
			pstatement.setInt(5, folder);
			pstatement.setInt(6, user);

			// Ejecutar la consulta de inserción
			pstatement.executeUpdate();
		}
	}

	public void update_doc(int folder, int document) throws SQLException {

		String query = "UPDATE document SET folder_id = ? WHERE document_id = ?";

		// Prepara la sentencia SQL
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			// Establecer los valores de los parámetros
			pstatement.setInt(1, folder);
			pstatement.setInt(2, document);

			// Ejecutar la consulta de actualización
			pstatement.executeUpdate();
		}
	}

	public List<Document> findDocumentsByFolder(int folderId) throws SQLException {
		
		List<Document> documentos = new ArrayList<>();
		
		System.out.println("BUSCANDO DOCUMENTO");
		
		String query = "SELECT * FROM document WHERE folder_id = ?";
		
		try (PreparedStatement statement = con.prepareStatement(query)) {
			statement.setInt(1, folderId);

			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					Document documento = new Document();
					documento.setDocumento_id(resultSet.getInt("document_id"));
					documento.setDocumento_name(resultSet.getString("document_name"));
					documento.setDocumento_creation(resultSet.getString("document_creation"));
					documento.setResumen(resultSet.getString("resumen"));
					documento.setType(resultSet.getString("type"));
					documento.setDfolder(resultSet.getInt("folder_id"));
					documento.setDuser(resultSet.getInt("user_Id"));

					documentos.add(documento);
				}
			}
		}

		return documentos;
	}

	private boolean documentExistsInFolder(String documentName, int folderId) throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM document WHERE document_name = ? AND folder_id = ?";
		try (PreparedStatement statement = con.prepareStatement(query)) {
			statement.setString(1, documentName);
			statement.setInt(2, folderId);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					int count = resultSet.getInt("count");
					return count > 0;
				}
			}
		}
		return false;
	}

	public Document findDocumentById(int documentId) throws SQLException {
		Document documento = null;
		String query = "SELECT * FROM document WHERE document_id = ?";
		try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
			preparedStatement.setInt(1, documentId);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					documento = new Document();
					documento.setDocumento_id(resultSet.getInt("document_id"));
					documento.setDocumento_name(resultSet.getString("document_name"));
					documento.setDocumento_creation(resultSet.getString("document_creation"));
					documento.setResumen(resultSet.getString("resumen"));
					documento.setType(resultSet.getString("type"));
					documento.setDfolder(resultSet.getInt("folder_id"));
					documento.setDuser(resultSet.getInt("user_Id"));
				}
			}
		}
		return documento;
	}

	public List<Folder> findCarpetasExcept(int folderId, int userId ) throws SQLException {
		List<Folder> carpetasExcept = new ArrayList<>();


		String query = "SELECT * FROM folder WHERE folder_id != ? AND user_id =?";
		try (PreparedStatement statement = con.prepareStatement(query)) {
			statement.setInt(1, folderId);
			statement.setInt(2, userId);
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					Folder carpeta = new Folder();
					carpeta.setFolder_id(resultSet.getInt("folder_id"));
					carpeta.setFolder_name(resultSet.getString("folder_name"));
					// Aquí se setearían otros atributos de la carpeta si es necesario
					carpetasExcept.add(carpeta);
				}
			}
		}

		return carpetasExcept;
	}

}
