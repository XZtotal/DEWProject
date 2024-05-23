

import org.apache.tomcat.util.json.JSONParser;
import org.json.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import utils.Utils;


/**
 * Servlet implementation class AlumnService
 */
public class AlumnService extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String BASE_URL = "http://localhost:9090/CentroEducativo";
	private static final String ALUMNO_URL = "http://localhost:9090/CentroEducativo/api/alumnos";
	//private static final String NOTAS_URL = "/asignaturas";
    /**
     * Default constructor. 
     */
    public AlumnService() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		    String key = (String) request.getAttribute("key");
		    String id = (String) request.getAttribute("dni");
		    if (key == null || key.isEmpty()) {
		        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Sesión no iniciada");
		        return;
		    }
		    if (id == null || id.isEmpty()) {
		        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "DNI del alumno es requerido");
		        return;
		    }
		    String alumnoInfo = null;
		    int status = 0;
		    try {
		    	HttpResponse<String> rrr = Utils.sendGetRequest(ALUMNO_URL + id+"?key="+key);
		    	if (rrr.statusCode() != 200 && rrr.statusCode() != 201 && rrr.statusCode() != 204) {
		            throw new RuntimeException("Error en la petición: " + rrr.statusCode());
		        }
		    	
			    status = rrr.statusCode();
		        alumnoInfo = rrr.body();
		        JSONObject json = (JSONObject) JSONObject.stringToValue(alumnoInfo);
		        
		        HttpResponse<String> rrr2 = Utils.sendGetRequest(BASE_URL+ "/" + id+"/asignaturas?key="+key);
		    	if (rrr2.statusCode() != 200 && rrr2.statusCode() != 201 && rrr2.statusCode() != 204) {
		            throw new RuntimeException("Error en la petición: " + rrr2.statusCode());
		        }
		        
		    	JSONArray notas = (JSONArray) JSONObject.stringToValue(rrr2.body());
		    	json.append("notas", notas);
		        
		        response.setContentType("application/json");
		        PrintWriter out = response.getWriter();
		        out.print(JSONObject.valueToString(json));
		        out.flush();
		    } catch (Exception e) {
		        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al obtener la información del alumno." + " status:"+ status+",  otra info: " + alumnoInfo);
		    }
	    }
	

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}


}
