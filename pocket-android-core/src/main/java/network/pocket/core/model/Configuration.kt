package network.pocket.core.model

/**
 * A Model Class that represents the custom configuration to be used.
 *
 * @see Blockchain
 *
 * @property devId the id used to interact with Pocket Api.
 * @property blockChains the list of blockchains to be used.
 * @property maxNodes maximum number of nodes to be used, default 5.
 * @property requestTimeOut timeout in ms, for every request made, default 1000 ms.
 *
 * @constructor Creates a Configuration Object.
 */
class Configuration(var devId: String, var blockChains: ArrayList<Blockchain>, var maxNodes: Int = 5, var requestTimeOut: Int = 1000) {}