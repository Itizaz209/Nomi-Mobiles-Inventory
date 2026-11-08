package com.example.ui.components

import android.Manifest
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanGlow
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@OptIn(ExperimentalGetImage::class)
@Composable
fun ImeiScannerDialog(
    title: String = "Scan Device Barcode / IMEI",
    onDismiss: () -> Unit,
    onScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    var camera by remember { mutableStateOf<Camera?>(null) }
    var isFlashOn by remember { mutableStateOf(false) }
    var manualInput by remember { mutableStateOf("") }
    var detectedImei by remember { mutableStateOf<String?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "scanline")
    val scanlineY by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanlineY"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .clip(RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(CyanAccent.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Point camera at IMEI barcode or 15-digit number",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_scanner_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Viewfinder Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    if (hasCameraPermission) {
                        AndroidView(
                            factory = { ctx ->
                                val previewView = PreviewView(ctx)
                                val cameraExecutor = Executors.newSingleThreadExecutor()
                                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                                cameraProviderFuture.addListener({
                                    val cameraProvider = cameraProviderFuture.get()
                                    val preview = Preview.Builder().build().also {
                                        it.surfaceProvider = previewView.surfaceProvider
                                    }

                                    val barcodeScanner = BarcodeScanning.getClient()

                                    val imageAnalysis = ImageAnalysis.Builder()
                                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                        .build()

                                    imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                                        val mediaImage = imageProxy.image
                                        if (mediaImage != null) {
                                            val image = InputImage.fromMediaImage(
                                                mediaImage,
                                                imageProxy.imageInfo.rotationDegrees
                                            )
                                            barcodeScanner.process(image)
                                                .addOnSuccessListener { barcodes ->
                                                    for (barcode in barcodes) {
                                                        val rawValue = barcode.rawValue ?: ""
                                                        if (rawValue.isNotBlank()) {
                                                            val cleanValue = cleanImeiString(rawValue)
                                                            if (cleanValue.isNotEmpty() && detectedImei == null) {
                                                                detectedImei = cleanValue
                                                                onScanned(cleanValue)
                                                            }
                                                        }
                                                    }
                                                }
                                                .addOnCompleteListener {
                                                    imageProxy.close()
                                                }
                                        } else {
                                            imageProxy.close()
                                        }
                                    }

                                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                                    try {
                                        cameraProvider.unbindAll()
                                        val boundCamera = cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            cameraSelector,
                                            preview,
                                            imageAnalysis
                                        )
                                        camera = boundCamera
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }, ContextCompat.getMainExecutor(ctx))

                                previewView
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Permission not granted state
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Camera Permission Required",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
                            ) {
                                Text("Grant Camera Access", color = Color.Black)
                            }
                        }
                    }

                    // Scanner Target Reticle Frame
                    Box(
                        modifier = Modifier
                            .size(width = 240.dp, height = 150.dp)
                            .border(2.dp, CyanAccent, RoundedCornerShape(12.dp))
                    ) {
                        // Animated Scan Beam
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .align(Alignment.TopCenter)
                                .padding(top = (150 * scanlineY).dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color.Transparent,
                                            CyanGlow,
                                            CyanGlow,
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                    }

                    // Torch Button Overlay
                    IconButton(
                        onClick = {
                            isFlashOn = !isFlashOn
                            camera?.cameraControl?.enableTorch(isFlashOn)
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Torch",
                            tint = if (isFlashOn) Color.Yellow else Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Paste & Instant Sample IMEI Actions
                Text(
                    text = "Quick Paste or Sample Test",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                            if (clipboard?.hasPrimaryClip() == true &&
                                clipboard.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true
                            ) {
                                val item = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
                                val clean = cleanImeiString(item)
                                if (clean.isNotEmpty()) {
                                    onScanned(clean)
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("paste_imei_button")
                    ) {
                        Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Paste", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            val sample = "35" + (1000000000000L..9999999999999L).random().toString()
                            onScanned(sample)
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("sample_imei_button")
                    ) {
                        Icon(Icons.Default.Smartphone, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sample IMEI", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Manual Input Fallback
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = manualInput,
                        onValueChange = { manualInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Or Type 15-digit IMEI") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("manual_imei_input")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (manualInput.isNotBlank()) {
                                onScanned(manualInput.trim())
                            }
                        },
                        enabled = manualInput.length >= 10,
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                        modifier = Modifier.testTag("apply_manual_imei_button")
                    ) {
                        Text("Apply", color = Color.Black)
                    }
                }
            }
        }
    }
}

private fun cleanImeiString(raw: String): String {
    // Look for a 14 to 16 digit number sequence (standard IMEI is 15 digits)
    val digitsOnly = raw.filter { it.isDigit() }
    if (digitsOnly.length in 14..16) {
        return digitsOnly.take(15)
    }
    // If not standard 15 digits, return trimmed raw text if it looks like a barcode code
    val regex = Regex("\\b\\d{14,16}\\b")
    val match = regex.find(raw)
    if (match != null) {
        return match.value.take(15)
    }
    return raw.trim()
}
