package com.src_resources.kerlib.eventbus

import org.junit.Test

class EventBusTest {

    open class A
    class B : A()

    @Test
    fun test_subTypeTest() {
        val aClass = A::class
        val bInstance = B()
        println(aClass.isInstance(bInstance))
    }

    @Test
    fun test_eventListenerDispatching() {
        val eb = EventBus()
        val el = object : EventListener {
            override fun receiveEvent(event: Event) {
                println("Event received.")
            }
        }
    }
}