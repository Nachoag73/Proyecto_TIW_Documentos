package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.Carpeta;
import models.User;

public class User_Dao {

    private Connection con;

    public User_Dao(Connection con) {
        super();
        this.con = con;
    }

    public void create_user(String username, String password, String email) throws SQLException {
        String query = "INSERT INTO user (username,  password, email) VALUES (?, ?, ?)";

        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setString(1, username);
            pstatement.setString(2, password);
            pstatement.setString(3, email);
            
            pstatement.executeUpdate();
        }
    }

    public User checkCredentials(String username, String password) throws SQLException {
        String query = "SELECT user_Id, username FROM user WHERE username = ? AND password = ?";

        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setString(1, username);
            pstatement.setString(2, password);
            
            try (ResultSet result = pstatement.executeQuery()) {
                if (result.next()) {
                    User user = new User();
                    user.setId(result.getInt("user_Id"));
                    user.setUsername(result.getString("username"));
                    return user;
                } else {
                    return null;
                }
            }
        }
    }

    public List<Carpeta> getAllCarpetas(User user) throws SQLException {
        String query = "SELECT * FROM folder WHERE user_Id = ?";
        List<Carpeta> todas = new ArrayList<>();

        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setInt(1, user.getId());
            
            try (ResultSet result = pstatement.executeQuery()) {
                while (result.next()) {
                    Carpeta c = new Carpeta();
                    c.setFolder_id(result.getInt("folder_id"));
                    c.setFolder_name(result.getString("folder_name"));
                    c.setUser_id(user);
                    c.setFolder_creation(result.getString("folder_creation"));

                    Carpeta father = new Carpeta();
                    father.setFolder_id(result.getInt("fatherfolder_id"));
                    c.setFatherfolder_id(father);

                    todas.add(c);
                }
            }
        }
        return todas;
    }

    public boolean userExists(String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM user WHERE username = ?";

        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, username);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0; // Devuelve true si el contador es mayor que 0 (el usuario existe)
                }
            }
        }
        return false; // Si no se encontró ningún usuario con ese nombre de usuario
    }

    public boolean emailExists(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM user WHERE email = ?";

        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, email);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0; // Devuelve true si el contador es mayor que 0 (el email existe)
                }
            }
        }
        return false; // Si no se encontró ningún usuario con ese correo electrónico
    }
}
