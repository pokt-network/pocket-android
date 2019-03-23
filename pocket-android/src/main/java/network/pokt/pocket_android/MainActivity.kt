package network.pokt.pocket_android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_main.*
import network.pokt.pocketcore.PocketCore


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val pocketCore = PocketCore("DEVID1", "ETH", arrayOf("4","1"))

        pocketCore.retrieveNodes { nodes ->
            if (nodes.isNotEmpty()) {
                val report = pocketCore.createReport(nodes.get(0).ipPort, "This is a test, please ignore")
                pocketCore.send(report){ msg ->
                    print(msg)
                }

                val address = "0xf892400Dc3C5a5eeBc96070ccd575D6A720F0F9f"
                val data =
                    "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getBalance\",\"params\":[\"$address\",\"latest\"],\"id\":67}"
                val relay = (pocketCore.createRelay("ETH", "4", data, "DEVID1"))
                pocketCore.send(relay) { response ->
                    print(response)
                }
            }
        }
    }
}