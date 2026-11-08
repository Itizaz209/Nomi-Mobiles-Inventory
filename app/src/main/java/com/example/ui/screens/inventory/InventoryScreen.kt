package com.example.ui.screens.inventory

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PhoneEntity
import com.example.data.model.ProductCategoryEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.RedDanger
import com.example.util.CurrencyUtils

@Composable
fun InventoryScreen(
    phones: List<PhoneEntity>,
    categories: List<ProductCategoryEntity>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    onAddProductClick: () -> Unit,
    onEditPhone: (PhoneEntity) -> Unit,
    onDeletePhone: (PhoneEntity) -> Unit,
    onSellPhone: (PhoneEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            // Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == "All" || selectedCategory.isBlank(),
                        onClick = { onSelectCategory("All") },
                        label = { Text("All (${phones.size})", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyanAccent,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
                items(categories) { cat ->
                    val isSelected = selectedCategory.equals(cat.name, ignoreCase = true)
                    val count = phones.count { it.category.equals(cat.name, ignoreCase = true) }
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectCategory(cat.name) },
                        label = { Text("${cat.name} ($count)", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyanAccent,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Inventory List
            if (phones.isEmpty()) {
                EmptyStateView(
                    title = "No Inventory Found",
                    message = "No handsets match your active filter or search. Tap Add Product to add new devices to stock.",
                    icon = Icons.Default.Inventory2,
                    actionText = "+ Add New Product",
                    onActionClick = onAddProductClick
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(phones, key = { it.id }) { phone ->
                        ProductItemCard(
                            phone = phone,
                            onEdit = { onEditPhone(phone) },
                            onDelete = { onDeletePhone(phone) },
                            onSell = { onSellPhone(phone) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp)) // padding for FAB
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = onAddProductClick,
            containerColor = CyanAccent,
            contentColor = Color.Black,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("inventory_fab_add_product")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Product", modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun ProductItemCard(
    phone: PhoneEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSell: () -> Unit
) {
    val isIPhone = phone.category.equals("iPhone", ignoreCase = true)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Brand & Model, Status badges
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                        Icon(
                            imageVector = if (isIPhone) Icons.Default.PhoneIphone else Icons.Default.Smartphone,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "${phone.brand} ${phone.model}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${phone.category} • ${phone.color}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                StatusBadge(status = phone.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Specs Row: Storage, RAM, PTA Status, Condition, Battery Health
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SpecPill(text = phone.storage)
                if (phone.ram != "N/A" && phone.ram.isNotBlank()) {
                    SpecPill(text = "${phone.ram} RAM")
                }
                StatusBadge(status = phone.ptaStatus)

                // Battery Health display (Requirement 9)
                if (phone.batteryHealth != null) {
                    val health = phone.batteryHealth
                    val tint = when {
                        health >= 85 -> GreenSuccess
                        health >= 75 -> GoldAmber
                        else -> RedDanger
                    }
                    Surface(
                        color = tint.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.BatteryChargingFull,
                                contentDescription = null,
                                tint = tint,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "$health%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = tint
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // IMEI Display (IMEI 2 is removed per requirement 8)
            if (phone.imei.isNotBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "IMEI: ${phone.imei}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = phone.condition,
                            fontSize = 11.sp,
                            color = CyanAccent,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Price & Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Asking Price",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = CurrencyUtils.formatPkr(phone.askingPrice),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = CyanAccent
                    )
                    Text(
                        text = "Cost: ${CurrencyUtils.formatPkr(phone.costPrice)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (phone.status == "In Stock") {
                        Button(
                            onClick = onSell,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyanAccent,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("sell_phone_btn_${phone.id}")
                        ) {
                            Icon(Icons.Default.PointOfSale, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sell", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedDanger, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecPill(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}
