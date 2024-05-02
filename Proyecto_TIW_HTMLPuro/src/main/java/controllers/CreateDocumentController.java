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

import dao.Document_Dao;
import dao.Folder_Dao;
import models.User;

@WebServlet("/CreateDocumentController")
public class CreateDocumentController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection con = null;

	public void init() throws ServletException {

		System.out.println("ESTAS EN CreateDocumentController");
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
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			String loginpath = getServletContext().getContextPath() + "/PaginaParaElegir.html";
			response.sendRedirect(loginpath);
			return;
		}

		String document_name = request.getParameter("document_name");
		String resumen = request.getParameter("resumen");
		String type = request.getParameter("type");
		String folder_name = request.getParameter("folder_name");
		int folder_id = -1;

		User user = (User) session.getAttribute("user");
		Document_Dao documentdao = new Document_Dao(con);
		Folder_Dao folderDao = new Folder_Dao(con);

		// Verificar que los parámetros no sean nulos o vacíos
		if (document_name == null || resumen == null || type == null || folder_name == null || document_name.isEmpty()
				|| resumen.isEmpty() || type.isEmpty() || folder_name.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}

		try {
			// Obtener el ID de la carpeta por su nombre
			folder_id = folderDao.findFolderIdByName(folder_name);
			if (folder_id == -1) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No encontrada la carpeta");
				return;
			}
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al obtener ID de la carpeta");
			return;
		}

		try {
			documentdao.create_doc(document_name, resumen, type, folder_id, user.getId());
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to create document");
			return;
		}

		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/ContenidoController?folder_id=" + folder_id;
		response.sendRedirect(path);
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