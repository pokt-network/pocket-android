package network.pokt.eth.abi.v2;

import network.pokt.eth.exceptions.EthContractException;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.BytesType;
import org.web3j.abi.datatypes.generated.AbiTypes;
import org.web3j.abi.datatypes.generated.Uint128;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.utils.Numeric;

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
    private static final List<String> numericTypePrefixes = Arrays.asList(new String[]{"uint", "int"});

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

    public String getEncodedFunctionCall(org.web3j.abi.datatypes.Function nativeFunction) throws EthContractException {
        return FunctionEncoder.encode(nativeFunction);
    }

    public String getEncodedFunctionCall(List<Object> params) throws EthContractException {
        return FunctionEncoder.encode(this.generateNativeFunction(params));
    }

    public org.web3j.abi.datatypes.Function generateNativeFunction(List<Object> params) throws EthContractException {
        return new org.web3j.abi.datatypes.Function(
                this.getName(),
                this.parseNativeFunctionInputs(params),
                this.parseNativeFunctionOutputs()
        );
    }

    private boolean isNumericType(String type) {
        for (String numericPrefix: numericTypePrefixes) {
            if (type.toLowerCase().startsWith(numericPrefix)) {
                return true;
            }
        }
        return false;
    }

    private List<Type> parseNativeFunctionInputs(List<Object> params) throws EthContractException {
        if (params.size() != this.inputs.size()) {
            throw new EthContractException("Invalid number of params");
        }

        List<Type> inputParameters = new ArrayList<>();
        try {
            for (int i = 0; i < this.inputs.size(); i++) {
                InputOutput input = this.inputs.get(i);
                Object param = params.get(i);
                if (this.isNumericType(input.getType()) && param instanceof BigInteger) {
                    Class<? extends Type> clazz = AbiTypes.getType(input.getType());
                    Constructor<?> ctor = clazz.getConstructor(BigInteger.class);
                    Object[] ctorParam = new Object[] { param };
                    Object inputParameter = ctor.newInstance(ctorParam);
                    inputParameters.add(clazz.cast(inputParameter));
                } else if (input.getType().equalsIgnoreCase("address") && param instanceof BigInteger) {
                    inputParameters.add(new Address((BigInteger) param));
                } else if (input.getType().equalsIgnoreCase("address") && param instanceof String) {
                    inputParameters.add(new Address((String) param));
                } else if (input.getType().equalsIgnoreCase("bool") && param instanceof Boolean) {
                    inputParameters.add(new Bool((Boolean) param));
                } else if (input.getType().toLowerCase().equalsIgnoreCase("bytes") && param instanceof byte[]) {
                    inputParameters.add(new DynamicBytes((byte[]) param));
                } else if (input.getType().toLowerCase().startsWith("bytes")) {
                    byte[] byteArrayParam;
                    if (param instanceof byte[]) {
                        byteArrayParam = ((byte[]) param);
                    } else if (param instanceof String) {
                        byteArrayParam = Numeric.hexStringToByteArray((String) param);
                    } else {
                        throw new EthContractException("Error encoding param: " + param.toString());
                    }
                    Class<? extends Type> clazz = AbiTypes.getType(input.getType());
                    Constructor<?> ctor = clazz.getConstructor(byte[].class);
                    Object[] ctorParam = new Object[] { byteArrayParam };
                    Object inputParameter = ctor.newInstance(ctorParam);
                    inputParameters.add(clazz.cast(inputParameter));
                } else if (input.getType().equalsIgnoreCase("string") && param instanceof String) {
                    inputParameters.add(new Utf8String((String) param));
                } else {
                    throw new EthContractException("Error encoding param: " + param.toString());
                }
            }
        } catch (Exception e) {
            throw new EthContractException(e.getMessage());
        }

        return inputParameters;
    }

    private List<TypeReference<?>> parseNativeFunctionOutputs() throws EthContractException {
        List<InputOutput> outputs = this.getOutputs();
        List<TypeReference<?>> outputTypes = new ArrayList();
        try {
            for (int i = 0; i < outputs.size(); i++) {
                InputOutput output = outputs.get(i);
                Class<? extends Type> clazz = AbiTypes.getType(output.getType());
                outputTypes.add(TypeReference.create(clazz));
            }
        } catch (Exception e) {
            throw new EthContractException(e.getMessage());
        }

        return outputTypes;
    }
}
