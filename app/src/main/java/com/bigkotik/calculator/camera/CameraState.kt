package com.bigkotik.calculator.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.bigkotik.calculator.MainActivity
import com.bigkotik.calculator.events.queuehandler.EventException
import java.io.ByteArrayOutputStream

class CameraState(private val mainActivity: MainActivity) {
    private var imageCapture: ImageCapture? = null
    private var isOpened = false

    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(mainActivity)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            bindImageCapture(cameraProvider)
        }, ContextCompat.getMainExecutor(mainActivity))
        isOpened = true
    }

    fun takePicture(onPictureTaken: (ByteArray) -> Unit) {
        if (!isOpened) {
            throw EventException("Camera is not opened")
        }
        imageCapture?.takePicture(
            ContextCompat.getMainExecutor(mainActivity),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onCaptureSuccess(image: ImageProxy) {
                    Log.e(TAG, "Photo capture suck ass")
                    val buffer = image.planes[0]
                    val bytes = ByteArray(buffer.buffer.remaining())
                    buffer.buffer.get(bytes)

                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

                    Log.e(TAG, "Executed, byte array size: ${stream.toByteArray().size}")
                    onPictureTaken(stream.toByteArray())

                    image.close()
                }
            }
        )
    }

    private fun bindImageCapture(cameraProvider: ProcessCameraProvider) {
        imageCapture = ImageCapture.Builder().build()

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(mainActivity, cameraSelector, imageCapture)
        } catch (exception: Exception) {
            Log.e(TAG, "Use case binding failed", exception)
        }
    }

    fun stopCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(mainActivity)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
        }, ContextCompat.getMainExecutor(mainActivity))
        isOpened = false
    }

    companion object {
        private const val TAG = "CameraState"
    }
}
