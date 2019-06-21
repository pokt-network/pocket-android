package network.pocket.aion.abi.v2;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import network.pocket.aion.aionSource.ABIDecoder;
import network.pocket.aion.aionSource.ABIStreamingEncoder;
import network.pocket.aion.aionSource.Address;
import network.pocket.aion.operations.EncodeFunctionCallOperation;

import java.util.ArrayList;
import java.util.List;

import static network.pocket.aion.util.HexStringUtil.bytesToHex;
import static network.pocket.aion.util.HexStringUtil.hexToBytes;

// QUESTIONS: once I parse the function call and the parameter types (string), what do I do?
// QUESTIONS: Then do I call ETH.SendTransaction or ETH.Call?

@RequiresApi(api = Build.VERSION_CODES.N)
public class AVMFunction {
    private String name;
    private String modifier;
    private String returnType;
    private ArrayList<String> params;

    private final String STRING1D = "String[]";
    private final String STRING = "String";
    private final String ADDRESS1D = "Address[]";
    private final String ADDRESS = "Address";
    private final String DOUBLE2D = "double[][]";
    private final String DOUBLE1D = "double[]";
    private final String DOUBLE = "double";
    private final String LONG2D = "long[][]";
    private final String LONG1D = "long[]";
    private final String LONG = "long";
    private final String FLOAT2D = "float[][]";
    private final String FLOAT1D = "float[]";
    private final String FLOAT = "float";
    private final String INT2D = "int[][]";
    private final String INT1D = "int[]";
    private final String INT = "int";
    private final String SHORT2D = "short[][]";
    private final String SHORT1D = "short[]";
    private final String SHORT = "short";
    private final String CHAR2D = "char[][]";
    private final String CHAR1D = "char[]";
    private final String CHAR = "char";
    private final String BOOLEAN2D = "boolean[][]";
    private final String BOOLEAN1D = "boolean[]";
    private final String BOOLEAN = "boolean";
    private final String BYTE2D = "byte[][]";
    private final String BYTE1D = "byte[]";
    private final String BYTE = "byte";

    public static AVMFunction functionParser(String rawFunction) { //TODO error handling
        AVMFunction avmFunction = new AVMFunction();
        // split by `(` character
        String[] result = rawFunction.split("\\(");
        // split by space to get name modifier and return type
        String[] nonParams = result[0].split("\\s+");
        // get the params by taking everything before the ')' and splitting it by commas
        String[] params = result[1].split("\\)")[0].split(",");
        // result2[0] is the function name
        int npLength = nonParams.length;
        avmFunction.modifier = nonParams[0].trim();
        if (npLength == 3) {
            avmFunction.returnType = nonParams[1];
            avmFunction.name = nonParams[2];
        } else if (npLength == 4) {
            avmFunction.returnType = nonParams[2];
            avmFunction.name = nonParams[3];
        }
        // for each param, add to params list
        for (String p : params) {
            String[] temp = p.split("\\s+");
            avmFunction.params.add(temp[0]);
        }
        return avmFunction;
    }

    public String getEncodedFunctionCall(List<Object> params) {
        ABIStreamingEncoder abiStreamingEncoder = new ABIStreamingEncoder();
        // encode function name
        abiStreamingEncoder.encodeOneString(this.name);
        // encode parameters
        if (params.size() != this.params.size()) {
            // todo what happens if mismatched size?
        }
        for (int i = 0; i < this.params.size(); ++i) {
            abiStreamingEncoder = encodeParam(abiStreamingEncoder, this.params.get(i), params.get(i));
        }
        return bytesToHex(abiStreamingEncoder.toBytes());
    }

    private ABIStreamingEncoder encodeParam(ABIStreamingEncoder encoder, String type, Object o) {
        switch (type) {
            case ADDRESS:
                encoder.encodeOneAddress((Address) o);
                break;
            case ADDRESS1D:
                encoder.encodeOneAddressArray((Address[]) o);
                break;
            case BYTE:
                encoder.encodeOneByte((byte) o);
                break;
            case BYTE1D:
                encoder.encodeOneByteArray((byte[]) o); // todo may be issues unboxing if kotlin autoboxes primitive arrays
                break;
            case BYTE2D:
                encoder.encodeOne2DByteArray((byte[][]) o);
                break;
            case BOOLEAN:
                encoder.encodeOneBoolean((boolean) o);
                break;
            case BOOLEAN1D:
                encoder.encodeOneBooleanArray((boolean[]) o);
                break;
            case BOOLEAN2D:
                encoder.encodeOne2DBooleanArray((boolean[][]) o);
                break;
            case CHAR:
                encoder.encodeOneCharacter((char) o);
                break;
            case CHAR1D:
                encoder.encodeOneCharacterArray((char[]) o);
                break;
            case CHAR2D:
                encoder.encodeOne2DCharacterArray((char[][]) o);
                break;
            case DOUBLE:
                encoder.encodeOneDouble((double) o);
                break;
            case DOUBLE1D:
                encoder.encodeOneDoubleArray((double[]) o);
                break;
            case DOUBLE2D:
                encoder.encodeOne2DDoubleArray((double[][]) o);
                break;
            case FLOAT:
                encoder.encodeOneFloat((float) o);
                break;
            case FLOAT1D:
                encoder.encodeOneFloatArray((float[]) o);
                break;
            case FLOAT2D:
                encoder.encodeOne2DFloatArray((float[][]) o);
                break;
            case INT:
                encoder.encodeOneInteger((int) o);
                break;
            case INT1D:
                encoder.encodeOneIntegerArray((int[]) o);
                break;
            case INT2D:
                encoder.encodeOne2DIntegerArray((int[][]) o);
                break;
            case LONG:
                encoder.encodeOneLong((long) o);
                break;
            case LONG1D:
                encoder.encodeOneLongArray((long[]) o);
                break;
            case LONG2D:
                encoder.encodeOne2DLongArray((long[][]) o);
                break;
            case SHORT:
                encoder.encodeOneShort((short) o);
                break;
            case SHORT1D:
                encoder.encodeOneShortArray((short[]) o);
                break;
            case SHORT2D:
                encoder.encodeOne2DShortArray((short[][]) o);
                break;
            case STRING:
                encoder.encodeOneString((String) o);
                break;
            case STRING1D:
                encoder.encodeOneStringArray((String[]) o);
                break;
            default:
                break;
        }
        return encoder;
    }

    public Object decodeAVMFunctionCall(String data) {
        ABIDecoder abiDecoder = new ABIDecoder(hexToBytes(data));
        Object result = new Object();
        switch (this.returnType) {
            case ADDRESS:
                result = abiDecoder.decodeOneAddress();
                break;
            case ADDRESS1D:
                result = abiDecoder.decodeOneAddressArray();
                break;
            case BYTE:
                result = abiDecoder.decodeOneByte();
                break;
            case BYTE1D:
                result = abiDecoder.decodeOneByteArray();
                break;
            case BYTE2D:
                result = abiDecoder.decodeOne2DByteArray();
                break;
            case BOOLEAN:
                result = abiDecoder.decodeOneBoolean();
                break;
            case BOOLEAN1D:
                result = abiDecoder.decodeOneBooleanArray();
                break;
            case BOOLEAN2D:
                result = abiDecoder.decodeOne2DBooleanArray();
                break;
            case CHAR:
                result = abiDecoder.decodeOneCharacter();
                break;
            case CHAR1D:
                result = abiDecoder.decodeOneCharacterArray();
                break;
            case CHAR2D:
                result = abiDecoder.decodeOne2DCharacterArray();
                break;
            case DOUBLE:
                result = abiDecoder.decodeOneDouble();
                break;
            case DOUBLE1D:
                result = abiDecoder.decodeOneDoubleArray();
                break;
            case DOUBLE2D:
                result = abiDecoder.decodeOne2DDoubleArray();
                break;
            case FLOAT:
                result = abiDecoder.decodeOneFloat();
                break;
            case FLOAT1D:
                result = abiDecoder.decodeOneFloatArray();
                break;
            case FLOAT2D:
                result = abiDecoder.decodeOne2DFloatArray();
                break;
            case INT:
                result = abiDecoder.decodeOneInteger();
                break;
            case INT1D:
                result = abiDecoder.decodeOneIntegerArray();
                break;
            case INT2D:
                result = abiDecoder.decodeOne2DIntegerArray();
                break;
            case LONG:
                result = abiDecoder.decodeOneLong();
                break;
            case LONG1D:
                result = abiDecoder.decodeOneLongArray();
                break;
            case LONG2D:
                result = abiDecoder.decodeOne2DLongArray();
                break;
            case SHORT:
                result = abiDecoder.decodeOneShort();
                break;
            case SHORT1D:
                result = abiDecoder.decodeOneShortArray();
                break;
            case SHORT2D:
                result = abiDecoder.decodeOne2DShortArray();
                break;
            case STRING:
                result = abiDecoder.decodeOneString();
                break;
            case STRING1D:
                result = abiDecoder.decodeOneStringArray();
                break;
            default:
                break;
        }
        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public ArrayList<String> getParams() {
        return params;
    }

    public void setParams(ArrayList<String> params) {
        this.params = params;
    }
}
