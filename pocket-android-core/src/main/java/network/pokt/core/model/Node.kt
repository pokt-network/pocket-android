package network.pokt.core.model

class Node(network: String, netId: String, ipPort: String) {

    var network = network
    var netId = netId
    lateinit var ip: String
    var port: Int? = null
    var ipPort = ipPort

    private val nonSSLProtocol = "http://"
    private val SSLProtocol = "https://"

    init {
        if (!this.ipPort.contains(nonSSLProtocol) || !this.ipPort.contains(SSLProtocol)) {
            this.ipPort = "$nonSSLProtocol$ipPort"
        }
    }

    constructor(network: String, netId: String, ip: String, port: Int, ipPort: String) : this(network, netId, ipPort) {
        this.network = network
        this.netId = netId
        this.ip = ip
        this.port = port
        this.ipPort = ipPort
    }

    fun isEqual(netId: String, network: String): Boolean {
        if (this.netId == netId && this.network == network) {
            return true
        }

        return false
    }
}


