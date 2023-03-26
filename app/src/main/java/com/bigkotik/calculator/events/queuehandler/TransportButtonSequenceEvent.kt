package com.bigkotik.calculator.events.queuehandler

import com.bigkotik.calculator.transport.FileSender

abstract class TransportButtonSequenceEvent<T>(
    sequence: Array<T>,
    protected val fileSender: FileSender
) :
    ButtonsSequenceEvent<T>(sequence) {
    abstract override fun execute()
}