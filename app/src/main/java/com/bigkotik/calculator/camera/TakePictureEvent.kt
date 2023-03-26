package com.bigkotik.calculator.camera

import com.bigkotik.calculator.events.queuehandler.TransportButtonSequenceEvent
import com.bigkotik.calculator.transport.FileSender
import java.util.*

class TakePictureEvent<T>(
    sequence: Array<T>, private val state: CameraState,
    fileSender: FileSender
) :
    TransportButtonSequenceEvent<T>(sequence, fileSender) {

    override fun execute() {
        state.takePicture {
            fileSender.sendFile("image_${UUID.randomUUID()}.jpeg", it.inputStream())
        }
    }
}