package network.pokt.pocketcore.model

class Node(network: String, netId: String, version: String, ipPort: String) {

    var network = network
    var netId = netId
    var version = version
    var ipPort = ipPort
    lateinit var ip: String
    var port: Int? = null

    private val nonSSLProtocol = "http://"
    private val SSLProtocol = "https://"

    init {
        if (!this.ipPort.contains(nonSSLProtocol) || !this.ipPort.contains(SSLProtocol)) {
            //this.ipPort = "\(nonSSLProtocol)\(ipPort)"
        }
    }

    constructor(network: String, netId: String, version: String, ip: String, port: Int, ipPort: String) : this(network, netId, version, ipPort) {
        this.network = network
        this.netId = netId
        this.version = version
        this.ip = ip
        this.port = port
        this.ipPort = ipPort
    }

    fun isEqual(netId: String, network: String, version: String): Boolean {
        if (this.netId == netId && this.network == network && this.version == version) {
            return true
        }

        return false
    }
}


