package network.pocket.aion

import android.content.Context
import network.pocket.aion.network.AionNetwork
import network.pocket.core.Pocket
import network.pocket.core.errors.PocketError

/**
 * Pocket Aion Plugging
 *
 * @property context App context.
 * @property devID the id used to interact with Pocket Api.
 * @property netIds @ArrayList of netid's of the Blockchain.
 * @property maxNodes maximum number of nodes to be used, default 5.
 * @property requestTimeOut timeout in ms, for every request made, default 1000 ms.
 * @property defaultNetID Network name.
 */
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
}