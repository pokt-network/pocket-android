package network.pocket.core.model

import network.pocket.core.util.Utils

/**
 * A Model Class that represents a Relay.
 *
 * @property blockchain the blockchain network name, ie: ETH, AION.
 * @property netId the netId of the blockchain.
 * @property devId the id used to interact with Pocket Api.
 * @property data the data to submit on this relay
 * @property method the HTTP method to submit in this relay (POST, PUT, GET, DELETE, OPTIONS, HEAD)
 * @property path the HTTP Path url to send this relay to
 * @property headers the HTTP headers to submit on this relay
 *
 * @constructor Creates a Relay Object.
 */
open class Relay(blockchain: String, netId: String, devId: String, data: String?, method: String?, path: String?, queryParams: Map<String, String>?, headers: Map<String,String>?) {

    var blockchain = blockchain
    var netId = netId
    var devId = devId
    var data = data
    var method = method
    var path = path
    var headers = headers
    @Transient
    var queryParams = queryParams

    init {
        this.queryParams?.let {queryMap ->
            this.path = this.path + "?" + queryMap.map {queryMapEntry ->
                "${queryMapEntry.key}=${queryMapEntry.value}"
            }.joinToString {queryParamStr ->
                "$queryParamStr&"
            }
        }

    }

    /**
     * Checks if this Relay has been configured correctly.
     *
     *
     * @return whether it's correctly configured.
     */
    fun isValid(): Boolean {
        return !Utils.areDirty(this.blockchain, this.netId, this.devId)
    }
}