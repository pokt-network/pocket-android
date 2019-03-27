package network.pokt.pocketaion

import network.pokt.pocketcore.PocketCore
import network.pokt.pocketcore.model.Wallet

class PocketAion(devId: String, netId: Array<String>, maxNodes: Int = 5, requestTimeOut: Int = 1000) : PocketCore(devId, "AION", netId, maxNodes, requestTimeOut) {


    override fun createWallet(subnetwork: String, data: String): Wallet {
        TODO("not implemented")
    }

    override fun importWallet(address: String, privateKey: String, subnetwork: String, data: String) {
        TODO("not implemented")
    }

    override fun createWallet() {
        TODO("not implemented")
    }

}