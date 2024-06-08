

import org.apache.tomcat.util.json.JSONParser;
import org.json.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import utils.Utils;


/**
 * Servlet implementation class AlumnService
 */
public class ProfeService extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String BASE_URL = "http://localhost:9090/CentroEducativo";	
	//private static final String NOTAS_URL = "/asignaturas";
    /**
     * Default constructor. 
     */
    public ProfeService() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
			HttpSession sesion = request.getSession(false);
			if(sesion == null) return;
			//Comprobamos rol
			if (!request.isUserInRole("profesor")) {
				response.sendError(403);
				return;
			}
			//Atributos de la sesión
		    String key = (String) sesion.getAttribute("key");
		    String id = (String) sesion.getAttribute("dni");
		    HttpCookie galleta = (HttpCookie) sesion.getAttribute("cookie");
		    //Comprobaciones varias
		    if (key == null || key.isEmpty()) {
		        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Sesión no iniciada, se requiere cerrar sesión");
		        return;
		    }
		    if (id == null || id.isEmpty()) {
		        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "DNI del profesor es requerido, se requiere cerrar sesión");
		        return;
		    }
		    
		    try {
		    	//Información del Profesor
		    	HttpResponse<String> res1 = Utils.sendGetRequest(BASE_URL+"/profesores/" + id+"?key="+key,galleta);
		    	if (res1.statusCode() != 200 && res1.statusCode() != 201 && res1.statusCode() != 204) {
		            throw new RuntimeException("Error en la petición (Información del Profesor): " + res1.statusCode());
		        }		    	
			    		    
			    String profInfo = res1.body();			    
		        JSONObject json = new JSONObject(profInfo);
		        
		        //Asignaturas del profesor
		        HttpResponse<String> res2 = Utils.sendGetRequest(BASE_URL+"/profesores/" + id+"/asignaturas?key="+key,galleta);
		    	if (res2.statusCode() != 200 && res2.statusCode() != 201 && res2.statusCode() != 204) {
		            throw new RuntimeException("Error en la petición (Asignaturas del profesor): " + res2.statusCode());
		        }
		    	
		        String rawAsig = res2.body();		        
		        JSONArray asig = new JSONArray(rawAsig);
		        //Añadimos las asignaturas del profesor al json	
		        json.put("asignaturas", asig);		    	
		        
		        response.setContentType("application/json");
		        PrintWriter out = response.getWriter();
		        out.print(JSONObject.valueToString(json));
		        out.flush();
		    } catch (Exception e) {
		        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al obtener la información del profesor.\n"+e.getMessage() +"\n\n "+ e.getLocalizedMessage());
		        
		    }
	    }
	

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}


}
