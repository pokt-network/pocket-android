package network.pocket.core.net

/**
 * Constants used by PocketCore backend.
 *
 * @property DISPATCH_PATH
 * @property REPORT_PATH
 * @property RELAY_PATH
 * @property JSON_CONTENT_TYPE
 */
class Constants {
    companion object {

        //val DISPATCH_NODE_URL = "https://dispatch.staging.pokt.network"
        const val DISPATCH_PATH = "/v1/dispatch"
        const val REPORT_PATH = "/v1/report"
        const val RELAY_PATH = "/v1/relay/"
        const val JSON_CONTENT_TYPE = "application/json"
    }
}