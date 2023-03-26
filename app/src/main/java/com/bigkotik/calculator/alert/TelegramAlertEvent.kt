package com.bigkotik.calculator.alert

import com.bigkotik.calculator.events.queuehandler.ButtonsSequenceEvent

class TelegramAlertEvent<T>(sequence: Array<T>) :
    ButtonsSequenceEvent<T>(sequence) {
    override fun execute() {

    }

}