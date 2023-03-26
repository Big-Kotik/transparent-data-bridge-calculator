package com.bigkotik.calculator.camera

import com.bigkotik.calculator.events.queuehandler.ButtonsSequenceEvent

class StartCameraEvent<T>(sequence: Array<T>, private val state: CameraState) :
    ButtonsSequenceEvent<T>(sequence) {
    override fun execute() {
        state.startCamera()
    }
}