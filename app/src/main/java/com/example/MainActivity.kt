package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.FilterSortDialog
import com.example.ui.components.GlobalSearchBarWithControls
import com.example.ui.components.ImeiScannerDialog
import com.example.ui.components.NomiPage
import com.example.ui.components.RightSidebarDrawer
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.expenses.AddExpenseDialog
import com.example.ui.screens.expenses.ExpensesScreen
import com.example.ui.screens.inventory.AddEditProductDialog
import com.example.ui.screens.inventory.InventoryScreen
import com.example.ui.screens.more.CustomersSuppliersScreen
import com.example.ui.screens.more.ReportsScreen
import com.example.ui.screens.more.SettingsScreen
import com.example.ui.screens.purchases.NewPurchaseDialog
import com.example.ui.screens.purchases.PurchasesScreen
import com.example.ui.screens.quickactions.RemoveBillDialog
import com.example.ui.screens.quickactions.RemoveExpenseDialog
import com.example.ui.screens.quickactions.RemoveProductDialog
import com.example.ui.screens.returns.ProcessReturnDialog
import com.example.ui.screens.returns.ReturnsScreen
import com.example.ui.screens.sales.CreateBillDialog
import com.example.ui.screens.sales.ReceiptDialog
import com.example.ui.screens.sales.SalesScreen
import com.example.ui.theme.NomiMobilesTheme
import com.example.ui.viewmodel.NomiViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: NomiViewModel = viewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()

            NomiMobilesTheme(darkTheme = isDarkMode) {
                NomiAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun NomiAppContent(viewModel: NomiViewModel) {
    val currentPage by viewModel.currentPage.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val isRightSidebarOpen by viewModel.isRightSidebarOpen.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedPtaFilter by viewModel.selectedPtaFilter.collectAsStateWithLifecycle()
    val selectedSortOption by viewModel.selectedSortOption.collectAsStateWithLifecycle()
    val showFilterDialog by viewModel.showFilterDialog.collectAsStateWithLifecycle()

    val inStockPhones by viewModel.filteredInStockPhones.collectAsStateWithLifecycle()
    val allPhones by viewModel.allPhones.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val allSales by viewModel.filteredSales.collectAsStateWithLifecycle()
    val allPurchases by viewModel.allPurchases.collectAsStateWithLifecycle()
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val allReturns by viewModel.allReturns.collectAsStateWithLifecycle()
    val supplierNames by viewModel.supplierNames.collectAsStateWithLifecycle()

    // Modals
    val showAddEditProduct by viewModel.showAddEditProductDialog.collectAsStateWithLifecycle()
    val showCreateBill by viewModel.showCreateBillDialog.collectAsStateWithLifecycle()
    val activeReceipt by viewModel.activeReceiptSale.collectAsStateWithLifecycle()
    val showNewPurchase by viewModel.showNewPurchaseDialog.collectAsStateWithLifecycle()
    val showAddExpense by viewModel.showAddExpenseDialog.collectAsStateWithLifecycle()
    val showProcessReturn by viewModel.showProcessReturnDialog.collectAsStateWithLifecycle()
    val showRemoveProduct by viewModel.showRemoveProductDialog.collectAsStateWithLifecycle()
    val showRemoveBill by viewModel.showRemoveBillDialog.collectAsStateWithLifecycle()
    val showRemoveExpense by viewModel.showRemoveExpenseDialog.collectAsStateWithLifecycle()
    val showCameraScanner by viewModel.showCameraScannerDialog.collectAsStateWithLifecycle()

    // Back handler
    BackHandler(enabled = isRightSidebarOpen || currentPage != NomiPage.DASHBOARD) {
        if (isRightSidebarOpen) {
            viewModel.closeRightSidebar()
        } else {
            viewModel.setPage(NomiPage.DASHBOARD)
        }
    }

    // REQUIREMENT 1: Layout & Spacing
    // Set top margin/padding to 5% across the entire application using BoxWithConstraints
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val topPadding = maxHeight * 0.05f

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 680.dp)
                    .align(Alignment.TopCenter)
            ) {
                // REQUIREMENT 2: Global Search Bar & Controls
                // Includes: Global search, Camera barcode scanner, Filters, Dark mode toggle, Scan IMEI button, and Right drawer toggle
                val activeFilterCount = (if (selectedCategory != "All") 1 else 0) +
                        (if (selectedPtaFilter != "All PTA") 1 else 0) +
                        (if (selectedSortOption != "Newest First") 1 else 0)

                GlobalSearchBarWithControls(
                    searchQuery = searchQuery,
                    onQueryChange = { viewModel.setSearchQuery(it) },
                    onCameraScanClick = { viewModel.openCameraScanner() },
                    onFilterClick = { viewModel.openFilterDialog() },
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { viewModel.toggleDarkMode() },
                    activeFilterCount = activeFilterCount
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Page Content Host
                Box(modifier = Modifier.weight(1f)) {
                    when (currentPage) {
                        NomiPage.DASHBOARD -> {
                            DashboardScreen(
                                inStockPhones = inStockPhones,
                                allPhones = allPhones,
                                allSales = allSales,
                                allPurchases = allPurchases,
                                allExpenses = allExpenses,
                                allReturns = allReturns,
                                categories = categories,
                                onNavigatePage = { viewModel.setPage(it) },
                                onFilterCategory = {
                                    viewModel.setSelectedCategory(it)
                                },
                                onQuickAddProduct = { viewModel.openAddProductDialog() },
                                onQuickCreateBill = { viewModel.openCreateBillDialog() },
                                onQuickAddExpense = { viewModel.openAddExpenseDialog() },
                                onQuickProcessReturn = { viewModel.openProcessReturnDialog() },
                                onQuickRemoveProduct = { viewModel.openRemoveProductDialog() },
                                onQuickRemoveBill = { viewModel.openRemoveBillDialog() },
                                onQuickRemoveExpense = { viewModel.openRemoveExpenseDialog() }
                            )
                        }

                        NomiPage.INVENTORY -> {
                            InventoryScreen(
                                phones = inStockPhones,
                                categories = categories,
                                selectedCategory = selectedCategory,
                                onSelectCategory = { viewModel.setSelectedCategory(it) },
                                onAddProductClick = { viewModel.openAddProductDialog() },
                                onEditPhone = { viewModel.openAddProductDialog(it) },
                                onDeletePhone = { viewModel.deleteProduct(it) },
                                onSellPhone = { viewModel.openCreateBillDialog(it) }
                            )
                        }

                        NomiPage.SALES -> {
                            SalesScreen(
                                sales = allSales,
                                onCreateBillClick = { viewModel.openCreateBillDialog() },
                                onViewReceipt = { viewModel.openReceiptForSale(it) },
                                onProcessReturn = { viewModel.openProcessReturnDialog() },
                                onDeleteBill = { viewModel.deleteBill(it) }
                            )
                        }

                        NomiPage.PURCHASES -> {
                            PurchasesScreen(
                                purchases = allPurchases,
                                onNewPurchaseClick = { viewModel.openNewPurchaseDialog() },
                                onDeletePurchase = { viewModel.deletePurchase(it) }
                            )
                        }

                        NomiPage.EXPENSES -> {
                            ExpensesScreen(
                                expenses = allExpenses,
                                onAddExpenseClick = { viewModel.openAddExpenseDialog() },
                                onDeleteExpense = { viewModel.deleteExpense(it) }
                            )
                        }

                        NomiPage.RETURNS -> {
                            ReturnsScreen(
                                returns = allReturns,
                                onProcessReturnClick = { viewModel.openProcessReturnDialog() }
                            )
                        }

                        NomiPage.CUSTOMERS -> {
                            CustomersSuppliersScreen(
                                sales = allSales,
                                purchases = allPurchases
                            )
                        }

                        NomiPage.REPORTS -> {
                            ReportsScreen(
                                inStockPhones = inStockPhones,
                                sales = allSales,
                                expenses = allExpenses
                            )
                        }

                        NomiPage.SETTINGS -> {
                            SettingsScreen(
                                phoneCount = allPhones.size,
                                saleCount = allSales.size,
                                expenseCount = allExpenses.size
                            )
                        }
                    }
                }

                // Bottom Navigation Bar
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("main_bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = currentPage == NomiPage.DASHBOARD && !isRightSidebarOpen,
                        onClick = {
                            viewModel.closeRightSidebar()
                            viewModel.setPage(NomiPage.DASHBOARD)
                        },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Dashboard", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = CyanAccent.copy(alpha = 0.2f),
                            selectedIconColor = CyanAccent,
                            selectedTextColor = CyanAccent
                        ),
                        modifier = Modifier.testTag("nav_item_dashboard")
                    )
                    NavigationBarItem(
                        selected = currentPage == NomiPage.INVENTORY && !isRightSidebarOpen,
                        onClick = {
                            viewModel.closeRightSidebar()
                            viewModel.setPage(NomiPage.INVENTORY)
                        },
                        icon = { Icon(Icons.Default.Inventory2, contentDescription = "Inventory") },
                        label = { Text("Inventory", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = CyanAccent.copy(alpha = 0.2f),
                            selectedIconColor = CyanAccent,
                            selectedTextColor = CyanAccent
                        ),
                        modifier = Modifier.testTag("nav_item_inventory")
                    )
                    NavigationBarItem(
                        selected = currentPage == NomiPage.SALES && !isRightSidebarOpen,
                        onClick = {
                            viewModel.closeRightSidebar()
                            viewModel.setPage(NomiPage.SALES)
                        },
                        icon = { Icon(Icons.Default.Receipt, contentDescription = "Sales") },
                        label = { Text("Sales", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = CyanAccent.copy(alpha = 0.2f),
                            selectedIconColor = CyanAccent,
                            selectedTextColor = CyanAccent
                        ),
                        modifier = Modifier.testTag("nav_item_sales")
                    )
                    NavigationBarItem(
                        selected = currentPage == NomiPage.PURCHASES && !isRightSidebarOpen,
                        onClick = {
                            viewModel.closeRightSidebar()
                            viewModel.setPage(NomiPage.PURCHASES)
                        },
                        icon = { Icon(Icons.Default.ShoppingBag, contentDescription = "Purchases") },
                        label = { Text("Purchases", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = CyanAccent.copy(alpha = 0.2f),
                            selectedIconColor = CyanAccent,
                            selectedTextColor = CyanAccent
                        ),
                        modifier = Modifier.testTag("nav_item_purchases")
                    )
                    NavigationBarItem(
                        selected = isRightSidebarOpen,
                        onClick = {
                            if (isRightSidebarOpen) viewModel.closeRightSidebar() else viewModel.openRightSidebar()
                        },
                        icon = { Icon(Icons.Default.Menu, contentDescription = "Menu") },
                        label = { Text("Menu", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = CyanAccent.copy(alpha = 0.2f),
                            selectedIconColor = CyanAccent,
                            selectedTextColor = CyanAccent
                        ),
                        modifier = Modifier.testTag("nav_item_menu")
                    )
                }
            }

            // REQUIREMENT 3: Sidebar Navigation moved to Right Side
            RightSidebarDrawer(
                isOpen = isRightSidebarOpen,
                onClose = { viewModel.closeRightSidebar() },
                currentPage = currentPage,
                onSelectPage = { viewModel.setPage(it) },
                categories = categories,
                selectedCategory = selectedCategory,
                onSelectCategory = { viewModel.setSelectedCategory(it) },
                onAddNewCategory = { viewModel.addNewCategory(it) },
                onDeleteCategory = { viewModel.deleteCategory(it) },
                onQuickAddProduct = { viewModel.openAddProductDialog() },
                onQuickCreateBill = { viewModel.openCreateBillDialog() },
                onQuickAddExpense = { viewModel.openAddExpenseDialog() },
                onQuickProcessReturn = { viewModel.openProcessReturnDialog() },
                onQuickRemoveProduct = { viewModel.openRemoveProductDialog() },
                onQuickRemoveBill = { viewModel.openRemoveBillDialog() },
                onQuickRemoveExpense = { viewModel.openRemoveExpenseDialog() }
            )
        }
    }

    // --- MODAL DIALOGS ---

    // Filters & Sorting Dialog
    if (showFilterDialog) {
        FilterSortDialog(
            selectedCategory = selectedCategory,
            onCategorySelected = { viewModel.setSelectedCategory(it) },
            categories = categories.map { it.name },
            selectedPta = selectedPtaFilter,
            onPtaSelected = { viewModel.setSelectedPta(it) },
            selectedSort = selectedSortOption,
            onSortSelected = { viewModel.setSelectedSort(it) },
            onReset = { viewModel.resetFilters() },
            onDismiss = { viewModel.closeFilterDialog() }
        )
    }

    // Add / Edit Product Dialog (Requirements 5, 7, 8, 9, 10, 11)
    if (showAddEditProduct) {
        AddEditProductDialog(
            initialPhone = viewModel.editingPhone,
            categories = categories,
            supplierList = supplierNames,
            onDismiss = { viewModel.closeAddProductDialog() },
            onSave = { phone -> viewModel.saveProduct(phone) },
            onAddNewCategory = { viewModel.addNewCategory(it) }
        )
    }

    // Create Customer Bill Dialog
    if (showCreateBill) {
        CreateBillDialog(
            inStockPhones = inStockPhones,
            preSelectedPhone = viewModel.preSelectedPhoneForSale,
            onDismiss = { viewModel.closeCreateBillDialog() },
            onBillCreated = { sale, phoneId -> viewModel.createBill(sale, phoneId) }
        )
    }

    // Customer Receipt View Dialog
    if (activeReceipt != null) {
        ReceiptDialog(
            sale = activeReceipt!!,
            onDismiss = { viewModel.closeReceiptDialog() }
        )
    }

    // Record Mobile Purchase Dialog (Requirement 11)
    if (showNewPurchase) {
        NewPurchaseDialog(
            existingSuppliers = supplierNames,
            onDismiss = { viewModel.closeNewPurchaseDialog() },
            onPurchaseConfirmed = { purchase, phone -> viewModel.recordPurchase(purchase, phone) }
        )
    }

    // Add Shop Expense Dialog
    if (showAddExpense) {
        AddExpenseDialog(
            onDismiss = { viewModel.closeAddExpenseDialog() },
            onSaveExpense = { viewModel.saveExpense(it) }
        )
    }

    // Process Return Dialog
    if (showProcessReturn) {
        ProcessReturnDialog(
            salesList = allSales,
            onDismiss = { viewModel.closeProcessReturnDialog() },
            onReturnConfirmed = { saleReturn, phoneId, saleId ->
                viewModel.processReturn(saleReturn, phoneId, saleId)
            }
        )
    }

    // Quick Action: Remove Product Dialog
    if (showRemoveProduct) {
        RemoveProductDialog(
            products = allPhones,
            onDismiss = { viewModel.closeRemoveProductDialog() },
            onDeleteProduct = { viewModel.deleteProduct(it) }
        )
    }

    // Quick Action: Remove Bill Dialog
    if (showRemoveBill) {
        RemoveBillDialog(
            sales = allSales,
            onDismiss = { viewModel.closeRemoveBillDialog() },
            onDeleteBill = { viewModel.deleteBill(it) }
        )
    }

    // Quick Action: Remove Expense Dialog
    if (showRemoveExpense) {
        RemoveExpenseDialog(
            expenses = allExpenses,
            onDismiss = { viewModel.closeRemoveExpenseDialog() },
            onDeleteExpense = { viewModel.deleteExpense(it) }
        )
    }

    // Camera Barcode / IMEI Scanner (Requirements 2 & 6)
    if (showCameraScanner) {
        ImeiScannerDialog(
            title = "Scan Device Barcode or IMEI",
            onDismiss = { viewModel.closeCameraScanner() },
            onScanned = { scanned -> viewModel.onBarcodeScanned(scanned) }
        )
    }
}
