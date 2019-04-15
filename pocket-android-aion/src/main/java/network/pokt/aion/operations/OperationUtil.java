package network.pokt.aion.operations;

import network.pokt.core.model.Wallet;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.liquidplayer.javascript.JSObject;

public class OperationUtil {

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
