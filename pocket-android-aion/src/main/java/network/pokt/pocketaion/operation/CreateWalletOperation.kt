package network.pokt.pocketaion.operation

import android.content.Context
import network.pokt.pocketcore.model.Wallet
import org.liquidplayer.javascript.JSContext
import org.liquidplayer.javascript.JSException
import network.pokt.pocketaion.util.RawFileUtil
import network.pokt.pocketaion.R

class CreateWalletOperation(context: Context, network: String, subnetwork: String): BaseOperation(context) {

    private val network: String? = null
    private val subnetwork: String? = null
    private var wallet: Wallet? = null

    override fun executeOperation(jsContext: JSContext) {
        jsContext.evaluateScript(RawFileUtil.readRawTextFile(this.context!!, R.raw.create_wallet))
        val walletObj = jsContext.property("wallet").toObject()
        this.wallet = OperationUtil.parseWalletObj(walletObj, this.network!!, this.subnetwork!!)
    }

    fun getWallet(): Wallet? {
        return this.wallet
    }

    override fun handle(exception: JSException) {
        super.handle(exception)
        this.wallet = null
    }
}