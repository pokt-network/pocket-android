package network.pokt.core.extensions

import org.json.JSONObject

fun JSONObject.hasError(): Boolean {
    return this.has("error")
}

fun JSONObject.getErrorMessage(): String{
    return this.getJSONObject("error").getString("message")
}

fun JSONObject.getErrorTitle(): String{
    return this.getJSONObject("error").getString("title")
}