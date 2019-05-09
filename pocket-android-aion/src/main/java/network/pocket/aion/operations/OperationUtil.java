package network.pocket.aion.operations;

import network.pocket.core.model.Wallet;
import org.jetbrains.annotations.NotNull;
import org.liquidplayer.javascript.JSObject;

/**
 * Utility Class.
 */
public class OperationUtil {

    /**
     *
     * @param walletObj jsonObject containing the Wallet information.
     * @param network Wallet Network.
     * @param subnetwork Wallet Subnetwork.
     *
     * @see Wallet
     *
     * @return the parsed Wallet
     */
    public static Wallet parseWalletObj(JSObject walletObj, @NotNull String network, @NotNull String subnetwork) {
        Wallet result;

        // Extract the address and private key
        if(walletObj == null || walletObj.isUndefined()) {
            return null;
        }

        String address = walletObj.property("address").toString();
        String privateKey = walletObj.property("privateKey").toString();

        if (address == null || address.isEmpty() || privateKey == null || privateKey.isEmpty()) {
            return null;
        }

        // Create the wallet
        result = new Wallet(privateKey, address, network, subnetwork);

        return result;
    }
}
