package network.pokt.pocketcore.util

class Utils {

    companion object {
        @Throws(Exception::class)
        fun areDirty(vararg values: String): Boolean {
            for (value in values) {
                if (value.isNullOrEmpty()) {
                    return false
                }
            }
            return true
        }
    }
}