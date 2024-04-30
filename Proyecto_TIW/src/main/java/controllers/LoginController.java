package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import dao.User_Dao;
import models.User;

@WebServlet("/LoginController")
public class LoginController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection = null;


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

		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// RECIBE EL USUARIO Y LA CONTRASEÑA
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		System.out.println("Username recibido: " + username);
		System.out.println("Password recibido: " + password);

		if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "DATOS INVALIDOS");
			return;
		}

		User_Dao user = new User_Dao(connection);
		User u = null;
		try {

			// CHekea las credenciales metidas con el DoPost
			u = user.checkCredentials(username, password);

			if (u != null) {
				System.out.println("Credenciales correctas. Usuario encontrado: " + u.getUsername());
			} else {
				System.out.println("Credenciales incorrectas. Usuario no encontrado.");
			}

		} catch (SQLException e) {
			System.out.println("Error al verificar las credenciales: " + e.getMessage());
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Credenciales erroneas");
			return;
		}

		String path = getServletContext().getContextPath();
		if (u == null) {
			System.out.println("Redirigiendo a la página de inicio debido a credenciales incorrectas.");
			path = getServletContext().getContextPath() + "/PaginaParaElegir.html";
		} else {
			System.out.println("Redirigiendo a la página de inicio después de iniciar sesión correctamente.");
			request.getSession().setAttribute("user", u);
			path = getServletContext().getContextPath() + "/PaginaInicio.html";
		}

		System.out.println("Redirigiendo a la URL: " + path);
		response.sendRedirect(path);
	}

	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
		}
	}
}
