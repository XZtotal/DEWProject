package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

public class LogUtil {
	public static String rutaArchivo = "/home/user/NotalOnline.log";
	
	public static void setPath(String path) {
        rutaArchivo = path;
    }
	public static boolean log(String user, String ip, String sl, String method) {
		// Intentamos abrir el archivo y escribir en él
		 boolean res = false;

		
        try {
        	String texto =  LocalDateTime.now() + " " + user + " " + ip + " " + sl + " " + method + "\n";
            // Creamos un objeto FileWriter con la ruta del archivo
        	FileWriter fw = new FileWriter(rutaArchivo, true);            
        	BufferedWriter bw = new BufferedWriter(fw);
            try {
            	// Escribimos el texto en el archivo
            	bw.write(texto);
            	System.out.println("Se ha escrito '" + texto + "' al final del archivo correctamente.");
            	res = true;
            }catch (IOException e) {
            	System.err.println("Error al escribir en el archivo: " + e.getMessage());
			}            
            // Cerramos el BufferedWriter y el FileWriter
            bw.close();            
            fw.close();
            
        } catch (IOException e) {           
        }
        return res;
    
	}
	
	public static boolean log(String text) {
		// Intentamos abrir el archivo y escribir en él
		 boolean res = false;
		
        try {
        	String texto =  LocalDateTime.now() + " [TEXT]: " + text + "\n";
            // Creamos un objeto FileWriter con la ruta del archivo
        	FileWriter fw = new FileWriter(rutaArchivo, true);            
        	BufferedWriter bw = new BufferedWriter(fw);
            try {
            	// Escribimos el texto en el archivo
            	bw.write(texto);
            	System.out.println("Se ha escrito '" + texto + "' al final del archivo correctamente.");
            	res = true;
            }catch (IOException e) {
            	System.err.println("Error al escribir en el archivo: " + e.getMessage());
			}            
            // Cerramos el BufferedWriter y el FileWriter
            bw.close();            
            fw.close();
            
        } catch (IOException e) {           
        }
        return res;
    
	}
	
	//Hace un log automatico pasandole el request y el nombre del servlet
	
	public static boolean log(HttpServletRequest request, String servlet) {
		return log(request.getRemoteUser(),request.getRemoteAddr(),servlet,request.getMethod());
	}
}
