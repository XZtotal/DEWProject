package log0;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Servlet implementation class log0
 */
public class log0 extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public log0() {
    	super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html"); /**la respuesta tendrá formato HTML*/
		
		PrintWriter out = response.getWriter();
		
		out.println("Nombre:"+getServletName()+"\n"+
					"Datos del formulario" + request.getQueryString() + "\n"+
					"Nombre del usuario:"+ request.getParameter("usuario") + "\n"+
					"IP del usuario"+request.getRemoteAddr()+"\n"+
					"Más información del usuario"+request.getHeader("User-Agent")+"\n"+
					"Fecha actual:" +LocalDateTime.now().toString()+"\n"+
					"URI del servlet:" +request.getRequestURI()+"\n"+
					"Método invocado:" +request.getMethod());
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
