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

import dao.Document_Dao;
import models.Document;

@WebServlet("/ContenidoController")
public class ContenidoController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection con = null;
	private TemplateEngine templateEngine;

	public ContenidoController() {
		super();
	}

	public void init() throws ServletException {
		System.out.println("ESTAS EN CONETNIDO CONTROLLER");

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

		int folderId;
		Document_Dao documentDao = null;
		// If the user is not logged in (not present in session) redirect to the login
		String loginpath = getServletContext().getContextPath() + "/PaginaParaElegir.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}

		// Obtener la ID de la carpeta desde la solicitud
		String folderIdParam = request.getParameter("folder_id");
		if (folderIdParam == null || folderIdParam.isEmpty()) {
			// Manejar el caso en que el parámetro es nulo o vacío
			// Puedes enviar un error al cliente o redirigirlo a una página de error
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El parámetro folder_id es nulo o vacío");
			return;
		}

		try {
			folderId = Integer.parseInt(folderIdParam);
		} catch (NumberFormatException e) {
			// Manejar el caso en que el parámetro no se pueda convertir a un entero
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El parámetro folder_id no es un número válido");
			return;
		}

		// Obtener los documentos de la carpeta específica

		documentDao = new Document_Dao(con);
		List<Document> documentos = new ArrayList<>();
		try {
			documentos = documentDao.findDocumentsByFolder(folderId);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving documents");
			return;
		}

		for (Document doc : documentos) {
			// Imprimir los detalles de cada documento en la consola
			System.out.println("ID del Documento: " + doc.getDocumento_id());
			System.out.println("Nombre del Documento: " + doc.getDocumento_name());
			System.out.println("Fecha de Creación: " + doc.getDocumento_creation());
			System.out.println("Resumen: " + doc.getResumen());
			System.out.println("Tipo: " + doc.getType());
			System.out.println("ID de la Carpeta: " + doc.getDfolder());
			System.out.println("ID del Usuario: " + doc.getDuser());
			System.out.println("----------------------------------------");

		}

		// Agregar documentos al contexto
		request.setAttribute("documentos", documentos);

		// Redirigir a la página de contenido
		String path = "/Contenido.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("documentos", documentos);
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
