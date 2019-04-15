package network.pokt.aion.abi.v2;

import android.content.Context;
import network.pokt.aion.operations.EncodeFunctionCallOperation;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Function {

    private boolean constant;
    private List<InputOutput> inputs;
    private String name;
    private List<InputOutput> outputs;
    private boolean payable;
    private JSONObject functionJSON;

    // Constants
    private static final String CONSTANT_KEY = "constant";
    private static final String INPUTS_KEY = "inputs";
    private static final String NAME_KEY = "name";
    private static final String OUTPUTS_KEY = "outputs";
    private static final String PAYABLE_KEY = "payable";
    private static final String TYPE_KEY = "type";
    private static final String FUNCTION_TYPE_VALUE = "function";

    public Function(boolean constant, List<InputOutput> inputs, String name, List<InputOutput>
            outputs, boolean payable, JSONObject functionJSON) {
        this.constant = constant;
        this.inputs = inputs;
        this.name = name;
        this.outputs = outputs;
        this.payable = payable;
        this.functionJSON = functionJSON;
    }

    public static Function parseFunctionElement(JSONObject functionJSON) throws JSONException {
        if (!functionJSON.getString(TYPE_KEY).equalsIgnoreCase(FUNCTION_TYPE_VALUE)) {
            return null;
        }
        boolean constant = functionJSON.getBoolean(CONSTANT_KEY);
        List<InputOutput> inputs = InputOutput.fromInputJSONArray(functionJSON.getJSONArray(INPUTS_KEY));
        String name = functionJSON.getString(NAME_KEY);
        List<InputOutput> outputs = InputOutput.fromInputJSONArray(functionJSON.getJSONArray(OUTPUTS_KEY));
        boolean payable = functionJSON.getBoolean(PAYABLE_KEY);
        return new Function(constant, inputs, name, outputs, payable, functionJSON);
    }

    public boolean isConstant() {
        return constant;
    }

    public List<InputOutput> getInputs() {
        return inputs;
    }

    public String getName() {
        return name;
    }

    public List<InputOutput> getOutputs() {
        return outputs;
    }

    public boolean isPayable() {
        return payable;
    }

    public JSONObject getFunctionJSON() {
        return this.functionJSON;
    }

    public String getEncodedFunctionCall(Context context, List<Object> params) {
        EncodeFunctionCallOperation operation = new EncodeFunctionCallOperation(context, this, params);
        boolean processExecuted = operation.startProcess();
        if (processExecuted) {
            return operation.getEncodedFunctionCall();
        } else {
            return null;
        }
    }
}
