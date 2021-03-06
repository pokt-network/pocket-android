package network.pocket.aion.rpc.callbacks

import network.pocket.aion.rpc.types.ObjectOrBoolean
import network.pocket.core.errors.PocketError
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigInteger

/**
 * Generic Callbacks used by EthRpc
 *
 * @see network.pocket.aion.rpc.EthRpc
 */
typealias StringCallback = (pocketError: PocketError?, result: String?) -> Unit
typealias BigIntegerCallback = (pocketError: PocketError?, result: BigInteger?) -> Unit
typealias BooleanCallback = (pocketError: PocketError?, result: Boolean?) -> Unit
typealias JSONObjectCallback = (pocketError: PocketError?, result: JSONObject?) -> Unit
typealias JSONArrayCallback = (pocketError: PocketError?, result: JSONArray?) -> Unit
typealias JSONObjectOrBooleanCallback = (pocketError: PocketError?, result: ObjectOrBoolean?) -> Unit
typealias AnyArrayCallback = (pocketError: PocketError?, result: Array<Any>?) -> Unit
typealias AnyCallback = (pocketError: PocketError?, result: Any?) -> Unit

