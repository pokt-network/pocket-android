package network.pocket.core.model

import network.pocket.core.util.Utils

/**
 * A Model Class that represents a Report.
 *
 * Used to report a fallen node.
 *
 * @property ip the ip of the Node that is currently unavailable.
 * @property message the message to report this node.
 *
 * @constructor Creates a Report Object.
 */
class Report(var ip: String, var message: String) {

    /**
     * Checks if this Report has been configured correctly.
     *
     *
     * @return whether it's correctly configured.
     */
    fun isValid(): Boolean {
        return (Utils.areDirty(ip, message))
    }
}