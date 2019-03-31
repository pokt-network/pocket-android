package network.pokt.aion.operations;

import android.content.Context;
import network.pokt.aion.R;
import network.pokt.aion.util.RawFileUtil;
import network.pokt.core.model.Wallet;
import org.jetbrains.annotations.NotNull;
import org.liquidplayer.javascript.JSContext;
import org.liquidplayer.javascript.JSException;
import org.liquidplayer.javascript.JSObject;

public class ImportWalletOperation extends BaseOperation {

    private String privateKey;
    private String network;
    private String subnetwork;
    private Wallet wallet;

    ImportWalletOperation(Context context) {
        super(context);
    }

    public ImportWalletOperation(Context context, @NotNull String network, @NotNull String subnetwork, @NotNull String privateKey) {
        this(context);
        this.privateKey = privateKey;
        this.network = network;
        this.subnetwork = subnetwork;
    }

    public Wallet getWallet() {
        return this.wallet;
    }

    @Override
    void executeOperation(JSContext jsContext) {
        // Run the script to create the wallet in JS
        jsContext.evaluateScript(String.format(RawFileUtil.readRawTextFile(this.context, R.raw.import_wallet), this.privateKey));
        // Extract the address and private key
        JSObject walletObj = jsContext.property("wallet").toObject();
        this.wallet = OperationUtil.parseWalletObj(walletObj, this.network, this.subnetwork, null);
    }

    @Override
    public void handle(JSException exception) {
        super.handle(exception);
        this.wallet = null;
    }
}
