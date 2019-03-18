package network.pokt.pocketcore.model

import network.pokt.pocketcore.exceptions.PocketError
import org.json.JSONArray
import org.json.JSONObject


class Dispatch(var configuration:Configuration){

    private fun getBlockchains(): ArrayList<Blockchain> {
        return this.configuration.blockChains
    }

    private fun createNodesArray(jsonArray: JSONArray, data: List<String>): ArrayList<Node> {
        var nodes = arrayListOf<Node>()
        for(i in 0 until jsonArray.length()){
            val node = Node(data[0], data[2], data[1], jsonArray.getString(i))
            nodes.add(node)
        }

        return nodes
    }

    fun parseDispatchResponse(json: JSONObject): ArrayList<Node> {
        var nodes = arrayListOf<Node>()
        val keyValuesMap = json.keys()

        if(!keyValuesMap.hasNext()){
            throw PocketError("Failed to parse Node object")
        }

        keyValuesMap.forEach {
            val data = it.split("|")

            if(data.size != 3){
                throw PocketError("Failed to parsed service nodes with error: Node information is missing 1 or more params: $data")
            }

            val jsonArray = json.getJSONArray(it)
            nodes.addAll(createNodesArray(jsonArray, data))

        }

        return nodes
    }
}