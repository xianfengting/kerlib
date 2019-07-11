package com.src_resources.kerlib.concurrent

class ThreadLooper private constructor(val messagePollingTimeout: Long) {
    companion object {
        private val looperThreadLocal = ThreadLocal<ThreadLooper>()

        fun getLooper(): ThreadLooper? {
            return looperThreadLocal.get()
        }

        fun prepare(messagePollingTimeout: Long = 60000) {
            if (looperThreadLocal.get() == null) {
                val looper = ThreadLooper(messagePollingTimeout)
                looperThreadLocal.set(looper)
            } else
                throw IllegalThreadStateException("This thread has already prepared its own ThreadLooper.")
        }

        fun startLoop() {
            checkWhetherPreparedLooper()
            looperThreadLocal.get()?.startLoop()
        }

        private fun checkWhetherPreparedLooper() {
            if (looperThreadLocal.get() == null)
                throw IllegalThreadStateException("This thread hasn't prepared its own ThreadLooper yet. "
                        + "It must call the method prepare() before using ThreadLooper.")
        }
    }

    internal val messageQueue = ThreadMessageQueue()

    fun startLoop() {
        while (true) {
            // Get one message from ThreadMessageQueue.
            // If the message is null, it means exiting the loop.
            val message = messageQueue.pollThreadMessage(messagePollingTimeout) ?: break
            // Pass the message to the handler.
            message.handler.handleThreadMessage(message)
        }
    }
}
