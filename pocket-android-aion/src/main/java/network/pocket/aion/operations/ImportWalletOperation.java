package network.pocket.aion.operations;

import android.content.Context;
import network.pocket.aion.R;
import network.pocket.aion.util.RawFileUtil;
import network.pocket.core.model.Wallet;
import org.jetbrains.annotations.NotNull;
import org.liquidplayer.javascript.JSContext;
import org.liquidplayer.javascript.JSException;
import org.liquidplayer.javascript.JSObject;

/**
 * Import Wallet Operation.
 *
 * @see BaseOperation
 *
 */
public class ImportWalletOperation extends BaseOperation {

    /**
     * Wallet private key.
     */
    private String privateKey;
    /**
     * Wallet Network.
     */
    private String network;
    /**
     * Wallet Subnetwork.
     */
    private String subnetwork;
    /**
     * Wallet to be imported.
     * @see Wallet
     */
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

    /**
     * Runs the operation to import a Wallet.
     *
     * @param jsContext LiquidPlayer context.
     */
    @Override
    void executeOperation(JSContext jsContext) {
        // Run the script to create the wallet in JS
        jsContext.evaluateScript(String.format(RawFileUtil.readRawTextFile(this.context, R.raw.import_wallet), this.privateKey));
        // Extract the address and private key
        JSObject walletObj = jsContext.property("wallet").toObject();
        this.wallet = OperationUtil.parseWalletObj(walletObj, this.network, this.subnetwork);
    }

    /**
     * Registers an exception to be thrown.
     *
     * @param exception error to be shown.
     */
    @Override
    public void handle(JSException exception) {
        super.handle(exception);
        this.wallet = null;
    }
}
