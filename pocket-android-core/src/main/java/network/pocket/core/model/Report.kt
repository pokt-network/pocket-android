package network.pocket.core.model

import network.pocket.core.util.Utils

class Report(var ip: String, var message: String) {

    fun isValid(): Boolean {
        return (Utils.areDirty(ip, message))
    }
}