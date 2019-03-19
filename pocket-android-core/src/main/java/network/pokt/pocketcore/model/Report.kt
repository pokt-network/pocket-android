package network.pokt.pocketcore.model

import network.pokt.pocketcore.util.Utils

class Report(var ip: String, var message: String) {

    fun isValid(): Boolean {
        return (Utils.areDirty(ip, message))
    }
}