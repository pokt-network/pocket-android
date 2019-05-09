package network.pocket.eth.rpc

import network.pocket.eth.models.EthRelay
import network.pocket.eth.network.EthNetwork
import network.pocket.eth.rpc.callbacks.*
import network.pocket.eth.rpc.types.BlockTag
import network.pocket.eth.util.HexStringUtil
import network.pocket.core.errors.PocketError
import network.pocket.core.model.Wallet
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

/**
 * Ethereum RPC
 *
 * @param ethNetwork Eth operation executor.
 *
 * @see EthNetwork
 */
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

    /**
     * Returns the current ethereum protocol version.
     *
     * @param callback listener for the protocol version.
     */
    fun protocolVersion(callback: StringCallback) {
        val relay = EthRelay(this.ethNetwork.netID, this.ethNetwork.devID, EthRpc.EthRpcMethod.eth_protocolVersion.name, null)
        this.ethNetwork.sendWithStringResult(relay, callback)
    }

    /**
     * Returns an object with data about the sync status or false.
     *
     * @param callback listener for the syncing status.
     */
    fun syncing(callback: JSONObjectOrBooleanCallback) {
        val relay = EthRelay(this.ethNetwork.netID, this.ethNetwork.devID, EthRpc.EthRpcMethod.eth_syncing.name, null)
        this.ethNetwork.sendWithJSONObjectOrBooleanResult(relay, callback)
    }

    /**
     * Returns the current price per gas in wei.
     *
     * @param callback listener for the price.
     */
    fun gasPrice(callback: BigIntegerCallback) {
        val relay = EthRelay(this.ethNetwork.netID, this.ethNetwork.devID, EthRpc.EthRpcMethod.eth_gasPrice.name, null)
        this.ethNetwork.sendWithBigIntegerResult(relay, callback)
    }

    /**
     * Returns the number of most recent block.
     *
     * @param callback listener for the most recent block
     */
    fun blockNumber(callback: BigIntegerCallback) {
        val relay = EthRelay(this.ethNetwork.netID, this.ethNetwork.devID, EthRpc.EthRpcMethod.eth_blockNumber.name, null)
        this.ethNetwork.sendWithBigIntegerResult(relay, callback)
    }

    /**
     * Returns the balance of the account of given address.
     *
     * @see BlockTag
     *
     * @param address address to check for balance.
     * @param blockTag integer block number, or the string "latest", "earliest" or "pending".
     * @param callback listener for balance from address.
     */
    fun getBalance(address: String, blockTag: BlockTag?, callback: BigIntegerCallback) {
        this.executeGenericQuantityRelay(EthRpcMethod.eth_getBalance, address, blockTag, callback)
    }

    /**
     * Returns the value from a storage position at a given address.
     *
     * @see BlockTag
     *
     * @param address address to check for balance.
     * @param storagePosition integer of the position in the storage.
     * @param blockTag integer block number, or the string "latest", "earliest" or "pending".
     * @param callback listener for storage at from address.
     */
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

    /**
     * Returns the number of transactions sent from an address.
     *
     * @see BlockTag
     *
     * @param address address.
     * @param blockTag integer block number, or the string "latest", "earliest" or "pending".
     */
    fun getTransactionCount(address: String, blockTag: BlockTag?, callback: BigIntegerCallback) {
        this.executeGenericQuantityRelay(
            EthRpcMethod.eth_getTransactionCount, address, blockTag, callback
        )
    }

    /**
     * Returns the number of transactions in a block from a block matching the given block hash.
     *
     * @param blockHashHex hash of a block.
     * @param callback listener for the transaction count By Hash.
     */
    fun getBlockTransactionCountByHash(blockHashHex: String, callback: BigIntegerCallback) {
        this.executeGenericCountByBlockHash(EthRpcMethod.eth_getBlockTransactionCountByHash, blockHashHex, callback)
    }

    /**
     * Returns the number of transactions in a block matching the given block number.
     *
     * @see BlockTag
     *
     * @param blockTag integer of a block number, or the string "earliest", "latest" or "pending".
     * @param callback listener for the transaction count By Number.
     */
    fun getBlockTransactionCountByNumber(blockTag: BlockTag, callback: BigIntegerCallback) {
        this.executeGenericCountForBlockTagQuery(blockTag, EthRpcMethod.eth_getBlockTransactionCountByNumber, callback)
    }

    /**
     * Returns code at a given address.
     *
     * @see BlockTag
     *
     * @param address address.
     * @param blockTag integer block number, or the string "latest", "earliest" or "pending".
     * @param callback listener to get the code.
     */
    fun getCode(address: String, blockTag: BlockTag?, callback: StringCallback) {
        var blockTag = blockTag
        blockTag = BlockTag.tagOrLatest(blockTag)
        val rpcParams = ArrayList<Any>()
        rpcParams.add(address)
        rpcParams.add(blockTag.blockTagString)
        val relay = EthRelay(this.ethNetwork.netID, this.ethNetwork.devID, EthRpc.EthRpcMethod.eth_getCode.name, JSONArray(rpcParams))
        this.ethNetwork.sendWithStringResult(relay, callback)
    }

    /**
     * Creates new message call transaction or a contract creation, if the data field contains code.
     *
     * @see Wallet
     *
     * @param wallet the wallet to be used in this transaction.
     * @param toAddress The address the transaction is directed to.
     * @param gas Integer of the gas provided for the transaction execution. It will return unused gas.
     * @param gasPrice Integer of the gasPrice used for each paid gas.
     * @param value Integer of the value sent with this transaction.
     * @param data The compiled code of a contract OR the hash of the invoked method signature and encoded parameters.
     * @param nonce Integer of a nonce. This allows to overwrite your own pending transactions that use the same nonce.
     * @param callback listener for this transaction status.
     */
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

    /**
     * Executes a new message call immediately without creating a transaction on the block chain.
     *
     * @param toAddress The address the transaction is directed to.
     * @param blockTag integer block number, or the string "latest", "earliest" or "pending".
     * @param fromAddress The address the transaction is sent from.
     * @param gas Integer of the gas provided for the transaction execution.
     * @param gasPrice Integer of the gasPrice used for each paid gas.
     * @param value Integer of the value sent with this transaction.
     * @param data Hash of the method signature and encoded parameters.
     * @param callback listener for this call status.
     */
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

    /**
     * Generates and returns an estimate of how much gas is necessary to allow the transaction to complete.
     * The transaction will not be added to the blockchain. Note that the estimate may be significantly more
     * than the amount of gas actually used by the transaction, for a variety of reasons including EVM mechanics and node performance.
     *
     * @param toAddress The address the transaction is directed to.
     * @param blockTag integer block number, or the string "latest", "earliest" or "pending".
     * @param fromAddress The address the transaction is sent from.
     * @param gas Integer of the gas provided for the transaction execution.
     * @param gasPrice Integer of the gasPrice used for each paid gas.
     * @param value Integer of the value sent with this transaction
     * @param data Hash of the method signature and encoded parameters.
     * @param callback listener for the estimated gas call.
     */
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

    /**
     * Returns information about a block by hash.
     *
     * @param blockHashHex Hash of a block.
     * @param fullTx Full transaction objects.
     * @param callback listener to get the Block By Hash.
     */
    fun getBlockByHash(blockHashHex: String, fullTx: Boolean, callback: JSONObjectCallback) {
        this.executeGetBlockByHex(
            EthRpcMethod.eth_getBlockByHash, blockHashHex, fullTx, callback
        )
    }

    /**
     * Returns information about a block by block number.
     *
     * @param blockTag integer block number, or the string "latest", "earliest" or "pending".
     * @param fullTx Full transaction objects.
     * @param callback listener to get the Block By Number.
     */
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

    /**
     * Returns the information about a transaction requested by transaction hash.
     *
     * @param txHashHex hash of a transaction.
     * @param callback listener to get transaction by Hash.
     */
    fun getTransactionByHash(txHashHex: String, callback: JSONObjectCallback) {
        this.executeGetObjectWithHex(EthRpcMethod.eth_getTransactionByHash, txHashHex, callback)
    }

    /**
     * Returns information about a transaction by block hash and transaction index position.
     *
     * @param blockHashHex hash of a block.
     * @param txIndex integer of the transaction index position.
     * @param callback listener to get the transaction Block Hash Index.
     */
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

    /**
     * Returns information about a transaction by block number and transaction index position.
     *
     * @param blockTag a block number, or the string "earliest", "latest" or "pending".
     * @param txIndex the transaction index position.
     * @param callback listener to get the transaction by block number at index.
     *
     */
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

    /**
     * Returns the receipt of a transaction by transaction hash.
     *
     * @param txHashHex hash of a transaction.
     * @param callback listener to get the transaction receipt.
     */
    fun getTransactionReceipt(
        txHashHex: String,
        callback: JSONObjectCallback
    ) {
        this.executeGetObjectWithHex(EthRpcMethod.eth_getTransactionReceipt, txHashHex, callback)
    }

    /**
     * Returns an array of all logs matching a given filter object.
     *
     * @param fromBlock Integer block number, or "latest" for the last mined block or "pending", "earliest" for not yet mined transactions.
     * @param toBlock Integer block number, or "latest" for the last mined block or "pending", "earliest" for not yet mined transactions.
     * @param addressList Contract address or a list of addresses from which logs should originate.
     * @param topics Topics are order-dependent. Each topic can also be an array of DATA with "or" options.
     * @param blockHashHex is a new filter option which restricts the logs returned to the single block with the 32-byte hash blockHash.
     * @param callback listener to get logs.
     *
     */
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
        var blockTag = BlockTag.tagOrLatest(blockTag)
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
