package network.pokt.pocketcore.net

import android.util.Log
import com.google.gson.Gson
import network.pokt.pocketcore.model.Configuration
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class API {

    private var gson = Gson().newBuilder().setPrettyPrinting().create()
    private val client = OkHttpClient()


    fun retrieveNodes(configuration: Configuration, callback: (json:JSONObject) -> Unit) {
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

            override fun onFailure(call: Call, e: IOException) {
                Log.i("error", e.stackTrace.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i("response", response.body()!!.toString())
                try {
                    val responseData = response.body()!!.string()
                    val json = JSONObject(responseData)
                    callback.invoke(json)

                } catch (e: Exception) {
                    Log.i("error", e.stackTrace.toString())
                }
            }
        })
    }
}