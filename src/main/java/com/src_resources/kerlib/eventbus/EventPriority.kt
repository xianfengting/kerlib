package com.src_resources.kerlib.eventbus

//data class EventPriority(val intValue: Int) {
//    companion object {
//        val HIGHEST = EventPriority(30000)
//        val HIGHER = EventPriority(20000)
//        val HIGH = EventPriority(10000)
//        val DEFAULT = EventPriority(0)
//        val LOW = EventPriority(-10000)
//        val LOWER = EventPriority(-20000)
//        val LOWEST = EventPriority(-30000)
//    }
//}

enum class EventPriority(val intValue: Int) {
    // High priorities
    HIGH_10(10), HIGH_9(9), HIGH_8(8), HIGH_7(7), HIGH_6(6),
    HIGH_5(5), HIGH_4(4), HIGH_3(3), HIGH_2(2), HIGH_1(1),
    // Default priority
    DEFAULT(0),
    // Low priorities
    LOW_1(-1), LOW_2(-2), LOW_3(-3), LOW_4(-4), LOW_5(-5),
    LOW_6(-6), LOW_7(-7), LOW_8(-8), LOW_9(-9), LOW_10(-10);

    companion object {
        fun getEventPrioritiesFromHighToLow(): Array<EventPriority> {
            return arrayOf(HIGH_10, HIGH_9, HIGH_8, HIGH_7, HIGH_6, HIGH_5, HIGH_4, HIGH_3, HIGH_2, HIGH_1,
                    DEFAULT, LOW_1, LOW_2, LOW_3, LOW_4, LOW_5, LOW_6, LOW_7, LOW_8, LOW_9, LOW_10)
        }
        fun getEventPrioritiesFromLowToHigh(): Array<EventPriority> {
            return arrayOf(LOW_10, LOW_9, LOW_8, LOW_7, LOW_6, LOW_5, LOW_4, LOW_3, LOW_2, LOW_1,
                DEFAULT, HIGH_1, HIGH_2, HIGH_3, HIGH_4, HIGH_5, HIGH_6, HIGH_7, HIGH_8, HIGH_9, HIGH_10)
        }
    }
}
