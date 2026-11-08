package com.example.ui.screens.quickactions

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.data.model.ExpenseEntity
import com.example.data.model.PhoneEntity
import com.example.data.model.SaleEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.RedDanger
import com.example.util.CurrencyUtils

// --- REMOVE PRODUCT DIALOG ---
@Composable
fun RemoveProductDialog(
    products: List<PhoneEntity>,
    onDismiss: () -> Unit,
    onDeleteProduct: (PhoneEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var productToDelete by remember { mutableStateOf<PhoneEntity?>(null) }

    val filtered = remember(products, searchQuery) {
        if (searchQuery.isBlank()) products else {
            products.filter {
                it.model.contains(searchQuery, ignoreCase = true) ||
                        it.brand.contains(searchQuery, ignoreCase = true) ||
                        it.imei.contains(searchQuery, ignoreCase = true) ||
                        it.color.contains(searchQuery, ignoreCase = true)
            }
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
                                .size(38.dp)
                                .background(RedDanger.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = RedDanger, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Remove Product", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Select an item from inventory to delete", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by model, brand, or IMEI...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CyanAccent) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("remove_product_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (filtered.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        Text("No products found", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filtered, key = { it.id }) { phone ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { productToDelete = phone }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${phone.brand} ${phone.model}", fontWeight = FontWeight.Bold)
                                        Text(
                                            "${phone.color} • ${phone.storage} • IMEI: ${if (phone.imei.isNotBlank()) phone.imei else "N/A"}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            "Cost: ${CurrencyUtils.formatPkr(phone.costPrice)} | Price: ${CurrencyUtils.formatPkr(phone.askingPrice)}",
                                            fontSize = 12.sp,
                                            color = CyanAccent,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Button(
                                        onClick = { productToDelete = phone },
                                        colors = ButtonDefaults.buttonColors(containerColor = RedDanger, contentColor = Color.White),
                                        modifier = Modifier.testTag("delete_item_btn_${phone.id}")
                                    ) {
                                        Text("Delete", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (productToDelete != null) {
        val phone = productToDelete!!
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("Confirm Product Removal", color = RedDanger) },
            text = {
                Text("Are you sure you want to permanently delete '${phone.brand} ${phone.model}' (IMEI: ${phone.imei}) from the inventory database?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteProduct(phone)
                        productToDelete = null
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDanger, contentColor = Color.White)
                ) {
                    Text("Delete Product")
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

// --- REMOVE BILL DIALOG ---
@Composable
fun RemoveBillDialog(
    sales: List<SaleEntity>,
    onDismiss: () -> Unit,
    onDeleteBill: (SaleEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var saleToDelete by remember { mutableStateOf<SaleEntity?>(null) }

    val filtered = remember(sales, searchQuery) {
        if (searchQuery.isBlank()) sales else {
            sales.filter {
                it.billNumber.contains(searchQuery, ignoreCase = true) ||
                        it.customerName.contains(searchQuery, ignoreCase = true) ||
                        it.imei.contains(searchQuery, ignoreCase = true) ||
                        it.phoneSummary.contains(searchQuery, ignoreCase = true)
            }
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(RedDanger.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = RedDanger, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Remove Bill / Sale", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Deletes invoice and returns handset to stock", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by bill #, customer, IMEI...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CyanAccent) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("remove_bill_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (filtered.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        Text("No sales bills found", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filtered, key = { it.id }) { sale ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { saleToDelete = sale }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${sale.billNumber} • ${sale.customerName}", fontWeight = FontWeight.Bold)
                                        Text(sale.phoneSummary, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(
                                            "Bill: ${CurrencyUtils.formatPkr(sale.salePrice)} (Paid: ${CurrencyUtils.formatPkr(sale.downPayment)})",
                                            fontSize = 12.sp,
                                            color = CyanAccent,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Button(
                                        onClick = { saleToDelete = sale },
                                        colors = ButtonDefaults.buttonColors(containerColor = RedDanger, contentColor = Color.White),
                                        modifier = Modifier.testTag("delete_bill_btn_${sale.id}")
                                    ) {
                                        Text("Remove", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (saleToDelete != null) {
        val s = saleToDelete!!
        AlertDialog(
            onDismissRequest = { saleToDelete = null },
            title = { Text("Confirm Bill Removal", color = RedDanger) },
            text = {
                Text("Deleting bill '${s.billNumber}' for customer '${s.customerName}' will restore the handset back to 'In Stock' in your inventory. Continue?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteBill(s)
                        saleToDelete = null
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDanger, contentColor = Color.White)
                ) {
                    Text("Delete Bill")
                }
            },
            dismissButton = {
                TextButton(onClick = { saleToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

// --- REMOVE EXPENSE DIALOG ---
@Composable
fun RemoveExpenseDialog(
    expenses: List<ExpenseEntity>,
    onDismiss: () -> Unit,
    onDeleteExpense: (ExpenseEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var expenseToDelete by remember { mutableStateOf<ExpenseEntity?>(null) }

    val filtered = remember(expenses, searchQuery) {
        if (searchQuery.isBlank()) expenses else {
            expenses.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.category.contains(searchQuery, ignoreCase = true)
            }
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(RedDanger.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = RedDanger, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Remove Shop Expense", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Deletes expense record from database", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search expenses...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CyanAccent) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (filtered.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        Text("No expenses found", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filtered, key = { it.id }) { exp ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expenseToDelete = exp }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(exp.title, fontWeight = FontWeight.Bold)
                                        Text(
                                            "${exp.category} • ${CurrencyUtils.formatShortDate(exp.date)}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            CurrencyUtils.formatPkr(exp.amount),
                                            fontSize = 13.sp,
                                            color = RedDanger,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Button(
                                        onClick = { expenseToDelete = exp },
                                        colors = ButtonDefaults.buttonColors(containerColor = RedDanger, contentColor = Color.White)
                                    ) {
                                        Text("Delete", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (expenseToDelete != null) {
        val exp = expenseToDelete!!
        AlertDialog(
            onDismissRequest = { expenseToDelete = null },
            title = { Text("Confirm Expense Removal", color = RedDanger) },
            text = {
                Text("Are you sure you want to delete expense '${exp.title}' for ${CurrencyUtils.formatPkr(exp.amount)}?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteExpense(exp)
                        expenseToDelete = null
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDanger, contentColor = Color.White)
                ) {
                    Text("Delete Expense")
                }
            },
            dismissButton = {
                TextButton(onClick = { expenseToDelete = null }) { Text("Cancel") }
            }
        )
    }
}
