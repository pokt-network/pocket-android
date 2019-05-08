package network.pocket.core.errors

/**
 * A custom error to encapsulate uncommon behaviour using the Pocket API.
 *
 * @see Error
 *
 * @param errorMsg the error message to display.
 *
 * @constructor constructs a PocketError.
 */
class PocketError(errorMsg: String) : Error(errorMsg)