package com.bigkotik.calculator.events.queuehandler

import java.util.*

class QueueEventHandler<T>(private val events: Array<Event<T>>) {
    private val q: Queue<T> = LinkedList()

    fun add(elem: T) {
        q.add(elem)
        events.forEach { event -> event.update(elem) }
    }

    fun check(): Event<T>? {
        var result: Event<T>? = null
        for (event in events) {
            if (event.check()) {
                result = event
                break
            }
        }
        if (result != null) {
            events.forEach { event -> event.clear() }
        }
        return result
    }
}