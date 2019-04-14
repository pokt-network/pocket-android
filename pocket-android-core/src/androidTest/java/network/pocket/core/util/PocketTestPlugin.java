package network.pocket.core.util;

import network.pocket.core.Pocket;
import org.jetbrains.annotations.NotNull;

public class PocketTestPlugin extends Pocket {

    public PocketTestPlugin(@NotNull String devId, @NotNull String networkName, @NotNull String[] netIds, int maxNodes, int requestTimeOut) {
        super(devId, networkName, netIds, maxNodes, requestTimeOut);
    }

//    @NotNull
//    @Override
//    public Wallet createWallet(@NotNull String network, @NotNull String netID, @Nullable JSONObject data) {
//        return new Wallet("0x0", "0x1", network, netID, data);
//    }
//
//    @NotNull
//    @Override
//    public Wallet importWallet(@NotNull String privateKey, @Nullable String address, @NotNull String network, @NotNull String netID, @Nullable JSONObject data) {
//        return new Wallet(privateKey, address, network, netID, data);
//    }
}
