package com.example.ui.screens.sales

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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.data.model.SaleEntity
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.RedDanger
import com.example.util.CurrencyUtils

@Composable
fun CreateBillDialog(
    inStockPhones: List<PhoneEntity>,
    preSelectedPhone: PhoneEntity? = null,
    onDismiss: () -> Unit,
    onBillCreated: (SaleEntity, Long) -> Unit
) {
    var selectedPhone by remember {
        mutableStateOf(preSelectedPhone ?: inStockPhones.firstOrNull())
    }
    var isPhoneDropdownOpen by remember { mutableStateOf(false) }
    var phoneFilterText by remember { mutableStateOf("") }

    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }

    var salePriceText by remember {
        mutableStateOf(selectedPhone?.askingPrice?.toLong()?.toString() ?: "")
    }
    var downPaymentText by remember {
        mutableStateOf(selectedPhone?.askingPrice?.toLong()?.toString() ?: "")
    }

    LaunchedEffect(selectedPhone) {
        if (selectedPhone != null) {
            val price = selectedPhone?.askingPrice?.toLong()?.toString() ?: ""
            salePriceText = price
            downPaymentText = price
        }
    }

    val paymentMethods = listOf("Cash", "Bank Transfer", "Raast", "JazzCash / EasyPaisa")
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
                .fillMaxHeight(0.90f)
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
                                imageVector = Icons.Default.PointOfSale,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Create Customer Bill / Sale",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Generate invoice and update inventory stock",
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

                    // 1. SELECT PRODUCT FROM STOCK
                    Column {
                        Text(
                            text = "Select Mobile From Stock",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        if (inStockPhones.isEmpty()) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "No handsets currently in stock. Please add products first.",
                                    color = GoldAmber,
                                    modifier = Modifier.padding(12.dp),
                                    fontSize = 13.sp
                                )
                            }
                        } else {
                            Box {
                                OutlinedTextField(
                                    value = selectedPhone?.let {
                                        "${it.brand} ${it.model} (${it.color}, ${it.storage}) - ${CurrencyUtils.formatPkr(it.askingPrice)}"
                                    } ?: "Click to choose handset",
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        Icon(
                                            Icons.Default.ArrowDropDown,
                                            contentDescription = null,
                                            modifier = Modifier.clickable { isPhoneDropdownOpen = true }
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { isPhoneDropdownOpen = true }
                                        .testTag("bill_phone_select_field")
                                )

                                DropdownMenu(
                                    expanded = isPhoneDropdownOpen,
                                    onDismissRequest = { isPhoneDropdownOpen = false },
                                    modifier = Modifier
                                        .fillMaxWidth(0.9f)
                                        .height(300.dp)
                                ) {
                                    OutlinedTextField(
                                        value = phoneFilterText,
                                        onValueChange = { phoneFilterText = it },
                                        placeholder = { Text("Filter by model or IMEI...") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                    )

                                    val filteredList = inStockPhones.filter {
                                        it.model.contains(phoneFilterText, ignoreCase = true) ||
                                                it.brand.contains(phoneFilterText, ignoreCase = true) ||
                                                it.imei.contains(phoneFilterText, ignoreCase = true)
                                    }

                                    filteredList.forEach { phone ->
                                        DropdownMenuItem(
                                            text = {
                                                Column {
                                                    Text(
                                                        text = "${phone.brand} ${phone.model} (${phone.color}, ${phone.storage})",
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = "IMEI: ${if (phone.imei.isNotBlank()) phone.imei else "N/A"} | Price: ${CurrencyUtils.formatPkr(phone.askingPrice)}",
                                                        fontSize = 11.sp,
                                                        color = CyanAccent
                                                    )
                                                }
                                            },
                                            onClick = {
                                                selectedPhone = phone
                                                isPhoneDropdownOpen = false
                                                phoneFilterText = ""
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 2. CUSTOMER DETAILS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("Customer Name *") },
                            placeholder = { Text("e.g. Ali Ahmed") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("bill_customer_name_input")
                        )

                        OutlinedTextField(
                            value = customerPhone,
                            onValueChange = { customerPhone = it.filter { ch -> ch.isDigit() || ch == '+' } },
                            label = { Text("Phone Number") },
                            placeholder = { Text("0300-1234567") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("bill_customer_phone_input")
                        )
                    }

                    // 3. PRICING & DOWN PAYMENT
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = salePriceText,
                            onValueChange = { salePriceText = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Final Sale Price (Rs) *") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("bill_sale_price_input")
                        )

                        OutlinedTextField(
                            value = downPaymentText,
                            onValueChange = { downPaymentText = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Paid Amount (Rs) *") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("bill_down_payment_input")
                        )
                    }

                    // Balance preview
                    val salePrice = salePriceText.toDoubleOrNull() ?: 0.0
                    val downPayment = downPaymentText.toDoubleOrNull() ?: 0.0
                    val balance = (salePrice - downPayment).coerceAtLeast(0.0)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (balance > 0) GoldAmber.copy(alpha = 0.12f) else GreenSuccess.copy(alpha = 0.12f),
                                RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (balance > 0) "Remaining Due Balance:" else "Payment Status: FULLY PAID",
                            fontWeight = FontWeight.Bold,
                            color = if (balance > 0) GoldAmber else GreenSuccess,
                            fontSize = 13.sp
                        )
                        Text(
                            text = CurrencyUtils.formatPkr(balance),
                            fontWeight = FontWeight.Black,
                            color = if (balance > 0) GoldAmber else GreenSuccess,
                            fontSize = 14.sp
                        )
                    }

                    // 4. PAYMENT METHOD
                    Column {
                        Text(
                            text = "Payment Method",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            paymentMethods.forEach { method ->
                                FilterChip(
                                    selected = selectedPaymentMethod == method,
                                    onClick = { selectedPaymentMethod = method },
                                    label = { Text(method, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyanAccent,
                                        selectedLabelColor = Color.Black
                                    )
                                )
                            }
                        }
                    }

                    // 5. NOTES
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Warranty / Bill Notes") },
                        placeholder = { Text("e.g. 7 Days Checking Warranty") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Actions
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
                            val phone = selectedPhone
                            if (phone == null) {
                                errorMessage = "Please select a handset from inventory."
                                return@Button
                            }
                            if (customerName.isBlank()) {
                                errorMessage = "Please enter customer name."
                                return@Button
                            }
                            val salePrice = salePriceText.toDoubleOrNull() ?: 0.0
                            if (salePrice <= 0) {
                                errorMessage = "Please enter a valid sale price."
                                return@Button
                            }
                            val downPayment = downPaymentText.toDoubleOrNull() ?: salePrice
                            val balance = (salePrice - downPayment).coerceAtLeast(0.0)
                            val profit = salePrice - phone.costPrice
                            val billNo = "INV-${System.currentTimeMillis().toString().takeLast(6)}"

                            val saleEntity = SaleEntity(
                                billNumber = billNo,
                                phoneId = phone.id,
                                phoneSummary = "${phone.brand} ${phone.model} (${phone.color}, ${phone.storage})",
                                imei = phone.imei,
                                customerName = customerName.trim(),
                                customerPhone = customerPhone.trim(),
                                salePrice = salePrice,
                                downPayment = downPayment,
                                dueBalance = balance,
                                profit = profit,
                                paymentMethod = selectedPaymentMethod,
                                status = if (balance > 0) "Partial" else "Paid",
                                saleDate = System.currentTimeMillis(),
                                notes = notes.trim()
                            )

                            onBillCreated(saleEntity, phone.id)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanAccent,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("confirm_create_bill_button")
                    ) {
                        Text("Create Bill", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
