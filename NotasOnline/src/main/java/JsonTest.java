

public class JsonTest {
	public static void main(String[] args) {
		String jsonResponse = "{\"dni\":\"12345678W\",\"nombre\":\"Juan\",\"apellidos\":\"Perez Rodriguez\"}";
		System.out.println("El nombre es: " + JsonTranslate.getNombre(jsonResponse));
		System.out.println("Los apellidos son: " + JsonTranslate.getApellidos(jsonResponse));
		System.out.println("El dni es: " + JsonTranslate.getDni(jsonResponse));
	}
}