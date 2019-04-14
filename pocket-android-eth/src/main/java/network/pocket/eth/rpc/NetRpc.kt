package network.pocket.eth.rpc

import network.pocket.eth.models.EthRelay
import network.pocket.eth.network.EthNetwork
import network.pocket.eth.rpc.callbacks.BigIntegerCallback
import network.pocket.eth.rpc.callbacks.BooleanCallback
import network.pocket.eth.rpc.callbacks.StringCallback

class NetRpc
    (private val ethNetwork: EthNetwork) {
    private enum class NetRpcMethod {
        net_version,
        net_listening,
        net_peerCount
    }

    fun version(callback: StringCallback) {
        val relay = EthRelay(this.ethNetwork.netID, this.ethNetwork.devID, NetRpcMethod.net_version.name, null)
        this.ethNetwork.sendWithStringResult(relay, callback)
    }

    fun listening(callback: BooleanCallback) {
        val relay = EthRelay(this.ethNetwork.netID, this.ethNetwork.devID, NetRpcMethod.net_listening.name, null)
        this.ethNetwork.sendWithBooleanResult(relay, callback)
    }

    fun peerCount(callback: BigIntegerCallback) {
        val relay = EthRelay(this.ethNetwork.netID, this.ethNetwork.devID, NetRpcMethod.net_peerCount.name, null)
        this.ethNetwork.sendWithBigIntegerResult(relay, callback)
    }
}
