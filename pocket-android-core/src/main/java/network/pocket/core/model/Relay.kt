package network.pocket.core.model

import network.pocket.core.util.Utils

/**
 * A Model Class that represents a Relay.
 *
 * @property blockchain the blockchain network name, ie: ETH, AION.
 * @property netId the netId of the blockchain.
 * @property devId the id used to interact with Pocket Api.
 * @property ipPort Ip url for this Node.
 *
 * @constructor Creates a Relay Object.
 */
open class Relay(blockchain: String, netId: String, devId: String, data: String) {

    var blockchain = blockchain
    var netId = netId
    var data = data
    var devId = devId

    constructor(blockchain: String, netId: String, devId: String) : this(blockchain, netId, devId, "") {
        this.data = ""
    }

    /**
     * Checks if this Relay has been configured correctly.
     *
     *
     * @return whether it's correctly configured.
     */
    fun isValid(): Boolean {
        return Utils.areDirty(this.blockchain, this.data, this.devId)
    }
}