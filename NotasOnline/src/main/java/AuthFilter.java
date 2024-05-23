

import java.io.IOException;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import utils.LogUtil;

/**
 * Servlet Filter implementation class AuthFilter
 */
public class AuthFilter extends HttpFilter implements Filter {
       static FilterConfig config;
    /**
     * @see HttpFilter#HttpFilter()
     */
    public AuthFilter() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		// place your code here
		HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        LogUtil.log("filtro activo");

        if (session != null && session.getAttribute("username") == null && request.getAttribute("key") == null) {
        	
        	String username = httpRequest.getRemoteUser(); // Obtener el nombre de usuario
        	
            Properties userParams = (Properties) config.getServletContext().getAttribute("users");
            String key = null;            
            String dni = userParams.getProperty(username + ".dni");
            LogUtil.log("dni: "+dni);
            String password = userParams.getProperty(username + ".pass");
            
            
            String url = "http://localhost:9090/CentroEducativo/login";
            
            String requestBody = "{\"dni\": \"" + dni + "\", \"password\": \"" + password + "\"}";

            
            HttpClient httpClient = HttpClient.newBuilder().build();

            // Crear una solicitud HTTP POST
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            try {
                // Enviar la solicitud y obtener la respuesta
                HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

                // Verificar el código de estado de la respuesta
                if (resp.statusCode() == 200) {
                    // Obtener las cookies de la respuesta
                    HttpHeaders headers = resp.headers();
                    List<String> cookies = headers.allValues("Set-Cookie");

                    // Iterar sobre las cookies para encontrar la que necesitas
                    for (String cookie : cookies) {
                        HttpCookie httpCookie = HttpCookie.parse(cookie).get(0);
                        if (httpCookie.getName().equals("JSESSIONID")) {
                            String valorCookie = httpCookie.getValue();
                            LogUtil.log("Valor de la cookie: " + valorCookie);
                            // Puedes guardar el valor de la cookie en una String o donde lo necesites
                            key = valorCookie;
                        }
                    }
                } else {
                	LogUtil.log("La solicitud no se completó correctamente. Código de estado: " + resp.statusCode());
                }
            } catch (Exception e) {
            	LogUtil.log("Se produjo un error al enviar la solicitud: " + e.getMessage());
            }
            
            LogUtil.log("dni encontrado=" + dni);
            LogUtil.log("key encontrada=" + key);
            
            session.setAttribute("dni", dni);
            session.setAttribute("key",key );
        
            
        }
        

		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
		config= fConfig;
	}

}
