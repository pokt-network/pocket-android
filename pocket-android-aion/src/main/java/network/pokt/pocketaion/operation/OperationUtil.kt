package network.pokt.pocketaion.operation

import network.pokt.core.model.Wallet
import org.json.JSONException
import org.json.JSONObject
import org.liquidplayer.javascript.JSObject

class OperationUtil {
    companion object {
        fun parseWalletObj(walletObj: JSObject?, network: String, subnetwork: String): Wallet? {
            var result: Wallet? = null

            // Extract the address and private key
            if (walletObj == null || walletObj.isUndefined!!) {
                return result
            }

            val address = walletObj.property("address").toString()
            val privateKey = walletObj.property("privateKey").toString()

            if (address == null || address.isEmpty() || privateKey == null || privateKey.isEmpty()) {
                return result
            }

            // Create the wallet
            try {
                result = Wallet(address, privateKey, subnetwork, JSONObject())
            } catch (e: JSONException) {
                result = null
            }

            return result
        }
    }
}