package network.pokt.core.model

import org.json.JSONArray
import org.json.JSONObject


class Dispatch(var configuration:Configuration){

    var nodes: List<Node> = ArrayList()

    private fun createNodesArray(jsonObject: JSONObject): ArrayList<Node> {
        var nodes = arrayListOf<Node>()

        val network = jsonObject.getString("name")
        val netId = jsonObject.getString("netid")

        if(jsonObject.has("ips")){
            val ipPortArray = jsonObject.getJSONArray("ips")
            for (i in 0..(ipPortArray.length() - 1)) {
                val node = Node(network, netId, ipPortArray.getString(i))
                nodes.add(node)
            }
        }

        return nodes
    }

    fun parseDispatchResponse(jsonArray: JSONArray): ArrayList<Node> {
        var nodes = arrayListOf<Node>()

        for (i in 0..(jsonArray.length() - 1)) {
            val jsonObject = jsonArray.getJSONObject(i)
            jsonObject.let {
                nodes.addAll(createNodesArray(jsonObject))
            }
        }

        if (nodes.isNotEmpty()) {
            this.nodes = nodes
        }

        return nodes
    }
}