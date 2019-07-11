package com.src_resources.kerlib.eventbus

import java.util.*
import kotlin.reflect.KClass

class EventBus(busName: String) {
    var name = busName
        private set
    private val listenerDispatcher = ListenerEventDispatcher(this)
    private val subscriberDispatcher = SubscriberEventDispatcher(this)
    private val listenerList = ArrayList<EventListener>()
    private val subscriberList = ArrayList<Any>()

    constructor() : this("EventBus-" + UUID.randomUUID().toString())

    fun registerEventListener(listener: EventListener, requiredEventType: KClass<out Event> = Event::class,
                              priority: EventPriority = EventPriority.DEFAULT) {
        listenerList.add(listener)
        listenerDispatcher.addEventListener(listener, requiredEventType, priority)
    }

    fun registerEventSubscriber(subscriberObj: Any) {
        subscriberList.add(subscriberObj)
        subscriberDispatcher.addEventSubscriber(subscriberObj)
    }

    fun unregisterEventListener(listener: EventListener) {
        listenerList.remove(listener)
        listenerDispatcher.removeEventListener(listener)
    }

    fun unregisterEventSubscriber(subscriberObj: Any) {
        subscriberList.remove(subscriberObj)
        subscriberDispatcher.removeEventSubscriber(subscriberObj)
    }

    fun postEvent(event: Event) {
        callEventDispatchersOrderByPriorities(event)
    }

    private fun callEventDispatchersOrderByPriorities(eventToDispatch: Event) {
        EventPriority.getEventPrioritiesFromHighToLow().forEach { priority ->
            listenerDispatcher.dispatchEvent(eventToDispatch, priority)
            subscriberDispatcher.dispatchEvent(eventToDispatch, priority)
        }
    }
}
