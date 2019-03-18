package network.pokt.pocketcore

import network.pokt.pocketcore.exceptions.PocketError
import network.pokt.pocketcore.extensions.getErrorMessage
import network.pokt.pocketcore.extensions.hasError
import network.pokt.pocketcore.model.*
import network.pokt.pocketcore.net.PocketAPI
import org.json.JSONObject

class PocketCore(devId: String, networkName:String, netId:Array<String>, version:String, maxNodes: Int = 5, requestTimeOut: Int = 1000) {

    private var dispatch: Dispatch? = null
    private var configuration: Configuration? = null

    init {
        var blockchains = arrayListOf<Blockchain>()
        netId.forEach{ netId ->
                blockchains.add(Blockchain(networkName, netId, version))
        }

        this.configuration = Configuration(devId, blockchains, maxNodes, requestTimeOut)
        this.dispatch = Dispatch(configuration!!)
    }

    constructor(devId:String, networkName:String, netId:String, version:String, maxNodes:Int = 5, requestTimeOut: Int = 1000):this(devId, networkName, arrayOf(netId), version, maxNodes, requestTimeOut)


    fun getNode(netID: String, network: String, version: String): Node? {
        if (this.configuration!!.isNodeEmpty()) {
            return null
        }

        var nodes = arrayListOf<Node>()
        this.configuration!!.nodes.forEach { node ->
            if (node.isEqual(netID, network, version)) {
                nodes.add(node)
            }
        }

        return if (nodes.isEmpty()) null else nodes.get((0 until  nodes.count()).random())

    }

    fun createRelay(blockchain: String, netID: String, version: String, data: String, devID: String): Relay {
        return Relay(blockchain, netID, version, data, devID)
    }

    fun send(relay:Relay, callback: (data: JSONObject) -> Unit){
        if (!relay.isValid()) {
            throw PocketError("Relay is missing a property, please verify all properties.")
            return
        }

        val node = getNode(relay.netId, relay.blockchain, relay.version)
        if(node == null){
            throw PocketError("Node is empty;")
            return
        }

        PocketAPI().send(relay, node.ipPort){jsonObject ->

            if(jsonObject.hasError()){
                throw PocketError(jsonObject.getErrorMessage())
            }else{
                callback.invoke(jsonObject)
            }
        }
    }


    fun retrieveNodes(callback: (nodes: ArrayList<Node>) -> Unit) {
        PocketAPI().retrieveNodes(dispatch!!.configuration) {
            it.let { jsonObject ->
                try {
                    val nodes = this.dispatch!!.parseDispatchResponse(jsonObject)
                    if(nodes.isNotEmpty()){
                        callback.invoke(nodes)
                    }else{
                        callback.invoke(nodes)
                    }

                } catch (error: PocketError) {
                    throw PocketError("There was an error parsing your nodes ${error.message}")
                }
            }
        }
    }
}