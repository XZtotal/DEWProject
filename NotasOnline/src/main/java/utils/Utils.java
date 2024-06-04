package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public class Utils {
	public static HttpResponse<String> sendRequest(String url, String method, JSONObject body) throws Exception {
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
        return response;
    }
	
	public static HttpResponse<String> sendGetRequest(String url, HttpCookie cookie) throws Exception {
	    HttpClient client = HttpClient.newHttpClient();

	    // Crear el valor del encabezado 'Cookie' a partir de la HttpCookie
	    String cookieHeader = cookie.getName() + "=" + cookie.getValue();

	    HttpRequest request = HttpRequest.newBuilder()
	            .uri(new URI(url))
	            .GET()
	            .header("Cookie", cookieHeader)
	            .build();

	    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

	    if (response.statusCode() != 200) {
	        throw new RuntimeException("Error en la petición: " + response.statusCode());
	    }
	    return response;
	}
	
	public static HttpResponse<String> sendPutRequest(String url, HttpCookie cookie, String body) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // Crear el valor del encabezado 'Cookie' a partir de la HttpCookie
        String cookieHeader = cookie.getName() + "=" + cookie.getValue();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .header("Cookie", cookieHeader)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 201) {
            throw new RuntimeException("Error en la petición PUT: " + response.statusCode());
        }
        return response;
    }



	public static JSONArray getAsignaturas(HttpServletRequest request, String key, String dni, List<String> cookies) throws IOException {
	URL url = new URL("http://localhost:9090/CentroEducativo/alumnos/" + dni + "/asignaturas?key=" + key);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        for (String cookie : cookies) {
            connection.addRequestProperty("Cookie", cookie.split(";", 2)[0]);
        }
        connection.setDoOutput(true);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("accept", "application/json");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line.trim());
            }
            return new JSONArray(result.toString());
        }
    }

	public static JSONObject getAlumno(HttpServletRequest request, String key, String dni, List<String> cookies) throws IOException {
        URL url = new URL("http://localhost:9090/CentroEducativo/alumnos/" + dni + "?key=" + key);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        for (String cookie : cookies) {
            connection.addRequestProperty("Cookie", cookie.split(";", 2)[0]);
        }
        connection.setDoOutput(true);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("accept", "application/json");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line.trim());
            }
            return new JSONObject(result.toString());
        }
    }
}
