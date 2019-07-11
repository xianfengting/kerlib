package com.src_resources.kerlib.concurrent

data class ThreadMessage(val handler: ThreadHandler, var what: Int, var obj: Any?,
                         val list: List<Any>? = ArrayList<Any>()) {
    companion object {
        fun obtain(handler: ThreadHandler, what: Int): ThreadMessage {
            return ThreadMessage(handler, what, null)
        }
    }
}
