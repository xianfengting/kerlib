package com.src_resources.kerlib.eventbus

import java.lang.IllegalArgumentException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.*

sealed class EventDispatcher(protected val targetEventBus: EventBus) {
    abstract fun addDispatchingTarget(target: Any)
    abstract fun removeDispatchingTarget(target: Any)
    abstract fun dispatchEvent(event: Event, priority: EventPriority)
}

class ListenerEventDispatcher(targetEventBus: EventBus)
        : EventDispatcher(targetEventBus) {
    private data class EventListenerWrapper(val listener: EventListener,
                                            val requiredEventType: KClass<out Event>,
                                            val priority: EventPriority)

    private val listenerList = ArrayList<EventListenerWrapper>()

    fun addEventListener(listener: EventListener, requiredEventType: KClass<out Event>, priority: EventPriority) {
        val wrapper = EventListenerWrapper(listener, requiredEventType, priority)
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

    override fun addDispatchingTarget(target: Any) {
        if (target is EventListener) {
            addEventListener(target, Event::class, EventPriority.DEFAULT)
        }
    }

    override fun removeDispatchingTarget(target: Any) {
        if (target is EventListener) {
            removeEventListener(target)
        }
    }

    override fun dispatchEvent(event: Event, priority: EventPriority) {
        listenerList.forEach { wrapper ->
            if (wrapper.requiredEventType.isInstance(event) && wrapper.priority.equals(priority))
                wrapper.listener.receiveEvent(event)
        }
    }
}

class SubscriberEventDispatcher(targetEventBus: EventBus)
        : EventDispatcher(targetEventBus) {
    private data class SubscriberWrapper(val subscriberObj: Any,
                                         val subscriberEventSubscribingMethodList: ArrayList<SubscribingMethodWrapper>)
    private data class SubscribingMethodWrapper(val method: KFunction<*>,
                                                val ownerSubscriber: Any,
                                                val priority: EventPriority)
    class IllegalSubscribingMethodException(message: String) : RuntimeException(message)

    private val eventSubscribingMethodList = ArrayList<SubscribingMethodWrapper>()
    private val subscriberWrapperList = ArrayList<SubscriberWrapper>()

    fun addEventSubscriber(subscriberObj: Any) {
        val wrapper = initSubscriberWrapperForSubscriber(subscriberObj)
        subscriberWrapperList.add(wrapper)
//        wrapper.subscriberEventSubscribingMethodList.forEach {
//            val methodWrapper = SubscribingMethodWrapper(it, wrapper.subscriberObj)
//            eventSubscribingMethodList.add(methodWrapper)
//        }
        eventSubscribingMethodList.addAll(wrapper.subscriberEventSubscribingMethodList)
    }

    fun removeEventSubscriber(subscriberObj: Any) {
        subscriberWrapperList.forEach { wrapper ->
            if (wrapper.subscriberObj.equals(subscriberObj)) {
                wrapper.subscriberEventSubscribingMethodList.forEach {
                    eventSubscribingMethodList.remove(it)
                }
                return@forEach
            }
        }
    }

    override fun addDispatchingTarget(target: Any) {
        addEventSubscriber(target)
    }

    override fun removeDispatchingTarget(target: Any) {
        removeEventSubscriber(target)
    }

    override fun dispatchEvent(event: Event, priority: EventPriority) {
        eventSubscribingMethodList.forEach { subscribingMethodWrapper ->
            if (subscribingMethodWrapper.priority.equals(priority))
                try {
                    subscribingMethodWrapper.method.call(subscribingMethodWrapper.ownerSubscriber, event)
                } catch (exception: IllegalArgumentException) {
                    // TODO
                }
        }
    }

    private fun initSubscriberWrapperForSubscriber(subscriberObj: Any): SubscriberWrapper {
        val subscriberClass = subscriberObj::class
        val subscribingMethodList = ArrayList<SubscribingMethodWrapper>()
        val wrapper = SubscriberWrapper(subscriberObj, subscribingMethodList)
        subscriberClass.memberFunctions.forEach { function ->
//            if (function is KFunction<Event>) {}
            val annotation = function.findAnnotation<SubscribeEvent>()
            if (annotation != null) {
                if (function.valueParameters.size == 1) {
                    val methodWrapper = SubscribingMethodWrapper(function, subscriberObj, annotation.priority)
                    subscribingMethodList.add(methodWrapper)
                } else
                    throw IllegalSubscribingMethodException("The subscribing function " +
                            "\"${function.name}\" must have only one parameter, but in " +
                            "fact it has ${function.valueParameters.size} parameters.")
            }
        }
//        subscriberWrapperList.add(wrapper)
//        eventSubscribingMethodList.addAll(subscribingMethodList)
        return wrapper
    }
}
