package network.pokt.aion.rpc

import network.pokt.aion.models.AionRelay
import network.pokt.aion.network.AionNetwork
import network.pokt.aion.rpc.callbacks.BigIntegerCallback
import network.pokt.aion.rpc.callbacks.BooleanCallback
import network.pokt.aion.rpc.callbacks.StringCallback

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
