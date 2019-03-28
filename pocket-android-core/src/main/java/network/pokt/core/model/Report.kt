package network.pokt.core.model

import network.pokt.core.util.Utils

class Report(var ip: String, var message: String) {

    fun isValid(): Boolean {
        return (Utils.areDirty(ip, message))
    }
}