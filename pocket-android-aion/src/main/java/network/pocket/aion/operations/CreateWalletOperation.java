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
 * Create Wallet Operation.
 *
 * @see BaseOperation
 *
 */
public class CreateWalletOperation extends BaseOperation {

    /**
     * Wallet network.
     */
    private String network;
    /**
     * Wallet subnetwork.
     */
    private String subnetwork;
    /**
     * Wallet to be parsed.
     */
    private Wallet wallet;

    CreateWalletOperation(Context context) {
        super(context);
    }

    public CreateWalletOperation(Context context, @NotNull String network, @NotNull String netID) {
        this(context);
        this.network = network;
        this.subnetwork = netID;
    }

    /**
     * Runs the operation to create a Wallet.
     *
     * @param jsContext LiquidPlayer context.
     */
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
