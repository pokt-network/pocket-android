package network.pocket.aion.rpc

import network.pocket.aion.models.AionRelay
import network.pocket.aion.network.AionNetwork
import network.pocket.aion.rpc.callbacks.BigIntegerCallback
import network.pocket.aion.rpc.callbacks.BooleanCallback
import network.pocket.aion.rpc.callbacks.StringCallback

class NetRpc
    (private val aionNetwork: AionNetwork) {
    private enum class NetRpcMethod {
        net_version,
        net_listening,
        net_peerCount
    }

    fun version(callback: StringCallback) {
        val relay = AionRelay(this.aionNetwork.netID, this.aionNetwork.devID, NetRpcMethod.net_version.name, null)
        this.aionNetwork.sendWithStringResult(relay, callback)
    }

    fun listening(callback: BooleanCallback) {
        val relay = AionRelay(this.aionNetwork.netID, this.aionNetwork.devID, NetRpcMethod.net_listening.name, null)
        this.aionNetwork.sendWithBooleanResult(relay, callback)
    }

    fun peerCount(callback: BigIntegerCallback) {
        val relay = AionRelay(this.aionNetwork.netID, this.aionNetwork.devID, NetRpcMethod.net_peerCount.name, null)
        this.aionNetwork.sendWithBigIntegerResult(relay, callback)
    }
}
