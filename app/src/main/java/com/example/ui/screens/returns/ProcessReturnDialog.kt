package com.example.ui.screens.returns

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.example.data.model.SaleEntity
import com.example.data.model.SaleReturnEntity
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.RedDanger
import com.example.util.CurrencyUtils

@Composable
fun ProcessReturnDialog(
    salesList: List<SaleEntity>,
    onDismiss: () -> Unit,
    onReturnConfirmed: (SaleReturnEntity, Long, Long) -> Unit
) {
    // Only allow returns on non-returned sales
    val eligibleSales = remember(salesList) {
        salesList.filter { it.status != "Returned" }
    }

    var selectedSale by remember { mutableStateOf(eligibleSales.firstOrNull()) }
    var isSaleDropdownOpen by remember { mutableStateOf(false) }
    var saleFilterText by remember { mutableStateOf("") }

    var refundAmountText by remember {
        mutableStateOf(selectedSale?.downPayment?.toLong()?.toString() ?: "")
    }
    var reason by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedSale) {
        if (selectedSale != null) {
            refundAmountText = selectedSale?.downPayment?.toLong()?.toString() ?: ""
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .padding(vertical = 16.dp)
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
                                .background(RedDanger.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = null,
                                tint = RedDanger,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Process Return / Refund",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Restores handset to In Stock and logs refund",
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

                    if (eligibleSales.isEmpty()) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "No sold bills available to return.",
                                color = GoldAmber,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    } else {
                        // Select Sale Bill
                        Column {
                            Text(
                                text = "Select Customer Sale / Invoice *",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box {
                                OutlinedTextField(
                                    value = selectedSale?.let {
                                        "${it.billNumber} - ${it.customerName} (${it.phoneSummary}) - ${CurrencyUtils.formatPkr(it.salePrice)}"
                                    } ?: "Select Bill",
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        Icon(
                                            Icons.Default.ArrowDropDown,
                                            contentDescription = null,
                                            modifier = Modifier.clickable { isSaleDropdownOpen = true }
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { isSaleDropdownOpen = true }
                                        .testTag("select_return_bill_field")
                                )

                                DropdownMenu(
                                    expanded = isSaleDropdownOpen,
                                    onDismissRequest = { isSaleDropdownOpen = false },
                                    modifier = Modifier
                                        .fillMaxWidth(0.9f)
                                        .height(280.dp)
                                ) {
                                    OutlinedTextField(
                                        value = saleFilterText,
                                        onValueChange = { saleFilterText = it },
                                        placeholder = { Text("Search by bill #, customer, IMEI...") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                    )

                                    val filtered = eligibleSales.filter {
                                        it.billNumber.contains(saleFilterText, ignoreCase = true) ||
                                                it.customerName.contains(saleFilterText, ignoreCase = true) ||
                                                it.imei.contains(saleFilterText, ignoreCase = true) ||
                                                it.phoneSummary.contains(saleFilterText, ignoreCase = true)
                                    }

                                    filtered.forEach { sale ->
                                        DropdownMenuItem(
                                            text = {
                                                Column {
                                                    Text(
                                                        text = "${sale.billNumber} • ${sale.customerName}",
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = "${sale.phoneSummary} (Paid: ${CurrencyUtils.formatPkr(sale.downPayment)})",
                                                        fontSize = 11.sp,
                                                        color = CyanAccent
                                                    )
                                                }
                                            },
                                            onClick = {
                                                selectedSale = sale
                                                isSaleDropdownOpen = false
                                                saleFilterText = ""
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Refund Amount
                        OutlinedTextField(
                            value = refundAmountText,
                            onValueChange = { refundAmountText = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Refund Amount (Rs) *") },
                            placeholder = { Text("e.g. Full or partial refund") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("refund_amount_input")
                        )

                        // Return Reason
                        OutlinedTextField(
                            value = reason,
                            onValueChange = { reason = it },
                            label = { Text("Return Reason *") },
                            placeholder = { Text("e.g. Battery health issue, customer changed mind, PTA lock issue") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("return_reason_input")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Actions
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
                            val sale = selectedSale
                            if (sale == null) {
                                errorMessage = "Please select a bill to process return."
                                return@Button
                            }
                            val refund = refundAmountText.toDoubleOrNull() ?: 0.0
                            if (refund < 0) {
                                errorMessage = "Please enter a valid refund amount."
                                return@Button
                            }
                            val finalReason = if (reason.isNotBlank()) reason.trim() else "Customer Return"

                            val returnEntity = SaleReturnEntity(
                                saleId = sale.id,
                                phoneId = sale.phoneId,
                                customerName = sale.customerName,
                                phoneSummary = sale.phoneSummary,
                                refundAmount = refund,
                                reason = finalReason,
                                returnDate = System.currentTimeMillis()
                            )

                            onReturnConfirmed(returnEntity, sale.phoneId, sale.id)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RedDanger, contentColor = Color.White),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("confirm_return_button")
                    ) {
                        Text("Confirm Return", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
