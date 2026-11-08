package com.example.ui.screens.sales

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.SaleEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.GreenSuccess
import com.example.util.CurrencyUtils

@Composable
fun ReceiptDialog(
    sale: SaleEntity,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val receiptText = """
        ==============================
                NOMI MOBILES
         Premium Smartphones & Gadgets
        ==============================
        Invoice No: ${sale.billNumber}
        Date: ${CurrencyUtils.formatDate(sale.saleDate)}
        ------------------------------
        Customer: ${sale.customerName}
        Phone: ${if (sale.customerPhone.isNotBlank()) sale.customerPhone else "N/A"}
        ------------------------------
        ITEM DESCRIPTION:
        ${sale.phoneSummary}
        IMEI: ${if (sale.imei.isNotBlank()) sale.imei else "N/A"}
        ------------------------------
        Total Bill:    ${CurrencyUtils.formatPkr(sale.salePrice)}
        Paid Amount:   ${CurrencyUtils.formatPkr(sale.downPayment)}
        Remaining Due: ${CurrencyUtils.formatPkr(sale.dueBalance)}
        Payment Via:   ${sale.paymentMethod}
        Status:        ${sale.status.uppercase()}
        ------------------------------
        Notes: ${if (sale.notes.isNotBlank()) sale.notes else "Thanks for your business!"}
        ==============================
         Thank You For Shopping With Us!
        ==============================
    """.trimIndent()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .clip(RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
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
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Customer Receipt",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = sale.billNumber,
                                style = MaterialTheme.typography.bodySmall,
                                color = CyanAccent,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Printable Receipt Canvas
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "NOMI MOBILES",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = CyanAccent,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Mobile Shop & iPhone Specialists",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = CurrencyUtils.formatDate(sale.saleDate),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Customer info
                        ReceiptRow("Customer Name", sale.customerName)
                        if (sale.customerPhone.isNotBlank()) {
                            ReceiptRow("Phone Number", sale.customerPhone)
                        }
                        ReceiptRow("Handset", sale.phoneSummary)
                        if (sale.imei.isNotBlank()) {
                            ReceiptRow("IMEI Number", sale.imei)
                        }
                        ReceiptRow("Payment Mode", sale.paymentMethod)

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(12.dp))

                        ReceiptRow("Total Bill", CurrencyUtils.formatPkr(sale.salePrice), isBold = true)
                        ReceiptRow("Amount Paid", CurrencyUtils.formatPkr(sale.downPayment), color = GreenSuccess)
                        if (sale.dueBalance > 0) {
                            ReceiptRow("Remaining Due", CurrencyUtils.formatPkr(sale.dueBalance), color = MaterialTheme.colorScheme.error, isBold = true)
                        }

                        if (sale.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Note: ${sale.notes}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, receiptText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share Receipt Via")
                            context.startActivity(shareIntent)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_receipt_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Receipt")
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color.Black),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Done", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    color: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (color != Color.Unspecified) color else MaterialTheme.colorScheme.onSurface
        )
    }
}
