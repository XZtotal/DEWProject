

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
        
        //para que ninguna pagina se guarde en cache
        httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setDateHeader("Expires", 0);

        if (session != null && httpRequest.getRemoteUser() != null && session.getAttribute("key") == null) {
        	
        	String username = httpRequest.getRemoteUser(); // Obtener el nombre de usuario
        	
            Properties userParams = (Properties) config.getServletContext().getAttribute("users");
            String key = null;            
            String password = userParams.getProperty(username + ".pass");
            String dni = userParams.getProperty(username + ".dni");
            HttpCookie galleta = null;
            
            
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

                
                if (resp.statusCode() == 200) {
                	
                    // Obtener las cookies de la respuesta
                    
                    HttpHeaders headers = resp.headers();
                    List<String> cookies = headers.allValues("Set-Cookie");

                    // Buscar la cookie
                    for (String cookie : cookies) {
                        HttpCookie httpCookie = HttpCookie.parse(cookie).get(0);
                        if (httpCookie.getName().equals("JSESSIONID")) {
                            String valorCookie = httpCookie.getValue();
                            galleta = httpCookie;
                        }
                    }
                    
                	key = resp.body();
                } else {
                	
                }
            } catch (Exception e) {
            }
            
            
            session.setAttribute("dni", dni);
            session.setAttribute("key",key );
            session.setAttribute("cookie",galleta);                
            
        }
        
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		config= fConfig;
	}

}
