package network.pokt.pocketaion.operation

import android.content.Context
import org.liquidplayer.javascript.JSContext
import org.liquidplayer.javascript.JSException
import network.pokt.pocketaion.util.RawFileUtil
import network.pokt.pocketaion.R
import network.pokt.pocketcore.model.Wallet

class ImportWalletOperation(context: Context) : BaseOperation(context) {

    private val privateKey: String? = null
    private val network: String? = null
    private val subnetwork: String? = null
    private var wallet: Wallet? = null

    fun ImportWalletOperation(context: Context, network: String, subnetwork: String, privateKey: String): ??? {
        this(context)
        this.privateKey = privateKey
        this.network = network
        this.subnetwork = subnetwork
    }

    fun getWallet(): Wallet? {
        return this.wallet
    }

    override fun executeOperation(jsContext: JSContext) {
        // Run the script to create the wallet in JS
        jsContext.evaluateScript(
            String.format(
                RawFileUtil.readRawTextFile(this.context!!, R.raw.import_wallet),
                this.privateKey
            )
        )
        // Extract the address and private key
        val walletObj = jsContext.property("wallet").toObject()
        this.wallet = OperationUtil.parseWalletObj(walletObj, this.network, this.subnetwork)
    }

    override fun handle(exception: JSException) {
        super.handle(exception)
        this.wallet = null
    }
}