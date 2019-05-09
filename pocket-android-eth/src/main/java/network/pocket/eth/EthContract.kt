package network.pocket.eth

import org.json.JSONArray
import org.json.JSONException

import java.math.BigInteger
import java.util.HashMap

import network.pocket.eth.abi.v2.Function
import network.pocket.eth.exceptions.EthContractException
import network.pocket.eth.network.EthNetwork
import network.pocket.eth.rpc.callbacks.AnyArrayCallback
import network.pocket.eth.rpc.callbacks.StringCallback
import network.pocket.core.errors.PocketError
import network.pocket.core.model.Wallet
import network.pocket.eth.abi.v2.FunctionCallDecoder

/**
 * Eth contract class
 */
class EthContract// Public interface
@Throws(JSONException::class)
constructor(
    val ethNetwork: EthNetwork,
    private val contractAddress: String,
    private val abiDefinition: JSONArray
) {
    private val functions = HashMap<String, Function>()

    init {
        this.parseContractFunctions()
    }

    /**
     * Executes an Eth function.
     *
     * @param functionName function name.
     * @param functionParams params to be sent.
     * @param fromAddress The address the transaction is sent from.
     * @param gas Integer of the gas provided for the transaction execution.
     * @param gasPrice Integer of the gasPrice used for each paid gas.
     * @param value Integer of the value sent with this transaction.
     * @param callback listener for execute constant function.
     */
    @Throws(EthContractException::class)
    fun executeConstantFunction(
        functionName: String,
        functionParams: List<Any>?,
        fromAddress: String?,
        gas: BigInteger?,
        gasPrice: BigInteger?,
        value: BigInteger?,
        callback: AnyArrayCallback
    ) {
        val function = this.functions[functionName]
        if (function == null || !function.isConstant) {
            throw EthContractException("Invalid function name or function is not constant")
        }

        var functionParams = when(functionParams) {
            null -> ArrayList()
            else -> functionParams
        }

        var nativeFunction: org.web3j.abi.datatypes.Function = function.generateNativeFunction(functionParams)
        val data = function.getEncodedFunctionCall(nativeFunction)

        this.ethNetwork.eth.call(
            this.contractAddress,
            null,
            fromAddress,
            gas,
            gasPrice,
            value,
            data
        ) { pocketError: PocketError?, result: String? ->
            pocketError?.let {
                callback.invoke(it, null)
                return@call
            }

            result?.let {
                var decodedResult = FunctionCallDecoder.decodeCall(nativeFunction, it)
                decodedResult?.let {decodedResultArray ->
                    if (decodedResultArray.size != function.outputs.size) {
                        callback.invoke(PocketError("Invalid result: $result"), null)
                    } else {
                        callback.invoke(null, decodedResultArray)
                    }
                    return@call
                }
            }

            callback.invoke(PocketError("Error decoding result: $result"), null)
        }
    }

    /**
     * Executes an Eth function.
     *
     * @param functionName function name.
     * @param functionParams params to be sent.
     * @param nonce Integer of a nonce. This allows to overwrite your own pending transactions that use the same nonce.
     * @param gas Integer of the gas provided for the transaction execution.
     * @param gasPrice Integer of the gasPrice used for each paid gas.
     * @param value Integer of the value sent with this transaction.
     * @param callback listener for execute constant function.
     */
    @Throws(EthContractException::class)
    fun executeFunction(
        functionName: String,
        wallet: Wallet,
        functionParams: List<Any>?,
        nonce: BigInteger?,
        gas: BigInteger,
        gasPrice: BigInteger,
        value: BigInteger,
        callback: StringCallback
    ) {
        val function = this.functions[functionName] ?: throw EthContractException("Invalid function name")

        var functionParams = when(functionParams) {
            null -> ArrayList()
            else -> functionParams
        }

        val data = function.getEncodedFunctionCall(functionParams)
        if (nonce != null) {
            this.ethNetwork.eth.sendTransaction(wallet, this.contractAddress, gas, gasPrice, value, data, nonce, callback)
        } else {
            this.ethNetwork.eth.getTransactionCount(wallet.address, null) { txCountError, result ->
                txCountError?.let {
                    callback.invoke(txCountError, null)
                    return@getTransactionCount
                }

                result?.let {
                    this.ethNetwork.eth.sendTransaction(wallet, this.contractAddress, gas, gasPrice, value, data, it, callback)
                    return@getTransactionCount
                }

                callback.invoke(PocketError("Unknown error fetching transaction account"), null)
            }
        }
    }

    // Private interface
    @Throws(JSONException::class)
    private fun parseContractFunctions() {
        for (i in 0 until this.abiDefinition!!.length()) {
            val abiElement = this.abiDefinition.optJSONObject(i)
            val function = Function.parseFunctionElement(abiElement)
            if (function != null) {
                functions[function.name] = function
            }
        }
    }
}
