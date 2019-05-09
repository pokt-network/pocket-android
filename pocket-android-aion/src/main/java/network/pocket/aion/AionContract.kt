package network.pocket.aion

import org.json.JSONArray
import org.json.JSONException

import java.math.BigInteger
import java.util.HashMap

import network.pocket.aion.abi.v2.Function
import network.pocket.aion.exceptions.AionContractException
import network.pocket.aion.network.AionNetwork
import network.pocket.aion.operations.DecodeFunctionCallOperation
import network.pocket.aion.rpc.callbacks.AnyArrayCallback
import network.pocket.aion.rpc.callbacks.StringCallback
import network.pocket.core.errors.PocketError
import network.pocket.core.model.Wallet

/**
 * Aion contract class
 */

class AionContract
@Throws(JSONException::class)
constructor(
    val aionNetwork: AionNetwork,
    private val contractAddress: String,
    private val abiDefinition: JSONArray
) {
    private val functions = HashMap<String, Function>()

    init {
        this.parseContractFunctions()
    }

    /**
     * Executes an Aion function.
     *
     * @param functionName function name.
     * @param functionParams params to be sent.
     * @param fromAddress The address the transaction is sent from.
     * @param nrg Integer of the gas provided for the transaction execution.
     * @param nrgPrice Integer of the gasPrice used for each paid gas.
     * @param value Integer of the value sent with this transaction.
     * @param callback listener for execute constant function.
     */
    @Throws(AionContractException::class)
    fun executeConstantFunction(
        functionName: String,
        functionParams: List<Any>?,
        fromAddress: String?,
        nrg: BigInteger?,
        nrgPrice: BigInteger?,
        value: BigInteger?,
        callback: AnyArrayCallback
    ) {
        val function = this.functions[functionName]
        if (function == null || !function.isConstant) {
            throw AionContractException("Invalid function name or function is not constant")
        }

        var parsedFunctionParams = when(functionParams) {
            null -> ArrayList()
            else -> functionParams
        }

        val data: String = function.getEncodedFunctionCall(this.aionNetwork.getContext(), parsedFunctionParams) ?: throw AionContractException("Error generating function call data")

        this.aionNetwork.eth.call(
            this.contractAddress,
            null,
            fromAddress,
            nrg,
            nrgPrice,
            value,
            data
        ) { pocketError: PocketError?, result: String? ->
            pocketError?.let {
                callback.invoke(it, null)
                return@call
            }

            result?.let {
                var decodedResult = this.decodeCallData(function, it)
                decodedResult?.let {decodedResultArray ->
                    callback.invoke(null, decodedResultArray)
                    return@call
                }
            }

            callback.invoke(PocketError("Error decoding result: $result"), null)
        }
    }

    /**
     * Executes an Aion function.
     *
     * @param functionName function name.
     * @param functionParams params to be sent.
     * @param nonce Integer of a nonce. This allows to overwrite your own pending transactions that use the same nonce.
     * @param nrg Integer of the gas provided for the transaction execution.
     * @param nrgPrice Integer of the gasPrice used for each paid gas.
     * @param value Integer of the value sent with this transaction.
     * @param callback listener for execute constant function.
     */
    @Throws(AionContractException::class)
    fun executeFunction(
        functionName: String,
        wallet: Wallet,
        functionParams: List<Any>?,
        nonce: BigInteger?,
        nrg: BigInteger,
        nrgPrice: BigInteger,
        value: BigInteger?,
        callback: StringCallback
    ) {
        val function = this.functions[functionName] ?: throw AionContractException("Invalid function name")

        var functionParams = when(functionParams) {
            null -> ArrayList()
            else -> functionParams
        }

        val data = function.getEncodedFunctionCall(this.aionNetwork.getContext(), functionParams)
        if (nonce != null) {
            this.aionNetwork.eth.sendTransaction(wallet, this.contractAddress, nrg, nrgPrice, value, data, nonce, callback)
        } else {
            this.aionNetwork.eth.getTransactionCount(wallet.address, null) { txCountError, result ->
                txCountError?.let {
                    callback.invoke(txCountError, null)
                    return@getTransactionCount
                }

                result?.let {
                    this.aionNetwork.eth.sendTransaction(wallet, this.contractAddress, nrg, nrgPrice, value, data, it, callback)
                    return@getTransactionCount
                }

                callback.invoke(PocketError("Unknown error fetching transaction account"), null)
            }
        }
    }

    // Private interface
    @Throws(JSONException::class)
    private fun parseContractFunctions() {
        for (i in 0 until this.abiDefinition.length()) {
            val abiElement = this.abiDefinition.optJSONObject(i)
            val function = Function.parseFunctionElement(abiElement)
            if (function != null) {
                functions[function.name] = function
            }
        }
    }

    @Throws(AionContractException::class)
    private fun decodeCallData(function: Function, data: String) : Array<Any>? {
        var result: Array<Any>? = null

        val context = this.aionNetwork.getContext()
        val operation = DecodeFunctionCallOperation(context, function, data)
        val operationSuccessful = operation.startProcess()

        if (operationSuccessful) {
            result = operation.decodedResponse
        }

        return result
    }
}
