package com.example.ui.screens.more

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExpenseEntity
import com.example.data.model.PhoneEntity
import com.example.data.model.PurchaseEntity
import com.example.data.model.SaleEntity
import com.example.ui.components.StatCard
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.RedDanger
import com.example.util.CurrencyUtils

// --- CUSTOMERS & SUPPLIERS DIRECTORY ---
@Composable
fun CustomersSuppliersScreen(
    sales: List<SaleEntity>,
    purchases: List<PurchaseEntity>,
    modifier: Modifier = Modifier
) {
    val customers = sales.groupBy { it.customerName }.map { (name, list) ->
        val totalSpent = list.sumOf { it.salePrice }
        val totalDue = list.sumOf { it.dueBalance }
        val phone = list.firstOrNull { it.customerPhone.isNotBlank() }?.customerPhone ?: ""
        Triple(name, totalSpent, totalDue) to phone
    }

    val suppliers = purchases.groupBy { it.supplierName }.map { (name, list) ->
        val totalPurchased = list.sumOf { it.purchasePrice }
        val totalDue = list.sumOf { it.dueBalance }
        val phone = list.firstOrNull { it.supplierPhone.isNotBlank() }?.supplierPhone ?: ""
        Triple(name, totalPurchased, totalDue) to phone
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "CUSTOMER DIRECTORY (${customers.size})",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = CyanAccent,
                letterSpacing = 1.sp
            )
        }

        if (customers.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No customers logged yet. Create a bill to record customer accounts.",
                        modifier = Modifier.padding(14.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(customers) { (data, phone) ->
                val (name, spent, due) = data
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(CyanAccent.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(name, fontWeight = FontWeight.Bold)
                                if (phone.isNotBlank()) {
                                    Text(phone, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Total: ${CurrencyUtils.formatPkr(spent)}", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            if (due > 0) {
                                Text("Due: ${CurrencyUtils.formatPkr(due)}", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            } else {
                                Text("No Balance", color = GreenSuccess, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "SUPPLIER DIRECTORY (${suppliers.size})",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = GoldAmber,
                letterSpacing = 1.sp
            )
        }

        if (suppliers.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No suppliers recorded yet.",
                        modifier = Modifier.padding(14.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(suppliers) { (data, phone) ->
                val (name, total, due) = data
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(GoldAmber.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Business, contentDescription = null, tint = GoldAmber, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(name, fontWeight = FontWeight.Bold)
                                if (phone.isNotBlank()) {
                                    Text(phone, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Purchased: ${CurrencyUtils.formatPkr(total)}", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            if (due > 0) {
                                Text("Payable: ${CurrencyUtils.formatPkr(due)}", color = GoldAmber, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}

// --- FINANCIAL REPORTS & P&L ---
@Composable
fun ReportsScreen(
    inStockPhones: List<PhoneEntity>,
    sales: List<SaleEntity>,
    expenses: List<ExpenseEntity>,
    modifier: Modifier = Modifier
) {
    val activeSales = sales.filter { it.status != "Returned" }
    val totalRevenue = activeSales.sumOf { it.salePrice }
    val grossProfit = activeSales.sumOf { it.profit }
    val totalExpenses = expenses.sumOf { it.amount }
    val netProfit = grossProfit - totalExpenses
    val stockCost = inStockPhones.sumOf { it.costPrice }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "PROFIT & LOSS (P&L) STATEMENT",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = CyanAccent,
                letterSpacing = 1.sp
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Financial Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    ReportRow("Total Handset Sales Revenue", CurrencyUtils.formatPkr(totalRevenue))
                    ReportRow("Gross Margin (Sales Profit)", CurrencyUtils.formatPkr(grossProfit), isPositive = true)
                    ReportRow("Total Shop Operating Expenses", CurrencyUtils.formatPkr(totalExpenses), isNegative = true)

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("True Net Profit", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                        Text(
                            text = CurrencyUtils.formatPkr(netProfit),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = if (netProfit >= 0) GreenSuccess else RedDanger
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "INVENTORY VALUATION",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    ReportRow("In-Stock Handsets Count", "${inStockPhones.size} Units")
                    ReportRow("Total Inventory Purchase Cost", CurrencyUtils.formatPkr(stockCost))
                    val retailValuation = inStockPhones.sumOf { it.askingPrice }
                    ReportRow("Expected Retail Realization", CurrencyUtils.formatPkr(retailValuation))
                    val potentialProfit = retailValuation - stockCost
                    ReportRow("Unrealized Stock Profit", CurrencyUtils.formatPkr(potentialProfit), isPositive = true)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun ReportRow(label: String, value: String, isPositive: Boolean = false, isNegative: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = when {
                isPositive -> GreenSuccess
                isNegative -> RedDanger
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

// --- SETTINGS SCREEN ---
@Composable
fun SettingsScreen(
    phoneCount: Int,
    saleCount: Int,
    expenseCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Shop Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("NOMI MOBILES", fontWeight = FontWeight.Bold, color = CyanAccent)
                Text("Mobile Phone Store & iPhone Specialist", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Offline-first local Room SQLite storage", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Local Database Records", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("• Handsets in Database: $phoneCount", fontSize = 13.sp)
                Text("• Customer Invoices: $saleCount", fontSize = 13.sp)
                Text("• Operating Expenses: $expenseCount", fontSize = 13.sp)
            }
        }
    }
}
