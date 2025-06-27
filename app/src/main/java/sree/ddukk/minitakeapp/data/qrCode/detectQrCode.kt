package sree.ddukk.minitakeapp.data.qrCode

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.flow.MutableStateFlow

fun detectQrCode(bitmap: Bitmap, flow: MutableStateFlow<String?>, onDetected: () -> Unit) {
    val image = InputImage.fromBitmap(bitmap, 0)
    val scanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    )

    scanner.process(image)
        .addOnSuccessListener { barcodes ->
            val url = barcodes.firstOrNull { it.valueType == Barcode.TYPE_URL }?.url?.url
            if (url != null) {
                onDetected()
                flow.value = url
            }
        }
        .addOnFailureListener {
            Log.e("QR", "Detection failed: ${it.message}")
        }
}
