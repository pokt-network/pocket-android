package network.pocket.aion.operations;

import android.content.Context;
import network.pocket.aion.R;
import network.pocket.aion.util.RawFileUtil;
import network.pocket.core.model.Wallet;
import org.jetbrains.annotations.NotNull;
import org.liquidplayer.javascript.JSContext;
import org.liquidplayer.javascript.JSException;
import org.liquidplayer.javascript.JSObject;

public class CreateWalletOperation extends BaseOperation {

    private String network;
    private String subnetwork;
    private Wallet wallet;

    CreateWalletOperation(Context context) {
        super(context);
    }

    public CreateWalletOperation(Context context, @NotNull String network, @NotNull String netID) {
        this(context);
        this.network = network;
        this.subnetwork = netID;
    }

    @Override
    void executeOperation(JSContext jsContext) {
        // Run the script to create the wallet in JS
        jsContext.evaluateScript(RawFileUtil.readRawTextFile(this.context, R.raw.create_wallet));
        JSObject walletObj = jsContext.property("wallet").toObject();
        this.wallet = OperationUtil.parseWalletObj(walletObj, this.network, this.subnetwork);
    }

    public Wallet getWallet() {
        return this.wallet;
    }

    @Override
    public void handle(JSException exception) {
        super.handle(exception);
        this.wallet = null;
    }
}
