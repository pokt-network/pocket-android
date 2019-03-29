package network.pokt.aion.network

import android.content.Context
import network.pokt.aion.AionContract
import network.pokt.aion.PocketAion
import network.pokt.aion.models.AionRelay
import network.pokt.aion.rpc.EthRpc
import network.pokt.aion.rpc.NetRpc
import network.pokt.aion.rpc.callbacks.*
import network.pokt.aion.rpc.types.ObjectOrBoolean
import network.pokt.aion.util.HexStringUtil
import network.pokt.core.errors.PocketError
import network.pokt.core.model.Wallet
import org.json.JSONArray
import org.json.JSONObject
import org.liquidplayer.javascript.JSON
import java.math.BigInteger

class AionNetwork {

    val netID: String
    val pocketAion: PocketAion
    val eth: EthRpc
    val net: NetRpc
    val devID: String

    constructor(netID: String, pocketAion: PocketAion) {
        this.netID = netID
        this.pocketAion = pocketAion
        this.devID = pocketAion.devID
        this.eth = EthRpc(this)
        this.net = NetRpc(this)
    }

    fun getContext() : Context {
        return this.pocketAion.context
    }

    fun importWallet(privateKey: String) : Wallet {
        return this.pocketAion.importWallet(privateKey, null, PocketAion.NETWORK, this.netID, null)
    }

    fun createSmartContractInstance(contractAddress: String, abiDefinition: JSONArray) : AionContract {
        return AionContract(this, contractAddress, abiDefinition)
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

    fun sendWithStringResult(aionRelay: AionRelay, callback: StringCallback) {
        this.pocketAion.send(aionRelay) { pocketError: PocketError?, jsonResponse: JSONObject? ->
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

    fun sendWithBooleanResult(aionRelay: AionRelay, callback: BooleanCallback) {
        this.pocketAion.send(aionRelay) { pocketError: PocketError?, jsonResponse: JSONObject? ->
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

    fun sendWithBigIntegerResult(aionRelay: AionRelay, callback: BigIntegerCallback) {
        this.pocketAion.send(aionRelay) { pocketError: PocketError?, jsonResponse: JSONObject? ->
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

    fun sendWithJSONObjectResult(aionRelay: AionRelay, callback: JSONObjectCallback) {
        this.pocketAion.send(aionRelay) { pocketError: PocketError?, jsonResponse: JSONObject? ->
            var error: PocketError? = null
            var result: JSONObject? = null

            pocketError?.let {
                error = it
            }

            jsonResponse?.let {
                error = parseErrorResponse(it)

                result = when (error) {
                    null -> it.getJSONObject("result")
                    else -> null
                }
            }

            if (error == null && result == null) {
                error = PocketError("Unknown error parsing response: $jsonResponse")
            }
            callback.invoke(error, result)
        }
    }

    fun sendWithJSONArrayResult(aionRelay: AionRelay, callback: JSONArrayCallback) {
        this.pocketAion.send(aionRelay) { pocketError: PocketError?, jsonResponse: JSONObject? ->
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

    fun sendWithJSONObjectOrBooleanResult(aionRelay: AionRelay, callback: JSONObjectOrBooleanCallback) {
        this.pocketAion.send(aionRelay) { pocketError: PocketError?, jsonResponse: JSONObject? ->
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