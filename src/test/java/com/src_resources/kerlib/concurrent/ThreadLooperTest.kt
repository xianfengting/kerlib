package com.src_resources.kerlib.concurrent

import org.junit.Test

class ThreadLooperTest {
    @Test
    fun test_looperAndMessage() {
        var handler : ThreadHandler? = null
        val looperThread = Thread {
            ThreadLooper.prepare(5000)
            handler = object : ThreadHandler() {
                override fun handleThreadMessage(msg: ThreadMessage) {
                    println("Message received! what=${msg.what}")
                }
            }
            ThreadLooper.startLoop()
        }
        looperThread.start()
        for (i in 0..9) {
            Thread.sleep(1000)
            if (handler == null) continue
            val msg = handler?.obtainThreadMessage(i) ?: continue
            handler?.sendThreadMessage(msg)
        }
    }
}
