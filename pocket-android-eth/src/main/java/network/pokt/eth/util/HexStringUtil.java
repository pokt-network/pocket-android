package network.pokt.eth.util;

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

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

}
