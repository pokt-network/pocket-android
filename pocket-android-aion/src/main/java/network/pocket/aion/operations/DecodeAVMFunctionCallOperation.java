package network.pocket.aion.operations;

import network.pocket.aion.aionSource.ABIDecoder;

import static network.pocket.aion.util.HexStringUtil.bytesToHex;

public class DecodeAVMFunctionCallOperation {
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

    private <T> T StringDecode(String p, byte[] data) {
        ABIDecoder abiDecoder = new ABIDecoder(data);
        String result = "0x";
        switch (p) {
            case ADDRESS:
                result = bytesToHex(abiDecoder.decodeOneAddress().toByteArray());
                break;
            case ADDRESS1D:
                result = bytesToHex(abiDecoder.decodeOneAddressArray();
                break;
            case BYTE:
                result = bytesToHex(abiDecoder.decodeOneByte();
                break;
            case BYTE1D:
                result = bytesToHex(abiDecoder.decodeOneByteArray();
                break;
            case BYTE2D:
                result = bytesToHex(abiDecoder.decodeOne2DByteArray();
                break;
            case BOOLEAN:
                result = bytesToHex(abiDecoder.decodeOneBoolean();
                break;
            case BOOLEAN1D:
                result = bytesToHex(abiDecoder.decodeOneBooleanArray();
                break;
            case BOOLEAN2D:
                result = bytesToHex(abiDecoder.decodeOne2DBooleanArray();
                break;
            case CHAR:
                result = bytesToHex(abiDecoder.decodeOneCharacter();
                break;
            case CHAR1D:
                result = bytesToHex(abiDecoder.decodeOneCharacterArray();
                break;
            case CHAR2D:
                result = bytesToHex(abiDecoder.decodeOne2DCharacterArray();
                break;
            case DOUBLE:
                result = bytesToHex(abiDecoder.decodeOneDouble();
                break;
            case DOUBLE1D:
                result = bytesToHex(abiDecoder.decodeOneDoubleArray();
                break;
            case DOUBLE2D:
                result = bytesToHex(abiDecoder.decodeOne2DDoubleArray();
                break;
            case FLOAT:
                result = bytesToHex(abiDecoder.decodeOneFloat();
                break;
            case FLOAT1D:
                result = bytesToHex(abiDecoder.decodeOneFloatArray();
                break;
            case FLOAT2D:
                result = bytesToHex(abiDecoder.decodeOne2DFloatArray();
                break;
            case INT:
                result = bytesToHex(abiDecoder.decodeOneInteger();
                break;
            case INT1D:
                result = bytesToHex(abiDecoder.decodeOneIntegerArray();
                break;
            case INT2D:
                result = bytesToHex(abiDecoder.decodeOne2DIntegerArray();
                break;
            case LONG:
                result = bytesToHex(abiDecoder.decodeOneLong();
                break;
            case LONG1D:
                result = bytesToHex(abiDecoder.decodeOneLongArray();
                break;
            case LONG2D:
                result = bytesToHex(abiDecoder.decodeOne2DLongArray();
                break;
            case SHORT:
                result = bytesToHex(abiDecoder.decodeOneShort();
                break;
            case SHORT1D:
                result = bytesToHex(abiDecoder.decodeOneShortArray();
                break;
            case SHORT2D:
                result = bytesToHex(abiDecoder.decodeOne2DShortArray();
                break;
            case STRING:
                result = bytesToHex(abiDecoder.decodeOneString();
                break;
            case STRING1D:
                result = bytesToHex(abiDecoder.decodeOneStringArray();
                break;
            default:
                break;
        }
    }
}
