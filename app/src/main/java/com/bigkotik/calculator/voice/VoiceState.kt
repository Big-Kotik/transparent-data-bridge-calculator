package com.bigkotik.calculator.voice

import android.media.MediaRecorder
import android.media.MediaRecorder.OutputFormat.OGG
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class VoiceState() {
    private var byteArrayOutputStream : ByteArrayOutputStream? = null
    private var recorder : MediaRecorder? = null
    private var executor = Executors.newSingleThreadExecutor()

    fun startRecording() {
        executor.submit {
            byteArrayOutputStream = ByteArrayOutputStream()

            val descriptors = ParcelFileDescriptor.createPipe()
            val parcelRead = ParcelFileDescriptor(descriptors[0])
            val parcelWrite = ParcelFileDescriptor(descriptors[1])

            val inputStream: InputStream = ParcelFileDescriptor.AutoCloseInputStream(parcelRead)

            recorder = MediaRecorder()
            recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder?.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder?.setOutputFile(parcelWrite.fileDescriptor)
            recorder?.prepare()

            try {
                recorder?.start()
            }
            catch (e : Exception) {
                Log.e(TAG, "Error on initializing recorder: ${e}: ${e.cause}: ${e.message}")
            }
            Log.e(TAG, "Started recorder")

            var read: Int
            val data = ByteArray(16384)

            while (inputStream.read(data, 0, data.size).also { read = it } != -1) {
                byteArrayOutputStream!!.write(data, 0, read)
            }

            byteArrayOutputStream!!.flush()
            Log.e(TAG, "Ended recorder")
        }
    }

    fun stopRecording(onRecordingTaken: (ByteArray) -> Unit) {
        recorder ?: return

        Log.e(TAG, "Ending recoder")
        recorder?.stop();
        recorder?.reset();
        recorder?.release();
        recorder = null

        executor.submit{
            Log.e(TAG, "Ready to execute sending context")
            onRecordingTaken(byteArrayOutputStream!!.toByteArray())
        }
    }

    companion object {
        const val TAG = "VoiceState"
    }

}
