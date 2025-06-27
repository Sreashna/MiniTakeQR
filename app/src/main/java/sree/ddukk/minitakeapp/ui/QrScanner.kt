package sree.ddukk.minitakeapp.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import sree.ddukk.minitakeapp.data.cameraAccess.CameraPreview
import sree.ddukk.minitakeapp.data.qrCode.detectQrCode

@Composable
fun QRScannerScreen() {
    val context = LocalContext.current
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val linkFlow = remember { MutableStateFlow<String?>(null) }
    var lastDetectedTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    val link by linkFlow.collectAsState()

    LaunchedEffect(Unit) {
        while (true) {
            if (System.currentTimeMillis() - lastDetectedTime > 500) {
                if (linkFlow.value != null) {
                    linkFlow.value = null
                }
            }
            delay(300)
        }
    }

    Box(Modifier.fillMaxSize()) {
        CameraPreview { bmp ->
            detectQrCode(bmp, linkFlow) { lastDetectedTime = System.currentTimeMillis() }
        }
        // Dark overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
        )
        // Transparent scanning window with border
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(260.dp)
                .background(Color.Transparent)
                .border(3.dp, Color.White, RoundedCornerShape(8.dp))
        )
        // Instruction text below frame
        Text(
            text = "Align QR code to fill inside the frame",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            color = Color.White,
            fontSize = 16.sp
        )
        // Link & copy button
        AnimatedVisibility(
            visible = link != null,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically(),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 180.dp)
        ) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clickable {
                        link?.let {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Invalid URL", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F1F)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🔗 QR Code Detected", color = Color.White, fontSize = 18.sp)
                    link?.let {
                        Text(
                            it,
                            color = Color.Cyan,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                            }
                        )
                        Button(
                            onClick = {
                                val clip = ClipData.newPlainText("QR Link", it)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT)
                                    .show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Copy", color = Color.White)
                        }
                    }
                }
            }

        }
    }
}

