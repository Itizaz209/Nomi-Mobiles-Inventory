package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.RedDanger

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector,
    iconTint: Color = CyanAccent,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(iconTint.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status) {
        "In Stock", "Paid", "PTA Approved" -> GreenSuccess.copy(alpha = 0.15f) to GreenSuccess
        "Sold", "Partial" -> GoldAmber.copy(alpha = 0.15f) to GoldAmber
        "Returned", "Non-PTA" -> RedDanger.copy(alpha = 0.15f) to RedDanger
        "JV (Gevey)", "JV" -> Color(0xFF8B5CF6).copy(alpha = 0.15f) to Color(0xFF8B5CF6)
        "Software Bypass", "Bypass" -> Color(0xFFEC4899).copy(alpha = 0.15f) to Color(0xFFEC4899)
        else -> CyanAccent.copy(alpha = 0.15f) to CyanAccent
    }

    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(8.dp))
            .border(1.dp, textColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun GlobalSearchBarWithControls(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onCameraScanClick: () -> Unit,
    onFilterClick: () -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    activeFilterCount: Int = 0,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        // Top control bar: Search bar + Barcode scan + Scan IMEI + Filters + Dark mode + Right Drawer
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Main search input with camera icon & scan IMEI button inside
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        "Search models, IMEI, bills...",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { onQueryChange("") },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Camera scanner icon inside search bar (no text, only camera image)
                        IconButton(
                            onClick = onCameraScanClick,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("search_camera_scanner_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Scan IMEI / Barcode",
                                tint = CyanAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanAccent,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("global_search_input")
            )

            // Filter button
            IconButton(
                onClick = onFilterClick,
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        if (activeFilterCount > 0) CyanAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(12.dp)
                    )
                    .border(
                        1.dp,
                        if (activeFilterCount > 0) CyanAccent else MaterialTheme.colorScheme.outline,
                        RoundedCornerShape(12.dp)
                    )
                    .testTag("filter_button")
            ) {
                Box(contentAlignment = Alignment.TopEnd) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filters",
                        tint = if (activeFilterCount > 0) CyanAccent else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                    if (activeFilterCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(CyanAccent, CircleShape)
                        )
                    }
                }
            }

            // Dark Mode toggle button
            IconButton(
                onClick = onToggleDarkMode,
                modifier = Modifier
                    .size(42.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                    .testTag("dark_mode_toggle_button")
            ) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Dark Mode",
                    tint = if (isDarkMode) GoldAmber else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun FilterSortDialog(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    categories: List<String>,
    selectedPta: String,
    onPtaSelected: (String) -> Unit,
    selectedSort: String,
    onSortSelected: (String) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    val sortOptions = listOf(
        "Newest First",
        "Price: High to Low",
        "Price: Low to High",
        "Model: A to Z"
    )

    val ptaOptions = listOf(
        "All PTA",
        "PTA Approved",
        "Non-PTA",
        "Non-PTA SIM Working",
        "JV",
        "Bypass"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Search & Sort Filters", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Categories
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val allCats = listOf("All") + categories
                    allCats.take(4).forEach { cat ->
                        FilterChip(
                            selected = (selectedCategory == cat),
                            onClick = { onCategorySelected(cat) },
                            label = { Text(cat, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanAccent,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                // PTA Status
                Text(
                    text = "PTA Approval Status",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ptaOptions.take(3).forEach { pta ->
                        FilterChip(
                            selected = (selectedPta == pta),
                            onClick = { onPtaSelected(pta) },
                            label = { Text(pta, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanAccent,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                // Sort By
                Text(
                    text = "Sort Products By",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    sortOptions.forEach { opt ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onSortSelected(opt) }
                                .padding(vertical = 6.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = opt,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (selectedSort == opt) CyanAccent else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (selectedSort == opt) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.weight(1f)
                            )
                            if (selectedSort == opt) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color.Black)
            ) {
                Text("Apply Filters")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onReset()
                onDismiss()
            }) {
                Text("Reset All")
            }
        }
    )
}

@Composable
fun EmptyStateView(
    title: String,
    message: String,
    icon: ImageVector,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(CyanAccent.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CyanAccent,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanAccent,
                    contentColor = Color.Black
                )
            ) {
                Text(actionText, fontWeight = FontWeight.Bold)
            }
        }
    }
}
