package com.example.ui.screens.purchases

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PurchaseEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.RedDanger
import com.example.util.CurrencyUtils

@Composable
fun PurchasesScreen(
    purchases: List<PurchaseEntity>,
    onNewPurchaseClick: () -> Unit,
    onDeletePurchase: (PurchaseEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            val totalPurchased = purchases.sumOf { it.purchasePrice }
            val totalPaid = purchases.sumOf { it.paidAmount }
            val totalSupplierDue = purchases.sumOf { it.dueBalance }

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
                        Text("Total Purchases", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(CurrencyUtils.formatPkr(totalPurchased), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CyanAccent)
                    }
                    Column {
                        Text("Paid to Suppliers", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(CurrencyUtils.formatPkr(totalPaid), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Supplier Dues", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(CurrencyUtils.formatPkr(totalSupplierDue), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GoldAmber)
                    }
                }
            }

            if (purchases.isEmpty()) {
                EmptyStateView(
                    title = "No Purchases Recorded",
                    message = "Record mobile purchases from suppliers to automatically stock inventory and manage supplier dues.",
                    icon = Icons.Default.ShoppingBag,
                    actionText = "+ Record New Purchase",
                    onActionClick = onNewPurchaseClick
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(purchases, key = { it.id }) { purchase ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(purchase.phoneSummary, fontWeight = FontWeight.Bold)
                                        Text(
                                            "Supplier: ${purchase.supplierName} • ${CurrencyUtils.formatShortDate(purchase.purchaseDate)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(onClick = { onDeletePurchase(purchase) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedDanger, modifier = Modifier.size(18.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Total Cost: ${CurrencyUtils.formatPkr(purchase.purchasePrice)}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    if (purchase.dueBalance > 0) {
                                        Text(
                                            "Due: ${CurrencyUtils.formatPkr(purchase.dueBalance)}",
                                            color = GoldAmber,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    } else {
                                        Text(
                                            "Fully Paid",
                                            color = CyanAccent,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onNewPurchaseClick,
            containerColor = CyanAccent,
            contentColor = Color.Black,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("purchases_fab_new")
        ) {
            Icon(Icons.Default.Add, contentDescription = "New Purchase", modifier = Modifier.size(28.dp))
        }
    }
}
