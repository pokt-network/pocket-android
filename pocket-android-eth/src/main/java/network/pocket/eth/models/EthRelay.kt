package network.pocket.eth.models

import network.pocket.eth.PocketEth
import network.pocket.core.model.Relay
import org.json.JSONArray
import org.json.JSONObject

/**
 * ETh Relay RPC model class
 *
 * @see Relay
 *
 * @param netID the netId of the blockchain.
 * @param devID the id used to interact with Pocket Api.
 * @param method RPC method.
 * @param params extra params.
 *
 * @property METHOD_KEY
 * @property PARAMS_KEY
 * @property JSONRPC_KEY
 * @property JSONRPC_VERSION
 * @property ID_KEY
 *
 * @constructor returns an Aion Relay
 *
 */
class EthRelay(
    netID: String, devID: String, var method: String, params: JSONArray?
) : Relay(PocketEth.NETWORK, netID, devID) {

    private val METHOD_KEY = "method"
    private val PARAMS_KEY = "params"
    private val JSONRPC_KEY = "jsonrpc"
    private val JSONRPC_VERSION = "2.0"
    private val ID_KEY = "id"

    init {
        var dataObj = JSONObject()
        dataObj.put(METHOD_KEY, this.method)
        dataObj.put(PARAMS_KEY, params ?: JSONArray())
        dataObj.put(JSONRPC_KEY, JSONRPC_VERSION)
        dataObj.put(ID_KEY, (1..999).random())
        this.data = dataObj.toString()
    }

}