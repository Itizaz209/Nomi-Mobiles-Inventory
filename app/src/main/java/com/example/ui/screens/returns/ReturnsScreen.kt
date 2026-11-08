package com.example.ui.screens.returns

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
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.example.data.model.SaleReturnEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.RedDanger
import com.example.util.CurrencyUtils

@Composable
fun ReturnsScreen(
    returns: List<SaleReturnEntity>,
    onProcessReturnClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            val totalRefunded = returns.sumOf { it.refundAmount }

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
                        Text("Processed Returns", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${returns.size} Returned Units", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = RedDanger)
                    }
                    Column {
                        Text("Total Refunded", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(CurrencyUtils.formatPkr(totalRefunded), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = RedDanger)
                    }
                }
            }

            if (returns.isEmpty()) {
                EmptyStateView(
                    title = "No Handset Returns",
                    message = "When a customer returns a purchased mobile, process it here to return it back to In Stock and log the refund.",
                    icon = Icons.Default.SwapHoriz,
                    actionText = "+ Process Return",
                    onActionClick = onProcessReturnClick
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(returns, key = { it.id }) { ret ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(ret.phoneSummary, fontWeight = FontWeight.Bold)
                                    Text(CurrencyUtils.formatPkr(ret.refundAmount), color = RedDanger, fontWeight = FontWeight.Bold)
                                }
                                Text(
                                    "Customer: ${ret.customerName} • ${CurrencyUtils.formatShortDate(ret.returnDate)}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Reason: ${ret.reason}",
                                    fontSize = 11.sp,
                                    color = CyanAccent
                                )
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
            onClick = onProcessReturnClick,
            containerColor = RedDanger,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("returns_fab_process")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Process Return", modifier = Modifier.size(28.dp))
        }
    }
}
