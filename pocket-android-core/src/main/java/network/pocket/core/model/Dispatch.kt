package network.pocket.core.model

import org.json.JSONArray
import org.json.JSONObject

/**
 * A Model Class that wraps the user Configuration.
 *
 * @see Configuration
 *
 * @property configuration the configuration to be used.
 * @property nodes list of nodes to be used.
 *
 * @constructor Creates a Dispatch Object.
 */
class Dispatch(var configuration:Configuration){

    var nodes: List<Node> = ArrayList()

    /**
     * Creates an ArrayList of Node elements
     *
     * @see Node
     *
     * @param jsonObject the jsonObject from the jsonArray response
     *
     * @return a list of Node
     */
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

    /**
     * Parse the response from the Dispatch service
     *
     * @see jsonArray
     * @see Node
     *
     * @param jsonArray the response from the Dispatcher
     *
     * @return a list of Node
     */
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