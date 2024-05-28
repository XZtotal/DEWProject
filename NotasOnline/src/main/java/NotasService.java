

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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import utils.Utils;


/**
 * Servlet implementation class AlumnService
 */
public class NotasService extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String BASE_URL = "http://localhost:9090/CentroEducativo";	
	//private static final String NOTAS_URL = "/asignaturas";
    /**
     * Default constructor. 
     */
    public NotasService() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
			HttpSession sesion = request.getSession(false);
			if(sesion == null) return;
			
			if (!request.isUserInRole("profesor")) {
				response.sendError(403);
				return;
			}
		    String key = (String) sesion.getAttribute("key");
		    String id = (String) sesion.getAttribute("dni");
		    HttpCookie galleta = (HttpCookie) sesion.getAttribute("cookie");
		    
		    String asignatura = request.getParameter("asignatura");
		    
		    if (key == null || key.isEmpty()) {
		        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Sesión no iniciada");
		        return;
		    }
		    if (id == null || id.isEmpty()) {
		        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "DNI del profesor es requerido");
		        return;
		    }
		    if (asignatura == null || asignatura.isEmpty()) {
		        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Asignatura no valida");
		        return;
		    }
		    
		   
		    try {
		    	//Asignaturas del profesor
		    	HttpResponse<String> res2 = Utils.sendGetRequest(BASE_URL+"/profesores/" + id+"/asignaturas?key="+key,galleta);
		    	if (res2.statusCode() != 200 && res2.statusCode() != 201 && res2.statusCode() != 204) {
		    		throw new RuntimeException("Error en la petición: " + res2.statusCode());
		    	}		    	
		    	
		    	JSONArray asig = new JSONArray(res2.body());
		    	boolean existe = false;
		    	
		    	for (int i = 0; i < asig.length(); i++) {
		    		if (asig.getJSONObject(i).getString("acronimo").equals(asignatura)) {
	                    existe = true;
	                    break;  
	                }
				}
		    	
		    	if (!existe) {
			        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Asignatura no valida");
			        return;
			    }
		    	
		    	//Alumnos
		    	HttpResponse<String> res1 = Utils.sendGetRequest(BASE_URL+"/alumnosyasignaturas?key="+key,galleta);
		    	if (res1.statusCode() != 200 && res1.statusCode() != 201 && res1.statusCode() != 204) {
		            throw new RuntimeException("Error en la petición: " + res1.statusCode());
		        }    	
		    	
		    	JSONArray alumnos = new JSONArray(res1.body());
		    	
		    	//Notas 
		    	HttpResponse<String> res3 = Utils.sendGetRequest(BASE_URL+"/asignaturas/"+asignatura+"/alumnos?key="+key,galleta);
		    	if (res3.statusCode() != 200 && res3.statusCode() != 201 && res3.statusCode() != 204) {
		            throw new RuntimeException("Error en la petición: " + res3.statusCode());
		        }
		    	
		    	JSONArray notas = new JSONArray(res3.body());
		    	
		    	for (int i = 0; i < notas.length(); i++) {
					JSONObject nota = notas.getJSONObject(i);
					for (int j = 0; j < alumnos.length(); j++) {
						JSONObject alum = alumnos.getJSONObject(j);
						if(alum.getString("dni").equals(nota.getString("alumno"))) {
							nota.put("nombre", alum.getString("nombre"));
							nota.put("apellidos", alum.getString("apellidos"));
							break;
						}
					}
				}
		        
		        JSONArray json = new JSONArray();
		        
		        response.setContentType("application/json");
		        PrintWriter out = response.getWriter();
		        out.print(JSONObject.valueToString(notas));
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
