package network.pocket.core.errors

/**
 * A custom @see Error to encapsulate uncommon behaviour using the Pocket API
 *
 *
 * @param errorMsg the error message to display
 */
class PocketError(errorMsg: String) : Error(errorMsg)