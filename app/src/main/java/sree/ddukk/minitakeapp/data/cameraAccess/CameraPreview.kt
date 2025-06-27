package sree.ddukk.minitakeapp.data.cameraAccess

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import sree.ddukk.minitakeapp.data.yuvToBitmap.yuvToBitmap
import java.util.concurrent.Executors


@Composable
fun CameraPreview(onFrame: (Bitmap) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val executor = remember { Executors.newSingleThreadExecutor() }

    AndroidView({ previewView }, modifier = Modifier.fillMaxSize())

    LaunchedEffect(true) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(context as ComponentActivity, arrayOf(Manifest.permission.CAMERA), 123)
        }

        val cameraProvider = ProcessCameraProvider.getInstance(context).get()

        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }

        val analysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build().also {
                it.setAnalyzer(executor) { imageProxy ->
                    yuvToBitmap(imageProxy)?.let(onFrame)
                    imageProxy.close()
                }
            }

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            analysis
        )
    }
}