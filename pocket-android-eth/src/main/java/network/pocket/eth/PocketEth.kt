package network.pocket.eth

import android.content.Context
import network.pocket.eth.network.EthNetwork
import network.pocket.core.Pocket
import network.pocket.core.errors.PocketError
import java.util.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

/**
 * Pocket Eth Plugging
 *
 * @property context App context.
 * @property devID the id used to interact with Pocket Api.
 * @property netIds @ArrayList of netid's of the Blockchain.
 * @property maxNodes maximum number of nodes to be used, default 5.
 * @property requestTimeOut timeout in ms, for every request made, default 1000 ms.
 * @property defaultNetID Network name.
 */

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
}