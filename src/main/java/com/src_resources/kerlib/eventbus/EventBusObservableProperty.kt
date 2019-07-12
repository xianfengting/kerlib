package com.src_resources.kerlib.eventbus

import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty

class EventBusObservableProperty<T>(initialValue: T,
                                             val targetEventBus: EventBus,
                                             val beforeChangeCallable: (KProperty<*>, T, T) -> Boolean
                                                    = { _, _, _ -> true },
                                             val afterChangeCallable: (KProperty<*>, T, T) -> Unit
                                                    = { _, _, _ -> },
                                             val async: Boolean = false)
            : ObservableProperty<T>(initialValue) {
    class BeforeChangeEvent<T>(val targetProperty: KProperty<*>,
                               val oldValue: T,
                               val newValue: T,
                               override var cancelled: Boolean = false) : Event(), Event.Cancellable
    class AfterChangeEvent<T>(val targetProperty: KProperty<*>,
                              val oldValue: T,
                              val newValue: T) : Event()

    override fun beforeChange(property: KProperty<*>, oldValue: T, newValue: T): Boolean {
        val cancelled = beforeChangeCallable(property, oldValue, newValue)
        val event = BeforeChangeEvent(property, oldValue, newValue, cancelled)
        targetEventBus.postEvent(event, async)
        return !event.cancelled
    }

    override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {
        afterChangeCallable(property, oldValue, newValue)
        val event = AfterChangeEvent(property, oldValue, newValue)
        targetEventBus.postEvent(event, async)
    }
}
