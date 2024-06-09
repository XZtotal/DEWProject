import org.json.*;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
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

import utils.Utils;

public class GetAlumno extends HttpServlet {
	private static final String BASE_URL = "http://localhost:9090/CentroEducativo";
	private String dniAlumno = null;
	
	public GetAlumno() {
		
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession sesion = request.getSession(false);
		if (sesion == null) {
			response.sendError(403);
			return;
		}
		
		//Aqui se comprueba que si es un alumno, puede ver sus propios detalles. Los pasos de aqui se repiten y explican mejor mas abajo
		if (request.isUserInRole("alumno")) { // Un alumno puede ver sus propios detalles
			String key = (String) sesion.getAttribute("key");
			String id = (String) sesion.getAttribute("dni");
			HttpCookie galleta = (HttpCookie) sesion.getAttribute("cookie");

			try {
				HttpResponse<String> res2 = Utils.sendGetRequest(BASE_URL+"/alumnos/" + id + "?key=" + key, galleta);
				if (res2.statusCode() != 200 && res2.statusCode() != 201 && res2.statusCode() != 204) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al obtenter al alumno");
				}
				JSONObject alum = null;

				alum = new JSONObject(res2.body());
				
				HttpResponse<String> res4 = Utils.sendGetRequest(BASE_URL+"/alumnos/" + id + "/asignaturas?key=" + key, galleta);
		    	if (res4.statusCode() != 200 && res4.statusCode() != 201 && res4.statusCode() != 204) {
		    		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al obtenter las asignaturas del alumno");;
		        }
		    	String rawAsigAlum = res4.body();
		    	JSONArray asigAlum = new JSONArray(rawAsigAlum);
		    	alum.put("asignaturas", asigAlum);

				PrintWriter out = response.getWriter();
				response.setContentType("application/json");
				out.print(alum);
				out.flush();
			} catch (Exception e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error, algo ha salido terriblemente mal.\n"+e.getMessage() +"\n\n "+ e.getLocalizedMessage());
			}
			return;
		}
		
		// Si el que accede no es un alumno (comprobado antes) y no es un profesor, prohibido.
		
		if (!request.isUserInRole("profesor")) {
			response.sendError(403);
			return;
		}
		
		
		//Obtiene los datos de la sesion, la clave y la cookie.
		
		String key = (String) sesion.getAttribute("key");
	    String id = (String) sesion.getAttribute("dni");
	    HttpCookie galleta = (HttpCookie) sesion.getAttribute("cookie");
	    
	    // Se obtiene el dni del alumno con un parametro en la peticion GET
	    dniAlumno=request.getParameter("dni");
	    
	    // Si el dni esta vacio, no es valido
	    if (dniAlumno == null || dniAlumno.isEmpty()) {
	        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "DNI vacio");
	        return;
	    }
	    
	    
	    try {
	    	
	    	// Obtiene el alumno especificado con su DNI
	    	HttpResponse<String> res2 = Utils.sendGetRequest(BASE_URL+"/alumnos/" + dniAlumno + "?key=" + key, galleta);
	    	if (res2.statusCode() != 200 && res2.statusCode() != 201 && res2.statusCode() != 204) {
	    		response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Este alumno no existe!");
	    	}
	    	
	    	JSONObject alum = null;
	    	//Intenta parsear el alumno como un objeto json.
	    	try {
	    		alum = new JSONObject(res2.body());
	    		if (alum.length() <= 0) response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Alumno no encontrado");
	    	} catch (Exception e) {
	    		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al parsear el alumno como JSON");
	    	}
	    	
	    	//Obtiene las asignaturas del profesor
	        HttpResponse<String> res3 = Utils.sendGetRequest(BASE_URL+"/profesores/" + id + "/asignaturas?key=" + key, galleta);
	    	if (res3.statusCode() != 200 && res3.statusCode() != 201 && res3.statusCode() != 204) {
	            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al obtenter las asignaturas del profesor");
	        }
	    	String rawAsig = res3.body();
	    	//Intenta convertir las asignaturas en un array de objetos JSON
	    	JSONArray asig = new JSONArray(rawAsig);
	    	
	    	//Obtiene las asignaturas del alumno
	        HttpResponse<String> res4 = Utils.sendGetRequest(BASE_URL+"/alumnos/" + dniAlumno + "/asignaturas?key=" + key, galleta);
	    	if (res4.statusCode() != 200 && res4.statusCode() != 201 && res4.statusCode() != 204) {
	    		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al obtenter las asignaturas del alumno");;
	        }
	    	String rawAsigAlum = res4.body();
	    	JSONArray asigAlum = new JSONArray(rawAsigAlum);
	    	boolean found = false;
	    	
	    	//Busca alguna de las asignaturas del alumno en las asignaturas que imparte el profesor
	    	for(int i = 0; i < asigAlum.length(); i++) {
	    		String asignaturaAlum = asigAlum.getJSONObject(i).getString("asignatura");
	    		for (int e = 0; e < asig.length(); e++) {
	    			String asignaturaProfe = asig.getJSONObject(e).getString("acronimo");
	    			if (asignaturaAlum.equals(asignaturaProfe)) {
	    				found = true;
	    				break;
	    			}
	    		}
	    		if (found) break;
	    	}
	    	
	    	if (!found) { // Si no encuentra el alumno devuelve un error
	    		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Este alumno no esta en ninguna de tus asignaturas.");
	    	}
	    	alum.put("asignaturas", asigAlum);
	    	
	    	//Devuelve la respuesta
	    	PrintWriter out = response.getWriter();
	    	response.setContentType("application/json");
	    	out.print(alum);	    	
	        out.flush();
	    	
	    } catch (Exception e) {
	        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error, algo ha salido terriblemente mal.\n"+e.getMessage() +"\n\n "+ e.getLocalizedMessage());
	    }
	    
	    
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}

