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
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import dao.Document_Dao;
import models.Document;

@WebServlet("/MostrarDocController")
public class MostrarDocController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection con = null;
	private TemplateEngine templateEngine;

	public MostrarDocController() {
		super();
	}

	public void init() throws ServletException {
		System.out.println("ESTAS EN MostrarDocController");
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

		int documentId;
		Document_Dao documentDao = new Document_Dao(con);
		Document documento = null;

		// If the user is not logged in (not present in session) redirect to the login
		String loginpath = getServletContext().getContextPath() + "/PaginaParaElegir.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}

		// Obtener la ID del documento desde la solicitud
		String documentIdParam = request.getParameter("document_id");
		if (documentIdParam == null || documentIdParam.isEmpty()) {
			// Manejar el caso en que el parámetro es nulo o vacío
			// Puedes enviar un error al cliente o redirigirlo a una página de error
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El parámetro document_id es nulo o vacío");
			return;
		}

		try {
			documentId = Integer.parseInt(documentIdParam);
		} catch (NumberFormatException e) {
			// Manejar el caso en que el parámetro no se pueda convertir a un entero
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El parámetro document_id no es un número válido");
			return;
		}

		// Obtener el documento específico de la base de datos

		try {
			documento = documentDao.findDocumentById(documentId);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving document");
			return;
		}

		if (documento == null) {
			// Manejar el caso en que el documento no se encuentre en la base de datos
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Documento no encontrado");
			return;
		}

		// Agregar el documento al contexto
		request.setAttribute("documento", documento);

		// Redirigir a la página de contenido de un solo documento
		String path = "/ContenidoDoc.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("documento", documento);
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	public void destroy() {
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException sqle) {
		}
	}

}
