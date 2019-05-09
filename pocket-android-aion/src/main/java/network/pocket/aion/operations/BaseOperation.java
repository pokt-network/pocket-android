package network.pocket.aion.operations;

import android.content.Context;
import network.pocket.aion.R;
import network.pocket.aion.util.RawFileUtil;
import org.liquidplayer.javascript.JSContext;
import org.liquidplayer.javascript.JSException;
import org.liquidplayer.node.Process;
import org.liquidplayer.node.Process.EventListener;

import java.util.concurrent.Semaphore;

/**
 * Abstract class that represents an Operation
 */
abstract class BaseOperation implements JSContext.IJSExceptionHandler, EventListener {

    Context context;
    Semaphore semaphore;
    private Process process;
    String errorMsg;

    public BaseOperation(Context context) {
        this.semaphore = new Semaphore(0);
        this.context = context;
    }

    /**
     * Gets the error message.
     *
     * @return error message
     *
     */
    public String getErrorMsg() {
        return this.errorMsg;
    }

    /**
     * Starts the process.
     *
     * @return whether the process was started correctly or was already running.
     *
     */
    public boolean startProcess() {
        if (process != null) {
            this.errorMsg = "Operation already executed";
            return false;
        }
        this.process = new Process(
                this.context,
                this.getOperationID(),
                Process.kMediaAccessPermissionsRead,
                this
        );
        this.semaphore.acquireUninterruptibly();
        return true;
    }

    /**
     * Executed when the process starts.
     *
     * @param process process to be executed.
     * @param jsContext
     */
    @Override
    public void onProcessStart(Process process, JSContext jsContext) {
        try {
            // Configures the JS context to run
            jsContext.setExceptionHandler(this);
            jsContext.evaluateScript(RawFileUtil.readRawTextFile(this.context, R.raw.globals));
            jsContext.evaluateScript(RawFileUtil.readRawTextFile(this.context, R.raw.web3_aion));
            jsContext.evaluateScript(RawFileUtil.readRawTextFile(this.context, R.raw.aion_instance));
            // Calls execute operation
            this.executeOperation(jsContext);
        } catch (Exception e) {
            this.errorMsg = e.getMessage();
        }
    }

    /**
     * Executed when the process finish execution.
     *
     * @param process process that was executed.
     * @param exitCode
     */
    @Override
    public void onProcessExit(Process process, int exitCode) {
        this.semaphore.release();
    }

    /**
     * Executed when the process finish execution.
     *
     * @param process process that failed.
     * @param error
     */
    @Override
    public void onProcessFailed(Process process, Exception error) {
        this.semaphore.release();
        this.errorMsg = error.getMessage();
    }

    /**
     * Executed when the will finish execution.
     *
     * @param process process that failed.
     * @param exitCode
     */
    @Override
    public void onProcessAboutToExit(Process process, int exitCode) {
        this.semaphore.release();
    }

    @Override
    public void handle(JSException exception) {
        this.errorMsg = exception.getMessage();
    }

    /**
     * Executed by onProcessStart to run custom operation code
     *
     */
    abstract void executeOperation(JSContext jsContext);

    /**
     * Gets the unique id of this operation
     *
     * @return the id of the Operation.
     */
    private String getOperationID() {
        int randomId = new Double(Math.random() * ((Integer.MAX_VALUE - 0) + 1) + 0).intValue();
        return String.format("%s-%d", this.getClass().getName(), randomId);
    }
}
