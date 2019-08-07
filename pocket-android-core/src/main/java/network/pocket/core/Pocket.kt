package network.pocket.core

import network.pocket.core.errors.PocketError
import network.pocket.core.model.*
import network.pocket.core.net.PocketAPI
import org.json.JSONArray

/**
 * Abstract Class used to interact with PocketApi.
 *
 * Extends this class to create your own custom plugins.
 *
 * @see PocketAPI
 * @see Dispatch
 *
 * @property dispatch dispatch with the current configuration.
 * @property devID the id used to interact with Pocket Api.
 * @property netIds @ArrayList of netid's of the Blockchain.
 * @property maxNodes maximum number of nodes to be used, default 5.
 * @property requestTimeOut timeout in ms, for every request made, default 1000 ms.
 *
 */
abstract class Pocket {

    private val dispatch: Dispatch
    private val devID: String
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

    /**
     * Sends relay to a Pocket node.
     *
     * @see Relay
     *
     * @param relay relay to be sent to the node.
     * @param callback listener for the send relay operation.
     *
     */
    // Public interfaces
    fun send(relay: Relay, callback: (error: PocketError?, data: String?) -> Unit) {
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

    open fun send(blockchain: String, netID: String, data: String?, method: String?, path: String?, queryParams: Map<String, String>?, headers: Map<String,String>?, callback: (error: PocketError?, data: String?) -> Unit) {
        send(Relay(blockchain, netID, this.dispatch.configuration.devId, data, method, path, queryParams, headers), callback)
    }

    /**
     * Adds a new Blockchain to the dispatch configuration.
     *
     * @see Dispatch
     * @see Configuration
     *
     * @param network the blockchain network name, ie: ETH, AION..
     * @param netID the netid of the Blockchain.
     *
     */
    fun addBlockchain(network: String, netID: String) {
        this.dispatch.configuration.blockChains.add(Blockchain(network, netID))
    }

    /**
     * Randomly selects a valid node
     *
     * @see Node
     *
     * @param nodes List of nodes
     *
     * @return a valid random Node
     *
     */
    // Private interface
    private fun getRandomNode(nodes: List<Node>?): Node? {
        return when(nodes) {
            null -> return null
            else -> {
                when (nodes.size) {
                    0 -> null
                    1 -> nodes.first()
                    else -> {
                        nodes[(0 until (nodes.count() - 1)).random()]
                    }
                }
            }
        }
    }

    /**
     * Gets a random node.
     *
     * If the dispatch node list is empty, a new list of nodes will be obtained from Pocket backend.
     *
     * @see Dispatch
     * @see Node
     *
     * @param network the blockchain network name, ie: ETH, AION.
     * @param netID the netid of the Blockchain.
     * @param retrieveNodes Whether to retrieve new nodes.
     *
     */
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

    /**
     * Parses the list of service nodes from Pocket dispatch.
     *
     *
     * @see Node
     *
     * @param callback listener for the parse operation.
     *
     */
    private fun retrieveNodes(callback: (error: PocketError?, nodes: List<Node>?) -> Unit) {
        PocketAPI.retrieveNodes(dispatch.configuration) { error, nodesJSON ->
            var pocketError = error
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