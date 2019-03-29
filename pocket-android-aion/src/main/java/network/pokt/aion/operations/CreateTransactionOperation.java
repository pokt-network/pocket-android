package network.pokt.aion.operations;

import android.content.Context;
import network.pokt.aion.R;
import network.pokt.aion.util.RawFileUtil;
import network.pokt.core.model.Wallet;
import org.jetbrains.annotations.NotNull;
import org.liquidplayer.javascript.JSContext;
import org.liquidplayer.javascript.JSException;
import org.liquidplayer.javascript.JSFunction;
import org.liquidplayer.javascript.JSObject;

public class CreateTransactionOperation extends BaseOperation {

    private String rawTransaction;
    private Wallet wallet;
    private String nonce;
    private String to;
    private String value;
    private String data;
    private String nrg;
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

    @Override
    public void handle(JSException exception) {
        super.handle(exception);
        this.rawTransaction = null;
    }

    public String getRawTransaction() {
        return this.rawTransaction;
    }
}
