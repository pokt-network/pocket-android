package network.pokt.eth.network

import android.content.Context
import network.pokt.eth.rpc.callbacks.*
import network.pokt.eth.EthContract
import network.pokt.eth.PocketEth
import network.pokt.eth.models.EthRelay
import network.pokt.eth.rpc.EthRpc
import network.pokt.eth.rpc.NetRpc
import network.pokt.eth.rpc.types.ObjectOrBoolean
import network.pokt.eth.util.HexStringUtil
import network.pokt.core.errors.PocketError
import network.pokt.core.model.Wallet
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.math.BigInteger

class EthNetwork {

    val netID: String
    val pocketEth: PocketEth
    val eth: EthRpc
    val net: NetRpc
    val devID: String

    constructor(netID: String, pocketEth: PocketEth) {
        this.netID = netID
        this.pocketEth = pocketEth
        this.devID = pocketEth.devID
        this.eth = EthRpc(this)
        this.net = NetRpc(this)
    }

    fun getContext() : Context {
        return this.pocketEth.context
    }

    fun importWallet(privateKey: String) : Wallet {
        return this.pocketEth.importWallet(privateKey, null, PocketEth.NETWORK, this.netID, null)
    }

    fun createSmartContractInstance(contractAddress: String, abiDefinition: JSONArray) : EthContract {
        return EthContract(this, contractAddress, abiDefinition)
    }

    private fun parseErrorResponse(jsonResponse: JSONObject) : PocketError? {
        return when {
            jsonResponse.has("error") -> {
                val errorObj = jsonResponse.getJSONObject("error")
                PocketError(errorObj.getString("message"))
            }
            else -> null
        }
    }

    fun sendWithStringResult(relay: EthRelay, callback: StringCallback) {
        this.pocketEth.send(relay) { pocketError: PocketError?, jsonResponse: JSONObject? ->
            var error: PocketError? = null
            var result: String? = null

            pocketError?.let {
                error = it
            }

            jsonResponse?.let {
                error = parseErrorResponse(it)

                result = when (error) {
                    null -> it.getString("result")
                    else -> null
                }
            }

            if (error == null && result == null) {
                error = PocketError("Unknown error parsing response: $jsonResponse")
            }
            callback.invoke(error, result)
        }
    }

    fun sendWithBooleanResult(relay: EthRelay, callback: BooleanCallback) {
        this.pocketEth.send(relay) { pocketError: PocketError?, jsonResponse: JSONObject? ->
            var error: PocketError? = null
            var result: Boolean? = null

            pocketError?.let {
                error = it
            }

            jsonResponse?.let {
                error = parseErrorResponse(it)
                result = when (error) {
                    null -> it.getBoolean("result")
                    else -> null
                }
            }

            if (error == null && result == null) {
                error = PocketError("Unknown error parsing response: $jsonResponse")
            }
            callback.invoke(error, result)
        }
    }

    fun sendWithBigIntegerResult(relay: EthRelay, callback: BigIntegerCallback) {
        this.pocketEth.send(relay) { pocketError: PocketError?, jsonResponse: JSONObject? ->
            var error: PocketError? = null
            var result: BigInteger? = null

            pocketError?.let {
                error = it
            }

            jsonResponse?.let {
                error = parseErrorResponse(it)

                result = when (error) {
                    null -> BigInteger(HexStringUtil.removeLeadingZeroX(it.getString("result")), 16)
                    else -> null
                }
            }

            if (error == null && result == null) {
                error = PocketError("Unknown error parsing response: $jsonResponse")
            }
            callback.invoke(error, result)
        }
    }

    fun sendWithJSONObjectResult(relay: EthRelay, callback: JSONObjectCallback) {
        this.pocketEth.send(relay) { pocketError: PocketError?, jsonResponse: JSONObject? ->
            var error: PocketError? = null
            var result: JSONObject? = null

            pocketError?.let {
                error = it
            }

            jsonResponse?.let {
                error = parseErrorResponse(it)

                result = when (error) {
                    null -> it.optJSONObject("result")
                    else -> null
                }
            }

            callback.invoke(error, result)
        }
    }

    fun sendWithJSONArrayResult(relay: EthRelay, callback: JSONArrayCallback) {
        this.pocketEth.send(relay) { pocketError: PocketError?, jsonResponse: JSONObject? ->
            var error: PocketError? = null
            var result: JSONArray? = null

            pocketError?.let {
                error = it
            }

            jsonResponse?.let {
                error = parseErrorResponse(it)

                result = when (error) {
                    null -> it.getJSONArray("result")
                    else -> null
                }
            }

            if (error == null && result == null) {
                error = PocketError("Unknown error parsing response: $jsonResponse")
            }
            callback.invoke(error, result)
        }
    }

    fun sendWithJSONObjectOrBooleanResult(relay: EthRelay, callback: JSONObjectOrBooleanCallback) {
        this.pocketEth.send(relay) { pocketError: PocketError?, jsonResponse: JSONObject? ->
            var error: PocketError? = null
            var result: ObjectOrBoolean? = null

            pocketError?.let {
                error = it
            }

            jsonResponse?.let {
                error = parseErrorResponse(it)
                result = when (error) {
                    null -> {
                        var relayResult: Any = it.get("result")
                        when(relayResult) {
                            is JSONObject -> ObjectOrBoolean(relayResult as? JSONObject)
                            is Boolean -> ObjectOrBoolean(relayResult as? Boolean)
                            else -> null
                        }
                    }
                    else -> null
                }
            }

            if (error == null && result == null) {
                error = PocketError("Unknown error parsing response: $jsonResponse")
            }
            callback.invoke(error, result)
        }
    }
}