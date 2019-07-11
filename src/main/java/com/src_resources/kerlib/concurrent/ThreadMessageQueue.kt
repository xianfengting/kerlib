package com.src_resources.kerlib.concurrent

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

internal class ThreadMessageQueue {
    private val messageBlockingQueue = LinkedBlockingQueue<ThreadMessage>()

    fun pollThreadMessage(timeout: Long): ThreadMessage? {
        return messageBlockingQueue.poll(timeout, TimeUnit.MILLISECONDS)
    }

    fun offerThreadMessage(msg: ThreadMessage): Boolean {
        return messageBlockingQueue.offer(msg)
    }
}
