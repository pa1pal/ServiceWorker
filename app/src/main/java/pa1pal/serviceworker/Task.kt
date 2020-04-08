package pa1pal.serviceworker

import android.annotation.SuppressLint
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.os.Process
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread

abstract class Task<Result> {
    private val handlerThread = WorkerHandlerThread()
    lateinit var mHandler: Handler
    var result: Result? = null

    init {
        handlerThread.start()
        Thread(Runnable {
            onExecuteTask()
        }).start()
    }

    companion object {
        const val MESSAGE_POST_RESULT = 0x1
    }

    /**
     * This method should run on separate worker thread and send data
     */
    @WorkerThread
    abstract fun onExecuteTask()

    /**
     * This method will get called when Message queue have some data and handler will pick that
     * pass in the finish method
     */
    @MainThread
    abstract fun onTaskComplete(result: Result)

    /**
     * Method to send the message data to handler
     */
    @WorkerThread
    open fun sendData(result: Result) {
        val message: Message = handlerThread.handler!!.obtainMessage(
            MESSAGE_POST_RESULT)
        message.obj = result
        message.sendToTarget()
    }

    /**
     * Method to call finish method
     */
    open fun finish(result: Result) {
        onTaskComplete(result)
    }

    /**
     * Worker handler thread class to handle incoming messages and process them
     */
    inner class WorkerHandlerThread :
        HandlerThread("WorkerHandlerThread", Process.THREAD_PRIORITY_BACKGROUND) {
        var handler: Handler? = null
            private set

        @SuppressLint("HandlerLeak")
        override fun onLooperPrepared() {
            handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    when (msg.what) {
                        MESSAGE_POST_RESULT -> {
                            when (msg.what) {
                                MESSAGE_POST_RESULT -> finish(msg.obj as Result)
                            }
                        }
                    }
                }
            }
        }
    }
}