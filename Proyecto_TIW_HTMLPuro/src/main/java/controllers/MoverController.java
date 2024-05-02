package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import dao.Document_Dao;
import dao.Folder_Dao;
import models.Folder;
import models.User;
import models.Document;

@WebServlet("/MoverController")
public class MoverController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection con = null;
	private TemplateEngine templateEngine;

	public MoverController() {
		super();
	}

	public void init() throws ServletException {
		System.out.println("ESTAS EN MoverController");

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
		
	    HttpSession session = request.getSession();
	    User user = (User) session.getAttribute("user"); // Suponiendo que "user" sea el atributo de sesión que contiene el usuario actual
	    if (user == null) {
	        response.sendRedirect("PaginaParaElegir.html");
	        return;
	    }

		int documentId;

		Document_Dao documentdao = new Document_Dao(con);
		Folder_Dao folderdao = new Folder_Dao(con);
		Document documento = null;
		Folder carpeta = null;

		String loginpath = getServletContext().getContextPath() + "/PaginaParaElegir.html";
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}

		String documentIdParam = request.getParameter("document_id");

		System.out.println(documentIdParam);

		if (documentIdParam == null || documentIdParam.isEmpty()) {
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

		try {

			documento = documentdao.findDocumentById(documentId);

			if (documento == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND,
						"No se encontró el documento con el ID proporcionado");
				return;
			}

			carpeta = folderdao.finfFolderById(documento.getDfolder());

			String mensaje = "Estás moviendo el documento " + documento.getDocumento_name() + " de la carpeta "
					+ carpeta.getFolder_name() + " a la carpeta destino: ";

			System.out.println(mensaje);

			List<Folder> carpetas = documentdao.findCarpetasExcept(documento.getDfolder(), user.getId());

			for (Folder Vercarpeta : carpetas) {
				System.out.println("Carpeta ID: " + Vercarpeta.getFolder_id());
				System.out.println("Nombre: " + Vercarpeta.getFolder_name());
				System.out.println("--------------------------------------------");

			}

			request.setAttribute("carpetas", carpetas);
			request.setAttribute("documento", documento);
			request.setAttribute("mensaje", mensaje);

			String path = "/PaginaInicio.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("carpetas", carpetas);
			ctx.setVariable("documento", documento);
			ctx.setVariable("mensaje", mensaje);
			templateEngine.process(path, ctx, response.getWriter());

		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error de base de datos");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		int documentId, folderId;
		Document_Dao documentDao = null;

		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect("PaginaParaElegir.html");
			return;
		}

		String documentIdParam = request.getParameter("document_id");
		String folderIdParam = request.getParameter("folder_id");

		System.out.println(documentIdParam);
		System.out.println(folderIdParam);

		if (documentIdParam == null || documentIdParam.isEmpty() || folderIdParam == null || folderIdParam.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Los parámetros document_id o folder_id son nulos o vacíos");
			return;
		}

		try {
			documentId = Integer.parseInt(documentIdParam);
			folderId = Integer.parseInt(folderIdParam);
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Los parámetros document_id o folder_id no son números válidos");
			return;
		}

		documentDao = new Document_Dao(con);
		try {
			documentDao.update_doc(folderId, documentId);

			System.out.println("SE HA ENVIADO A " + folderId);

			String ctxpath = getServletContext().getContextPath();
			String path = ctxpath + "/ContenidoController?folder_id=" + folderId;
			response.sendRedirect(path);

		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error de base de datos");
		}
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
