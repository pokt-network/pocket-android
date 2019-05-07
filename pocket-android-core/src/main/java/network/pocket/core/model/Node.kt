package network.pocket.core.model

/**
 * A Model Class that represents a Node.
 *
 * @property network the blockchain network name, ie: ETH, AION.
 * @property netId the netId of the blockchain.
 * @property ipPort Ip url for this Node.
 * @constructor Creates a Node Object.
 */
class Node(network: String, netId: String, ipPort: String) {

    var network = network
    var netId = netId
    var ip: String
    var port: Int? = null
    var ipPort = ipPort

    private val nonSSLProtocol = "http://"
    private val SSLProtocol = "https://"
    private val defaultSSLPort = 443
    private val defaultHTTPPort = 80

    init {
        val ipPortData = this.ipPort.split(":")
        if (ipPortData.size == 2) {
            this.ip = ipPortData[0]
            val portStr = ipPortData[1]
            this.port = when(portStr) {
                "" -> defaultHTTPPort
                else -> portStr.toInt()
            }
        } else {
            // Default to localhost effectively making this node error out on contact
            this.ip = "localhost"
            this.port = defaultHTTPPort
        }

        if (!this.ipPort.contains(nonSSLProtocol) || !this.ipPort.contains(SSLProtocol)) {
            if (this.port == defaultSSLPort) {
                this.ipPort = "$SSLProtocol$ipPort"
            } else {
                this.ipPort = "$nonSSLProtocol$ipPort"
            }

        }
    }

    constructor(network: String, netId: String, ip: String, port: Int, ipPort: String) : this(network, netId, ipPort) {
        this.network = network
        this.netId = netId
        this.ip = ip
        this.port = port
        this.ipPort = ipPort

        if (!this.ipPort.contains(nonSSLProtocol) || !this.ipPort.contains(SSLProtocol)) {
            if (this.port == defaultSSLPort) {
                this.ipPort = "$SSLProtocol$ipPort"
            } else {
                this.ipPort = "$nonSSLProtocol$ipPort"
            }

        }
    }

    fun isEqual(netId: String, network: String): Boolean {
        if (this.netId == netId && this.network == network) {
            return true
        }

        return false
    }
}


