package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.User_Dao;
import models.User;
import models.Carpeta;

@WebServlet("/PaginaInicioController")
public class PaginaInicioController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private User_Dao usuarioDao;

	public void init() throws ServletException {
		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
			
			System.out.println("SE HA PODIDO CONECTAR CON LA BASE DE DATOS");
			usuarioDao = new User_Dao(connection);
		} catch (ClassNotFoundException | SQLException e) {
			throw new UnavailableException("Couldn't initialize database connection");
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		if (session.getAttribute("usuario") != null) {
			User usuarioActual = (User) session.getAttribute("usuario");
			try {
				List<Carpeta> carpetasUsuario = usuarioDao.getAllCarpetas(usuarioActual);
				request.setAttribute("carpetasUsuario", carpetasUsuario);
				request.getRequestDispatcher("PaginaInicio.html").forward(request, response);
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"Error al obtener carpetas del usuario");
			}
		} else {
			response.sendRedirect(request.getContextPath() + "/Login.html");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String nombreDocumento = request.getParameter("nombreDocumento");
		String nombreCarpetaDestino = request.getParameter("nombreCarpetaDestino");

		boolean exitoMovimiento = moverDocumento(nombreDocumento, nombreCarpetaDestino);

		if (exitoMovimiento) {
			response.sendRedirect(request.getContextPath() + "/PaginaInicioController");
		} else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al mover el documento");
		}
	}

	private boolean moverDocumento(String nombreDocumento, String nombreCarpetaDestino) {
		try {
			String idDocumento = obtenerIdDocumento(nombreDocumento);
			String idCarpetaDestino = obtenerIdCarpeta(nombreCarpetaDestino);
			return moverDocumentoEnBaseDatos(idDocumento, idCarpetaDestino);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private String obtenerIdDocumento(String nombreDocumento) throws SQLException {
		String idDocumento = null;
		String sql = "SELECT id_documento FROM documentos WHERE nombre = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, nombreDocumento);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					idDocumento = resultSet.getString("id_documento");
				}
			}
		}
		return idDocumento;
	}

	private String obtenerIdCarpeta(String nombreCarpeta) throws SQLException {
		String idCarpeta = null;
		String sql = "SELECT id_carpeta FROM carpetas WHERE nombre = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, nombreCarpeta);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					idCarpeta = resultSet.getString("id_carpeta");
				}
			}
		}
		return idCarpeta;
	}

	private boolean moverDocumentoEnBaseDatos(String idDocumento, String idCarpetaDestino) throws SQLException {
		try (PreparedStatement statement = connection
				.prepareStatement("UPDATE documentos SET id_carpeta = ? WHERE id_documento = ?")) {
			statement.setString(1, idCarpetaDestino);
			statement.setString(2, idDocumento);
			int filasActualizadas = statement.executeUpdate();
			return filasActualizadas > 0;
		}
	}

	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
