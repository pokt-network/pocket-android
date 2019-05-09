package network.pocket.eth.exceptions;

/**
 * A custom exception to encapsulate uncommon behaviour using Pocket Eth.
 *
 * @see Exception
 *
 *
 */
public class EthContractException extends Exception {
    /**
     *
     * @param reason error message.
     */
    public EthContractException(String reason) {
        super(reason);
    }
}
