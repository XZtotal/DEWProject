import java.lang.reflect.Array;

import org.json.*;

public abstract class JsonTranslate {
	public String[] responseToArray(String resp) {
		JSONObject o = new JSONObject(resp);
		o.getJSONObject("dni");
		return new String[1];
	}
}