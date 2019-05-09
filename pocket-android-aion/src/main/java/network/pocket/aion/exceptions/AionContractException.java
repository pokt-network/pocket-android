package network.pocket.aion.exceptions;

/**
 * A custom exception to encapsulate uncommon behaviour using Pocket Aion.
 *
 * @see Exception
 *
 *
 */
public class AionContractException extends Exception {

    /**
     *
     * @param reason error message.
     */
    public AionContractException(String reason) {
        super(reason);
    }
}
