package com.example.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExpenseEntity
import com.example.data.model.PhoneEntity
import com.example.data.model.ProductCategoryEntity
import com.example.data.model.PurchaseEntity
import com.example.data.model.SaleEntity
import com.example.data.model.SaleReturnEntity
import com.example.ui.components.LiveFinancialGraphCard
import com.example.ui.components.NomiPage
import com.example.ui.components.StatCard
import com.example.ui.theme.BlueSky
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.RedDanger
import com.example.util.CurrencyUtils
import java.util.Calendar

@Composable
fun DashboardScreen(
    inStockPhones: List<PhoneEntity>,
    allPhones: List<PhoneEntity> = emptyList(),
    allSales: List<SaleEntity>,
    allExpenses: List<ExpenseEntity>,
    allReturns: List<SaleReturnEntity> = emptyList(),
    allPurchases: List<PurchaseEntity> = emptyList(),
    categories: List<ProductCategoryEntity>,
    onNavigatePage: (NomiPage) -> Unit,
    onFilterCategory: (String) -> Unit,
    // Quick action callbacks
    onQuickAddProduct: () -> Unit,
    onQuickCreateBill: () -> Unit,
    onQuickAddExpense: () -> Unit,
    onQuickProcessReturn: () -> Unit,
    onQuickRemoveProduct: () -> Unit,
    onQuickRemoveBill: () -> Unit,
    onQuickRemoveExpense: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Current Month calculations (Requirement 4)
    val calendar = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val startOfMonth = calendar.timeInMillis

    val monthlySales = allSales
        .filter { it.saleDate >= startOfMonth && it.status != "Returned" }
        .sumOf { it.salePrice }

    val monthlyProfit = allSales
        .filter { it.saleDate >= startOfMonth && it.status != "Returned" }
        .sumOf { it.profit }

    val monthlyExpenses = allExpenses
        .filter { it.date >= startOfMonth }
        .sumOf { it.amount }

    val stockValuation = inStockPhones.sumOf { it.costPrice }

    // Live Stock Breakdown metrics (Requirement 5)
    val iphonesInStockCount = inStockPhones.count { it.category.equals("iPhone", ignoreCase = true) }
    val androidInStockCount = inStockPhones.count { it.category.equals("Android", ignoreCase = true) }
    val soldOutCount = allSales.count { it.status != "Returned" }
    val returnCount = allReturns.size
    val replaceCount = allReturns.size // Replacement/returns
    val purchasesCount = allPurchases.size

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. QUICK ACTIONS SECTION
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "QUICK ACTIONS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = CyanAccent,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickActionTile(
                            title = "Add Product",
                            icon = Icons.Default.AddCircle,
                            color = GreenSuccess,
                            modifier = Modifier.weight(1f),
                            testTag = "quick_add_product_tile",
                            onClick = onQuickAddProduct
                        )
                        QuickActionTile(
                            title = "Create Bill",
                            icon = Icons.Default.PointOfSale,
                            color = CyanAccent,
                            modifier = Modifier.weight(1f),
                            testTag = "quick_create_bill_tile",
                            onClick = onQuickCreateBill
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickActionTile(
                            title = "Add Expense",
                            icon = Icons.Default.MoneyOff,
                            color = GoldAmber,
                            modifier = Modifier.weight(1f),
                            testTag = "quick_add_expense_tile",
                            onClick = onQuickAddExpense
                        )
                        QuickActionTile(
                            title = "Process Return",
                            icon = Icons.Default.SwapHoriz,
                            color = Color(0xFFEC4899),
                            modifier = Modifier.weight(1f),
                            testTag = "quick_process_return_tile",
                            onClick = onQuickProcessReturn
                        )
                    }
                }
            }
        }

        // 2. LIVE FINANCIAL PERFORMANCE GRAPH (Requirement 2)
        item {
            LiveFinancialGraphCard(
                sales = allSales,
                expenses = allExpenses
            )
        }

        // 3. CURRENT MONTH SUMMARY (Requirement 4)
        // Replaced the four boxes with: monthly sales, monthly profit, monthly expenses, stock valuation
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "CURRENT MONTH SUMMARY",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black,
                    color = CyanAccent,
                    letterSpacing = 1.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Monthly Sales",
                        value = CurrencyUtils.formatPkr(monthlySales),
                        subtitle = "Recorded this month",
                        icon = Icons.Default.Receipt,
                        iconTint = CyanAccent,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigatePage(NomiPage.SALES) }
                    )
                    StatCard(
                        title = "Monthly Profit",
                        value = CurrencyUtils.formatPkr(monthlyProfit),
                        subtitle = "Gross sales profit",
                        icon = Icons.Default.TrendingUp,
                        iconTint = if (monthlyProfit >= 0) GreenSuccess else RedDanger,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigatePage(NomiPage.REPORTS) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Monthly Expenses",
                        value = CurrencyUtils.formatPkr(monthlyExpenses),
                        subtitle = "Operating overheads",
                        icon = Icons.Default.MoneyOff,
                        iconTint = GoldAmber,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigatePage(NomiPage.EXPENSES) }
                    )
                    StatCard(
                        title = "Stock Valuation",
                        value = CurrencyUtils.formatPkr(stockValuation),
                        subtitle = "${inStockPhones.size} in-stock units",
                        icon = Icons.Default.AttachMoney,
                        iconTint = BlueSky,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigatePage(NomiPage.INVENTORY) }
                    )
                }
            }
        }

        // 4. LIVE STOCK BREAKDOWN (Requirement 5)
        // [iphones (redirect inventory), android (redirect inventory), sold out (redirect sales),
        //  return (redirect returned), replace (redirect returned), Purchases (redirect purchases recorded)]
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LIVE STOCK BREAKDOWN",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = CyanAccent,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Tap to view list",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Row 1: iPhones & Android
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LiveStockTile(
                            title = "iPhones",
                            count = "$iphonesInStockCount In Stock",
                            icon = Icons.Default.PhoneIphone,
                            color = CyanAccent,
                            modifier = Modifier.weight(1f),
                            testTag = "tile_breakdown_iphones",
                            onClick = {
                                onFilterCategory("iPhone")
                                onNavigatePage(NomiPage.INVENTORY)
                            }
                        )
                        LiveStockTile(
                            title = "Android",
                            count = "$androidInStockCount In Stock",
                            icon = Icons.Default.Smartphone,
                            color = GreenSuccess,
                            modifier = Modifier.weight(1f),
                            testTag = "tile_breakdown_android",
                            onClick = {
                                onFilterCategory("Android")
                                onNavigatePage(NomiPage.INVENTORY)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Row 2: Sold Out & Return
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LiveStockTile(
                            title = "Sold Out",
                            count = "$soldOutCount Sold",
                            icon = Icons.Default.Receipt,
                            color = GoldAmber,
                            modifier = Modifier.weight(1f),
                            testTag = "tile_breakdown_sold",
                            onClick = { onNavigatePage(NomiPage.SALES) }
                        )
                        LiveStockTile(
                            title = "Return",
                            count = "$returnCount Returned",
                            icon = Icons.Default.SwapHoriz,
                            color = RedDanger,
                            modifier = Modifier.weight(1f),
                            testTag = "tile_breakdown_return",
                            onClick = { onNavigatePage(NomiPage.RETURNS) }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Row 3: Replace & Purchases
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LiveStockTile(
                            title = "Replace",
                            count = "$replaceCount Units",
                            icon = Icons.Default.SwapHoriz,
                            color = Color(0xFF8B5CF6),
                            modifier = Modifier.weight(1f),
                            testTag = "tile_breakdown_replace",
                            onClick = { onNavigatePage(NomiPage.RETURNS) }
                        )
                        LiveStockTile(
                            title = "Purchases",
                            count = "$purchasesCount Orders",
                            icon = Icons.Default.ShoppingBag,
                            color = BlueSky,
                            modifier = Modifier.weight(1f),
                            testTag = "tile_breakdown_purchases",
                            onClick = { onNavigatePage(NomiPage.PURCHASES) }
                        )
                    }
                }
            }
        }

        // 5. REMOVE ACTIONS SECTION (Requirement 3)
        // Positioned ON THE TOP OF EXPLORE BY CATEGORY!
        // Displayed as: [Product, Bill, Expense]
        // Box color is different (neutral surface), red color only for bin icon and text!
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("remove_actions_section")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "REMOVE ACTIONS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = RedDanger,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Delete from database",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // The 3 items: [Product, Bill, Expense] with red only for bin icon and text
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RemoveItemBox(
                            title = "Product",
                            modifier = Modifier.weight(1f),
                            testTag = "remove_action_product_tile",
                            onClick = onQuickRemoveProduct
                        )
                        RemoveItemBox(
                            title = "Bill",
                            modifier = Modifier.weight(1f),
                            testTag = "remove_action_bill_tile",
                            onClick = onQuickRemoveBill
                        )
                        RemoveItemBox(
                            title = "Expense",
                            modifier = Modifier.weight(1f),
                            testTag = "remove_action_expense_tile",
                            onClick = onQuickRemoveExpense
                        )
                    }
                }
            }
        }

        // 6. CATEGORY EXPLORER CHIPS
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "EXPLORE BY CATEGORY",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            onFilterCategory("All")
                            onNavigatePage(NomiPage.INVENTORY)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        CategoryExplorerChip(
                            title = "All Products",
                            count = inStockPhones.size,
                            onClick = {
                                onFilterCategory("All")
                                onNavigatePage(NomiPage.INVENTORY)
                            }
                        )
                    }
                    items(categories) { cat ->
                        val count = inStockPhones.count { it.category.equals(cat.name, ignoreCase = true) }
                        CategoryExplorerChip(
                            title = cat.name,
                            count = count,
                            onClick = {
                                onFilterCategory(cat.name)
                                onNavigatePage(NomiPage.INVENTORY)
                            }
                        )
                    }
                }
            }
        }

        // 7. RECENT IN-STOCK PHONES
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT INVENTORY",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "See Full Stock →",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyanAccent,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigatePage(NomiPage.INVENTORY) }
                )
            }
        }

        if (inStockPhones.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Inventory is currently empty. Tap 'Add Product' to stock handsets.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(inStockPhones.take(4)) { phone ->
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigatePage(NomiPage.INVENTORY) }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${phone.brand} ${phone.model}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                if (phone.batteryHealth != null) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${phone.batteryHealth}%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (phone.batteryHealth >= 85) GreenSuccess else GoldAmber
                                    )
                                }
                            }
                            Text(
                                text = "${phone.category} • ${phone.color} • ${phone.ptaStatus}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = CurrencyUtils.formatPkr(phone.askingPrice),
                            fontWeight = FontWeight.Black,
                            color = CyanAccent,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun QuickActionTile(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    testTag: String = "",
    onClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f)),
        modifier = modifier
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// Requirement 5: Live Stock Breakdown Tile
@Composable
private fun LiveStockTile(
    title: String,
    count: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    testTag: String = "",
    onClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        modifier = modifier
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
                Text(
                    text = count,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.sp
                )
            }
        }
    }
}

// Requirement 3: [Product, Bill, Expense] with neutral box background and red only for bin icon and text
@Composable
private fun RemoveItemBox(
    title: String,
    modifier: Modifier = Modifier,
    testTag: String = "",
    onClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface, // Distinct neutral box color (not red!)
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = modifier
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Bin icon in RED
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = RedDanger,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            // Text in RED
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = RedDanger,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun CategoryExplorerChip(
    title: String,
    count: Int,
    onClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .background(CyanAccent.copy(alpha = 0.2f), CircleShape)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = count.toString(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = CyanAccent
                )
            }
        }
    }
}
