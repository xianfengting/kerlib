package com.src_resources.kerlib.eventbus

import kotlin.reflect.KClass

abstract class EventDispatcher(protected val targetEventBus: EventBus) {
    abstract fun dispatchEvent(event: Event)
}

class ListenerEventDispatcher(targetEventBus: EventBus)
        : EventDispatcher(targetEventBus) {
    private data class EventListenerWrapper(val listener: EventListener,
                                            val requiredEventType: KClass<out Event>)

    private val listenerList = ArrayList<EventListenerWrapper>()

    fun addEVentListener(listener: EventListener, requiredEventType: KClass<out Event>) {
        val wrapper = EventListenerWrapper(listener, requiredEventType)
        listenerList.add(wrapper)
    }

    fun removeEventListener(listener: EventListener) {
        listenerList.forEach { wrapper ->
            if (wrapper.listener.equals(listener)) {
                listenerList.remove(wrapper)
                return@forEach
            }
        }
    }

    override fun dispatchEvent(event: Event) {
        listenerList.forEach { wrapper ->
            if (wrapper.requiredEventType.isInstance(event))
                wrapper.listener.receiveEvent(event)
        }
    }
}
