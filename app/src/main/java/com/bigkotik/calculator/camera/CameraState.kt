package com.bigkotik.calculator.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.util.Log
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.bigkotik.calculator.MainActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraState(private val mainActivity: MainActivity) {
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    fun startCamera() {
        cameraExecutor = Executors.newSingleThreadExecutor()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(mainActivity)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            bindImageCapture(cameraProvider)
        }, ContextCompat.getMainExecutor(mainActivity))
    }

    fun takePicture(onPictureTaken: (ByteArray) -> Unit) {
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
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraState"
    }
}
