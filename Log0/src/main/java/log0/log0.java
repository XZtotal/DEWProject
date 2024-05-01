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
		
		String preTituloHTML5 = "<!DOCTYPE html>\n<html>\n<head>\n"
				+ "<meta http-equiv=\"Content-type\" content=\"text/html; charset=utf-8\"/>";
		
		out.println(preTituloHTML5+"<title>Log0</title></head><body>");
		
		out.println("<p>Nombre: "+getServletName()+"</p>");
		out.println("<p>Nombre del usuario:  "+ request.getParameter("usuario")+"</p>");
		out.println("<p>Contraseña del usuario:  " + request.getParameter("contrasena")+"</p>");
		out.println("<p>IP del usuario:  "+request.getRemoteAddr()+"</p>");
		out.println("<p>Más información del usuario:  "+request.getHeader("User-Agent")+"</p>");
		out.println("<p>Fecha actual:  " +LocalDateTime.now().toString()+"</p>");
		out.println("<p>URI del servlet:  " +request.getRequestURI()+"</p>");
		out.println("<p>Método invocado:  " +request.getMethod()+"</p>");
		out.println("<img src=\"https://th.bing.com/th/id/R.67f45e761519fd772264f8186eea8da9?rik=j1irfg6WT2MQTA&amp;riu=http%3a%2f%2flh5.ggpht.com%2f-AMQf7on8nuY%2fUbtRxOLeRyI%2fAAAAAAAAACs%2fnKmm66KQdJE%2fs9000%2fgatitos-bebe-3.jpg&amp;ehk=n4TNCpyMqKUwMjPGhRtQLRzTCMmV41R8Qx%2beL2hLA%2bE%3d&amp;risl=&amp;pid=ImgRaw&amp;r=0\" alt=\"Gatitos bebés\" width=\"300\">");
				
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
