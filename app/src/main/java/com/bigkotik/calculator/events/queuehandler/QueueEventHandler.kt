package com.bigkotik.calculator.events.queuehandler

import java.util.*

class QueueEventHandler<T>(private val events: Array<Event<T>>) {
    private val q: Queue<T> = LinkedList()

    fun add(elem: T) {
        q.add(elem)
        events.forEach { event -> event.update(elem) }
        check()
    }

    private fun check() {
        var flag = false
        events.forEach { event ->
            if (event.check()) {
                flag = true
                event.execute()
            }
        }
        if (flag) {
            events.forEach { event -> event.clear() }
        }
    }
}