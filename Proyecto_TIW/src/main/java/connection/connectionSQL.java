package connection;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class connectionSQL extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		final String DB_URL = "jdbc:mysql://localhost:3306/BDProyecto_TIW?serverTimezone=UTC";
		final String USER = "root";
		final String PASS = "Na23122002.733";
		String result = "Connection worked";
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (Exception e) {
			result = "Connection failed";
			e.printStackTrace();
		}
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		out.println(result);
		out.close();
	}
}
