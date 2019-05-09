package network.pocket.aion.operations;

import android.content.Context;
import network.pocket.aion.R;
import network.pocket.aion.util.RawFileUtil;
import network.pocket.core.model.Wallet;
import org.jetbrains.annotations.NotNull;
import org.liquidplayer.javascript.JSContext;
import org.liquidplayer.javascript.JSException;
import org.liquidplayer.javascript.JSFunction;
import org.liquidplayer.javascript.JSObject;

/**
 * Create Transaction Operation.
 *
 * @see BaseOperation
 *
 */
public class CreateTransactionOperation extends BaseOperation {

    /**
     * Raw transaction operation.
     */
    private String rawTransaction;
    /**
     * Wallet used by transaction.
     * @see Wallet
     */
    private Wallet wallet;
    /**
     * Transaction Nonce.
     */
    private String nonce;
    /**
     * Destination address.
     */
    private String to;
    /**
     * Amount to be sent.
     */
    private String value;
    /**
     * Transaction data.
     */
    private String data;
    /**
     * Integer of the gas provided for the transaction execution. It will return unused gas.
     */
    private String nrg;
    /**
     * Returns the current price per gas in wei.
     */
    private String nrgPrice;

    CreateTransactionOperation(Context context) {
        super(context);
    }

    public CreateTransactionOperation(Context context, @NotNull Wallet wallet, @NotNull String nonce, @NotNull String to, String value, String data, @NotNull String nrg, @NotNull String nrgPrice) {
        this(context);
        this.wallet = wallet;
        this.nonce = nonce;
        this.to = to;
        this.value = value;
        this.data = data == null ? "" : data;
        this.nrg = nrg;
        this.nrgPrice = nrgPrice;
    }

    /**
     * Runs the operation to create a transaction.
     *
     * @param jsContext LiquidPlayer context.
     */
    @Override
    void executeOperation(JSContext jsContext) {
        // Parse input parameters for transaction signature
        String privateKey = this.wallet.getPrivateKey();
        String jsCode = String.format(RawFileUtil.readRawTextFile(this.context, R.raw.create_transaction),
                this.nonce, this.to, this.value, this.data, this.nrg, this.nrgPrice, privateKey);

        // Run code
        jsContext.evaluateScript(jsCode);

        // Get promise and set callback
        JSObject promise = jsContext.property("txPromise").toObject();
        promise.property("then").toFunction().call(promise, this.getTransactionSignatureCallback(jsContext));
    }

    /**
     * Gets the callback for this transaction.
     *
     * @param jsContext LiquidPlayer context.
     *
     * @return JSFunction
     */
    private JSFunction getTransactionSignatureCallback(JSContext jsContext) {
        return new JSFunction(jsContext, "then") {
            public void then(JSObject result) {
                CreateTransactionOperation opInstance = CreateTransactionOperation.this;
                // Create the new Transaction
                if(result == null) {
                    opInstance.rawTransaction = null;
                    return;
                }
                CreateTransactionOperation.this.rawTransaction = result.property("rawTransaction").toString();
            }
        };
    }

    /**
     * Registers an exception to be thrown.
     *
     * @param exception error to be shown.
     */
    @Override
    public void handle(JSException exception) {
        super.handle(exception);
        this.rawTransaction = null;
    }

    /**
     *
     * @return rawTransaction String
     */
    public String getRawTransaction() {
        return this.rawTransaction;
    }
}
