package network.pokt.core.model

import android.content.Context
import com.orhanobut.hawk.Hawk
import com.pvryan.easycrypt.ECResultListener
import com.pvryan.easycrypt.symmetric.ECSymmetric
import org.json.JSONException
import org.json.JSONArray
import org.json.JSONObject
import network.pokt.core.errors.WalletPersistenceError

typealias WalletSaveListener = (walletPersistenceError: WalletPersistenceError?) -> Unit
typealias WalletRetrieveListener = (walletPersistenceError: WalletPersistenceError?, wallet: Wallet?) -> Unit

open class Wallet(var privateKey: String, var address: String, var network: String, var netID: String, var data: JSONObject?) : JSONObject() {
    private val crypto = ECSymmetric()

    init {
        this.put(addressKey, address)
        this.put(privateKeyKey, privateKey)
        this.put(networkKey, network)
        this.put(netIDKey, netID)
        this.put(dataKey, data)
    }

    companion object {
        private const val walletRecordKeysKey = "POCKET_WALLETS_RECORD_KEYS"
        private const val addressKey = "address"
        private const val privateKeyKey = "privateKey"
        private const val networkKey = "network"
        private const val netIDKey = "netID"
        private const val dataKey = "data"

        fun recordKey(network: String?, netID: String?, address: String?) : String {
            return "$network/$netID/$address"
        }

        fun isSaved(network: String?, netID: String?, address: String?, context: Context): Boolean {
            Hawk.init(context).build()
            return Hawk.contains(Wallet.recordKey(network, netID, address))
        }

        @Throws(WalletPersistenceError::class)
        fun getWalletsRecordKeys(context: Context): List<String> {
            Hawk.init(context).build()
            val recordKeys = Hawk.get(walletRecordKeysKey, JSONArray())
            val recordKeysList: MutableList<String> = ArrayList()

            for (i in 0 until recordKeys.length()) {
                try {
                    recordKeysList.add(recordKeys.getString(i))
                } catch (e: JSONException) {
                    throw WalletPersistenceError(e.message ?: "Error getting wallet record keys")
                }

            }

            return recordKeysList
        }

        @Throws(WalletPersistenceError::class)
        fun retrieve(network: String, netID: String, address: String, passphrase: String, context: Context, listener: WalletRetrieveListener) {
            if (!Wallet.isSaved(network, netID, address, context)) {
                throw WalletPersistenceError(
                    String.format(
                        "No wallet found for Network %s and Address %s",
                        network,
                        address
                    )
                )
            }

            val recordKey = Wallet.recordKey(network, netID, address)
            Hawk.init(context).build()
            val encryptedWalletJson: String = Hawk.get(recordKey)

            var encryptedJSON = when(encryptedWalletJson) {
                "" -> throw WalletPersistenceError("Error retrieving wallet from local storage")
                null -> throw WalletPersistenceError("Error retrieving wallet from local storage")
                else -> encryptedWalletJson
            }

            val crypto = ECSymmetric()
            crypto.decrypt(encryptedJSON, passphrase, object : ECResultListener {
                override fun onProgress(newBytes: Int, bytesProcessed: Long, totalBytes: Long) {
                    // DO NOTHING
                }

                override fun <T> onSuccess(result: T) {
                    val decryptedWalletJSON = result as String
                    try {
                        val decryptedJSONWallet = JSONObject(decryptedWalletJSON)
                        val privateKey = decryptedJSONWallet.getString(Wallet.privateKeyKey)
                        val address = decryptedJSONWallet.getString(Wallet.addressKey)
                        val network = decryptedJSONWallet.getString(Wallet.networkKey)
                        val netID = decryptedJSONWallet.getString(Wallet.netIDKey)
                        val data = decryptedJSONWallet.optJSONObject(Wallet.dataKey)
                        listener.invoke(null, Wallet(privateKey, address, network, netID, data))
                    } catch (jsonEx: JSONException) {
                        listener.invoke(WalletPersistenceError(jsonEx.message ?: "Error decrypting wallet data"),null)
                    }

                }

                override fun onFailure(message: String, e: Exception) {
                    listener.invoke(WalletPersistenceError(message),null)
                }
            })
        }
    }

    fun recordKey(): String {
        return Wallet.recordKey(this.network, this.netID, this.address)
    }

    // Persistence interfaces
    @Throws(WalletPersistenceError::class)
    fun save(passphrase: String, context: Context, listener: WalletSaveListener) {
        if (this.isSaved(context)) {
            throw WalletPersistenceError("Wallet already exists")
        }

        val recordKey = this.recordKey()

        crypto.encrypt(this.toString(), passphrase, object : ECResultListener {
            override fun onProgress(newBytes: Int, bytesProcessed: Long, totalBytes: Long) {
                // DO NOTHING
            }

            override fun <T> onSuccess(result: T) {
                val encryptedWalletJSON = result as String
                // Save record
                Hawk.init(context).build()
                Hawk.put(recordKey, encryptedWalletJSON)


                // Update recordkey list
                val recordKeys = Hawk.get(walletRecordKeysKey, JSONArray())
                recordKeys.put(recordKey)
                Hawk.put(walletRecordKeysKey, recordKeys)

                // Call listener
                listener.invoke(null)
            }

            override fun onFailure(message: String, e: Exception) {
                listener.invoke(WalletPersistenceError(e.message ?: "Error encrypting wallet data"))
            }
        })
    }

    @Throws(WalletPersistenceError::class)
    fun delete(context: Context): Boolean {
        if (!this.isSaved(context)) {
            throw WalletPersistenceError("Wallet has not been saved yet")
        }

        Hawk.init(context).build()
        val recordKey = this.recordKey()

        // Update recordkey list
        val recordKeys = Hawk.get(walletRecordKeysKey, JSONArray())
        for (i in 0 until recordKeys.length()) {
            try {
                if (recordKeys.getString(i) == recordKey) {
                    recordKeys.remove(i)
                    Hawk.put(walletRecordKeysKey, recordKeys)
                    break
                }
            } catch (e: JSONException) {
                throw WalletPersistenceError(e.message ?: "Error updating the record key list")
            }

        }

        // Delete record
        return Hawk.delete(recordKey)
    }

    fun isSaved(context: Context): Boolean {
        return Wallet.isSaved(this.network, this.netID, this.address, context)
    }
}