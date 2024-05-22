import org.json.JSONObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AlumnMateria extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String BASE_URL = "http://localhost:9090/CentroEducativo/alumnos/";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String dni = request.getParameter("dni");
        if (dni == null || dni.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Dni del alumno es requerido");
            return;
        }

        try {
            String alumnoInfo = sendRequest(BASE_URL + dni, "GET", null);
            JSONObject json = new JSONObject(alumnoInfo);

            String nombre = JsonTranslate.getNombre(alumnoInfo);
            String apellidos = JsonTranslate.getApellidos(alumnoInfo);
            String fotoUrl = "http://localhost:9090/CentroEducativo/fotos/" + dni + ".jpg"; // URL de la foto

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<!DOCTYPE html>");
            out.println("<html lang='es'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>Detalle Alumno</title>");
            out.println("<link href='https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css' rel='stylesheet'>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='container'>");
            out.println("<h1 class='mt-5'>Detalle del Alumno</h1>");
            out.println("<div class='card mt-3'>");
            out.println("<div class='card-body'>");
            out.println("<h5 class='card-title'>" + nombre + " " + apellidos + "</h5>");
            out.println("<p class='card-text'><strong>DNI:</strong> " + dni + "</p>");
            out.println("<p class='card-text'><strong>Nombre:</strong> " + nombre + "</p>");
            out.println("<p class='card-text'><strong>Apellidos:</strong> " + apellidos + "</p>");
            out.println("<img src='" + fotoUrl + "' alt='Foto del alumno' class='img-fluid mt-3'>");
            out.println("</div>");
            out.println("</div>");
            out.println("</div>");
            out.println("<script src='https://code.jquery.com/jquery-3.5.1.slim.min.js'></script>");
            out.println("<script src='https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.2/dist/umd/popper.min.js'></script>");
            out.println("<script src='https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js'></script>");
            out.println("</body>");
            out.println("</html>");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al obtener la información del alumno");
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