import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class PropertiesInitializer implements ServletContextListener {
	//Al inicializar la web
	 @Override
	    public void contextInitialized(ServletContextEvent sce) {
	        System.out.println("Aplicación web iniciada. Cargando parámetros de configuración.");

	        // Coloca tu código de inicialización aquí
	    }
	 
	 	//Al finalizar la web
	    @Override
	    public void contextDestroyed(ServletContextEvent sce) {
	        System.out.println("Aplicación web detenida. Realizando limpieza.");
	    }
}
