package network.pokt.aion.models

import network.pokt.aion.PocketAion
import network.pokt.core.model.Relay
import org.json.JSONArray
import org.json.JSONObject

class AionRelay(
    netID: String, devID: String, var method: String, var params: JSONArray?
) : Relay(PocketAion.NETWORK, netID, devID) {

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