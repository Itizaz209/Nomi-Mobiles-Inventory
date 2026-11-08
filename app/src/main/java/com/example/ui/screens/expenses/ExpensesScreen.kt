package com.example.ui.screens.expenses

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
import androidx.compose.material.icons.filled.MoneyOff
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
import com.example.data.model.ExpenseEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.RedDanger
import com.example.util.CurrencyUtils

@Composable
fun ExpensesScreen(
    expenses: List<ExpenseEntity>,
    onAddExpenseClick: () -> Unit,
    onDeleteExpense: (ExpenseEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            val totalExpenseAmount = expenses.sumOf { it.amount }

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
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Shop Expenses", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(CurrencyUtils.formatPkr(totalExpenseAmount), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = GoldAmber)
                    }
                    Text("${expenses.size} records", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (expenses.isEmpty()) {
                EmptyStateView(
                    title = "No Expenses Logged",
                    message = "Track shop rent, electricity, staff salary, tea, and maintenance to compute real net profit.",
                    icon = Icons.Default.MoneyOff,
                    actionText = "+ Add Shop Expense",
                    onActionClick = onAddExpenseClick
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(expenses, key = { it.id }) { expense ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(expense.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(
                                        "${expense.category} • ${CurrencyUtils.formatShortDate(expense.date)} • Via ${expense.paymentMethod}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (expense.notes.isNotBlank()) {
                                        Text(
                                            expense.notes,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        CurrencyUtils.formatPkr(expense.amount),
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAmber,
                                        fontSize = 14.sp
                                    )
                                    IconButton(onClick = { onDeleteExpense(expense) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedDanger, modifier = Modifier.size(18.dp))
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
            onClick = onAddExpenseClick,
            containerColor = GoldAmber,
            contentColor = Color.Black,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("expenses_fab_add")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Expense", modifier = Modifier.size(28.dp))
        }
    }
}
