package com.src_resources.kerlib.concurrent

import java.lang.RuntimeException

class ThreadLooper private constructor(val messagePollingTimeout: Long, val loopOwnerThread: Thread) {
    companion object {
        private val looperThreadLocal = ThreadLocal<ThreadLooper>()
        private val looperMap = HashMap<Thread, ThreadLooper>()

        fun getLooperOfCurrentThread(): ThreadLooper? {
            return looperThreadLocal.get()
        }

        fun getLooper(thread: Thread): ThreadLooper? {
            return looperMap[thread]
        }

        fun prepare(messagePollingTimeout: Long = Long.MAX_VALUE) {
            if (looperThreadLocal.get() == null) {
                val thread = Thread.currentThread()
                val looper = ThreadLooper(messagePollingTimeout, thread)
                looperThreadLocal.set(looper)
                looperMap[thread] = looper
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

    class ThreadLooperException(msg: String) : RuntimeException(msg)

    internal val messageQueue = ThreadMessageQueue()
    var isLoopRunning = false
            private set

    fun startLoop() {
        if (!Thread.currentThread().equals(loopOwnerThread))
            throw ThreadLooperException("Only the owner thread of this ThreadLooper can start this loop.")
        if (isLoopRunning)
            throw ThreadLooperException("The loop has already started running.")
        try {
            isLoopRunning = true
            while (true) {
                // Get one message from ThreadMessageQueue.
                // If the message is null, it means exiting the loop.
                val message = messageQueue.pollThreadMessage(messagePollingTimeout) ?: break
                // Pass the message to the handler.
                passThreadMessageToHandler(message)
            }
        } finally {
            isLoopRunning = false
        }
    }

    fun stopLoop() {
        loopOwnerThread.interrupt()
    }

    private fun passThreadMessageToHandler(msg: ThreadMessage) {
        msg.handler.processThreadMessageHandling(msg)
    }
}
