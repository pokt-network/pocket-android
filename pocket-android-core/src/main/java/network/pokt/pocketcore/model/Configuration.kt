package network.pokt.pocketcore.model

class Configuration(var devId:String, var blockChains:ArrayList<Blockchain>, var maxNodes:Int = 5, var requestTimeOut:Int = 1000) {

    lateinit var nodes:ArrayList<Node>

    fun isNodeEmpty():Boolean{
        return this.nodes.count() == 0
    }
}