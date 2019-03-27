package network.pokt.pocketcore.interfaces

import network.pokt.pocketcore.model.Wallet

interface PocketPlugin {
    fun createWallet(subnetwork: String, data: String): Wallet
    fun importWallet(address: String, privateKey: String, subnetwork: String, data: String)
    fun createWallet()
}