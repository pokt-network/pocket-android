package network.pokt.eth.rpc

import network.pokt.eth.models.EthRelay
import network.pokt.eth.network.EthNetwork
import network.pokt.eth.rpc.callbacks.*
import network.pokt.eth.rpc.types.BlockTag
import network.pokt.eth.util.HexStringUtil
import network.pokt.core.errors.PocketError
import network.pokt.core.model.Wallet
import org.json.JSONArray
import org.json.JSONObject
import org.web3j.crypto.Credentials
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.utils.Numeric
import java.math.BigInteger
import java.util.ArrayList
import java.util.HashMap

class EthRpc
    (private val ethNetwork: EthNetwork) {
    private enum class EthRpcMethod {
        eth_protocolVersion,
        eth_syncing,
        eth_gasPrice,
        eth_blockNumber,
        eth_getBalance,
        eth_getStorageAt,
        eth_getTransactionCount,
        eth_getBlockTransactionCountByHash,
        eth_getBlockTransactionCountByNumber,
        eth_getCode,
        eth_sendRawTransaction,
        eth_call,
        eth_estimateGas,
        eth_getBlockByHash,
        eth_getBlockByNumber,
        eth_getTransactionByHash,
        eth_getTransactionByBlockHashAndIndex,
        eth_getTransactionByBlockNumberAndIndex,
        eth_getTransactionReceipt,
        eth_getLogs
    }

    fun protocolVersion(callback: StringCallback) {
        val relay = EthRelay(this.ethNetwork.netID, this.ethNetwork.devID, EthRpc.EthRpcMethod.eth_protocolVersion.name, null)
        this.ethNetwork.sendWithStringResult(relay, callback)
    }

    fun syncing(callback: JSONObjectOrBooleanCallback) {
        val relay = EthRelay(this.ethNetwork.netID, this.ethNetwork.devID, EthRpc.EthRpcMethod.eth_syncing.name, null)
        this.ethNetwork.sendWithJSONObjectOrBooleanResult(relay, callback)
    }

    fun gasPrice(callback: BigIntegerCallback) {
        val relay = EthRelay(this.ethNetwork.netID, this.ethNetwork.devID, EthRpc.EthRpcMethod.eth_gasPrice.name, null)
        this.ethNetwork.sendWithBigIntegerResult(relay, callback)
    }

    fun blockNumber(callback: BigIntegerCallback) {
        val relay = EthRelay(this.ethNetwork.netID, this.ethNetwork.devID, EthRpc.EthRpcMethod.eth_blockNumber.name, null)
        this.ethNetwork.sendWithBigIntegerResult(relay, callback)
    }

    fun getBalance(address: String, blockTag: BlockTag?, callback: BigIntegerCallback) {
        this.executeGenericQuantityRelay(EthRpcMethod.eth_getBalance, address, blockTag, callback)
    }

    fun getStorageAt(address: String, storagePosition: BigInteger, blockTag: BlockTag?, callback: StringCallback) {
        var blockTag = blockTag
        blockTag = BlockTag.tagOrLatest(blockTag)
        val rpcParams = ArrayList<Any>()
        rpcParams.add(address)
        rpcParams.add(HexStringUtil.prependZeroX(storagePosition.toString(16)))
        rpcParams.add(blockTag.blockTagString)
        val relay = EthRelay(this.ethNetwork.netID, this.ethNetwork.devID, EthRpc.EthRpcMethod.eth_blockNumber.name, JSONArray(rpcParams))
        this.ethNetwork.sendWithStringResult(relay, callback)
    }

    fun getTransactionCount(address: String, blockTag: BlockTag?, callback: BigIntegerCallback) {
        this.executeGenericQuantityRelay(
            EthRpcMethod.eth_getTransactionCount, address, blockTag, callback
        )
    }

    fun getBlockTransactionCountByHash(blockHashHex: String, callback: BigIntegerCallback) {
        this.executeGenericCountByBlockHash(EthRpcMethod.eth_getBlockTransactionCountByHash, blockHashHex, callback)
    }

    fun getBlockTransactionCountByNumber(blockTag: BlockTag, callback: BigIntegerCallback) {
        this.executeGenericCountForBlockTagQuery(blockTag, EthRpcMethod.eth_getBlockTransactionCountByNumber, callback)
    }

    fun getCode(address: String, blockTag: BlockTag?, callback: StringCallback) {
        var blockTag = blockTag
        blockTag = BlockTag.tagOrLatest(blockTag)
        val rpcParams = ArrayList<Any>()
        rpcParams.add(address)
        rpcParams.add(blockTag.blockTagString)
        val relay = EthRelay(this.ethNetwork.netID, this.ethNetwork.devID, EthRpc.EthRpcMethod.eth_getCode.name, JSONArray(rpcParams))
        this.ethNetwork.sendWithStringResult(relay, callback)
    }

    @Throws(PocketError::class)
    fun sendTransaction(
        wallet: Wallet,
        toAddress: String,
        gas: BigInteger,
        gasPrice: BigInteger,
        value: BigInteger,
        data: String?,
        nonce: BigInteger,
        callback: StringCallback
    ) {
        if (!wallet.netID.equals(this.ethNetwork.netID, ignoreCase = true)) {
            throw PocketError( "Invalid wallet netID: ${wallet.netID}")
        }

        val dataValue = when(data) {
            null -> ""
            else -> data
        }
        val rawTx = RawTransaction.createTransaction(nonce, gasPrice, gas, toAddress, value, dataValue)

        val signedTxBytes = TransactionEncoder.signMessage(rawTx, Credentials.create(ECKeyPair.create(Numeric.hexStringToByteArray(wallet.privateKey))))
        var rawTransaction = Numeric.toHexString(signedTxBytes)
        var rpcParams = JSONArray()
        rpcParams.put(rawTransaction)
        val relay = EthRelay(this.ethNetwork.netID, this.ethNetwork.devID, EthRpc.EthRpcMethod.eth_sendRawTransaction.name, rpcParams)
        this.ethNetwork.sendWithStringResult(relay, callback)
    }

    fun call(
        toAddress: String, blockTag: BlockTag?,
        fromAddress: String?, gas: BigInteger?, gasPrice: BigInteger?, value: BigInteger?,
        data: String, callback: StringCallback
    ) {
        val relay = this.callOrEstimateGasQuery(
            EthRpcMethod.eth_call, toAddress, blockTag,
            fromAddress, gas, gasPrice, value, data
        )
        this.ethNetwork.sendWithStringResult(relay, callback)
    }

    fun estimateGas(
        toAddress: String,
        blockTag: BlockTag?,
        fromAddress: String?,
        gas: BigInteger?,
        gasPrice: BigInteger?,
        value: BigInteger?,
        data: String,
        callback: BigIntegerCallback
    ) {
        val relay = this.callOrEstimateGasQuery(
            EthRpcMethod.eth_estimateGas, toAddress,
            blockTag, fromAddress, gas, gasPrice, value, data
        )
        this.ethNetwork.sendWithBigIntegerResult(relay, callback)
    }

    fun getBlockByHash(blockHashHex: String, fullTx: Boolean, callback: JSONObjectCallback) {
        this.executeGetBlockByHex(
            EthRpcMethod.eth_getBlockByHash, blockHashHex, fullTx, callback
        )
    }

    fun getBlockByNumber(blockTag: BlockTag?, fullTx: Boolean, callback: JSONObjectCallback) {
        var blockTag = blockTag
        blockTag = BlockTag.tagOrLatest(blockTag)
        this.executeGetBlockByHex(
            EthRpcMethod.eth_getBlockByNumber,
            blockTag.blockTagString,
            fullTx,
            callback
        )
    }

    fun getTransactionByHash(txHashHex: String, callback: JSONObjectCallback) {
        this.executeGetObjectWithHex(EthRpcMethod.eth_getTransactionByHash, txHashHex, callback)
    }

    fun getTransactionByBlockHashAndIndex(
        blockHashHex: String,
        txIndex: BigInteger,
        callback: JSONObjectCallback
    ) {
        this.executeGetObjectWithHexPair(
            EthRpcMethod.eth_getTransactionByBlockHashAndIndex,
            blockHashHex, HexStringUtil.prependZeroX(txIndex.toString(16)), callback
        )
    }

    fun getTransactionByBlockNumberAndIndex(
        blockTag: BlockTag,
        txIndex: BigInteger,
        callback: JSONObjectCallback
    ) {
        var blockTag = blockTag

        blockTag = BlockTag.tagOrLatest(blockTag)
        this.executeGetObjectWithHexPair(
            EthRpcMethod.eth_getTransactionByBlockNumberAndIndex,
            blockTag.blockTagString, HexStringUtil.prependZeroX(txIndex.toString(16)), callback
        )
    }

    fun getTransactionReceipt(
        txHashHex: String,
        callback: JSONObjectCallback
    ) {
        this.executeGetObjectWithHex(EthRpcMethod.eth_getTransactionReceipt, txHashHex, callback)
    }

    fun getLogs(
        fromBlock: BlockTag?, toBlock: BlockTag?,
        addressList: List<String>?, topics: List<String>?, blockHashHex: String?,
        callback: JSONArrayCallback
    ) {
        val queryParams = HashMap<String, Any>()

        if (addressList != null) {
            queryParams["address"] = addressList
        }

        if (topics != null) {
            queryParams["topics"] = topics
        }

        if (blockHashHex != null) {
            queryParams["blockhash"] = blockHashHex
        } else {
            queryParams["fromBlock"] = BlockTag.tagOrLatest(fromBlock)
            queryParams["toBlock"] = BlockTag.tagOrLatest(toBlock)
        }

        val rpcParams = ArrayList<Any>()
        rpcParams.add(queryParams)
        val relay = EthRelay(this.ethNetwork.netID, this.ethNetwork.devID, EthRpcMethod.eth_getLogs.name, JSONArray(rpcParams))
        this.ethNetwork.sendWithJSONArrayResult(relay, callback)
    }

    // Private interfaces
    private fun executeGenericQuantityRelay(rpcMethod: EthRpcMethod, address: String, blockTag: BlockTag?, callback: BigIntegerCallback) {
        var blockTag = blockTag
        blockTag = BlockTag.tagOrLatest(blockTag)
        val rpcParams = ArrayList<Any>()
        rpcParams.add(address)
        rpcParams.add(blockTag.blockTagString)
        val relay = EthRelay(this.ethNetwork.netID, this.ethNetwork.devID, rpcMethod.name, JSONArray(rpcParams))
        this.ethNetwork.sendWithBigIntegerResult(relay, callback)
    }

    private fun executeGenericCountByBlockHash(rpcMethod: EthRpcMethod, blockHashHex: String, callback: BigIntegerCallback) {
        val rpcParams = ArrayList<Any>()
        rpcParams.add(blockHashHex)
        val relay = EthRelay(this.ethNetwork.netID, this.ethNetwork.devID, rpcMethod.name, JSONArray(rpcParams))
        this.ethNetwork.sendWithBigIntegerResult(relay, callback)
    }

    private fun executeGenericCountForBlockTagQuery(
        blockTag: BlockTag,
        rpcMethod: EthRpcMethod,
        callback: BigIntegerCallback
    ) {
        var blockTag = blockTag
        blockTag = BlockTag.tagOrLatest(blockTag)
        val rpcParams = ArrayList<Any>()
        rpcParams.add(blockTag.blockTagString)
        val relay = EthRelay(this.ethNetwork.netID, this.ethNetwork.devID, rpcMethod.name, JSONArray(rpcParams))
        this.ethNetwork.sendWithBigIntegerResult(relay, callback)
    }

    private fun callOrEstimateGasQuery(
        rpcMethod: EthRpcMethod,
        toAddress: String,
        blockTag: BlockTag?,
        fromAddress: String?,
        gas: BigInteger?,
        gasPrice: BigInteger?,
        value: BigInteger?,
        data: String?
    ): EthRelay {
        var blockTag = blockTag
        blockTag = BlockTag.tagOrLatest(blockTag)
        val callTxParams = HashMap<String, Any>()
        callTxParams["to"] = toAddress
        if (fromAddress != null) {
            callTxParams["from"] = fromAddress
        }
        if (gas != null) {
            callTxParams["gas"] = HexStringUtil.prependZeroX(gas.toString(16))
        }
        if (gasPrice != null) {
            callTxParams["gasPrice"] = HexStringUtil.prependZeroX(gasPrice.toString(16))
        }

        if (value != null) {
            callTxParams["value"] = HexStringUtil.prependZeroX(value.toString(16))
        }

        if (data != null) {
            callTxParams["data"] = data
        }
        val callTxObj = JSONObject(callTxParams)

        val rpcParams = ArrayList<Any>()
        rpcParams.add(callTxObj)
        if (rpcMethod == EthRpc.EthRpcMethod.eth_call) {
            rpcParams.add(blockTag.blockTagString)
        }

        return EthRelay(this.ethNetwork.netID, this.ethNetwork.devID, rpcMethod.name, JSONArray(rpcParams))
    }

    private fun executeGetBlockByHex(
        rpcMethod: EthRpcMethod, blockIdHex: String, fullTx: Boolean, callback: JSONObjectCallback
    ) {
        val rpcParams = ArrayList<Any>()
        rpcParams.add(blockIdHex)
        rpcParams.add(fullTx)
        val relay = EthRelay(this.ethNetwork.netID, this.ethNetwork.devID, rpcMethod.name, JSONArray(rpcParams))
        this.ethNetwork.sendWithJSONObjectResult(relay, callback)
    }

    private fun executeGetObjectWithHex(
        rpcMethod: EthRpcMethod, hex: String, callback: JSONObjectCallback
    ) {
        val rpcParams = ArrayList<Any>()
        rpcParams.add(hex)
        val relay = EthRelay(this.ethNetwork.netID, this.ethNetwork.devID, rpcMethod.name, JSONArray(rpcParams))
        this.ethNetwork.sendWithJSONObjectResult(relay, callback)
    }

    private fun executeGetObjectWithHexPair(
        rpcMethod: EthRpcMethod,
        hexPrimary: String,
        hexSecondary: String,
        callback: JSONObjectCallback
    ) {
        val rpcParams = ArrayList<Any>()
        rpcParams.add(hexPrimary)
        rpcParams.add(hexSecondary)
        val relay = EthRelay(this.ethNetwork.netID, this.ethNetwork.devID, rpcMethod.name, JSONArray(rpcParams))
        this.ethNetwork.sendWithJSONObjectResult(relay, callback)
    }
}
