package network.pocket.core.net

import com.google.gson.Gson
import network.pocket.core.BuildConfig
import network.pocket.core.errors.PocketError
import network.pocket.core.model.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import java.lang.Exception

/**
 * Class that interacts with Pocket backend.
 *
 */
internal class PocketAPI {

    companion object {
        open class PocketApiCallback : Callback {

            var responseCallback: (error: PocketError?, response: JSONObject?) -> Unit = { _: PocketError?, _: JSONObject? -> }

            constructor(responseCallback: ((error: PocketError?, response: JSONObject?) -> Unit)?) {
                this.responseCallback = responseCallback ?: this.responseCallback
            }

            override fun onFailure(call: Call, error: IOException) {
                this.responseCallback.invoke(PocketError(error.message ?: "Request failed"), null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body()?.let {
                        responseBody ->
                    val responseBodyStr = responseBody.string().replace("\\n","").replace("\\","").replace("\"{","{").replace("}\"","}")
                    try {
                        val json = JSONObject(responseBodyStr)
                        this.responseCallback.invoke(null, json)
                    }catch (error: Exception){
                        this.responseCallback.invoke(PocketError("Failed to parse the response to a valid Json \n $responseBodyStr \n ${error.printStackTrace()}"), null)
                    }
                    return@onResponse
                }

                this.responseCallback.invoke(PocketError("Invalid Response Body"), null)
                return
            }
        }

        private var gson = Gson().newBuilder().create()
        private var client = OkHttpClient().newBuilder().followRedirects(false).followSslRedirects(false).build()

        /**
         * Sends a relay to a specific node.
         *
         * @see Relay
         * @see Node
         *
         * @param relay relay to be sent to the node.
         * @param node specific node to be used.
         * @param relayCallback callback listener for the send relay operation.
         */
        fun send(relay: Relay, node: Node, relayCallback: ((error: PocketError?, response: JSONObject?) -> Unit)?) {
            val url = node.ipPort.plus(Constants.RELAY_PATH)
            val json = gson.toJson(relay)
            val request = Request.Builder()
                .url(url)
                .post(
                    RequestBody.create(MediaType.parse(Constants.JSON_CONTENT_TYPE), json)
                ).build()

            client.newCall(request).enqueue(object : PocketApiCallback(relayCallback) {
                override fun onFailure(call: Call, error: IOException) {
                    super.onFailure(call, error)
                    PocketAPI.send(Report(node.ip, error.message ?: "Request failed"), null)
                }
            })
        }

        /**
         * Sends a report to Pocket.
         *
         * @see Report
         *
         * @param report report to be sent to Pocket.
         * @param reportCallback callback listener for the send report operation.
         */
        fun send(report: Report, reportCallback: ((error: PocketError?, response: JSONObject?) -> Unit)?) {
            val url = BuildConfig.DISPATCH_NODE_URL.plus(Constants.REPORT_PATH)
            val json = gson.toJson(report)
            val request = Request.Builder()
                .url(url)
                .post(
                    RequestBody.create(MediaType.parse(Constants.JSON_CONTENT_TYPE), json)
                ).build()

            client.newCall(request).enqueue(PocketApiCallback(reportCallback))
        }

        /**
         * Retrieves live nodes per the configuration.
         *
         * @see Configuration
         * @see Node
         *
         * @param configuration the configuration to be used.
         * @param callback callback listener for the retrieve nodes operation.
         */
        fun retrieveNodes(configuration: Configuration, callback: (error: PocketError?, nodesJSON: JSONArray?) -> Unit) {
            val url = BuildConfig.DISPATCH_NODE_URL.plus(Constants.DISPATCH_PATH)
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
                    callback.invoke(PocketError(error.message ?: "Request failed"), null)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.body()?.let {
                        responseBody ->
                        val jsonObj = JSONTokener(responseBody.string()).nextValue()
                        when (jsonObj) {
                            is JSONObject -> callback.invoke(PocketError("Invalid json format for $jsonObj"), null)
                            is JSONArray -> callback.invoke(null, jsonObj)
                            else -> {
                                callback.invoke(PocketError("Invalid JSON Array $jsonObj"), null)
                            }
                        }
                        return@onResponse
                    }

                    callback.invoke(PocketError("Invalid Response"), null)
                }
            })
        }
    }
}