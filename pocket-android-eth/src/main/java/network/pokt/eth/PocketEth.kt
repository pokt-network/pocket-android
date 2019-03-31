package network.pokt.eth

import android.content.Context
import network.pokt.eth.network.EthNetwork
import network.pokt.core.Pocket
import network.pokt.core.model.Wallet
import network.pokt.core.errors.PocketError
import java.util.*
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Keys
import network.pokt.eth.util.HexStringUtil
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.json.JSONObject
import java.security.Security



class PocketEth @Throws constructor(
    val context: Context,
    devId: String,
    netIds: List<String>,
    maxNodes: Int = 5,
    requestTimeOut: Int = 1000,
    private val defaultNetID: String = Networks.RINKEBY.netID
) : Pocket(devId, "ETH", netIds.toTypedArray(), maxNodes, requestTimeOut) {

    enum class Networks(val netID: String) {
        MAINNET("1"),
        ROPSTEN("3"),
        RINKEBY("4"),
        GOERLI("5"),
        KOVAN("42")
    }

    companion object {
        val NETWORK = "ETH"
    }
    var mainnet: EthNetwork? = null
    var ropsten: EthNetwork? = null
    var rinkeby: EthNetwork? = null
    var goerli: EthNetwork? = null
    var kovan: EthNetwork? = null
    var default: EthNetwork
    private var networks: MutableMap<String, EthNetwork> = HashMap()

    init {
        if (netIds.isEmpty()) {
            throw PocketError("netIds cannot be empty")
        }
        var defaultNetwork: EthNetwork? = null
        netIds.forEach { netID ->
            val network = this.network(netID)
            if (netID.contentEquals(defaultNetID)) {
                defaultNetwork = network
            }
        }
        this.default = defaultNetwork ?: this.network(defaultNetID)
        val provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)
        // Web3j will set up the provider lazily when it's first used.
        // BC with same package name, shouldn't happen in real life.
        if (provider != null && provider.javaClass != BouncyCastleProvider::class.java) {
            // Android registers its own BC provider. As it might be outdated and might not include
            // all needed ciphers, we substitute it with a known BC bundled in the app.
            // Android's BC has its package rewritten to "com.android.org.bouncycastle" and because
            // of that it's possible to have another BC implementation loaded in VM.
            Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
            Security.insertProviderAt(BouncyCastleProvider(), 1)
        }
    }

    fun network(netID: String) : EthNetwork {
        val result: EthNetwork = when {
            this.networks.containsKey(netID) -> this.networks[netID]!!
            else -> {
                val network = EthNetwork(netID, this)
                this.networks[netID] = network
                this.addBlockchain(NETWORK, netID)
                network
            }
        }

        if (netID.contentEquals(Networks.MAINNET.netID)) {
            this.mainnet = result
        } else if (netID.contentEquals(Networks.ROPSTEN.netID)) {
            this.ropsten = result
        } else if (netID.contentEquals(Networks.RINKEBY.netID)) {
            this.rinkeby = result
        } else if (netID.contentEquals(Networks.GOERLI.netID)) {
            this.goerli = result
        } else if (netID.contentEquals(Networks.KOVAN.netID)) {
            this.kovan = result
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
        val result: Wallet?
        // Try re-creating the wallet
        val ecKeyPair: ECKeyPair
        val privateKeyBytes: ByteArray
        try {
            privateKeyBytes = HexStringUtil.hexStringToByteArray(privateKey)
            ecKeyPair = ECKeyPair.create(privateKeyBytes)
        } catch (e: Exception) {
            throw PocketError(e.message ?: "Unknown error importing wallet")
        }
        result = Wallet(ecKeyPair.privateKey.toString(16), HexStringUtil.prependZeroX(Keys.getAddress(ecKeyPair)), network, netID, data)
        return result
    }

    @Throws
    override fun createWallet(network: String, netID: String, data: JSONObject?): Wallet {
        // Try creating the wallet
        val result: Wallet
        val ecKeyPair: ECKeyPair
        try {
            ecKeyPair = Keys.createEcKeyPair()
        } catch (e: Exception) {
            throw PocketError(e.message ?: "Unknown error creating wallet")
        }

        val privateKey = ecKeyPair.privateKey.toString(16)
        val address = Keys.getAddress(ecKeyPair)

        try {
            result = this.importWallet(privateKey, address, network, netID, data)
        } catch (e: Exception) {
            throw PocketError(e.message ?: "Unknown error creating wallet")
        }
        return result
    }
}