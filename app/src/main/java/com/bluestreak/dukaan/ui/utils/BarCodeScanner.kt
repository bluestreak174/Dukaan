package com.bluestreak.dukaan.ui.utils

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bluestreak.dukaan.R
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

@Composable
fun BarCodeScannerIconButton(
    modifier: Modifier = Modifier,
    getBarCodeProduct: (String) -> Unit
){
    val context = LocalContext.current
    val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_EAN_13)
        .enableAutoZoom()
        .build()
    val scanner = GmsBarcodeScanning.getClient(context, options)
    IconButton(
        onClick = {
            scanner.startScan()
                .addOnSuccessListener { barcode ->
                    // Task completed successfully
                    val productBarCode: String? = barcode.rawValue
                    //Log.d("Dukaan", "$productBarCode")
                    getBarCodeProduct(productBarCode ?: "")
                }
                .addOnCanceledListener {
                    // Task canceled
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                }
        },
        modifier = Modifier.padding(0.dp),
        enabled = true,
    ) {
        Icon(
            Icons.Default.QrCode ,
            contentDescription = stringResource(R.string.scan_and_add_item),

            )
    }
}