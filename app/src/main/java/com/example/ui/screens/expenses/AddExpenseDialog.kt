package com.example.ui.screens.expenses

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.RedDanger
import com.example.util.MobileModelsData

@Composable
fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onSaveExpense: (ExpenseEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(MobileModelsData.expenseCategories.first()) }
    var amountText by remember { mutableStateOf("") }
    val paymentMethods = listOf("Cash", "Bank Transfer", "JazzCash / EasyPaisa")
    var selectedPaymentMethod by remember { mutableStateOf("Cash") }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
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
                                imageVector = Icons.Default.MoneyOff,
                                contentDescription = null,
                                tint = GoldAmber,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Add Shop Expense",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Records operating costs for true net profit",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

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

                // Expense Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Expense Title *") },
                    placeholder = { Text("e.g. Shop Rent for this month, Electricity Bill") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("expense_title_input")
                )

                // Category selection
                Column {
                    Text(
                        text = "Category",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = CyanAccent
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        MobileModelsData.expenseCategories.chunked(3).forEach { rowCats ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                rowCats.forEach { cat ->
                                    FilterChip(
                                        selected = selectedCategory == cat,
                                        onClick = {
                                            selectedCategory = cat
                                            if (title.isBlank()) title = cat
                                        },
                                        label = { Text(cat.split(" ").first(), fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = GoldAmber,
                                            selectedLabelColor = Color.Black
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Amount (Rs) *") },
                    placeholder = { Text("e.g. 45000") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("expense_amount_input")
                )

                // Payment Method
                Column {
                    Text(
                        text = "Paid Via",
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

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

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
                            val amount = amountText.toDoubleOrNull() ?: 0.0
                            if (amount <= 0) {
                                errorMessage = "Please enter a valid expense amount."
                                return@Button
                            }
                            val finalTitle = if (title.isNotBlank()) title.trim() else selectedCategory
                            val expense = ExpenseEntity(
                                title = finalTitle,
                                category = selectedCategory,
                                amount = amount,
                                paymentMethod = selectedPaymentMethod,
                                date = System.currentTimeMillis(),
                                notes = notes.trim()
                            )
                            onSaveExpense(expense)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAmber, contentColor = Color.Black),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("confirm_save_expense_button")
                    ) {
                        Text("Save Expense", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
