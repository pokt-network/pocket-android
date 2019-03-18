package network.pokt.pocket_android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_main.*
import network.pokt.pocketcore.PocketCore
import network.pokt.pocketcore.model.Blockchain
import network.pokt.pocketcore.model.Configuration


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val blockchain = Blockchain("ETH", "4", "0")
        val blockchain1 = Blockchain("ETH", "1", "0")
        val array = arrayListOf<Blockchain>()
        array.add(blockchain)
        array.add(blockchain1)
        val configuration = Configuration("DEVID1", array)

        val pocketCore = PocketCore("DEVID1", "ETH", "4", "0")

        pocketCore.retrieveNodes { nodes ->
            if (nodes.isNotEmpty()) {
                val address = "0xf892400Dc3C5a5eeBc96070ccd575D6A720F0F9f"
                val data =
                    "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getBalance\",\"params\":[\"$address\",\"latest\"],\"id\":67}"
                val relay = (pocketCore.createRelay("ETH", "4", "0", data, "DEVID1"))
                pocketCore.send(relay) { response ->
                    print(response)
                    val someyhing = ""
                }
            }
        }
    }
}