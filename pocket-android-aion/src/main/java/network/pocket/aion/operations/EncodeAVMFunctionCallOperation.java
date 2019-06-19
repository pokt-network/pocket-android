package network.pocket.aion.operations;

import android.os.Build;
import android.support.annotation.RequiresApi;
import network.pocket.aion.aionSource.ABIStreamingEncoder;

import java.util.ArrayList;

import static network.pocket.aion.util.HexStringUtil.bytesToHex;

// QUESTIONS: once I parse the function call and the parameter types (string), what do I do?
// QUESTIONS: Then do I call ETH.SendTransaction or ETH.Call?
@RequiresApi(api = Build.VERSION_CODES.N)
public class EncodeAVMFunctionCallOperation {
    class Function {
        public String name;
        public String modifier;
        public String returnType;
        public ArrayList<String> params; // array of param<type / name>
    }

    public String encodeFunction(String rawFunction) {
        Function f = functionParser(rawFunction);
        return encode(f);
    }

    private Function functionParser(String rawFunction) { //TODO error handling
        Function f = new Function();
        // split by `(` character
        String[] result = rawFunction.split("\\(");
        // split by space to get name modifier and return type
        String[] nonParams = result[0].split("\\s+");
        // get the params by taking everything before the ')' and splitting it by commas
        String[] params = result[1].split("\\)")[0].split(",");
        // result2[0] is the function name
        int npLength = nonParams.length;
        f.modifier = nonParams[0].trim();
        if (npLength == 3) {
            f.returnType = nonParams[1];
            f.name = nonParams[2];
        } else if (npLength == 4) {
            f.returnType = nonParams[2];
            f.name = nonParams[3];
        }
        // for each param, add to params list
        for (String p : params) {
            String[] temp = p.split("\\s+");
            f.params.add(temp[0]);
        }
        return f;
    }

    private String encode(Function f) {
        ABIStreamingEncoder abiStreamingEncoder = new ABIStreamingEncoder();
        String result = "0x";
        String name = bytesToHex(abiStreamingEncoder.encodeOneString(f.name).toBytes());
        result = result.concat(name);
        for (String p : f.params) result = result.concat(bytesToHex(abiStreamingEncoder.encodeOneString(p).toBytes()));
        return result;
    }

    final String STRING1D = "String[]";
    final String STRING = "String";
    final String ADDRESS1D = "Address[]";
    final String ADDRESS = "Address";
    final String DOUBLE2D = "double[][]";
    final String DOUBLE1D = "double[]";
    final String DOUBLE = "double";
    final String LONG2D = "long[][]";
    final String LONG1D = "long[]";
    final String LONG = "long";
    final String FLOAT2D = "float[][]";
    final String FLOAT1D = "float[]";
    final String FLOAT = "float";
    final String INT2D = "int[][]";
    final String INT1D = "int[]";
    final String INT = "int";
    final String SHORT2D = "short[][]";
    final String SHORT1D = "short[]";
    final String SHORT = "short";
    final String CHAR2D = "char[][]";
    final String CHAR1D = "char[]";
    final String CHAR = "char";
    final String BOOLEAN2D = "bool[][]";
    final String BOOLEAN1D = "bool[]";
    final String BOOLEAN = "bool";
    final String BYTE2D = "byte[][]";
    final String BYTE1D = "byte[]";
    final String BYTE = "byte";

    private String encodeParam(String type){
        ABIStreamingEncoder encoder = new ABIStreamingEncoder();
        switch (type) {
            case ADDRESS:
                encoder.encodeOneAddress();
                break;
            case ADDRESS1D:
                encoder.encodeOneAddressArray();
                break;
            case BYTE:
                encoder.encodeOneByte();
                break;
            case BYTE1D:
                encoder.encodeOneByteArray();
                break;
            case BYTE2D:
                encoder.encodeOne2DByteArray();
                break;
            case BOOLEAN:
                encoder.encodeOneBoolean();
                break;
            case BOOLEAN1D:
                encoder.encodeOneBooleanArray();
                break;
            case BOOLEAN2D:
                encoder.encodeOne2DBooleanArray();
                break;
            case CHAR:
                encoder.encodeOneCharacter();
                break;
            case CHAR1D:
                encoder.encodeOneCharacterArray();
                break;
            case CHAR2D:
                encoder.encodeOne2DCharacterArray();
                break;
            case DOUBLE:
                encoder.encodeOneDouble();
                break;
            case DOUBLE1D:
                encoder.encodeOneDoubleArray();
                break;
            case DOUBLE2D:
                encoder.encodeOne2DDoubleArray();
                break;
            case FLOAT:
                encoder.encodeOneFloat();
                break;
            case FLOAT1D:
                encoder.encodeOneFloatArray();
                break;
            case FLOAT2D:
                encoder.encodeOne2DFloatArray();
                break;
            case INT:
                encoder.encodeOneInteger();
                break;
            case INT1D:
                encoder.encodeOneIntegerArray();
                break;
            case INT2D:
                encoder.encodeOne2DIntegerArray();
                break;
            case LONG:
                encoder.encodeOneLong();
                break;
            case LONG1D:
                encoder.encodeOneLongArray();
                break;
            case LONG2D:
                encoder.encodeOne2DLongArray();
                break;
            case SHORT:
                encoder.encodeOneShort();
                break;
            case SHORT1D:
                encoder.encodeOneShortArray();
                break;
            case SHORT2D:
                encoder.encodeOne2DShortArray();
                break;
            case STRING:
                encoder.encodeOneString();
                break;
            case STRING1D:
                encoder.encodeOneStringArray();
                break;
            default:
                break;
        }
    }
}
