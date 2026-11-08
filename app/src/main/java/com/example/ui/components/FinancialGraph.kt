package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExpenseEntity
import com.example.data.model.SaleEntity
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.RedDanger
import com.example.util.CurrencyUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun LiveFinancialGraphCard(
    sales: List<SaleEntity>,
    expenses: List<ExpenseEntity>,
    modifier: Modifier = Modifier
) {
    var selectedPeriod by remember { mutableStateOf("7 Days") } // "7 Days", "30 Days", "Year"

    // Filter data based on selected period
    val now = System.currentTimeMillis()
    val periodMillis = when (selectedPeriod) {
        "7 Days" -> 7L * 24 * 60 * 60 * 1000
        "30 Days" -> 30L * 24 * 60 * 60 * 1000
        else -> 365L * 24 * 60 * 60 * 1000
    }
    val startTime = now - periodMillis

    val periodSales = sales.filter { it.saleDate >= startTime && it.status != "Returned" }
    val periodExpenses = expenses.filter { it.date >= startTime }

    val totalRevenue = periodSales.sumOf { it.salePrice }
    val totalProfit = periodSales.sumOf { it.profit }
    val totalExpense = periodExpenses.sumOf { it.amount }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("live_financial_performance_graph")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Title & Period Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(CyanAccent.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Financial Performance",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Live revenue, profit & expense trends",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Time Period Selector
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("7 Days", "30 Days").forEach { period ->
                        val isSelected = (selectedPeriod == period)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) CyanAccent else MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .clickable { selectedPeriod = period }
                                .padding(0.dp)
                        ) {
                            Text(
                                text = period,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Summary stats under graph title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FinancialMetricMini("Revenue", CurrencyUtils.formatPkr(totalRevenue), CyanAccent)
                FinancialMetricMini("Profit", CurrencyUtils.formatPkr(totalProfit), GreenSuccess)
                FinancialMetricMini("Expenses", CurrencyUtils.formatPkr(totalExpense), GoldAmber)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Canvas Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                val numBuckets = if (selectedPeriod == "7 Days") 7 else 10
                val bucketDuration = periodMillis / numBuckets

                // Group revenue, profit, expenses by bucket
                val bucketData = (0 until numBuckets).map { i ->
                    val bStart = startTime + (i * bucketDuration)
                    val bEnd = bStart + bucketDuration

                    val rev = periodSales.filter { it.saleDate in bStart until bEnd }.sumOf { it.salePrice }
                    val prf = periodSales.filter { it.saleDate in bStart until bEnd }.sumOf { it.profit }
                    val exp = periodExpenses.filter { it.date in bStart until bEnd }.sumOf { it.amount }

                    val label = SimpleDateFormat("dd/MM", Locale.US).format(Date(bStart))
                    Triple(rev, prf, exp) to label
                }

                val maxVal = bucketData.maxOfOrNull {
                    maxOf(it.first.first, it.first.second, it.first.third)
                }?.coerceAtLeast(10000.0) ?: 10000.0

                val primaryColor = CyanAccent
                val successColor = GreenSuccess
                val warningColor = GoldAmber
                val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)

                Canvas(modifier = Modifier.fillMaxWidth().height(140.dp)) {
                    val w = size.width
                    val h = size.height - 18.dp.toPx()

                    // Draw 3 horizontal grid lines
                    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    for (step in 1..3) {
                        val y = h * (step / 3f)
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1f,
                            pathEffect = dashEffect
                        )
                    }

                    val slotWidth = w / numBuckets
                    val barWidth = (slotWidth * 0.24f).coerceIn(4f, 16f)

                    bucketData.forEachIndexed { idx, pair ->
                        val (metrics, _) = pair
                        val (rev, prf, exp) = metrics
                        val slotCenter = idx * slotWidth + (slotWidth / 2f)

                        // 3 bars per slot: Revenue (Teal), Profit (Green), Expense (Amber)
                        val revHeight = ((rev / maxVal) * h).toFloat().coerceAtLeast(3f)
                        val prfHeight = ((prf / maxVal) * h).toFloat().coerceAtLeast(if (prf > 0) 3f else 0f)
                        val expHeight = ((exp / maxVal) * h).toFloat().coerceAtLeast(if (exp > 0) 3f else 0f)

                        // Bar 1: Revenue
                        drawRoundRect(
                            color = primaryColor,
                            topLeft = Offset(slotCenter - barWidth * 1.6f, h - revHeight),
                            size = Size(barWidth, revHeight),
                            cornerRadius = CornerRadius(4f, 4f)
                        )

                        // Bar 2: Profit
                        if (prfHeight > 0) {
                            drawRoundRect(
                                color = successColor,
                                topLeft = Offset(slotCenter - barWidth * 0.5f, h - prfHeight),
                                size = Size(barWidth, prfHeight),
                                cornerRadius = CornerRadius(4f, 4f)
                            )
                        }

                        // Bar 3: Expense
                        if (expHeight > 0) {
                            drawRoundRect(
                                color = warningColor,
                                topLeft = Offset(slotCenter + barWidth * 0.6f, h - expHeight),
                                size = Size(barWidth, expHeight),
                                cornerRadius = CornerRadius(4f, 4f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Chart Legends
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ChartLegendItem("Revenue", CyanAccent)
                Spacer(modifier = Modifier.width(16.dp))
                ChartLegendItem("Profit", GreenSuccess)
                Spacer(modifier = Modifier.width(16.dp))
                ChartLegendItem("Expenses", GoldAmber)
            }
        }
    }
}

@Composable
private fun FinancialMetricMini(label: String, value: String, color: Color) {
    Column {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun ChartLegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
