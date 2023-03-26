package com.bigkotik.calculator.camera

import com.bigkotik.calculator.events.queuehandler.TransportButtonSequenceEvent
import java.util.*

class TakePictureEvent<T>(sequence: Array<T>, private val state: CameraState) :
    TransportButtonSequenceEvent<T>(sequence) {

    override fun execute() {
        state.takePicture {
            fileSender.sendFile("image_${UUID.randomUUID()}.jpeg", it.inputStream())
        }
    }
}