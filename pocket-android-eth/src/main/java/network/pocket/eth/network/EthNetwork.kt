package network.pocket.eth.network

import network.pocket.eth.rpc.callbacks.*
import network.pocket.eth.EthContract
import network.pocket.eth.PocketEth
import network.pocket.eth.models.EthRelay
import network.pocket.eth.rpc.EthRpc
import network.pocket.eth.rpc.NetRpc
import network.pocket.eth.rpc.types.ObjectOrBoolean
import network.pocket.eth.util.HexStringUtil
import network.pocket.core.errors.PocketError
import network.pocket.core.model.Wallet
import org.json.JSONArray
import org.json.JSONObject
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Keys
import java.math.BigInteger

/**
 * Eth Network operation executor.
 */
class EthNetwork {

    val netID: String
    private val pocketEth: PocketEth
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

    @Throws
    fun importWallet(
        privateKey: String
    ): Wallet {
        val result: Wallet?
        val ecKeyPair: ECKeyPair
        val privateKeyBytes: ByteArray
        try {
            privateKeyBytes = HexStringUtil.hexStringToByteArray(privateKey)
            ecKeyPair = ECKeyPair.create(privateKeyBytes)
        } catch (e: Exception) {
            throw PocketError(e.message ?: "Unknown error importing wallet")
        }
        result = Wallet(ecKeyPair.privateKey.toString(16), HexStringUtil.prependZeroX(Keys.getAddress(ecKeyPair)), PocketEth.NETWORK, netID)
        return result
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
    @Throws
    fun createWallet(): Wallet {
        val result: Wallet
        val ecKeyPair: ECKeyPair
        try {
            ecKeyPair = Keys.createEcKeyPair()
        } catch (e: Exception) {
            throw PocketError(e.message ?: "Unknown error creating wallet")
        }

        val privateKey = ecKeyPair.privateKey.toString(16)

        try {
            result = this.importWallet(privateKey)
        } catch (e: Exception) {
            throw PocketError(e.message ?: "Unknown error creating wallet")
        }
        return result
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

    /**
     * Sends an Eth relay and returns the response as a String.
     *
     * @param relay relay to be send.
     * @param callback listener for the send relay operation.
     */
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

    /**
     * Sends an Eth relay and returns the response as a Boolean.
     *
     * @param relay relay to be send.
     * @param callback listener for the send relay operation.
     */
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

    /**
     * Sends an Eth relay and returns the response as a BigInteger.
     *
     * @param relay relay to be send.
     * @param callback listener for the send relay operation.
     */
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

    /**
     * Sends an Eth relay and returns the response as a JsonObject.
     *
     * @param relay relay to be send.
     * @param callback listener for the send relay operation.
     */
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

    /**
     * Sends an Eth relay and returns the response as a JsonArray.
     *
     * @param relay relay to be send.
     * @param callback listener for the send relay operation.
     */
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

    /**
     * Sends an Eth relay and returns the response as a JsonObjectOrBoolean.
     *
     * @param relay relay to be send.
     * @param callback listener for the send relay operation.
     */
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