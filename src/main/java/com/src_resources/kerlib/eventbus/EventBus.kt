package com.src_resources.kerlib.eventbus

import com.src_resources.kerlib.Disposable
import com.src_resources.kerlib.concurrent.ThreadHandler
import com.src_resources.kerlib.concurrent.ThreadLooper
import com.src_resources.kerlib.concurrent.ThreadMessage
import java.util.*
import java.util.concurrent.locks.LockSupport
import kotlin.reflect.KClass

class EventBus(busName: String): Disposable {
    private inner class EventPostingThreadHandler : ThreadHandler(eventPostingThread) {
        override fun handleThreadMessage(msg: ThreadMessage) {
            if (msg.obj is Event) {
                val event = msg.obj as Event
                callEventDispatchersOrderByPriorities(event)
            }
        }
    }
    private class EventPostingThread(name: String) : Thread(name) {
        var starterThreadTemp: Thread? = null
        var looper: ThreadLooper? = null
        override fun run() {
            // Prepare the ThreadLooper.
            ThreadLooper.prepare()
            // Then call LockSupport.unpark() to wake up the thread that
            // started this thread. Then that thread will initialize
            // "eventPostingThreadHandler" on this thread.
            LockSupport.unpark(starterThreadTemp)
            // Get the looper.
            looper = ThreadLooper.getLooperOfCurrentThread()
            // Start to loop.
            ThreadLooper.startLoop()
        }
    }

    var name = busName
        private set
    private val listenerDispatcher = ListenerEventDispatcher(this)
    private val subscriberDispatcher = SubscriberEventDispatcher(this)
    private val listenerList = ArrayList<EventListener>()
    private val subscriberList = ArrayList<Any>()
    private val eventPostingThread = EventPostingThread("EventPostingThread-$name")
    private var eventPostingThreadHandler: EventPostingThreadHandler? = null
    private var asyncPostingStarted = false
    private var disposed = false

    init {
        // There is no need to set it to daemon.
//        eventPostingThread.isDaemon = true
    }

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

    fun postEvent(event: Event, async: Boolean = false) {
        if (async) {
            postEventAsync(event)
        } else {
            callEventDispatchersOrderByPriorities(event)
        }
    }

    override fun dispose() {
        if (!disposed) {
            if (asyncPostingStarted) {
                eventPostingThread.looper?.stopLoop()
            }
        }
    }

    protected fun finalize() {
        dispose()
    }

    private fun callEventDispatchersOrderByPriorities(eventToDispatch: Event) {
        EventPriority.getEventPrioritiesFromHighToLow().forEach { priority ->
            listenerDispatcher.dispatchEvent(eventToDispatch, priority)
            subscriberDispatcher.dispatchEvent(eventToDispatch, priority)
        }
    }

    private fun postEventAsync(event: Event) {
        if (!asyncPostingStarted) {
            eventPostingThread.starterThreadTemp = Thread.currentThread()
            eventPostingThread.start()
            // Wait for the event posting thread wake the current thread up.
            // Because thread handlers need to be initialized with a thread that
            // has already called ThreadLooper.prepare().
            // We should make the current thread stop here and "eventPostingThread"
            // will call ThreadLooper.prepare(). After that, it will call
            // LockSupport.unpark() and the current thread will continue to
            // initialize "eventPostingThreadHandler".
            LockSupport.park()
            eventPostingThreadHandler = EventPostingThreadHandler()
            asyncPostingStarted = true
        }
        val message = eventPostingThreadHandler!!.obtainThreadMessage(1)
        message.obj = event
        eventPostingThreadHandler!!.sendThreadMessage(message)
    }
}
