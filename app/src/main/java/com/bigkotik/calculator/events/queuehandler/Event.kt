package com.bigkotik.calculator.events.queuehandler

interface Event<T> {
    fun update(elem: T)
    fun clear()
    fun check(): Boolean
    fun execute()
}