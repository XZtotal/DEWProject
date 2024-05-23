

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class AuthTest
 */
public class AuthTest extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AuthTest() {
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
		response.getWriter().append("Served at: ").append(request.getContextPath());
		response.getWriter().append("  login: "+login+", isProfesor: "+isProfesor+", isAlumno: " + isAlumno+ ", dni: "+ (session!=null ? request.getAttribute("dni") : "nosession" ) +", key:  "+ (session!=null ? request.getAttribute("key") : "nosession" )); 
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
