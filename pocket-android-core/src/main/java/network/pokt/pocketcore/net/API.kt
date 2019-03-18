package network.pokt.pocketcore.net

import android.util.Log
import com.google.gson.Gson
import network.pokt.pocketcore.model.Configuration
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class API {

    private var gson = Gson().newBuilder().setPrettyPrinting().create()

    fun getActiveNodes(configuration: Configuration) {
        Log.i("msg","HERE 1")
        val client = OkHttpClient()
        val url = Constants.DISPATCH_NODE_URL.plus(Constants.DISPATCH_PATH)
        val json = gson.toJson(configuration)
        val request = Request.Builder()
            .url(url)
            .post(
                RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"), json
                )
            )
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                Log.i("msg","HERE 2")
                Log.i("error", e.stackTrace.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i("response",response.body()!!.toString())
                try {
                    val responseData = response.body()!!.string()
                    val json = JSONObject(responseData)


                } catch (e: Exception) {
                    Log.i("msg","HERE 3")
                    Log.i("error", e.stackTrace.toString())
                    print(e)
                }

            }
        })
    }



}