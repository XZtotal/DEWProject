

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet Filter implementation class LogFilter
 */
public class LogFilter extends HttpFilter implements Filter {
	static FilterConfig config;
    String rutaArchivo;
       
    /**
     * @see HttpFilter#HttpFilter()
     */
    public LogFilter() {
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
        
        //para que ninguna pagina se guarde en cache
        httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setDateHeader("Expires", 0);
        

        // Registrar la solicitud
        LocalDateTime timestamp = LocalDateTime.now();
        String user = (String) httpRequest.getSession().getAttribute("dni");
        String ip = httpRequest.getRemoteAddr();
        String servletPath = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

       
        String logEntry = String.format("%s %s %s %s %s %s%n", timestamp, user, ip, servletPath, method);

        try (FileOutputStream archivo = new FileOutputStream(rutaArchivo, true); PrintStream output = new PrintStream(archivo)) {
            output.println(logEntry);
        }
        
        // Continuar con la cadena de filtros
        chain.doFilter(request, response);
    }
	

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
		config = fConfig;
        rutaArchivo = config.getServletContext().getInitParameter("logFilePath");
		
	}

}
