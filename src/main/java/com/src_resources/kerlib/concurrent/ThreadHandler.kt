package com.src_resources.kerlib.concurrent

abstract class ThreadHandler {

    private val messageQueue: ThreadMessageQueue

    init {
        val looper = ThreadLooper.getLooper()
        if (looper == null) {
            throw IllegalThreadStateException("This thread hasn't prepared its ThreadLooper, and ThreadHandler "
                    + "must be initialized in a thread that has prepared its own ThreadLooper.")
        } else {
            messageQueue = looper.messageQueue
        }
    }

    fun obtainThreadMessage(what: Int) = ThreadMessage.obtain(this, what)

    fun sendThreadMessage(msg: ThreadMessage) = messageQueue.offerThreadMessage(msg)

    abstract fun handleThreadMessage(msg: ThreadMessage)
}
