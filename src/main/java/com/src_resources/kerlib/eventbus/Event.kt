package com.src_resources.kerlib.eventbus

/**
 * Base class of events.
 */
open class Event {
    /**
     * If a class that extends the Event class wants to have result,
     * it must implement this interface.
     */
    interface Resultful {
        abstract var result: Result
    }

    /**
     * If a class that extends the Event class wants to be cancellable,
     * it must implement this interface.
     */
    interface Cancellable {
        abstract var cancelled: Boolean
    }

    /**
     * This enum class defines the results of an event.
     */
    enum class Result {
        DENIED,
        DEFAULT,
        ALLOWED
    }
}
