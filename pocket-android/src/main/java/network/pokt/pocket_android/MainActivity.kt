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

        val pocketCore = PocketCore(configuration)

        val nodes = pocketCore.retrieveNodes {nodes ->
            for (node in nodes){
                val something = ""
            }
        }
    }
}