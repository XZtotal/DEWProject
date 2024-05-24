package listener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import utils.LogUtil;

public class PropertiesInitializer implements ServletContextListener {

	// Al inicializar la web
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("Aplicación web iniciada. Cargando parámetros de configuración.");
		LogUtil.log("Aplicación web iniciada. Cargando parámetros de configuración.");

		Properties usersp = new Properties();
		try (InputStream input = sce.getServletContext().getResourceAsStream("/WEB-INF/users.properties")) {
			if (input != null) {
				usersp.load(input);
				sce.getServletContext().setAttribute("users", usersp);
				LogUtil.log(usersp.getProperty("maria.dni"));
			} else {
				System.out.println("No se encontró el archivo users.properties");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	// Al finalizar la web
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("Aplicación web detenida. Realizando limpieza.");
	}
}
