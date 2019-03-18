package network.pokt.pocketcore

import network.pokt.pocketcore.exceptions.PocketError
import network.pokt.pocketcore.model.Configuration
import network.pokt.pocketcore.model.Dispatch
import network.pokt.pocketcore.model.Node
import network.pokt.pocketcore.net.PocketAPI

class PocketCore(configuration: Configuration) {

    private var dispatch: Dispatch? = null

    init {
        this.dispatch = Dispatch(configuration)
    }


    fun retrieveNodes(callback: (nodes: ArrayList<Node>) -> Unit) {
        PocketAPI().retrieveNodes(dispatch!!.configuration) {
            it.let { jsonObject ->
                try{
                    val nodes = this.dispatch!!.parseDispatchResponse(jsonObject)
                    callback.invoke(nodes)
                }catch (error:PocketError){
                    throw PocketError("There was an error parsing your nodes ${error.message}")
                }
            }
        }
    }
}