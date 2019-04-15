package network.pokt.core.model

class Configuration(var devId: String, var blockChains: ArrayList<Blockchain>, var maxNodes: Int = 5, var requestTimeOut: Int = 1000) {}