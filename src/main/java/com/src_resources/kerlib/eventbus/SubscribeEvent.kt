package com.src_resources.kerlib.eventbus

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class SubscribeEvent(val priority: EventPriority = EventPriority.DEFAULT)
