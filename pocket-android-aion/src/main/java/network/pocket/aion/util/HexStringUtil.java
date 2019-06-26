package network.pocket.aion.util;

/**
 * Utility class for Hex parsing.
 */
public class HexStringUtil {

    private static final String ZERO_X = "0x";

    public static String prependZeroX(String hex) {
        if (hasZeroHexPrefix(hex)) {
            return hex;
        } else {
            return ZERO_X + hex;
        }
    }

    public static String removeLeadingZeroX(String hex) {
        if (hasZeroHexPrefix(hex)) {
            if (hex.length() > ZERO_X.length()) {
                return hex.substring(ZERO_X.length(), hex.length());
            } else {
                return "";
            }
        } else {
            return hex;
        }
    }

    public static boolean hasZeroHexPrefix(String hex) {
        return hex.length() >= ZERO_X.length() && hex.substring(0, 2).equalsIgnoreCase(ZERO_X);
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
