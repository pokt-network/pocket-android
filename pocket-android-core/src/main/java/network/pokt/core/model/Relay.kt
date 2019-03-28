package network.pokt.core.model

import network.pokt.core.util.Utils

class Relay(blockchain: String, netId: String, data: String, devId: String) {

    var blockchain = blockchain
    var netId = netId
    var data = data
    var devId = devId


    fun isValid(): Boolean {
        return Utils.areDirty(this.blockchain, this.data, this.devId)
    }
}