

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class LogTest
 */
public class LogTest extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LogTest() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		 String login = request.getRemoteUser();
	        HttpSession session = request.getSession(false);
	        boolean isProfesor = request.isUserInRole("profesor");
	        boolean isAlumno = request.isUserInRole("alumno");

	        // Generar respuesta HTML
	        response.setContentType("text/html");
	        response.getWriter().append("<html><body>")
	                .append("Served at: ").append(request.getContextPath()).append("<br>")
	                .append("login: ").append(login != null ? login : "no login").append("<br>")
	                .append("isProfesor: ").append(Boolean.toString(isProfesor)).append("<br>")
	                .append("isAlumno: ").append(Boolean.toString(isAlumno)).append("<br>")
	                .append("dni: ").append(session != null ? (String) session.getAttribute("dni") : "nosession").append("<br>")
	                .append("key: ").append(session != null ? (String) session.getAttribute("key") : "nosession").append("<br>")
	                .append("IP: ").append(request.getRemoteAddr()).append("<br>")
	                .append("User-Agent: ").append(request.getHeader("User-Agent")).append("<br>")
	                .append("Current Time: ").append(java.time.LocalDateTime.now().toString()).append("<br>")
	                .append("Requested URI: ").append(request.getRequestURI()).append("<br>")
	                .append("Method: ").append(request.getMethod()).append("<br>")
	                .append("</body></html>");	
	        }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
