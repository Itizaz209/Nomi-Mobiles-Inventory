package com.example.ui.screens.inventory

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.TabletAndroid
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.ProductCategoryEntity
import com.example.ui.components.ImeiScannerDialog
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.Navy700
import com.example.ui.theme.RedDanger
import com.example.util.MobileModelsData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductDialog(
    initialPhone: PhoneEntity? = null,
    categories: List<ProductCategoryEntity>,
    supplierList: List<String> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (PhoneEntity) -> Unit,
    onAddNewCategory: (String) -> Unit
) {
    // Category State
    var selectedCategory by remember {
        mutableStateOf(initialPhone?.category ?: if (categories.isNotEmpty()) categories.first().name else "iPhone")
    }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }
    var showCreateCategoryField by remember { mutableStateOf(false) }
    var newCategoryText by remember { mutableStateOf("") }

    // Phone / Brand State
    var isIPhone by remember {
        mutableStateOf(selectedCategory.equals("iPhone", ignoreCase = true))
    }
    LaunchedEffect(selectedCategory) {
        isIPhone = selectedCategory.equals("iPhone", ignoreCase = true)
    }

    var selectedBrand by remember {
        mutableStateOf(
            initialPhone?.brand ?: if (isIPhone) "Apple" else "Samsung"
        )
    }
    var isBrandDropdownExpanded by remember { mutableStateOf(false) }

    // Model Selection (Searchable dropdown / roller)
    var selectedModel by remember {
        mutableStateOf(
            initialPhone?.model ?: if (isIPhone) MobileModelsData.iPhoneModels.first() else (MobileModelsData.androidModelsByBrand[selectedBrand]?.first() ?: "Galaxy S24 Ultra")
        )
    }
    var modelSearchQuery by remember { mutableStateOf("") }
    var isModelDropdownExpanded by remember { mutableStateOf(false) }

    // Official Colors Dropdown
    val availableColors = remember(selectedModel, isIPhone, selectedBrand) {
        if (isIPhone) {
            MobileModelsData.getIPhoneColors(selectedModel)
        } else {
            MobileModelsData.androidColors
        }
    }
    var selectedColor by remember {
        mutableStateOf(initialPhone?.color ?: availableColors.firstOrNull() ?: "Black")
    }
    var isColorDropdownExpanded by remember { mutableStateOf(false) }

    // iPhone Battery Health Slider (50% to 100%)
    var batteryHealthValue by remember {
        mutableFloatStateOf(initialPhone?.batteryHealth?.toFloat() ?: 90f)
    }

    // Single IMEI Field (IMEI 2 removed)
    var imei by remember { mutableStateOf(initialPhone?.imei ?: "") }
    var showImeiScanner by remember { mutableStateOf(false) }

    // Storage, RAM, PTA, Condition
    var selectedStorage by remember { mutableStateOf(initialPhone?.storage ?: "128 GB") }
    var selectedRam by remember { mutableStateOf(initialPhone?.ram ?: if (isIPhone) "N/A" else "8 GB") }
    var selectedPta by remember { mutableStateOf(initialPhone?.ptaStatus ?: "PTA Approved") }
    var selectedCondition by remember { mutableStateOf(initialPhone?.condition ?: "10/10 Like New / Pinpack") }

    // Prices (PKR)
    var costPriceText by remember { mutableStateOf(if ((initialPhone?.costPrice ?: 0.0) > 0) initialPhone?.costPrice?.toLong().toString() else "") }
    var askingPriceText by remember { mutableStateOf(if ((initialPhone?.askingPrice ?: 0.0) > 0) initialPhone?.askingPrice?.toLong().toString() else "") }

    // Supplier with disappearing placeholder
    var supplierText by remember { mutableStateOf(initialPhone?.supplierName ?: "") }
    var isSupplierDropdownExpanded by remember { mutableStateOf(false) }

    var notes by remember { mutableStateOf(initialPhone?.notes ?: "") }
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
                                .background(CyanAccent.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isIPhone) Icons.Default.PhoneIphone else Icons.Default.Smartphone,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (initialPhone == null) "Add Product to Inventory" else "Edit Product",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Fill specifications, pricing and IMEI",
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

                // Scrollable Form Content
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

                    // 1. PRODUCT CATEGORY SELECTION (Requirement 7)
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Product Category",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                            Text(
                                text = if (showCreateCategoryField) "Cancel New" else "+ New Category",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyanAccent,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable {
                                    showCreateCategoryField = !showCreateCategoryField
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        val allCategoryNames = remember(categories) {
                            val defaultList = listOf("iPhone", "Android", "Accessories", "Tablets", "Smart Watches")
                            (categories.map { it.name } + defaultList).distinct()
                        }

                        ExposedDropdownMenuBox(
                            expanded = isCategoryDropdownExpanded,
                            onExpandedChange = { isCategoryDropdownExpanded = !isCategoryDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedCategory,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Select Product Category") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownExpanded)
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyanAccent,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .testTag("dropdown_product_category")
                            )

                            ExposedDropdownMenu(
                                expanded = isCategoryDropdownExpanded,
                                onDismissRequest = { isCategoryDropdownExpanded = false }
                            ) {
                                allCategoryNames.forEach { catName ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = when {
                                                        catName.contains("iPhone", ignoreCase = true) -> Icons.Default.PhoneIphone
                                                        catName.contains("Android", ignoreCase = true) -> Icons.Default.Smartphone
                                                        catName.contains("Tablet", ignoreCase = true) -> Icons.Default.TabletAndroid
                                                        catName.contains("Watch", ignoreCase = true) -> Icons.Default.Watch
                                                        else -> Icons.Default.Category
                                                    },
                                                    contentDescription = null,
                                                    tint = if (selectedCategory.equals(catName, ignoreCase = true)) CyanAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(
                                                    text = catName,
                                                    fontWeight = if (selectedCategory.equals(catName, ignoreCase = true)) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (selectedCategory.equals(catName, ignoreCase = true)) CyanAccent else MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedCategory = catName
                                            isCategoryDropdownExpanded = false
                                            if (catName.equals("iPhone", ignoreCase = true)) {
                                                selectedBrand = "Apple"
                                                selectedModel = MobileModelsData.iPhoneModels.first()
                                            } else if (catName.equals("Android", ignoreCase = true)) {
                                                selectedBrand = "Samsung"
                                                selectedModel = MobileModelsData.androidModelsByBrand["Samsung"]?.first() ?: "Galaxy S24 Ultra"
                                            }
                                        }
                                    )
                                }
                            }
                        }

                        // Create New Category on-the-fly
                        AnimatedVisibility(visible = showCreateCategoryField) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = newCategoryText,
                                    onValueChange = { newCategoryText = it },
                                    placeholder = { Text("Enter Category Name") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (newCategoryText.isNotBlank()) {
                                            onAddNewCategory(newCategoryText.trim())
                                            selectedCategory = newCategoryText.trim()
                                            newCategoryText = ""
                                            showCreateCategoryField = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color.Black)
                                ) {
                                    Text("Add")
                                }
                            }
                        }
                    }

                    // 2. BRAND SELECTION (Dropdown with Android brands or Apple)
                    Column {
                        Text(
                            text = "Brand",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        val availableBrands = remember(isIPhone) {
                            if (isIPhone) listOf("Apple") else MobileModelsData.androidBrands
                        }

                        ExposedDropdownMenuBox(
                            expanded = isBrandDropdownExpanded,
                            onExpandedChange = {
                                if (!isIPhone) {
                                    isBrandDropdownExpanded = !isBrandDropdownExpanded
                                }
                            }
                        ) {
                            OutlinedTextField(
                                value = selectedBrand,
                                onValueChange = {},
                                readOnly = true,
                                enabled = !isIPhone,
                                label = { Text(if (isIPhone) "Brand (Fixed for iPhone)" else "Select Brand") },
                                trailingIcon = {
                                    if (!isIPhone) {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isBrandDropdownExpanded)
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyanAccent,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .testTag("dropdown_brand")
                            )

                            if (!isIPhone) {
                                ExposedDropdownMenu(
                                    expanded = isBrandDropdownExpanded,
                                    onDismissRequest = { isBrandDropdownExpanded = false }
                                ) {
                                    availableBrands.forEach { brand ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = brand,
                                                    fontWeight = if (selectedBrand == brand) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (selectedBrand == brand) CyanAccent else MaterialTheme.colorScheme.onSurface
                                                )
                                            },
                                            onClick = {
                                                selectedBrand = brand
                                                selectedModel = MobileModelsData.androidModelsByBrand[brand]?.first() ?: ""
                                                isBrandDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 3. SEARCHABLE MODEL DROPDOWN / ROLLER (Requirement 7)
                    Column {
                        Text(
                            text = if (isIPhone) "iPhone Model" else "Model Name ($selectedBrand)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        val availableModels = if (isIPhone) {
                            MobileModelsData.iPhoneModels
                        } else {
                            MobileModelsData.androidModelsByBrand[selectedBrand] ?: listOf(selectedModel)
                        }

                        val filteredModels = if (modelSearchQuery.isBlank()) {
                            availableModels
                        } else {
                            availableModels.filter { it.contains(modelSearchQuery, ignoreCase = true) }
                        }

                        ExposedDropdownMenuBox(
                            expanded = isModelDropdownExpanded,
                            onExpandedChange = { isModelDropdownExpanded = !isModelDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedModel,
                                onValueChange = {
                                    selectedModel = it
                                    modelSearchQuery = it
                                },
                                label = { Text("Select or search model") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isModelDropdownExpanded)
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .testTag("model_select_input")
                            )

                            ExposedDropdownMenu(
                                expanded = isModelDropdownExpanded,
                                onDismissRequest = { isModelDropdownExpanded = false },
                                modifier = Modifier.height(280.dp)
                            ) {
                                // Search filter inside dropdown
                                OutlinedTextField(
                                    value = modelSearchQuery,
                                    onValueChange = { modelSearchQuery = it },
                                    placeholder = { Text("Filter models...") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                )

                                filteredModels.forEach { model ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = model,
                                                fontWeight = if (selectedModel == model) FontWeight.Bold else FontWeight.Normal,
                                                color = if (selectedModel == model) CyanAccent else MaterialTheme.colorScheme.onSurface
                                            )
                                        },
                                        onClick = {
                                            selectedModel = model
                                            isModelDropdownExpanded = false
                                            modelSearchQuery = ""
                                            // auto select first official color
                                            if (isIPhone) {
                                                selectedColor = MobileModelsData.getIPhoneColors(model).firstOrNull() ?: "Natural Titanium"
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // 4. COLOR SELECTION DROPDOWN (Requirement 10)
                    Column {
                        Text(
                            text = if (isIPhone) "Official iPhone Color" else "Device Color",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        ExposedDropdownMenuBox(
                            expanded = isColorDropdownExpanded,
                            onExpandedChange = { isColorDropdownExpanded = !isColorDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedColor,
                                onValueChange = { selectedColor = it },
                                label = { Text("Select color") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Palette,
                                        contentDescription = null,
                                        tint = CyanAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isColorDropdownExpanded)
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .testTag("color_select_input")
                            )

                            ExposedDropdownMenu(
                                expanded = isColorDropdownExpanded,
                                onDismissRequest = { isColorDropdownExpanded = false },
                                modifier = Modifier.height(260.dp)
                            ) {
                                availableColors.forEach { colorName ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = colorName,
                                                    fontWeight = if (selectedColor == colorName) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (selectedColor == colorName) CyanAccent else MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedColor = colorName
                                            isColorDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // 5. iPHONE BATTERY HEALTH SLIDER (Requirement 9)
                    if (isIPhone) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.BatteryChargingFull,
                                            contentDescription = null,
                                            tint = when {
                                                batteryHealthValue >= 85f -> GreenSuccess
                                                batteryHealthValue >= 75f -> GoldAmber
                                                else -> RedDanger
                                            },
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "iPhone Battery Health",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    // Clearly displayed percentage
                                    Text(
                                        text = "${batteryHealthValue.toInt()}%",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = when {
                                            batteryHealthValue >= 85f -> GreenSuccess
                                            batteryHealthValue >= 75f -> GoldAmber
                                            else -> RedDanger
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Slider from 50% to 100%
                                Slider(
                                    value = batteryHealthValue,
                                    onValueChange = { batteryHealthValue = it },
                                    valueRange = 50f..100f,
                                    steps = 49,
                                    colors = SliderDefaults.colors(
                                        thumbColor = CyanAccent,
                                        activeTrackColor = CyanAccent
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("battery_health_slider")
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("50%", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("75%", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("85% (Service Peak)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("100%", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }

                    // 6. SINGLE IMEI FIELD & SCANNER (Requirement 6 & 8)
                    Column {
                        Text(
                            text = "IMEI (15-digit)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = imei,
                                onValueChange = { imei = it.filter { ch -> ch.isDigit() } },
                                placeholder = { Text("e.g. 358923091234567") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("product_imei_input")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { showImeiScanner = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyanAccent,
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier.testTag("open_imei_camera_scanner_button")
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Scan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // 7. STORAGE & PTA STATUS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Storage",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            var isStorageOpen by remember { mutableStateOf(false) }
                            Box {
                                OutlinedTextField(
                                    value = selectedStorage,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        Icon(
                                            Icons.Default.ArrowDropDown,
                                            contentDescription = null,
                                            modifier = Modifier.clickable { isStorageOpen = true }
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { isStorageOpen = true }
                                )
                                DropdownMenu(
                                    expanded = isStorageOpen,
                                    onDismissRequest = { isStorageOpen = false }
                                ) {
                                    MobileModelsData.storageOptions.forEach { opt ->
                                        DropdownMenuItem(
                                            text = { Text(opt) },
                                            onClick = {
                                                selectedStorage = opt
                                                isStorageOpen = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "PTA Status",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            var isPtaOpen by remember { mutableStateOf(false) }
                            Box {
                                OutlinedTextField(
                                    value = selectedPta,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        Icon(
                                            Icons.Default.ArrowDropDown,
                                            contentDescription = null,
                                            modifier = Modifier.clickable { isPtaOpen = true }
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { isPtaOpen = true }
                                )
                                DropdownMenu(
                                    expanded = isPtaOpen,
                                    onDismissRequest = { isPtaOpen = false }
                                ) {
                                    MobileModelsData.ptaOptions.forEach { opt ->
                                        DropdownMenuItem(
                                            text = { Text(opt) },
                                            onClick = {
                                                selectedPta = opt
                                                isPtaOpen = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 8. CONDITION
                    Column {
                        Text(
                            text = "Condition",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            MobileModelsData.conditionOptions.take(3).forEach { cond ->
                                FilterChip(
                                    selected = selectedCondition == cond,
                                    onClick = { selectedCondition = cond },
                                    label = { Text(cond.split(" ").first(), fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyanAccent,
                                        selectedLabelColor = Color.Black
                                    )
                                )
                            }
                        }
                    }

                    // 9. PRICING (Cost Price & Asking Price)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = costPriceText,
                            onValueChange = { costPriceText = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Cost Price (Rs)") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("product_cost_price_input")
                        )

                        OutlinedTextField(
                            value = askingPriceText,
                            onValueChange = { askingPriceText = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Asking Price (Rs)") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("product_asking_price_input")
                        )
                    }

                    // 10. SUPPLIER (Requirement 11: proper disappearing placeholder "Select or Enter Supplier")
                    Column {
                        Text(
                            text = "Purchase Supplier",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        ExposedDropdownMenuBox(
                            expanded = isSupplierDropdownExpanded,
                            onExpandedChange = { isSupplierDropdownExpanded = !isSupplierDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = supplierText,
                                onValueChange = { supplierText = it },
                                placeholder = {
                                    // Requirement 11: "Select or Enter Supplier" placeholder
                                    Text(
                                        text = "Select or Enter Supplier",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                singleLine = true,
                                trailingIcon = {
                                    if (supplierList.isNotEmpty()) {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSupplierDropdownExpanded)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .testTag("supplier_input_field")
                            )

                            if (supplierList.isNotEmpty()) {
                                ExposedDropdownMenu(
                                    expanded = isSupplierDropdownExpanded,
                                    onDismissRequest = { isSupplierDropdownExpanded = false }
                                ) {
                                    supplierList.forEach { supplierName ->
                                        DropdownMenuItem(
                                            text = { Text(supplierName) },
                                            onClick = {
                                                supplierText = supplierName
                                                isSupplierDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 11. NOTES
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes / Accessories included") },
                        placeholder = { Text("e.g. Original Box, 20W Charger, Cable") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Action Buttons
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
                            if (selectedModel.isBlank()) {
                                errorMessage = "Please select or enter a model name."
                                return@Button
                            }
                            val cost = costPriceText.toDoubleOrNull() ?: 0.0
                            val asking = askingPriceText.toDoubleOrNull() ?: 0.0

                            val phoneToSave = PhoneEntity(
                                id = initialPhone?.id ?: 0,
                                category = selectedCategory,
                                brand = if (isIPhone) "Apple" else selectedBrand,
                                model = selectedModel.trim(),
                                color = selectedColor.trim(),
                                imei = imei.trim(),
                                storage = selectedStorage,
                                ram = selectedRam,
                                batteryHealth = if (isIPhone) batteryHealthValue.toInt() else null,
                                ptaStatus = selectedPta,
                                condition = selectedCondition,
                                costPrice = cost,
                                askingPrice = asking,
                                status = initialPhone?.status ?: "In Stock",
                                supplierName = supplierText.trim(),
                                notes = notes.trim(),
                                dateAdded = initialPhone?.dateAdded ?: System.currentTimeMillis()
                            )
                            onSave(phoneToSave)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanAccent,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_product_button")
                    ) {
                        Text("Save Product", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Camera IMEI Scanner Dialog
    if (showImeiScanner) {
        ImeiScannerDialog(
            title = "Scan Product IMEI",
            onDismiss = { showImeiScanner = false },
            onScanned = { scannedImei ->
                imei = scannedImei
                showImeiScanner = false
            }
        )
    }
}
