package network.pokt.pocketcore.model

import network.pokt.pocketcore.util.Utils

class Relay(blockchain: String, netId: String, version: String, data: String, devId: String) {

    var blockchain = blockchain
    var netId = netId
    var version = version
    var data = data
    var devId = devId


    fun isValid(): Boolean {
        return Utils.areDirty(this.blockchain, this.version, this.data, this.devId)
    }
}