package network.pocket.core.errors

/**
 * A custom error to encapsulate issues while storing or retrieving wallet info.
 *
 * @see Error
 *
 * @param errorMsg the error message to display.
 *
 * @constructor constructs a WalletPersistenceError.
 */
class WalletPersistenceError(errorMsg: String) : Error(errorMsg)