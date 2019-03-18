package network.pokt.pocketcore

import network.pokt.pocketcore.model.Configuration
import network.pokt.pocketcore.model.Dispatch
import network.pokt.pocketcore.model.Node
import network.pokt.pocketcore.net.API

class PocketCore(configuration:Configuration) {

    private var dispatch: Dispatch? = null

    init {
        this.dispatch = Dispatch(configuration)
    }


    fun retrieveNodes(callback: (nodes: ArrayList<Node>) -> Unit) {
        API().retrieveNodes(dispatch!!.configuration) {
            it.let {jsonObject ->
                val nodes = this.dispatch!!.parseDispatchResponse(jsonObject)
                callback.invoke(nodes)
            }
        }
    }
}