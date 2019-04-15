package network.pokt.eth.abi.v2;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InputOutput {

    private String name;
    private String type;

    // Constants
    private static final String NAME_KEY = "name";
    private static final String TYPE_KEY = "type";

    public InputOutput(String name, @NotNull String type) {
        this.name = name;
        this.type = type;
    }

    public static List<InputOutput> fromInputJSONArray(@NotNull JSONArray inputArrayJSON) throws JSONException {
        List<InputOutput> result = new ArrayList<>();
        for (int i = 0; i < inputArrayJSON.length(); i++) {
            result.add(InputOutput.fromInputJSONObject(inputArrayJSON.getJSONObject(i)));
        }
        return result;
    }

    public static InputOutput fromInputJSONObject(@NotNull JSONObject inputObj) throws JSONException {
        String inputName = inputObj.optString(NAME_KEY);
        String inputType = inputObj.getString(TYPE_KEY);
        return new InputOutput(inputName, inputType);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
