package com.example.ui.screens.sales

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SaleEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.RedDanger
import com.example.util.CurrencyUtils

@Composable
fun SalesScreen(
    sales: List<SaleEntity>,
    onCreateBillClick: () -> Unit,
    onViewReceipt: (SaleEntity) -> Unit,
    onProcessReturn: (SaleEntity) -> Unit,
    onDeleteBill: (SaleEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            // Header Stats
            val totalRevenue = sales.filter { it.status != "Returned" }.sumOf { it.salePrice }
            val totalReceived = sales.filter { it.status != "Returned" }.sumOf { it.downPayment }
            val totalDue = sales.filter { it.status != "Returned" }.sumOf { it.dueBalance }

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Sales", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(CurrencyUtils.formatPkr(totalRevenue), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CyanAccent)
                    }
                    Column {
                        Text("Received", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(CurrencyUtils.formatPkr(totalReceived), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GreenSuccess)
                    }
                    Column {
                        Text("Customer Dues", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(CurrencyUtils.formatPkr(totalDue), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            if (sales.isEmpty()) {
                EmptyStateView(
                    title = "No Bills Created Yet",
                    message = "Generate sales bills for your customers to track invoices, profits, and dues.",
                    icon = Icons.Default.Receipt,
                    actionText = "+ Create New Bill",
                    onActionClick = onCreateBillClick
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(sales, key = { it.id }) { sale ->
                        SaleItemCard(
                            sale = sale,
                            onViewReceipt = { onViewReceipt(sale) },
                            onReturn = { onProcessReturn(sale) },
                            onDelete = { onDeleteBill(sale) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onCreateBillClick,
            containerColor = CyanAccent,
            contentColor = Color.Black,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("sales_fab_create_bill")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Create Bill", modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun SaleItemCard(
    sale: SaleEntity,
    onViewReceipt: () -> Unit,
    onReturn: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewReceipt() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = sale.billNumber,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CyanAccent
                    )
                    Text(
                        text = "${sale.customerName} • ${CurrencyUtils.formatShortDate(sale.saleDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                StatusBadge(status = sale.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = sale.phoneSummary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (sale.imei.isNotBlank()) {
                Text(
                    text = "IMEI: ${sale.imei}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Sale: ${CurrencyUtils.formatPkr(sale.salePrice)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    if (sale.dueBalance > 0) {
                        Text(
                            text = "Due: ${CurrencyUtils.formatPkr(sale.dueBalance)}",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    } else {
                        Text(
                            text = "Paid via ${sale.paymentMethod}",
                            color = GreenSuccess,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = onViewReceipt,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Receipt", fontSize = 11.sp)
                    }

                    if (sale.status != "Returned") {
                        IconButton(onClick = onReturn) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = "Return", tint = CyanAccent, modifier = Modifier.size(18.dp))
                        }
                    }

                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Bill", tint = RedDanger, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
