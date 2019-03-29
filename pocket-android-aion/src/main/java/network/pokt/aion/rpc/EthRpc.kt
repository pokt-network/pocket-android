package network.pokt.aion.rpc

import network.pokt.aion.models.AionRelay
import network.pokt.aion.network.AionNetwork
import network.pokt.aion.operations.CreateTransactionOperation
import network.pokt.aion.rpc.callbacks.*
import network.pokt.aion.rpc.types.BlockTag
import network.pokt.aion.util.HexStringUtil
import network.pokt.core.errors.PocketError
import network.pokt.core.model.Wallet
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigInteger
import java.util.ArrayList
import java.util.HashMap

class EthRpc
    (private val aionNetwork: AionNetwork) {
    private enum class AionRpcMethod {
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
        val relay = AionRelay(this.aionNetwork.netID, this.aionNetwork.devID, EthRpc.AionRpcMethod.eth_protocolVersion.name, null)
        this.aionNetwork.sendWithStringResult(relay, callback)
    }

    fun syncing(callback: JSONObjectOrBooleanCallback) {
        val relay = AionRelay(this.aionNetwork.netID, this.aionNetwork.devID, EthRpc.AionRpcMethod.eth_syncing.name, null)
        this.aionNetwork.sendWithJSONObjectOrBooleanResult(relay, callback)
    }

    fun nrgPrice(callback: BigIntegerCallback) {
        val relay = AionRelay(this.aionNetwork.netID, this.aionNetwork.devID, EthRpc.AionRpcMethod.eth_gasPrice.name, null)
        this.aionNetwork.sendWithBigIntegerResult(relay, callback)
    }

    fun blockNumber(callback: BigIntegerCallback) {
        val relay = AionRelay(this.aionNetwork.netID, this.aionNetwork.devID, EthRpc.AionRpcMethod.eth_blockNumber.name, null)
        this.aionNetwork.sendWithBigIntegerResult(relay, callback)
    }

    fun getBalance(address: String, blockTag: BlockTag?, callback: BigIntegerCallback) {
        this.executeGenericQuantityRelay(AionRpcMethod.eth_getBalance, address, blockTag, callback)
    }

    fun getStorageAt(address: String, storagePosition: BigInteger, blockTag: BlockTag?, callback: StringCallback) {
        var blockTag = blockTag
        blockTag = BlockTag.tagOrLatest(blockTag)
        val rpcParams = ArrayList<Any>()
        rpcParams.add(address)
        rpcParams.add(HexStringUtil.prependZeroX(storagePosition.toString(16)))
        rpcParams.add(blockTag.blockTagString)
        val relay = AionRelay(this.aionNetwork.netID, this.aionNetwork.devID, EthRpc.AionRpcMethod.eth_blockNumber.name, JSONArray(rpcParams))
        this.aionNetwork.sendWithStringResult(relay, callback)
    }

    fun getTransactionCount(address: String, blockTag: BlockTag?, callback: BigIntegerCallback) {
        this.executeGenericQuantityRelay(
            AionRpcMethod.eth_getTransactionCount, address, blockTag, callback
        )
    }

    fun getBlockTransactionCountByHash(blockHashHex: String, callback: BigIntegerCallback) {
        this.executeGenericCountByBlockHash(AionRpcMethod.eth_getBlockTransactionCountByHash, blockHashHex, callback)
    }

    fun getBlockTransactionCountByNumber(blockTag: BlockTag, callback: BigIntegerCallback) {
        this.executeGenericCountForBlockTagQuery(blockTag, AionRpcMethod.eth_getBlockTransactionCountByNumber, callback)
    }

    fun getCode(address: String, blockTag: BlockTag?, callback: StringCallback) {
        var blockTag = blockTag
        blockTag = BlockTag.tagOrLatest(blockTag)
        val rpcParams = ArrayList<Any>()
        rpcParams.add(address)
        rpcParams.add(blockTag.blockTagString)
        val relay = AionRelay(this.aionNetwork.netID, this.aionNetwork.devID, EthRpc.AionRpcMethod.eth_getCode.name, JSONArray(rpcParams))
        this.aionNetwork.sendWithStringResult(relay, callback)
    }

    @Throws(PocketError::class)
    fun sendTransaction(
        wallet: Wallet,
        toAddress: String,
        nrg: BigInteger,
        nrgPrice: BigInteger,
        value: BigInteger?,
        data: String?,
        nonce: BigInteger,
        callback: StringCallback
    ) {
        if (!wallet.netID.equals(this.aionNetwork.netID, ignoreCase = true)) {
            throw PocketError( "Invalid wallet netID: ${wallet.netID}")
        }

        val nonce = HexStringUtil.prependZeroX(nonce.toString(16))
        val nrg = HexStringUtil.prependZeroX(nrg.toString(16))
        val nrgPrice = HexStringUtil.prependZeroX(nrgPrice.toString(16))
        var strValue: String? = if (value != null) {
            HexStringUtil.prependZeroX(value.toString(16))
        } else {
            null
        }

        var txOperation = CreateTransactionOperation(this.aionNetwork.getContext(), wallet, nonce,
            toAddress, strValue, data, nrg, nrgPrice)
        var operationSuccessful = txOperation.startProcess()
        if (!operationSuccessful) {
            throw PocketError(txOperation.errorMsg)
        }

        var rawTransaction = txOperation.rawTransaction
        var rpcParams = JSONArray()
        rpcParams.put(rawTransaction)

        val relay = AionRelay(this.aionNetwork.netID, this.aionNetwork.devID, EthRpc.AionRpcMethod.eth_sendRawTransaction.name, rpcParams)
        this.aionNetwork.sendWithStringResult(relay, callback)
    }

    fun call(
        toAddress: String, blockTag: BlockTag?,
        fromAddress: String?, nrg: BigInteger?, nrgPrice: BigInteger?, value: BigInteger?,
        data: String, callback: StringCallback
    ) {
        val relay = this.callOrEstimateGasQuery(
            AionRpcMethod.eth_call, toAddress, blockTag,
            fromAddress, nrg, nrgPrice, value, data
        )
        this.aionNetwork.sendWithStringResult(relay, callback)
    }

    fun estimateGas(
        toAddress: String,
        blockTag: BlockTag?,
        fromAddress: String?,
        nrg: BigInteger?,
        nrgPrice: BigInteger?,
        value: BigInteger?,
        data: String,
        callback: BigIntegerCallback
    ) {
        val relay = this.callOrEstimateGasQuery(
            AionRpcMethod.eth_estimateGas, toAddress,
            blockTag, fromAddress, nrg, nrgPrice, value, data
        )
        this.aionNetwork.sendWithBigIntegerResult(relay, callback)
    }

    fun getBlockByHash(blockHashHex: String, fullTx: Boolean, callback: JSONObjectCallback) {
        this.executeGetBlockByHex(
            AionRpcMethod.eth_getBlockByHash, blockHashHex, fullTx, callback
        )
    }

    fun getBlockByNumber(blockTag: BlockTag?, fullTx: Boolean, callback: JSONObjectCallback) {
        var blockTag = blockTag
        blockTag = BlockTag.tagOrLatest(blockTag)
        this.executeGetBlockByHex(
            AionRpcMethod.eth_getBlockByNumber,
            blockTag.blockTagString,
            fullTx,
            callback
        )
    }

    fun getTransactionByHash(txHashHex: String, callback: JSONObjectCallback) {
        this.executeGetObjectWithHex(AionRpcMethod.eth_getTransactionByHash, txHashHex, callback)
    }

    fun getTransactionByBlockHashAndIndex(
        blockHashHex: String,
        txIndex: BigInteger,
        callback: JSONObjectCallback
    ) {
        this.executeGetObjectWithHexPair(
            AionRpcMethod.eth_getTransactionByBlockHashAndIndex,
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
            AionRpcMethod.eth_getTransactionByBlockNumberAndIndex,
            blockTag.blockTagString, HexStringUtil.prependZeroX(txIndex.toString(16)), callback
        )
    }

    fun getTransactionReceipt(
        txHashHex: String,
        callback: JSONObjectCallback
    ) {
        this.executeGetObjectWithHex(AionRpcMethod.eth_getTransactionReceipt, txHashHex, callback)
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
        val relay = AionRelay(this.aionNetwork.netID, this.aionNetwork.devID, AionRpcMethod.eth_getLogs.name, JSONArray(rpcParams))
        this.aionNetwork.sendWithJSONArrayResult(relay, callback)
    }

    // Private interfaces
    private fun executeGenericQuantityRelay(rpcMethod: AionRpcMethod, address: String, blockTag: BlockTag?, callback: BigIntegerCallback) {
        var blockTag = blockTag
        blockTag = BlockTag.tagOrLatest(blockTag)
        val rpcParams = ArrayList<Any>()
        rpcParams.add(address)
        rpcParams.add(blockTag.blockTagString)
        val relay = AionRelay(this.aionNetwork.netID, this.aionNetwork.devID, rpcMethod.name, JSONArray(rpcParams))
        this.aionNetwork.sendWithBigIntegerResult(relay, callback)
    }

    private fun executeGenericCountByBlockHash(rpcMethod: AionRpcMethod, blockHashHex: String, callback: BigIntegerCallback) {
        val rpcParams = ArrayList<Any>()
        rpcParams.add(blockHashHex)
        val relay = AionRelay(this.aionNetwork.netID, this.aionNetwork.devID, rpcMethod.name, JSONArray(rpcParams))
        this.aionNetwork.sendWithBigIntegerResult(relay, callback)
    }

    private fun executeGenericCountForBlockTagQuery(
        blockTag: BlockTag,
        rpcMethod: AionRpcMethod,
        callback: BigIntegerCallback
    ) {
        var blockTag = blockTag
        blockTag = BlockTag.tagOrLatest(blockTag)
        val rpcParams = ArrayList<Any>()
        rpcParams.add(blockTag.blockTagString)
        val relay = AionRelay(this.aionNetwork.netID, this.aionNetwork.devID, rpcMethod.name, JSONArray(rpcParams))
        this.aionNetwork.sendWithBigIntegerResult(relay, callback)
    }

    private fun callOrEstimateGasQuery(
        rpcMethod: AionRpcMethod,
        toAddress: String,
        blockTag: BlockTag?,
        fromAddress: String?,
        nrg: BigInteger?,
        nrgPrice: BigInteger?,
        value: BigInteger?,
        data: String?
    ): AionRelay {
        var blockTag = blockTag
        blockTag = BlockTag.tagOrLatest(blockTag)
        val callTxParams = HashMap<String, Any>()
        callTxParams["to"] = toAddress
        if (fromAddress != null) {
            callTxParams["from"] = fromAddress
        }
        if (nrg != null) {
            callTxParams["nrg"] = HexStringUtil.prependZeroX(nrg.toString(16))
        }
        if (nrgPrice != null) {
            callTxParams["nrgPrice"] = HexStringUtil.prependZeroX(nrgPrice.toString(16))
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
        rpcParams.add(blockTag.blockTagString)

        return AionRelay(this.aionNetwork.netID, this.aionNetwork.devID, rpcMethod.name, JSONArray(rpcParams))
    }

    private fun executeGetBlockByHex(
        rpcMethod: AionRpcMethod, blockIdHex: String, fullTx: Boolean, callback: JSONObjectCallback
    ) {
        val rpcParams = ArrayList<Any>()
        rpcParams.add(blockIdHex)
        rpcParams.add(fullTx)
        val relay = AionRelay(this.aionNetwork.netID, this.aionNetwork.devID, rpcMethod.name, JSONArray(rpcParams))
        this.aionNetwork.sendWithJSONObjectResult(relay, callback)
    }

    private fun executeGetObjectWithHex(
        rpcMethod: AionRpcMethod, hex: String, callback: JSONObjectCallback
    ) {
        val rpcParams = ArrayList<Any>()
        rpcParams.add(hex)
        val relay = AionRelay(this.aionNetwork.netID, this.aionNetwork.devID, rpcMethod.name, JSONArray(rpcParams))
        this.aionNetwork.sendWithJSONObjectResult(relay, callback)
    }

    private fun executeGetObjectWithHexPair(
        rpcMethod: AionRpcMethod,
        hexPrimary: String,
        hexSecondary: String,
        callback: JSONObjectCallback
    ) {
        val rpcParams = ArrayList<Any>()
        rpcParams.add(hexPrimary)
        rpcParams.add(hexSecondary)
        val relay = AionRelay(this.aionNetwork.netID, this.aionNetwork.devID, rpcMethod.name, JSONArray(rpcParams))
        this.aionNetwork.sendWithJSONObjectResult(relay, callback)
    }
}
