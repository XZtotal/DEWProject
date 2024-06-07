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
		
		if (request.isUserInRole("alumno")) { // Un alumno puede ver sus propios detalles
			String key = (String) sesion.getAttribute("key");
			String id = (String) sesion.getAttribute("dni");
			HttpCookie galleta = (HttpCookie) sesion.getAttribute("cookie");

			try {
				HttpResponse<String> res2 = Utils.sendGetRequest(BASE_URL+"/alumnos/" + id + "?key=" + key, galleta);
				if (res2.statusCode() != 200 && res2.statusCode() != 201 && res2.statusCode() != 204) {
					throw new RuntimeException("Error en la petición para obtencion de alumno: " + res2.statusCode());
				}
				JSONObject alum = null;

				alum = new JSONObject(res2.body());



				PrintWriter out = response.getWriter();
				response.setContentType("application/json");
				out.print(alum);
				out.flush();
			} catch (Exception e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error, algo ha salido terriblemente mal.\n"+e.getMessage() +"\n\n "+ e.getLocalizedMessage());
			}
			return;
		}
		
		if (!request.isUserInRole("profesor")) {
			response.sendError(403);
			return;
		}
		
		
		String key = (String) sesion.getAttribute("key");
	    String id = (String) sesion.getAttribute("dni");
	    HttpCookie galleta = (HttpCookie) sesion.getAttribute("cookie");
	    
	    
	    
	    if (key == null || key.isEmpty()) {
	        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Sesión no iniciada");
	        return;
	    }
	    if (id == null || id.isEmpty()) {
	        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "DNI del profesor es requerido");
	        return;
	    }
	    if (dniAlumno == null || dniAlumno.isEmpty()) {
	        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Alumno no valido");
	        return;
	    }
	    
	    dniAlumno=request.getParameter("dni");
	    
	    try {
	    	// Obten el alumno especificado con su DNI
	    	HttpResponse<String> res2 = Utils.sendGetRequest(BASE_URL+"/alumnos/" + dniAlumno + "?key=" + key, galleta);
	    	if (res2.statusCode() != 200 && res2.statusCode() != 201 && res2.statusCode() != 204) {
	    		throw new RuntimeException("Error en la petición para obtencion de alumno: " + res2.statusCode());
	    	}
	    	JSONObject alum = null;
	    	try {
	    		alum = new JSONObject(res2.body());
	    		if (alum.length() <= 0) response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Este no es el alumno que estas buscando...");
	    	} catch (Exception e) {
	    		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Este no es el alumno que estas buscando...");
	    	}
	    	//Asignaturas del profesor
	        HttpResponse<String> res3 = Utils.sendGetRequest(BASE_URL+"/profesores/" + id + "/asignaturas?key=" + key, galleta);
	    	if (res3.statusCode() != 200 && res3.statusCode() != 201 && res3.statusCode() != 204) {
	            throw new RuntimeException("Error en la petición (Asignaturas del profesor): " + res3.statusCode());
	        }
	    	String rawAsig = res3.body();
	    	JSONArray asig = new JSONArray(rawAsig);
	    	//Asignaturas del alumno
	        HttpResponse<String> res4 = Utils.sendGetRequest(BASE_URL+"/alumnos/" + dniAlumno + "/asignaturas?key=" + key, galleta);
	    	if (res4.statusCode() != 200 && res4.statusCode() != 201 && res4.statusCode() != 204) {
	            throw new RuntimeException("Error en la petición (Asignaturas del alumno): " + res4.statusCode());
	        }
	    	String rawAsigAlum = res4.body();
	    	JSONArray asigAlum = new JSONArray(rawAsigAlum);
	    	boolean found = false;
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
	    	
	    	if (!found) {
	    		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Este alumno no esta en ninguna de tus asignaturas.");
	    	}
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

