package com.example.ui.screens.purchases

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.PhoneEntity
import com.example.data.model.PurchaseEntity
import com.example.ui.components.ImeiScannerDialog
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.RedDanger
import com.example.util.MobileModelsData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPurchaseDialog(
    existingSuppliers: List<String>,
    onDismiss: () -> Unit,
    onPurchaseConfirmed: (PurchaseEntity, PhoneEntity) -> Unit
) {
    // Handset selection
    var isIPhone by remember { mutableStateOf(true) }
    var selectedBrand by remember { mutableStateOf("Apple") }
    var selectedModel by remember { mutableStateOf(MobileModelsData.iPhoneModels.first()) }
    var isModelDropdownOpen by remember { mutableStateOf(false) }

    val availableColors = remember(selectedModel, isIPhone) {
        if (isIPhone) MobileModelsData.getIPhoneColors(selectedModel) else MobileModelsData.androidColors
    }
    var selectedColor by remember { mutableStateOf(availableColors.first()) }
    var isColorDropdownOpen by remember { mutableStateOf(false) }

    var batteryHealthValue by remember { mutableFloatStateOf(92f) }
    var imei by remember { mutableStateOf("") }
    var showImeiScanner by remember { mutableStateOf(false) }

    var selectedStorage by remember { mutableStateOf("128 GB") }
    var selectedPta by remember { mutableStateOf("PTA Approved") }
    var selectedCondition by remember { mutableStateOf("10/10 Like New / Pinpack") }

    // Financials
    var purchasePriceText by remember { mutableStateOf("") }
    var paidAmountText by remember { mutableStateOf("") }
    var askingPriceText by remember { mutableStateOf("") }

    // Supplier state with disappearing placeholder "Select or Enter Supplier"
    var supplierText by remember { mutableStateOf("") }
    var supplierPhone by remember { mutableStateOf("") }
    var isSupplierDropdownOpen by remember { mutableStateOf(false) }

    val paymentMethods = listOf("Cash", "Bank Transfer", "Raast")
    var selectedPaymentMethod by remember { mutableStateOf("Cash") }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .padding(vertical = 12.dp)
                .clip(RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
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
                                .background(GoldAmber.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = null,
                                tint = GoldAmber,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Record Mobile Purchase",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Registers handset purchase & updates stock automatically",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Form
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (errorMessage != null) {
                        Surface(
                            color = RedDanger.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                color = RedDanger,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // 1. SUPPLIER INPUT (Requirement 11: "Select or Enter Supplier" placeholder)
                    Column {
                        Text(
                            text = "Purchase Supplier",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        ExposedDropdownMenuBox(
                            expanded = isSupplierDropdownOpen,
                            onExpandedChange = { isSupplierDropdownOpen = !isSupplierDropdownOpen }
                        ) {
                            OutlinedTextField(
                                value = supplierText,
                                onValueChange = { supplierText = it },
                                placeholder = {
                                    // Requirement 11 exact placeholder:
                                    Text(
                                        text = "Select or Enter Supplier",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                singleLine = true,
                                trailingIcon = {
                                    if (existingSuppliers.isNotEmpty()) {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSupplierDropdownOpen)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .testTag("purchase_supplier_input")
                            )

                            if (existingSuppliers.isNotEmpty()) {
                                ExposedDropdownMenu(
                                    expanded = isSupplierDropdownOpen,
                                    onDismissRequest = { isSupplierDropdownOpen = false }
                                ) {
                                    existingSuppliers.forEach { supp ->
                                        DropdownMenuItem(
                                            text = { Text(supp) },
                                            onClick = {
                                                supplierText = supp
                                                isSupplierDropdownOpen = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = supplierPhone,
                            onValueChange = { supplierPhone = it },
                            label = { Text("Supplier Phone (Optional)") },
                            placeholder = { Text("0300-9876543") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // 2. DEVICE TYPE & BRAND
                    Column {
                        Text(
                            text = "Handset Type",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = isIPhone,
                                onClick = {
                                    isIPhone = true
                                    selectedBrand = "Apple"
                                    selectedModel = MobileModelsData.iPhoneModels.first()
                                    selectedColor = MobileModelsData.getIPhoneColors(selectedModel).first()
                                },
                                label = { Text("Apple iPhone") },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CyanAccent, selectedLabelColor = Color.Black)
                            )
                            FilterChip(
                                selected = !isIPhone,
                                onClick = {
                                    isIPhone = false
                                    selectedBrand = "Samsung"
                                    selectedModel = MobileModelsData.androidModelsByBrand["Samsung"]?.first() ?: ""
                                    selectedColor = MobileModelsData.androidColors.first()
                                },
                                label = { Text("Android Handset") },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CyanAccent, selectedLabelColor = Color.Black)
                            )
                        }
                    }

                    // Model searchable dropdown
                    Column {
                        Text(
                            text = "Model Name",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        val models = if (isIPhone) {
                            MobileModelsData.iPhoneModels
                        } else {
                            MobileModelsData.androidModelsByBrand[selectedBrand] ?: listOf(selectedModel)
                        }

                        ExposedDropdownMenuBox(
                            expanded = isModelDropdownOpen,
                            onExpandedChange = { isModelDropdownOpen = !isModelDropdownOpen }
                        ) {
                            OutlinedTextField(
                                value = selectedModel,
                                onValueChange = { selectedModel = it },
                                label = { Text("Select or type model") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isModelDropdownOpen) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )

                            ExposedDropdownMenu(
                                expanded = isModelDropdownOpen,
                                onDismissRequest = { isModelDropdownOpen = false },
                                modifier = Modifier.height(260.dp)
                            ) {
                                models.forEach { m ->
                                    DropdownMenuItem(
                                        text = { Text(m) },
                                        onClick = {
                                            selectedModel = m
                                            isModelDropdownOpen = false
                                            if (isIPhone) {
                                                selectedColor = MobileModelsData.getIPhoneColors(m).first()
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Color dropdown
                    Column {
                        Text(
                            text = "Color",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        ExposedDropdownMenuBox(
                            expanded = isColorDropdownOpen,
                            onExpandedChange = { isColorDropdownOpen = !isColorDropdownOpen }
                        ) {
                            OutlinedTextField(
                                value = selectedColor,
                                onValueChange = { selectedColor = it },
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isColorDropdownOpen) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = isColorDropdownOpen,
                                onDismissRequest = { isColorDropdownOpen = false }
                            ) {
                                availableColors.forEach { c ->
                                    DropdownMenuItem(
                                        text = { Text(c) },
                                        onClick = {
                                            selectedColor = c
                                            isColorDropdownOpen = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Battery Health Slider for iPhone (50% to 100%)
                    if (isIPhone) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("iPhone Battery Health", fontWeight = FontWeight.Bold)
                                    Text("${batteryHealthValue.toInt()}%", fontWeight = FontWeight.Black, color = CyanAccent)
                                }
                                Slider(
                                    value = batteryHealthValue,
                                    onValueChange = { batteryHealthValue = it },
                                    valueRange = 50f..100f,
                                    steps = 49,
                                    colors = SliderDefaults.colors(thumbColor = CyanAccent, activeTrackColor = CyanAccent)
                                )
                            }
                        }
                    }

                    // Single IMEI & Camera Scan
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = imei,
                            onValueChange = { imei = it.filter { ch -> ch.isDigit() } },
                            label = { Text("IMEI (15-digit)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { showImeiScanner = true },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color.Black)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Scan", fontSize = 12.sp)
                        }
                    }

                    // Purchase Price & Paid to Supplier
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = purchasePriceText,
                            onValueChange = {
                                purchasePriceText = it.filter { ch -> ch.isDigit() }
                                if (paidAmountText.isBlank()) paidAmountText = purchasePriceText
                            },
                            label = { Text("Purchase Cost (Rs) *") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("purchase_cost_input")
                        )

                        OutlinedTextField(
                            value = paidAmountText,
                            onValueChange = { paidAmountText = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Paid to Supplier (Rs)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Target Resell Asking Price
                    OutlinedTextField(
                        value = askingPriceText,
                        onValueChange = { askingPriceText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Target Retail Price (Rs) *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            val purchaseCost = purchasePriceText.toDoubleOrNull() ?: 0.0
                            if (purchaseCost <= 0) {
                                errorMessage = "Please enter purchase cost."
                                return@Button
                            }
                            val paid = paidAmountText.toDoubleOrNull() ?: purchaseCost
                            val asking = askingPriceText.toDoubleOrNull() ?: (purchaseCost * 1.15)
                            val due = (purchaseCost - paid).coerceAtLeast(0.0)

                            val phone = PhoneEntity(
                                category = if (isIPhone) "iPhone" else "Android",
                                brand = if (isIPhone) "Apple" else selectedBrand,
                                model = selectedModel.trim(),
                                color = selectedColor.trim(),
                                imei = imei.trim(),
                                storage = selectedStorage,
                                ram = if (isIPhone) "N/A" else "8 GB",
                                batteryHealth = if (isIPhone) batteryHealthValue.toInt() else null,
                                ptaStatus = selectedPta,
                                condition = selectedCondition,
                                costPrice = purchaseCost,
                                askingPrice = asking,
                                status = "In Stock",
                                supplierName = supplierText.trim(),
                                notes = notes.trim(),
                                dateAdded = System.currentTimeMillis()
                            )

                            val purchase = PurchaseEntity(
                                phoneSummary = "${phone.brand} ${phone.model} (${phone.color}, ${phone.storage})",
                                supplierName = if (supplierText.isNotBlank()) supplierText.trim() else "Direct Walk-in / Wholesale",
                                supplierPhone = supplierPhone.trim(),
                                purchasePrice = purchaseCost,
                                paidAmount = paid,
                                dueBalance = due,
                                paymentMethod = selectedPaymentMethod,
                                purchaseDate = System.currentTimeMillis(),
                                notes = notes.trim()
                            )

                            onPurchaseConfirmed(purchase, phone)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color.Black),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("confirm_purchase_button")
                    ) {
                        Text("Save Purchase", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showImeiScanner) {
        ImeiScannerDialog(
            title = "Scan Purchase IMEI",
            onDismiss = { showImeiScanner = false },
            onScanned = { scanned ->
                imei = scanned
                showImeiScanner = false
            }
        )
    }
}
