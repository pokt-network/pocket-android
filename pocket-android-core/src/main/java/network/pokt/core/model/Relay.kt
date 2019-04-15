package network.pokt.core.model

import network.pokt.core.util.Utils

open class Relay(blockchain: String, netId: String, devId: String, data: String) {

    var blockchain = blockchain
    var netId = netId
    var data = data
    var devId = devId

    constructor(blockchain: String, netId: String, devId: String) : this(blockchain, netId, devId, "") {
        this.data = ""
    }

    fun isValid(): Boolean {
        return Utils.areDirty(this.blockchain, this.data, this.devId)
    }
}