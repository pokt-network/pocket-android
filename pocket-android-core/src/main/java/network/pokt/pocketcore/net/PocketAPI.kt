package network.pokt.pocketcore.net

import com.google.gson.Gson
import network.pokt.pocketcore.exceptions.PocketError
import network.pokt.pocketcore.model.Configuration
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception

class PocketAPI {

    private var gson = Gson().newBuilder().setPrettyPrinting().create()
    private val client = OkHttpClient()


    fun retrieveNodes(configuration: Configuration, callback: (json: JSONObject) -> Unit) {
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
                try {
                    val json = JSONObject(responseData)
                    callback.invoke(json)
                }catch (error: Exception){
                    throw PocketError("Failed to parse the response to a valid Json \n $responseData \n ${error.printStackTrace()}")
                }
            }
        })
    }
}