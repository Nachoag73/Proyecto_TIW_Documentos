package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.User_Dao;

@WebServlet("/RegisterController")
public class RegisterController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public RegisterController() {
		super();
	}

	public void init() throws ServletException {

		try {
			System.out.println("ESTAS EN REGISTER CONTROLLER");
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
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Recuperar parámetros del formulario de registro
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String password = request.getParameter("password");

		// Validar que los campos no estén vacíos
		if (username == null || username.isEmpty() || password == null || password.isEmpty() || email == null
				|| email.isEmpty()) {
			// Si algún campo está vacío, devolver un error de solicitud incorrecta
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}

		// VALIDA QUE EL MAIL TIENE EL FORMATO ADECUADO
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
		Pattern pattern = Pattern.compile(emailRegex);
		Matcher matcher = pattern.matcher(email);
		if (!matcher.matches()) {
			// Si el correo electrónico no cumple con el formato esperado, devolver un error
			// de solicitud incorrecta
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid email format");
			return;
		}

		// INSERTAR NUEVO USUARIO

		User_Dao user = new User_Dao(connection);
		try {

			// COMPROBAMOS SI EL USUARIO EXISTE VIENDO EL USERNAME Y EL EMAIL
			System.out.println("Validando si el usuario ya existe...");
			if (user.userExists(username) || user.emailExists(email)) {
				// Si el usuario ya existe, devolver un error de solicitud incorrecta
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "User already exists");
				return;
			}

			// Insertar el nuevo usuario en la base de datos
			user.create_user(username, password, email);
			System.out.println("Usuario insertado correctamente en la base de datos.");

			// Redirigir al usuario a la página de inicio de sesión
			response.sendRedirect(request.getContextPath() + "/Login.html");
			System.out.println("Redirigiendo a: " + request.getContextPath() + "/Login.html");

		} catch (SQLException e) {
			// Si ocurre un error durante la creación del usuario en la base de datos,
			// devolver un error de servidor interno
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creating user");
			return;
		}
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
