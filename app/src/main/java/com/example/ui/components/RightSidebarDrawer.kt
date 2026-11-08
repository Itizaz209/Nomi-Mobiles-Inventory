package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tablet
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductCategoryEntity
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.RedDanger

enum class NomiPage(val label: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard),
    INVENTORY("Inventory & Products", Icons.Default.Inventory2),
    SALES("Sales & Bills", Icons.Default.Receipt),
    PURCHASES("Purchases", Icons.Default.ShoppingBag),
    EXPENSES("Shop Expenses", Icons.Default.MoneyOff),
    RETURNS("Returns & Refunds", Icons.Default.SwapHoriz),
    CUSTOMERS("Customers & Suppliers", Icons.Default.People),
    REPORTS("P&L Financial Reports", Icons.Default.Analytics),
    SETTINGS("Settings & Data", Icons.Default.Settings)
}

@Composable
fun RightSidebarDrawer(
    isOpen: Boolean,
    onClose: () -> Unit,
    currentPage: NomiPage,
    onSelectPage: (NomiPage) -> Unit,
    categories: List<ProductCategoryEntity>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    onAddNewCategory: (String) -> Unit,
    onDeleteCategory: (ProductCategoryEntity) -> Unit = {},
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
    var showNewCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryInput by remember { mutableStateOf("") }
    var categoryToDelete by remember { mutableStateOf<ProductCategoryEntity?>(null) }

    if (isOpen) {
        // Scrim background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable { onClose() }
        )
    }

    AnimatedVisibility(
        visible = isOpen,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it }),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .widthIn(min = 280.dp, max = 340.dp)
                    .clickable(enabled = false) {}, // prevent click-through
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp, bottom = 20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Drawer Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(CyanAccent, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "NM",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "NOMI MOBILES",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Management System",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CyanAccent
                                )
                            }
                        }

                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.testTag("close_right_sidebar_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Sidebar",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // SECTION 1: PAGES & SECTIONS
                    Text(
                        text = "PAGES & SECTIONS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )

                    NomiPage.values().forEach { page ->
                        val isSelected = (currentPage == page)
                        val itemColor = if (isSelected) CyanAccent else MaterialTheme.colorScheme.onSurface
                        val bg = if (isSelected) CyanAccent.copy(alpha = 0.14f) else Color.Transparent

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 2.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(bg)
                                .clickable {
                                    onSelectPage(page)
                                    onClose()
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                .testTag("sidebar_nav_${page.name.lowercase()}"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = page.icon,
                                contentDescription = null,
                                tint = itemColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = page.label,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = itemColor,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // SECTION 2: PRODUCT CATEGORIES
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PRODUCT CATEGORIES",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        IconButton(
                            onClick = { showNewCategoryDialog = true },
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Category",
                                tint = CyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // "All Categories" item
                    val allSelected = (selectedCategory.isBlank() || selectedCategory == "All")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 2.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (allSelected) CyanAccent.copy(alpha = 0.12f) else Color.Transparent)
                            .clickable {
                                onSelectCategory("All")
                                onSelectPage(NomiPage.INVENTORY)
                                onClose()
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            tint = if (allSelected) CyanAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "All Products",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (allSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (allSelected) CyanAccent else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    categories.forEach { cat ->
                        val isCatSelected = (selectedCategory == cat.name)
                        val icon = when (cat.name.lowercase()) {
                            "iphone" -> Icons.Default.Smartphone
                            "android" -> Icons.Default.Smartphone
                            "accessories" -> Icons.Default.Headphones
                            "tablets" -> Icons.Default.Tablet
                            "smart watches" -> Icons.Default.Watch
                            else -> Icons.Default.Category
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 2.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isCatSelected) CyanAccent.copy(alpha = 0.12f) else Color.Transparent)
                                .clickable {
                                    onSelectCategory(cat.name)
                                    onSelectPage(NomiPage.INVENTORY)
                                    onClose()
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("sidebar_cat_${cat.name.lowercase()}"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isCatSelected) CyanAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = cat.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isCatSelected) CyanAccent else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )

                            // Delete Category Button
                            IconButton(
                                onClick = { categoryToDelete = cat },
                                modifier = Modifier
                                    .size(24.dp)
                                    .testTag("delete_cat_btn_${cat.name.lowercase()}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Delete ${cat.name}",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // SECTION 3: QUICK ACTIONS IN SIDEBAR
                    Text(
                        text = "QUICK ACTIONS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )

                    // Add actions
                    SidebarActionButton(
                        label = "Add Product",
                        icon = Icons.Default.AddCircle,
                        tint = GreenSuccess,
                        onClick = {
                            onClose()
                            onQuickAddProduct()
                        }
                    )
                    SidebarActionButton(
                        label = "Create Bill",
                        icon = Icons.Default.PointOfSale,
                        tint = CyanAccent,
                        onClick = {
                            onClose()
                            onQuickCreateBill()
                        }
                    )
                    SidebarActionButton(
                        label = "Add Expense",
                        icon = Icons.Default.MoneyOff,
                        tint = GoldAmber,
                        onClick = {
                            onClose()
                            onQuickAddExpense()
                        }
                    )
                    SidebarActionButton(
                        label = "Process Return",
                        icon = Icons.Default.SwapHoriz,
                        tint = RedDanger,
                        onClick = {
                            onClose()
                            onQuickProcessReturn()
                        }
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "REMOVE ACTIONS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = RedDanger.copy(alpha = 0.8f),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )

                    SidebarActionButton(
                        label = "Remove Product",
                        icon = Icons.Default.Delete,
                        tint = RedDanger,
                        onClick = {
                            onClose()
                            onQuickRemoveProduct()
                        }
                    )
                    SidebarActionButton(
                        label = "Remove Bill",
                        icon = Icons.Default.Delete,
                        tint = RedDanger,
                        onClick = {
                            onClose()
                            onQuickRemoveBill()
                        }
                    )
                    SidebarActionButton(
                        label = "Remove Expense",
                        icon = Icons.Default.Delete,
                        tint = RedDanger,
                        onClick = {
                            onClose()
                            onQuickRemoveExpense()
                        }
                    )
                }
            }
        }
    }

    // Add New Category Dialog
    if (showNewCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showNewCategoryDialog = false },
            title = { Text("Create New Category") },
            text = {
                OutlinedTextField(
                    value = newCategoryInput,
                    onValueChange = { newCategoryInput = it },
                    label = { Text("Category Name") },
                    placeholder = { Text("e.g. Tablets, Airpods, Chargers") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_category_dialog_input")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCategoryInput.isNotBlank()) {
                            onAddNewCategory(newCategoryInput.trim())
                            newCategoryInput = ""
                            showNewCategoryDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color.Black)
                ) {
                    Text("Save Category")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewCategoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Category Confirmation Dialog
    if (categoryToDelete != null) {
        val cat = categoryToDelete!!
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            title = { Text("Delete Category?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete '${cat.name}'? Products in this category will remain in inventory.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteCategory(cat)
                        categoryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDanger, contentColor = Color.White)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SidebarActionButton(
    label: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
