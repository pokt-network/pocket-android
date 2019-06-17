package network.pocket.aion.operations;

import android.os.Build;
import android.support.annotation.RequiresApi;
import network.pocket.aion.aionSource.ABIEncoder;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;

import static network.pocket.aion.util.HexStringUtil.bytesToHex;

public class EncodeAVMFunctionCallOperation {
    enum AVMTypes {
        BYTE("byte"), BYTE1D("byte[]"), BYTE2D("byte[][]"),
        BOOLEAN("bool"), BOOLEAN1D("bool[]"), BOOLEAN2D("bool[][]"),
        CHAR("char"), CHAR1D("char[]"), CHAR2D("char[][]"),
        SHORT("short"), SHORT1D("short[]"), SHORT2D("short[][]"),
        INT("int"), INT1D("int[]"), INT2D("int[][]"),
        FLOAT("float"), FLOAT1D("float[]"), FLOAT2D("float[][]"),
        LONG("long"), LONG1D("long[]"), LONG2D("long[][]"),
        DOUBLE("double"), DOUBLE1D("double[]"), DOUBLE2D("double[][]"),
        ADDRESS("address"), ADDRESS1D("address[]"), STRING("string"),
        String1D("string[]");

        private final String name;

        AVMTypes(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    class Function {
        public String name;
        public String modifier;
        public String returnType;
        public ArrayList<String> params; // array of param<type / name>
    }

    public Function FunctionParser(String rawFunction) { //TODO error handling
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Function FunctionParserReflection(Method method) {
        Function f = new Function();
        f.modifier = Modifier.toString(method.getModifiers());
        f.name = method.getName();
        f.returnType = method.getReturnType().getSimpleName();
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            if (!parameter.isNamePresent()) {
                throw new IllegalArgumentException("Parameter names are not present!");
            }
            f.params.add(parameter.getType().getSimpleName());
        }
        return f;
    }

    public void Encode(Function f) {
        String result = "0x";
        String name = bytesToHex(ABIEncoder.encodeOneString(f.name));
        result = result.concat(name);
        for (String p : f.params) {

        }
    }

    private String ParameterEncode(String p){
        // TODO
    }
}
