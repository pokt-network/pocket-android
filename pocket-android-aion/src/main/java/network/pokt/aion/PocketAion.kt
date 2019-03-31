package network.pokt.aion

import android.content.Context
import network.pokt.aion.network.AionNetwork
import network.pokt.core.Pocket
import network.pokt.core.model.Wallet
import network.pokt.aion.operations.CreateWalletOperation
import network.pokt.aion.operations.ImportWalletOperation
import network.pokt.core.errors.PocketError
import org.json.JSONObject

class PocketAion @Throws constructor(
    val context: Context,
    devId: String,
    netIds: List<String>,
    maxNodes: Int = 5,
    requestTimeOut: Int = 1000,
    private val defaultNetID: String = Networks.MASTERY.netID
) : Pocket(devId, "AION", netIds.toTypedArray(), maxNodes, requestTimeOut) {

    enum class Networks(val netID: String) {
        MAINNET("256"),
        MASTERY("32")
    }

    companion object {
        val NETWORK = "AION"
    }
    var mainnet: AionNetwork? = null
    var mastery: AionNetwork? = null
    var default: AionNetwork
    private var networks: MutableMap<String, AionNetwork> = HashMap()

    init {
        if (netIds.isEmpty()) {
            throw PocketError("netIds cannot be empty")
        }
        var defaultNetwork: AionNetwork? = null
        netIds.forEach { netID ->
            val network = this.network(netID)
            if (netID.contentEquals(defaultNetID)) {
                defaultNetwork = network
            }
        }
        this.default = defaultNetwork ?: this.network(defaultNetID)
    }

    fun network(netID: String) : AionNetwork {
        val result: AionNetwork = when {
            this.networks.containsKey(netID) -> this.networks[netID]!!
            else -> {
                val network = AionNetwork(netID, this)
                this.networks[netID] = network
                this.addBlockchain(NETWORK, netID)
                network
            }
        }

        if (netID.contentEquals(Networks.MAINNET.netID)) {
            this.mainnet = result
        } else if (netID.contentEquals(Networks.MASTERY.netID)) {
            this.mastery = result
        }

        return result
    }

    @Throws
    override fun importWallet(
        privateKey: String,
        address: String?,
        network: String,
        netID: String,
        data: JSONObject?
    ): Wallet {
        val result: Wallet
        val importWalletOperation = ImportWalletOperation(this.context, NETWORK, netID, privateKey)
        val operationSuccessful = importWalletOperation.startProcess()
        val isError = importWalletOperation.errorMsg != null
        result = when {
            operationSuccessful && !isError -> importWalletOperation.wallet
            else -> throw PocketError(importWalletOperation.errorMsg)
        }
        return result
    }

    @Throws
    override fun createWallet(network: String, netID: String, data: JSONObject?): Wallet {
        val result: Wallet
        val createWalletOperation = CreateWalletOperation(this.context, NETWORK, netID, data)
        val operationSuccessful = createWalletOperation.startProcess()
        val isError = createWalletOperation.errorMsg != null
        result = when {
            operationSuccessful && !isError -> createWalletOperation.wallet
            else -> throw PocketError(createWalletOperation.errorMsg)
        }
        return result
    }
}