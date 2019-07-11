package com.src_resources.kerlib.eventbus

import org.junit.Test
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.valueParameters

class EventBusTest {

    open class A
    class B : A()
    class C {
        fun test(i: Int) {}
    }
    class Subscriber {
        @SubscribeEvent(priority = EventPriority.HIGH_10)
        fun handleEvent1(event: Event) {
            println("Event received in Subscriber class. Level high 10")
        }

        @SubscribeEvent(priority = EventPriority.HIGH_8)
        fun handleEvent2(event: Event) {
            println("Event received in Subscriber class. Level high 8")
        }

        @SubscribeEvent(priority = EventPriority.HIGH_9)
        fun handleEvent3(event: Event) {
            println("Event received in Subscriber class. Level high 9")
        }
    }

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
                println("Event received. Level high 7")
            }
        }
        eb.registerEventListener(el, priority = EventPriority.HIGH_7)
        val subscriber = Subscriber()
        eb.registerEventSubscriber(subscriber)
        val e = Event()
        eb.postEvent(e)
    }

//    @Test
//    fun test_kFunction() {
//        val c = C::class
//        val instance = c.createInstance()
//        c.memberFunctions.forEach { func ->
//            func.valueParameters.forEach { kPar ->
//                println(kPar.name)
//                println(kPar.kind.name)
//                println(kPar.kind.ordinal)
//                println(kPar.kind.declaringClass.name)
//            }
//        }
//    }
}