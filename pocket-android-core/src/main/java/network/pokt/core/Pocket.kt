package network.pokt.core

import network.pokt.core.errors.PocketError
import network.pokt.core.model.*
import network.pokt.core.net.PocketAPI
import org.json.JSONArray
import org.json.JSONObject

abstract class Pocket {

    private val dispatch: Dispatch
    val devID: String
    private val netIds: Array<String>
    private val maxNodes: Int
    private val requestTimeOut: Int

    constructor(devID: String, network: String, netIds: Array<String>, maxNodes: Int = 5, requestTimeOut: Int = 1000) {
        var blockchains = arrayListOf<Blockchain>()
        netIds.forEach { netId ->
            blockchains.add(Blockchain(network, netId))
        }

        this.dispatch = Dispatch(Configuration(devID, blockchains, maxNodes, requestTimeOut))
        this.devID = devID
        this.netIds = netIds
        this.maxNodes = maxNodes
        this.requestTimeOut = requestTimeOut
    }

    // Abstract interfaces to be overwritten
    @Throws
    abstract fun createWallet(network: String, netID: String, data: JSONObject?): Wallet

    @Throws
    abstract fun importWallet(privateKey: String, address: String?, network: String, netID: String, data: JSONObject?): Wallet

    // Public interfaces
    fun send(relay: Relay, callback: (error: PocketError?, data: JSONObject?) -> Unit) {
        if (!relay.isValid()) {
            callback.invoke(PocketError("Relay is missing a property, please verify all properties."), null)
            return
        }

        getNode(relay.blockchain, relay.netId, true) { pocketError: PocketError?, node: Node? ->
            pocketError?.let { pocketError ->
                callback.invoke(pocketError, null)
                return@getNode
            }

            node?.let { node ->
                PocketAPI.send(relay, node, callback)
                return@getNode
            }

            callback.invoke(PocketError("No nodes available for ${relay.blockchain} - ${relay.netId}"), null)
        }
    }

    open fun send(blockchain: String, netID: String, data: String, callback: (error: PocketError?, data: JSONObject?) -> Unit) {
        send(Relay(blockchain, netID, this.dispatch.configuration.devId, data), callback)
    }

    fun addBlockchain(network: String, netID: String) {
        this.dispatch.configuration.blockChains.add(Blockchain(network, netID))
    }

    // Private interface
    private fun getRandomNode(nodes: List<Node>): Node? {
        return nodes[(0 until nodes.count()).random()]
    }

    private fun getNode(network: String, netID: String, retrieveNodes: Boolean = false, callback: (error: PocketError?, node: Node?) -> Unit) {
        var nodes = arrayListOf<Node>()
        this.dispatch.nodes.forEach { node ->
            if (node.isEqual(netID, network)) {
                nodes.add(node)
            }
        }

        if (nodes.isEmpty() && retrieveNodes) {
            this.retrieveNodes { error, nodes ->
                error?.let { pocketError ->
                    callback.invoke(pocketError, null)
                    return@retrieveNodes
                }

                nodes?.let {nodeList ->
                    callback.invoke(null, this.getRandomNode(nodeList))
                    return@retrieveNodes
                }

                callback.invoke(PocketError("Unknown error fetching nodes"), null)
            }
        } else {
            callback.invoke(null, this.getRandomNode(nodes))
        }
    }

    private fun retrieveNodes(callback: (error: PocketError?, nodes: List<Node>?) -> Unit) {
        PocketAPI.retrieveNodes(dispatch.configuration) { error, nodesJSON ->
            var pocketError = error ?: null
            var nodeList: List<Node>?

            try {
                nodeList = this.dispatch.parseDispatchResponse(nodesJSON ?: JSONArray())
            } catch (error: PocketError) {
                pocketError = error
                nodeList = ArrayList()
            }


            callback.invoke(pocketError, nodeList)
        }
    }
}