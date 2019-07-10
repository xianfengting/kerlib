package com.src_resources.kerlib.eventbus

import java.util.*
import kotlin.reflect.KClass

class EventBus(busName: String) {
    var name = busName
        private set
    private val listenerDispatcher = ListenerEventDispatcher(this)
    private val listenerList = ArrayList<EventListener>()

    constructor() : this("EventBus-" + UUID.randomUUID().toString())

    fun registerEventListener(listener: EventListener, requiredEventType: KClass<out Event> = Event::class) {
        listenerList.add(listener)
        listenerDispatcher.addEVentListener(listener, requiredEventType)
    }

    fun unregisterEventListener(listener: EventListener) {
        listenerList.remove(listener)
        listenerDispatcher.removeEventListener(listener)
    }

    fun postEvent(event: Event) {
        listenerDispatcher.dispatchEvent(event)
    }
}
