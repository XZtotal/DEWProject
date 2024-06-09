

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

import org.apache.jasper.tagplugins.jstl.Util;

import utils.LogUtil;


/**
 * Servlet Filter implementation class LogFilter
 */
public class LogFilter extends HttpFilter implements Filter {
	static FilterConfig config;
       
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
        // Método principal del filtro que procesa cada solicitud
	HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Configura las cabeceras HTTP para evitar el almacenamiento en caché
        httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setDateHeader("Expires", 0);
        
        // Registra la URI del servlet
        String servletPath = httpRequest.getRequestURI();
       
        
        // Llama al método log de LogUtil para registrar la solicitud
        LogUtil.log(httpRequest, servletPath);
        
        // Continuar con la cadena de filtros
        chain.doFilter(request, response);
    }
	

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
		// Método de inicialización del filtro
		config = fConfig;      
		// Obtiene la ruta del archivo de registro desde los parámetros de contexto
		LogUtil.setPath(config.getServletContext().getInitParameter("logFilePath"));
		// Verifica si la ruta del archivo de registro es válida
		if (LogUtil.rutaArchivo != null) {
            		LogUtil.setPath(LogUtil.rutaArchivo);
                } else {
            		throw new ServletException("logFilePath context parameter is missing");
		}
	}

}
