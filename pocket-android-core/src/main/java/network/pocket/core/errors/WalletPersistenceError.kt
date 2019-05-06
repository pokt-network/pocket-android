package network.pocket.core.errors

/**
 * A custom @see Error to encapsulate issues while storing or retrieving wallet info
 *
 *
 * @param errorMsg the error message to display
 */
class WalletPersistenceError(errorMsg: String) : Error(errorMsg)