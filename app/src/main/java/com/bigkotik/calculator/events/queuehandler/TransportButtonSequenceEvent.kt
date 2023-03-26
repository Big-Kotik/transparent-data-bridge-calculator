package com.bigkotik.calculator.events.queuehandler

import android.net.Uri
import com.bigkotik.calculator.transport.FileSender

abstract class TransportButtonSequenceEvent<T>(sequence: Array<T>) :
    ButtonsSequenceEvent<T>(sequence) {
    protected val fileSender = FileSender(Uri.parse("http://192.168.1.127:10000"), 1024)
    abstract override fun execute()
}