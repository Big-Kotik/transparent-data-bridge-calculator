package com.bigkotik.calculator.events.queuehandler

abstract class ButtonsSequenceEvent<T>(private val sequence: Array<T>): Event<T> {
    private var idInSequence = 0

    override fun update(elem: T) {
        if (elem != sequence[idInSequence++]) {
            clear()
        }
    }

    override fun clear() {
        idInSequence = 0
    }

    override fun check(): Boolean {
        return idInSequence == sequence.size
    }

    abstract override fun execute()
}