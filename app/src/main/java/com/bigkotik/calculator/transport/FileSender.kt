package com.bigkotik.calculator.transport

import android.net.Uri
import android.util.Log
import com.bigkotik.transparentdatabridge.*
import com.google.protobuf.ByteString
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.io.InputStream

class FileSender(serverUri: Uri, private val bufSize: Int) {
    private val channel = let {
        val builder = ManagedChannelBuilder.forAddress(serverUri.host, serverUri.port)
        if (serverUri.scheme == "https") {
            builder.useTransportSecurity()
        } else {
            builder.usePlaintext()
        }
        builder.executor(Dispatchers.IO.asExecutor()).build()
    }

    private val stub =
        TransparentDataBridgeServiceGrpcKt.TransparentDataBridgeServiceCoroutineStub(channel)


    fun sendFile(filename: String, stream: InputStream) {
        Dispatchers.IO.asExecutor().execute {
            runBlocking {
                try {
                    stub.sendChunks(fileToFlow(filename, stream))
                } catch (e: StatusException) {
                    Log.e(TAG, "Status exception: ${e}")
                } catch (e: IOException) {
                    Log.e(TAG, "IO exception: ${e}")
                }
            }
        }
    }

    private fun fileToFlow(filename: String, stream: InputStream): Flow<File> = flow {
        emitFilename(filename)
        emitFileContents(stream)
    }.flowOn(Dispatchers.IO)

    private suspend fun FlowCollector<File>.emitFilename(filename: String) {
        val file = File.newBuilder()
            .setRequest(
                SendFileRequest.newBuilder()
                    .setFileName(filename)
            )
            .build()
        emit(file)
    }

    private suspend fun FlowCollector<File>.emitFileContents(stream: InputStream) {
        val buffer = ByteArray(bufSize)
        var bytesRead: Int
        while (true) {
            bytesRead = stream.read(buffer)
            if (bytesRead == -1) {
                break
            }
            val fileChunk = FileChunk.newBuilder()
                .setChunk(ByteString.copyFrom(buffer, 0, bytesRead))
            val file = File.newBuilder()
                .setChunk(fileChunk)
                .build()
            emit(file)
        }
    }
    companion object {
        const val TAG = "fucking transport"
    }
}