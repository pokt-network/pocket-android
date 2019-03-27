package network.pokt.pocketcore

import network.pokt.pocketcore.exceptions.PocketError
import network.pokt.pocketcore.extensions.getErrorMessage
import network.pokt.pocketcore.extensions.hasError
import network.pokt.pocketcore.interfaces.PocketPlugin
import network.pokt.pocketcore.model.*
import network.pokt.pocketcore.net.PocketAPI
import org.json.JSONObject

open class PocketCore(devId: String, networkName: String, netId: Array<String>, maxNodes: Int = 5, requestTimeOut: Int = 1000) : PocketPlugin {

    private var dispatch: Dispatch? = null
    private var configuration: Configuration

    init {
        var blockchains = arrayListOf<Blockchain>()
        netId.forEach { netId ->
            blockchains.add(Blockchain(networkName, netId))
        }

        this.configuration = Configuration(devId, blockchains, maxNodes, requestTimeOut)
        this.dispatch = Dispatch(configuration)
    }

    constructor(devId: String, networkName: String, netId: String, maxNodes: Int = 5, requestTimeOut: Int = 1000) : this(devId, networkName, arrayOf(netId), maxNodes, requestTimeOut)


    fun getNode(netID: String, network: String): Node? {
        if (this.configuration!!.isNodeEmpty()) {
            return null
        }

        var nodes = arrayListOf<Node>()
        this.configuration!!.nodes.forEach { node ->
            if (node.isEqual(netID, network)) {
                nodes.add(node)
            }
        }

        return if (nodes.isEmpty()) null else nodes.get((0 until nodes.count()).random())

    }

    fun createRelay(blockchain: String, netID: String, data: String, devID: String): Relay {
        return Relay(blockchain, netID, data, devID)
    }

    fun createReport(ip: String, message: String): Report {
        return Report(ip, message)
    }

    fun send(relay: Relay, callback: (data: JSONObject) -> Unit) {
        if (!relay.isValid()) {
            throw PocketError("Relay is missing a property, please verify all properties.")
            return
        }

        val node = getNode(relay.netId, relay.blockchain)
        if (node == null) {
            throw PocketError("Node is empty;")
            return
        }

        PocketAPI().send(relay, node.ipPort) { jsonObject ->

            if (jsonObject.hasError()) {
                throw PocketError(jsonObject.getErrorMessage())
            } else {
                callback.invoke(jsonObject)
            }
        }
    }

    fun send(report: Report, callback: (response: String) -> Unit){
        if (!report.isValid()) {
            throw PocketError("Report is missing a property, please verify all properties.")
            return
        }

        PocketAPI().send(report){response ->
            callback.invoke(response)
        }
    }

    fun retrieveNodes(callback: (nodes: ArrayList<Node>?) -> Unit) {
        PocketAPI().retrieveNodes(dispatch!!.configuration) {jsonArray ->
            if(jsonArray != null){
                try {
                    val nodes = this.dispatch!!.parseDispatchResponse(jsonArray)
                    if (nodes.isNotEmpty()) {
                        callback.invoke(nodes)
                    } else {
                        callback.invoke(nodes)
                    }

                } catch (error: PocketError) {
                    throw PocketError("There was an error parsing your nodes ${error.message}")
                }
            }else{
                throw PocketError("Node is empty")
            }
        }
    }

    override fun createWallet(subnetwork: String, data: String): Wallet {
        throw PocketError("This method must be overridden")
    }

    override fun importWallet(address: String, privateKey: String, subnetwork: String, data: String) {
        throw PocketError("This method must be overridden")
    }

    override fun createWallet() {
        throw PocketError("This method must be overridden")
    }
}