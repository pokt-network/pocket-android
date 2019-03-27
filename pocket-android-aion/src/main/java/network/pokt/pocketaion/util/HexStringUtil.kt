package network.pokt.pocketaion.util

class HexStringUtil {

    private val ZERO_X = "0x"

    fun prependZeroX(hex: String): String {
        return if (hasZeroHexPrefix(hex)) {
            hex
        } else {
            ZERO_X + hex
        }
    }

    fun removeLeadingZeroX(hex: String): String {
        return if (hasZeroHexPrefix(hex)) {
            if (hex.length > ZERO_X.length) {
                hex.substring(ZERO_X.length, hex.length)
            } else {
                ""
            }
        } else {
            hex
        }
    }

    fun hasZeroHexPrefix(hex: String): Boolean {
        return if (hex.length >= ZERO_X.length) hex.substring(0, 2).equals(ZERO_X, ignoreCase = true) else false
    }
}