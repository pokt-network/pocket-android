package network.pokt.pocketcore.net

import com.google.gson.Gson
import network.pokt.pocketcore.exceptions.PocketError
import network.pokt.pocketcore.model.Configuration
import network.pokt.pocketcore.model.Relay
import network.pokt.pocketcore.model.Report
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception

class PocketAPI {

    private var gson = Gson().newBuilder().setPrettyPrinting().create()
    private var client = OkHttpClient().newBuilder().followRedirects(false).followSslRedirects(false).build()

    fun send(relay: Relay, ipPort:String,  relayCallback: (json: JSONObject) -> Unit) {
        val url = ipPort.plus(Constants.RELAY_PATH)
        val json = gson.toJson(relay)
        val request = Request.Builder()
            .url(url)
            .post(
                RequestBody.create(MediaType.parse(Constants.JSON_CONTENT_TYPE), json)
            ).build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, error: IOException) {
                throw PocketError("Failed to retrieve nodes, please check your configuration $relay \n ${error.stackTrace}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body()!!.string().replace("\\n","").replace("\\","").replace("\"{","{").replace("}\"","}")
                try {
                    val json = JSONObject(responseData)
                    relayCallback.invoke(json)
                }catch (error: Exception){
                    throw PocketError("Failed to parse the response to a valid Json \n $responseData \n ${error.printStackTrace()}")
                }
            }
        })
    }

    fun send(report: Report, reportCallback: (response: String) -> Unit) {
        val url = Constants.DISPATCH_NODE_URL.plus(Constants.REPORT_PATH)
        val json = gson.toJson(report)
        val request = Request.Builder()
            .url(url)
            .post(
                RequestBody.create(MediaType.parse(Constants.JSON_CONTENT_TYPE), json)
            ).build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, error: IOException) {
                throw PocketError("Failed to send report \n ${error.stackTrace}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body()!!.string()
                try {
                    reportCallback.invoke(responseData)
                }catch (error: Exception){
                    throw PocketError("Failed to parse the response to a valid Json \n $responseData \n ${error.printStackTrace()}")
                }
            }
        })
    }

    fun retrieveNodes(configuration: Configuration, callback: (jsonArray: JSONArray) -> Unit) {
        val url = Constants.DISPATCH_NODE_URL.plus(Constants.DISPATCH_PATH)
        val json = gson.toJson(configuration)
        val request = Request.Builder()
            .url(url)
            .post(
                RequestBody.create(
                    MediaType.parse(Constants.JSON_CONTENT_TYPE), json
                )
            )
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, error: IOException) {
                throw PocketError("Failed to retrieve nodes, please check your configuration $configuration \n ${error.stackTrace}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body()!!.string()
                val jsonArray = JSONArray(responseData)
                callback.invoke(jsonArray)

            }
        })
    }
}