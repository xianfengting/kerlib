package com.src_resources.kerlib.eventbus

import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty

abstract class EventBusObservableProperty<T>(initialValue: T,
                                             val targetEventBus: EventBus,
                                             val beforeChangeCallable: (EventBusObservableProperty<*>, T, T) -> Boolean
                                                    = { _, _, _ -> true },
                                             val afterChangeCallable: (EventBusObservableProperty<*>, T, T) -> Unit
                                                    = { _, _, _ -> },
                                             val async: Boolean = false)
            : ObservableProperty<T>(initialValue) {
    class BeforeChangeEvent<T>(val targetProperty: EventBusObservableProperty<*>,
                               val oldValue: T,
                               val newValue: T,
                               override var cancelled: Boolean = false) : Event(), Event.Cancellable
    class AfterChangeEvent<T>(val targetProperty: EventBusObservableProperty<*>,
                              val oldValue: T,
                              val newValue: T) : Event()

    override fun beforeChange(property: KProperty<*>, oldValue: T, newValue: T): Boolean {
        val propertyObj = property as EventBusObservableProperty<*>
        val cancelled = beforeChangeCallable(propertyObj, oldValue, newValue)
        val event = BeforeChangeEvent(propertyObj, oldValue, newValue, cancelled)
        targetEventBus.postEvent(event, async)
        return event.cancelled
    }

    override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {
        val propertyObj = property as EventBusObservableProperty<*>
        afterChangeCallable(propertyObj, oldValue, newValue)
        val event = AfterChangeEvent(propertyObj, oldValue, newValue)
        targetEventBus.postEvent(event, async)
    }
}
