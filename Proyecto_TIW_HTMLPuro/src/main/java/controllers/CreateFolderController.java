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

import org.apache.commons.lang.StringEscapeUtils;

import dao.Folder_Dao;
import models.User;

@WebServlet("/CreateFolderController")
public class CreateFolderController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection con = null;

	public void init() throws ServletException {

		System.out.println("ESTAS EN CreateFolderController");
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

		boolean isBadRequest = false;
		String folder_name = null;
		int fatherfolder = 1;
		User user = (User) session.getAttribute("user");
		Folder_Dao folderdao = new Folder_Dao(con);

		try {

			folder_name = StringEscapeUtils.escapeJava(request.getParameter("folder_name"));

		} catch (NumberFormatException | NullPointerException e) {
			isBadRequest = true;
			e.printStackTrace();
		}
		if (isBadRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}

		try {
			folderdao.create_folder(folder_name, fatherfolder, user.getId());
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to create folder");
			return;
		}

		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/PaginaInicioController";
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