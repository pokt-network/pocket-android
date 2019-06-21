package network.pocket.aion

import android.os.Build
import android.support.annotation.RequiresApi
import org.json.JSONException

import java.math.BigInteger
import java.util.HashMap

import network.pocket.aion.exceptions.AionContractException
import network.pocket.aion.network.AionNetwork
import network.pocket.aion.rpc.callbacks.AnyCallback
import network.pocket.aion.rpc.callbacks.StringCallback
import network.pocket.core.errors.PocketError
import network.pocket.core.model.Wallet
import network.pocket.aion.abi.v2.AVMFunction as AVMFunction

/**
 * AionAVM contract class
 */

@RequiresApi(Build.VERSION_CODES.N)
class AionAVMContract
@Throws(JSONException::class)
constructor(
    val aionNetwork: AionNetwork,
    private val contractAddress: String,
    private val abiDefinition: String
) {
    private val functions = HashMap<String, AVMFunction>()

    init {
        this.parseContractFunctions()
    }

    /**
     * Executes an AionAVM function.
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
        callback: AnyCallback
    ) {
        val function = this.functions[functionName]
            ?: throw AionContractException("Invalid function name or function is not constant")

        val parsedFunctionParams = when (functionParams) {
            null -> ArrayList()
            else -> functionParams
        }

        val data: String = function.getEncodedFunctionCall(parsedFunctionParams)
            ?: throw AionContractException("Error generating function call data")

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
                val decodedResult = this.decodeCallData(function, it)
                decodedResult?.let { d ->
                    callback.invoke(null, d)
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

        var functionParams = when (functionParams) {
            null -> ArrayList()
            else -> functionParams
        }

        val data = function.getEncodedFunctionCall(functionParams)
        if (nonce != null) {
            this.aionNetwork.eth.sendTransaction(
                wallet,
                this.contractAddress,
                nrg,
                nrgPrice,
                value,
                data,
                nonce,
                callback
            )
        } else {
            this.aionNetwork.eth.getTransactionCount(wallet.address, null) { txCountError, result ->
                txCountError?.let {
                    callback.invoke(txCountError, null)
                    return@getTransactionCount
                }

                result?.let {
                    this.aionNetwork.eth.sendTransaction(
                        wallet,
                        this.contractAddress,
                        nrg,
                        nrgPrice,
                        value,
                        data,
                        it,
                        callback
                    )
                    return@getTransactionCount
                }

                callback.invoke(PocketError("Unknown error fetching transaction account"), null)
            }
        }
    }

    // Private interface
    private fun parseContractFunctions() {
        val lines = this.abiDefinition.lines()
        lines.forEach {
            val function = AVMFunction.functionParser(it)
            if (function != null) {
                functions[function.name] = function
            }
        }
    }


    @Throws(AionContractException::class)
    private fun decodeCallData(function: AVMFunction, data: String): Any? {
        return function.decodeAVMFunctionCall(data)
    }
}
