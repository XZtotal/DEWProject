

import org.json.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


/**
 * Servlet implementation class AlumnService
 */
public class AlumnService extends HttpServlet {
	private static final long serialVersionUID = 1L;

	
	private static final String BASE_URL = "http://localhost:9090/CentroEducativo/alumnos/";
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
		 String id = request.getParameter("dni");
	        if (id == null || id.isEmpty()) {
	            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Dni del alumno es requerido");
	            return;
	        }

	        try {
	            String alumnoInfo = sendRequest(BASE_URL + id, "GET", null);
	            response.setContentType("application/json");
	            PrintWriter out = response.getWriter();
	            out.print(alumnoInfo);
	            out.flush();
	        } catch (Exception e) {
	            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al obtener la información del alumno");
	        }
	    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		  try {
	            String requestBody = request.getReader().lines()
	                    .reduce("", (accumulator, actual) -> accumulator + actual);

	            JSONObject json = new JSONObject(requestBody);
	            String result = sendRequest(BASE_URL, "POST", json);
	            response.setContentType("application/json");
	            PrintWriter out = response.getWriter();
	            out.print(result);
	            out.flush();
	        } catch (Exception e) {
	            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al crear al alumno");
	        }	
		  }

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		 String id = request.getParameter("dni");
	        if (id == null || id.isEmpty()) {
	            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Dni del alumno es requerido");
	            return;
	        }

	        try {
	            String requestBody = request.getReader().lines()
	                    .reduce("", (accumulator, actual) -> accumulator + actual);

	            JSONObject json = new JSONObject(requestBody);
	            String result = sendRequest(BASE_URL + id, "PUT", json);
	            response.setContentType("application/json");
	            PrintWriter out = response.getWriter();
	            out.print(result);
	            out.flush();
	        } catch (Exception e) {
	            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al actualizar la información del alumno");
	        }
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String id = request.getParameter("dni");
        if (id == null || id.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Dni del alumno es requerido");
            return;
        }

        try {
            String result = sendRequest(BASE_URL + id, "DELETE", null);
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print(result);
            out.flush();
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al eliminar al alumno");
        }
	}
	
	private String sendRequest(String url, String method, JSONObject body) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(new URI(url));

        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
            requestBuilder.header("Content-Type", "application/json");
            requestBuilder.method(method, HttpRequest.BodyPublishers.ofString(body.toString()));
        } else {
            requestBuilder.method(method, HttpRequest.BodyPublishers.noBody());
        }

        HttpRequest request = requestBuilder.build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200 && response.statusCode() != 201 && response.statusCode() != 204) {
            throw new RuntimeException("Error en la petición: " + response.statusCode());
        }
        return response.body();
    }
}
