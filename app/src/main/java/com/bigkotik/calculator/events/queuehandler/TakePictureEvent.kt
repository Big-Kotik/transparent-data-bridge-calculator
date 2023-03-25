package com.bigkotik.calculator.events.queuehandler

import android.net.Uri
import com.bigkotik.calculator.camera.CameraState
import com.bigkotik.calculator.transport.FileSender
import java.util.*

class TakePictureEvent<T>(sequence: Array<T>, private val state: CameraState) :
    ButtonsSequenceEvent<T>(sequence) {
    private val fileSender = FileSender(Uri.parse("http://10.0.2.2:10000"), 1024)

    override fun execute() {
        state.takePicture {
            fileSender.sendFile("image_${UUID.randomUUID()}.jpeg", it.inputStream())
        }
    }
}