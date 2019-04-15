package network.pokt.aion.util;

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

}
