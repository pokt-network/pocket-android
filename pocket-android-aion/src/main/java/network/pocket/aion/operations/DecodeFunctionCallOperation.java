package network.pocket.aion.operations;

import android.content.Context;
import network.pocket.aion.R;
import network.pocket.aion.abi.v2.Function;
import network.pocket.aion.util.RawFileUtil;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.liquidplayer.javascript.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Decode Function Call Operation.
 *
 * @see BaseOperation
 *
 */
public class DecodeFunctionCallOperation extends BaseOperation {

    /**
     * Encoded Response.
     */
    private String encodedResponse;
    /**
     * Function to decode.
     * @see Function
     */
    private Function function;
    /**
     * Decoded response.
     */
    private Object[] decodedResponse = null;

    DecodeFunctionCallOperation(Context context) {
        super(context);
    }

    public DecodeFunctionCallOperation(@NotNull Context context, @NotNull Function function, @NotNull String encodedResponse) {
        super(context);
        this.function = function;
        this.encodedResponse = encodedResponse;
    }

    /**
     * Runs the operation to decode a Function.
     *
     * @param jsContext LiquidPlayer context.
     */
    @Override
    void executeOperation(JSContext jsContext) {
        // Add biginteger polyfill
        jsContext.evaluateScript(RawFileUtil.readRawTextFile(context, R.raw.biginteger));

        // Prepare data for decoding
        try {
            String jsCode = String.format(RawFileUtil.readRawTextFile(this.context, R.raw.decode_function_call), this.encodedResponse, this.function.getFunctionJSON().getJSONArray("outputs").toString());
            jsContext.evaluateScript(jsCode);
            JSValue decodedResponse = jsContext.property("decodedValue");
            if (decodedResponse.isArray()) {
                this.decodedResponse = DecodeFunctionCallOperation.parseJSValues(decodedResponse.toJSArray());
            } else {
                this.decodedResponse = new Object[]{DecodeFunctionCallOperation.jsValueToObject(decodedResponse)};
            }
            System.out.println("STOP");
        } catch (JSONException jse) {
            decodedResponse = null;
        } catch (Exception ex) {
            decodedResponse = null;
        }
    }

    public Object[] getDecodedResponse() {
        return decodedResponse;
    }

    /**
     * Registers an exception to be thrown.
     *
     * @param exception error to be shown.
     */
    @Override
    public void handle(JSException exception) {
        super.handle(exception);
        this.decodedResponse = null;
    }

    private static Object[] parseJSValues(JSBaseArray values) {
        List<Object> result = new ArrayList<>();

        for (int i = 0; i < values.size(); i++) {
            result.add(DecodeFunctionCallOperation.jsValueToObject(values.propertyAtIndex(i)));
        }


        if (values.size() != result.size()) {
            result = null;
        }
        return result.toArray();
    }

    /**
     * This utility is mainly used for decoding smart contract function return values
     *
     * @param objParam
     *
     * @return values
     */
    private static Object jsValueToObject(JSValue objParam) {
        Object result;

        if (objParam.isString()) {
            result = objParam.toString();
        } else if (objParam.isBoolean()) {
            result = objParam.toBoolean();
        } else if (objParam.isNumber()) {
            result = objParam.toNumber();
        } else if (objParam.isArray()) {
            result = objParam.toJSArray().toArray();
        } else if (objParam.isTypedArray()) {
            result = parseJSValues(objParam.toJSArray());
        } else {
            result = null;
        }

        return result;
    }
}
