package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import dao.Folder_Dao;
import models.User;
import models.Folder;

@WebServlet("/PaginaInicioController")
public class PaginaInicioController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection con = null;

	public void init() throws ServletException {
		System.out.println("ESTAS EN PaginaInicioController");
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
		
		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
			System.out.println("SE HA PODIDO CONECTAR CON LA BASE DE DATOS");

		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
		
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// If the user is not logged in (not present in session) redirect to the login
		String loginpath = getServletContext().getContextPath() + "/PaginaParaElegir.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		
		User user = (User) session.getAttribute("user");
		Folder_Dao folderDao = new Folder_Dao(con);
		List<Folder> carpetas = new ArrayList<Folder>();

		try {
			carpetas = folderDao.findFolder(user.getId());
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover folders");
			return;
		}

		// Imprimir los detalles de cada carpeta en la consola
		for (Folder carpeta : carpetas) {
			System.out.println("Carpeta ID: " + carpeta.getFolder_id());
			System.out.println("Nombre: " + carpeta.getFolder_name());
			System.out.println("Fecha de creación: " + carpeta.getFolder_creation());
			System.out.println("ID de la carpeta padre: " + carpeta.getFatherfolder_id());
			System.out.println("ID de usuario: " + carpeta.getUser_id());
			System.out.println("--------------------------------------------");
		}

		// Agregar carpetas al contexto
		request.setAttribute("carpetas", carpetas);

		// Redirigir a la página de inicio
		
		String path = "/PaginaInicio.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("carpetas", carpetas);
		templateEngine.process(path, ctx, response.getWriter());
		

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
