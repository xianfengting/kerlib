package com.src_resources.kerlib.concurrent

import java.lang.NullPointerException

abstract class ThreadHandler
        private constructor(arg_targetLooper: ThreadLooper?, arg_messageQueue: ThreadMessageQueue?) {

    private var targetLooper: ThreadLooper? = arg_targetLooper
    private var messageQueue: ThreadMessageQueue? = arg_messageQueue

    protected val looper: ThreadLooper
            get() = targetLooper as ThreadLooper

//    init {
//        val looper = ThreadLooper.getLooperOfCurrentThread()
//        if (looper == null) {
//            throw IllegalThreadStateException("This thread hasn't prepared its ThreadLooper, and ThreadHandler "
//                    + "must be initialized in a thread that has prepared its own ThreadLooper.")
//        } else {
//            this.targetLooper = looper
//            messageQueue = looper.messageQueue
//        }
//    }

    constructor() : this(null, null) {
        val looper = ThreadLooper.getLooperOfCurrentThread()
        if (looper == null) {
            throw IllegalThreadStateException("This thread hasn't prepared its ThreadLooper, and ThreadHandler "
                    + "must be initialized in a thread that has prepared its own ThreadLooper.")
        } else {
            this.targetLooper = looper
            messageQueue = looper.messageQueue
        }
    }

    constructor(targetThread: Thread) : this(null, null) {
        val looper = ThreadLooper.getLooper(targetThread)
        if (looper == null) {
            throw IllegalThreadStateException("The target thread hasn't prepared its ThreadLooper, and ThreadHandler "
                    + "must be initialized in a thread that has prepared its own ThreadLooper.")
        } else {
            this.targetLooper = looper
            messageQueue = looper.messageQueue
        }
    }

    fun obtainThreadMessage(what: Int) = ThreadMessage.obtain(this, what)

    fun sendThreadMessage(msg: ThreadMessage) = messageQueue!!.offerThreadMessage(msg)

    fun sendEmptyThreadMessage(what: Int) {
        val msg = obtainThreadMessage(what)
        msg.what = what
        sendThreadMessage(msg)
    }

    abstract fun handleThreadMessage(msg: ThreadMessage)

    internal fun processThreadMessageHandling(msg: ThreadMessage) {
        handleThreadMessage(msg)
    }

//    private fun throwNullPointerException() {
//        throw NullPointerException()
//    }
}
