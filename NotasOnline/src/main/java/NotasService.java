
import org.apache.tomcat.util.json.JSONParser;
import org.json.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.BufferedReader;
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

	// private static final String NOTAS_URL = "/asignaturas";
	/**
	 * Default constructor.
	 */
	public NotasService() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession sesion = request.getSession(false);
		if (sesion == null)
			return;
		// Comprobamos rol
		if (!request.isUserInRole("profesor")) {
			response.sendError(403);
			return;
		}
		// Atributos de la sesión
		String key = (String) sesion.getAttribute("key");
		String id = (String) sesion.getAttribute("dni");
		HttpCookie galleta = (HttpCookie) sesion.getAttribute("cookie");

		String asignatura = request.getParameter("asignatura"); // El código de la asignatura solicitada

		// Comprobaciones varias
		if (key == null || key.isEmpty()) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Sesión no iniciada");
			return;
		}
		if (id == null || id.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "DNI del profesor es requerido");
			return;
		}
		if (asignatura == null || asignatura.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Asignatura no especificada");
			return;
		}

		try {
			// GET Asignaturas del profesor
			HttpResponse<String> res2 = Utils.sendGetRequest(BASE_URL + "/profesores/" + id + "/asignaturas?key=" + key,
					galleta);
			if (res2.statusCode() != 200 && res2.statusCode() != 201 && res2.statusCode() != 204) {
				throw new RuntimeException("Error en la petición: " + res2.statusCode());
			}

			JSONArray asig = new JSONArray(res2.body());

			// Comprobamos que la asignatura solicitada por el profesor le pertenece, si no,
			// no se le permite el acceso.
			boolean existe = false;
			for (int i = 0; i < asig.length(); i++) {
				if (asig.getJSONObject(i).getString("acronimo").equals(asignatura)) {
					existe = true;
					break;
				}
			}
			if (!existe) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "Asignatura no permitida");
				return;
			}

			// GET Alumnos
			HttpResponse<String> res1 = Utils.sendGetRequest(BASE_URL + "/alumnosyasignaturas?key=" + key, galleta);
			if (res1.statusCode() != 200 && res1.statusCode() != 201 && res1.statusCode() != 204) {
				throw new RuntimeException("Error en la petición: " + res1.statusCode());
			}

			JSONArray alumnos = new JSONArray(res1.body());

			// Notas de una asignatura devuelve un JSONArray con objetos JSON con dos
			// atributos: alumno(el dni), nota
			HttpResponse<String> res3 = Utils
					.sendGetRequest(BASE_URL + "/asignaturas/" + asignatura + "/alumnos?key=" + key, galleta);
			if (res3.statusCode() != 200 && res3.statusCode() != 201 && res3.statusCode() != 204) {
				throw new RuntimeException("Error en la petición: " + res3.statusCode());
			}

			JSONArray notas = new JSONArray(res3.body());

			// Añadimos información extra de los alumnos (nombre y apellidos) al json de
			// notas.
			for (int i = 0; i < notas.length(); i++) {
				JSONObject nota = notas.getJSONObject(i);
				for (int j = 0; j < alumnos.length(); j++) {
					JSONObject alum = alumnos.getJSONObject(j);
					if (alum.getString("dni").equals(nota.getString("alumno"))) {
						nota.put("nombre", alum.getString("nombre"));
						nota.put("apellidos", alum.getString("apellidos"));
						break;
					}
				}
			}

			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.print(JSONObject.valueToString(notas));
			out.flush();
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Error al obtener la información del profesor.\n" + e.getMessage() + "\n\n "
							+ e.getLocalizedMessage());

		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.sendError(404);
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession sesion = request.getSession(false);
		if (sesion == null)
			return;

		if (!request.isUserInRole("profesor")) {
			response.sendError(403);
			return;
		}
		// Atributos de sesión
		String key = (String) sesion.getAttribute("key");
		String id = (String) sesion.getAttribute("dni");
		HttpCookie galleta = (HttpCookie) sesion.getAttribute("cookie");
		// Parámetro de asignatura
		String asignatura = request.getParameter("asignatura");

		// Comprobaciones varias
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

		// Extraemos el json que esta en el cuerpo del mensaje
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = request.getReader();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		JSONArray nuevasNotas = new JSONArray(sb.toString());

		try {
			// GET Asignaturas del profesor
			HttpResponse<String> resAsig = Utils
					.sendGetRequest(BASE_URL + "/profesores/" + id + "/asignaturas?key=" + key, galleta);
			if (resAsig.statusCode() != 200 && resAsig.statusCode() != 201 && resAsig.statusCode() != 204) {
				throw new RuntimeException("Error en la petición: " + resAsig.statusCode());
			}

			JSONArray asig = new JSONArray(resAsig.body());
			boolean tieneAsignatura = false;

			for (int i = 0; i < asig.length(); i++) {
				if (asig.getJSONObject(i).getString("acronimo").equals(asignatura)) {
					tieneAsignatura = true;
					break;
				}
			}
			if (!tieneAsignatura) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Asignatura no valida");
				return;
			}

			// GET Alumnos de dicha asignatura
			HttpResponse<String> resAlumnos = Utils
					.sendGetRequest(BASE_URL + "/asignaturas/" + asignatura + "/alumnos?key=" + key, galleta);
			if (resAlumnos.statusCode() != 200 && resAlumnos.statusCode() != 201 && resAlumnos.statusCode() != 204) {
				throw new RuntimeException("Error en la petición: " + resAlumnos.statusCode());
			}
			JSONArray alumnos = new JSONArray(resAlumnos.body());

			// Comprobramos que todos los alumnos estén en la asignatura
			for (int i = 0; i < nuevasNotas.length(); i++) {
				JSONObject nota = nuevasNotas.getJSONObject(i);
				boolean existeAlumno = false;
				for (int j = 0; j < alumnos.length(); j++) {
					JSONObject alum = alumnos.getJSONObject(j);
					if (alum.getString("alumno").equals(nota.getString("dni"))) {
						existeAlumno = true;
						break;
					}
				}
				if (!existeAlumno) {
					response.sendError(HttpServletResponse.SC_FORBIDDEN, "El alumno con dni=" + nota.getString("dni")
							+ " no pertenece a ninguna de tus asignaturas.");
					return;
				}
			}

			// Modificamos las Notas de cada alumno
			for (int i = 0; i < nuevasNotas.length(); i++) {
				JSONObject nota = nuevasNotas.getJSONObject(i);
				String dni = nota.getString("dni");
				Double nota2 = nota.getDouble("nota");
				if (nota2 > 10)
					nota2 = 10d;
				else if (nota2 < 0)
					nota2 = 0d;
				HttpResponse<String> res3 = Utils.sendPutRequest(
						BASE_URL + "/alumnos/" + dni + "/asignaturas/" + asignatura + "?key=" + key, galleta,
						nota2 + "");
			}
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("text/plain");
			PrintWriter out = response.getWriter();
			out.print("OK :)");
			out.flush();

		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Error al obtener la información del profesor.\n" + e.getMessage() + "\n\n "
							+ e.getLocalizedMessage());

		}
	}

}
