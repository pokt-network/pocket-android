package network.pocket.core.util

/**
 * Pocket Core Utility class
 *
 */
class Utils {

    companion object {
        /**
         * Method used to pre-check for null or empty values.
         *
         * @throws Exception if params are invalid.
         * @param values the values to be pre-checked
         *
         * @return true if values are valid
         */
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