import org.json.*;

public abstract class JsonTranslate {
	static public String getNombre(String json) {
		try {
			JSONObject obj = new JSONObject(json); // Se convierte el string a un objeto json para poder aplicar los metodos ya implementados en la libreria.
			String nombre = obj.getString("nombre"); // Se busca el atributo "nombre" que tiene como valor un String.
			return nombre;
		} catch (JSONException err) { // En caso de que el objeto json no se haya podido crear o no exista el atributo buscado
			System.out.println("El objeto json esta malformado o no existe el atributo nombre en la respuesta.");
			return "";
		} catch (Exception e) {
			System.out.println("Algo no ha salido bien...");
			return "";
		}
	}
	
	static public String getApellidos(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			String nombre = obj.getString("apellidos");
			return nombre;
		} catch (JSONException err) {
			System.out.println("El objeto json esta malformado o no existe el atributo apellidos en la respuesta.");
			return "";
		} catch (Exception e) {
			System.out.println("Algo no ha salido bien...");
			return "";
		}
	}
	
	static public String getDni(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			String nombre = obj.getString("dni");
			return nombre;
		} catch (JSONException err) {
			System.out.println("El objeto json esta malformado o no existe el atributo dni en la respuesta.");
			return "";
		} catch (Exception e) {
			System.out.println("Algo no ha salido bien...");
			return "";
		}
	}
}