package com.src_resources.kerlib.eventbus

/**
 * This interface provides the way to receive events by implementing interfaces.<br>
 * If you want to receive events that posted on event bus, except using the
 * SubscribeEvent annotation, you can also implement this interface and then
 * register one instance of the listener to the event bus.
 */
interface EventListener {
    /**
     * If an event is posted on the event bus that this listener has registered to,
     * the event bus will call this method back and you can handle this event here.
     * @param event The event that is posted on the event bus.
     */
    abstract fun receiveEvent(event: Event)
}
