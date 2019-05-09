package network.pocket.aion.network

import android.content.Context
import network.pocket.aion.AionContract
import network.pocket.aion.PocketAion
import network.pocket.aion.models.AionRelay
import network.pocket.aion.rpc.EthRpc
import network.pocket.aion.rpc.NetRpc
import network.pocket.aion.rpc.callbacks.*
import network.pocket.aion.rpc.types.ObjectOrBoolean
import network.pocket.aion.util.HexStringUtil
import network.pocket.core.errors.PocketError
import network.pocket.core.model.Wallet
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigInteger

/**
 * Aion Network operation executor.
 */
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

    /**
     * Gets current context.
     *
     * @return App context.
     */
    fun getContext() : Context {
        return this.pocketAion.context
    }

    /**
     * Creates a Wallet.
     *
     * @see Wallet
     *
     * @throws PocketError
     *
     * @return Created wallet.
     */
    fun createWallet() : Wallet {
        val result: Wallet
        val createWalletOperation = network.pocket.aion.operations.CreateWalletOperation(
            this.pocketAion.context,
            network.pocket.aion.PocketAion.NETWORK,
            netID
        )
        val operationSuccessful = createWalletOperation.startProcess()
        val isError = createWalletOperation.errorMsg != null
        result = when {
            operationSuccessful && !isError -> createWalletOperation.wallet
            else -> throw PocketError(createWalletOperation.errorMsg)
        }
        return result
    }

    /**
     * Imports an Aion Wallet.
     *
     * @see Wallet
     *
     * @param privateKey used to decode the Wallet.
     *
     * @throws PocketError
     *
     * @return Created wallet.
     */
    fun importWallet(privateKey: String) : Wallet {
        val result: Wallet
        val importWalletOperation = network.pocket.aion.operations.ImportWalletOperation(
            this.pocketAion.context,
            network.pocket.aion.PocketAion.NETWORK,
            netID,
            privateKey
        )
        val operationSuccessful = importWalletOperation.startProcess()
        val isError = importWalletOperation.errorMsg != null
        result = when {
            operationSuccessful && !isError -> importWalletOperation.wallet
            else -> throw PocketError(importWalletOperation.errorMsg)
        }
        return result
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

    /**
     * Sends an Aion relay and returns the response as a String.
     *
     * @param aionRelay relay to be send.
     * @param callback listener for the send relay operation.
     */
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

    /**
     * Sends an Aion relay and returns the response as a Boolean.
     *
     * @param aionRelay relay to be send.
     * @param callback listener for the send relay operation.
     */
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

    /**
     * Sends an Aion relay and returns the response as a BigInteger.
     *
     * @param aionRelay relay to be send.
     * @param callback listener for the send relay operation.
     */
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

    /**
     * Sends an Aion relay and returns the response as a JsonObject.
     *
     * @param aionRelay relay to be send.
     * @param callback listener for the send relay operation.
     */
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

    /**
     * Sends an Aion relay and returns the response as a JsonArray.
     *
     * @param aionRelay relay to be send.
     * @param callback listener for the send relay operation.
     */
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

    /**
     * Sends an Aion relay and returns the response as a JsonObjectOrBoolean.
     *
     * @param aionRelay relay to be send.
     * @param callback listener for the send relay operation.
     */
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