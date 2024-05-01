
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class log1
 */
public class log1 extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	// Ruta del archivo en Linux
    static String rutaArchivo = "/home/user/Escritorio/testlog0.txt";

    /**
     * Default constructor. 
     */
    public log1() {
        // TODO Auto-generated constructor stub
    	
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
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
		boolean saved = escribirLog(request.getParameter("usuario"),request.getRemoteAddr(),"log0",request.getMethod());				
		out.println("<p>Log saved:  " + (saved ? "Si" : "No")+"</p>");
		
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	//2020-06-09T19:38:14.278 prof1 158.11.11.11 acceso GET

	public static boolean escribirLog(String user, String ip, String sl, String method) {
		// Intentamos abrir el archivo y escribir en él
		 boolean res = false;
		 
        try {
        	String texto =  LocalDateTime.now() + " " + user + " " + ip + " " + sl + " " + method + "\n";
            // Creamos un objeto FileWriter con la ruta del archivo
        	FileWriter fw = new FileWriter(rutaArchivo, true);            
        	BufferedWriter bw = new BufferedWriter(fw);
            try {
            	// Escribimos el texto en el archivo
            	bw.write(texto);
            	System.out.println("Se ha escrito '" + texto + "' al final del archivo correctamente.");
            	res = true;
            }catch (IOException e) {
            	System.err.println("Error al escribir en el archivo: " + e.getMessage());
			}            
            // Cerramos el BufferedWriter y el FileWriter
            bw.close();            
            fw.close();
            
        } catch (IOException e) {           
        }
        return res;
    
	}

}
