package network.pokt.pocketaion.operation

import android.content.Context
import network.pokt.pocketaion.R
import network.pokt.pocketaion.util.RawFileUtil
import org.liquidplayer.javascript.JSContext
import org.liquidplayer.javascript.JSException
import org.liquidplayer.node.Process.EventListener
import org.liquidplayer.node.Process
import java.util.concurrent.Semaphore

abstract class BaseOperation : JSContext.IJSExceptionHandler, EventListener {

    var context: Context? = null
    var semaphore: Semaphore? = null
    private var process: Process? = null
    var errorMsg: String? = null

    constructor(context: Context) {
        this.semaphore = Semaphore(0)
        this.context = context
    }

    // Creates the process
    fun startProcess(): Boolean {
        if (process != null) {
            this.errorMsg = "Operation already executed"
            return false
        }
        this.process = Process(
            this.context,
            this.getOperationID(),
            Process.kMediaAccessPermissionsRead,
            this
        )
        this.semaphore?.acquireUninterruptibly()
        return true
    }

    override fun onProcessStart(process: Process, jsContext: JSContext) {
        try {
            // Configures the JS context to run
            jsContext.setExceptionHandler(this)
            jsContext.evaluateScript(RawFileUtil.readRawTextFile(this.context!!, R.raw.globals))
            jsContext.evaluateScript(RawFileUtil.readRawTextFile(this.context!!, R.raw.web3_aion))
            jsContext.evaluateScript(RawFileUtil.readRawTextFile(this.context!!, R.raw.aion_instance))
            // Calls execute operation
            this.executeOperation(jsContext)
        } catch (e: Exception) {
            this.errorMsg = e.message
        }

    }

    override fun onProcessExit(process: Process, exitCode: Int) {
        this.semaphore!!.release()
    }

    override fun onProcessFailed(process: Process, error: Exception) {
        this.semaphore!!.release()
        this.errorMsg = error.message
    }

    override fun onProcessAboutToExit(process: Process, exitCode: Int) {
        this.semaphore!!.release()
    }

    override fun handle(exception: JSException) {
        this.errorMsg = exception.message
    }

    // Gets called by onProcessStart to run custom operation code
    internal abstract fun executeOperation(jsContext: JSContext)

    // Gets the unique id of this operation
    private fun getOperationID(): String {
        val randomId = (Math.random() * (Integer.MAX_VALUE - 0 + 1) + 0).toInt()
        return String.format("%s-%d", this.javaClass.getName(), randomId)
    }
}