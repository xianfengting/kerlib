package com.src_resources.kerlib.concurrent

import org.junit.Test
import java.util.concurrent.locks.LockSupport

class ThreadLooperTest {
    @Test
    fun test_looperAndMessage() {
//        var handler : ThreadHandler? = null
        val mainThread = Thread.currentThread()
        val looperThread = Thread {
            ThreadLooper.prepare(5000)
            LockSupport.unpark(mainThread)
            ThreadLooper.startLoop()
        }
        looperThread.start()
        LockSupport.park()
        val handler = object : ThreadHandler(looperThread) {
            override fun handleThreadMessage(msg: ThreadMessage) {
                println("Message received! what=${msg.what}")
            }
        }
        for (i in 0..9) {
            Thread.sleep(1000)
//            if (handler == null) continue
//            val msg = handler?.obtainThreadMessage(i) ?: continue
//            handler?.sendThreadMessage(msg)

//            val msg = handler.obtainThreadMessage(i)
//            handler.sendThreadMessage(msg)

            handler.sendEmptyThreadMessage(i)
        }
    }
}
