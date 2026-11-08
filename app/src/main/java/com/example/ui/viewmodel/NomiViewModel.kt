package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.NomiDatabase
import com.example.data.model.ExpenseEntity
import com.example.data.model.PhoneEntity
import com.example.data.model.ProductCategoryEntity
import com.example.data.model.PurchaseEntity
import com.example.data.model.SaleEntity
import com.example.data.model.SaleReturnEntity
import com.example.data.repository.NomiRepository
import com.example.ui.components.NomiPage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NomiViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NomiRepository

    init {
        val database = NomiDatabase.getDatabase(application)
        repository = NomiRepository(database.nomiDao())
    }

    // Database Flows
    val allPhones: StateFlow<List<PhoneEntity>> = repository.allPhones
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inStockPhones: StateFlow<List<PhoneEntity>> = repository.inStockPhones
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<ProductCategoryEntity>> = repository.categories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSales: StateFlow<List<SaleEntity>> = repository.allSales
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReturns: StateFlow<List<SaleReturnEntity>> = repository.allReturns
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPurchases: StateFlow<List<PurchaseEntity>> = repository.allPurchases
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allExpenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val supplierNames: StateFlow<List<String>> = repository.supplierNames
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI & Navigation States
    private val _currentPage = MutableStateFlow(NomiPage.DASHBOARD)
    val currentPage: StateFlow<NomiPage> = _currentPage.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true) // Default Dark Navy & Cyan theme
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _isRightSidebarOpen = MutableStateFlow(false)
    val isRightSidebarOpen: StateFlow<Boolean> = _isRightSidebarOpen.asStateFlow()

    // Search & Filter States
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedPtaFilter = MutableStateFlow("All PTA")
    val selectedPtaFilter: StateFlow<String> = _selectedPtaFilter.asStateFlow()

    private val _selectedSortOption = MutableStateFlow("Newest First")
    val selectedSortOption: StateFlow<String> = _selectedSortOption.asStateFlow()

    private val _showFilterDialog = MutableStateFlow(false)
    val showFilterDialog: StateFlow<Boolean> = _showFilterDialog.asStateFlow()

    // Dialogs & Modals States
    var editingPhone: PhoneEntity? = null
    private val _showAddEditProductDialog = MutableStateFlow(false)
    val showAddEditProductDialog: StateFlow<Boolean> = _showAddEditProductDialog.asStateFlow()

    var preSelectedPhoneForSale: PhoneEntity? = null
    private val _showCreateBillDialog = MutableStateFlow(false)
    val showCreateBillDialog: StateFlow<Boolean> = _showCreateBillDialog.asStateFlow()

    private val _activeReceiptSale = MutableStateFlow<SaleEntity?>(null)
    val activeReceiptSale: StateFlow<SaleEntity?> = _activeReceiptSale.asStateFlow()

    private val _showNewPurchaseDialog = MutableStateFlow(false)
    val showNewPurchaseDialog: StateFlow<Boolean> = _showNewPurchaseDialog.asStateFlow()

    private val _showAddExpenseDialog = MutableStateFlow(false)
    val showAddExpenseDialog: StateFlow<Boolean> = _showAddExpenseDialog.asStateFlow()

    private val _showProcessReturnDialog = MutableStateFlow(false)
    val showProcessReturnDialog: StateFlow<Boolean> = _showProcessReturnDialog.asStateFlow()

    private val _showRemoveProductDialog = MutableStateFlow(false)
    val showRemoveProductDialog: StateFlow<Boolean> = _showRemoveProductDialog.asStateFlow()

    private val _showRemoveBillDialog = MutableStateFlow(false)
    val showRemoveBillDialog: StateFlow<Boolean> = _showRemoveBillDialog.asStateFlow()

    private val _showRemoveExpenseDialog = MutableStateFlow(false)
    val showRemoveExpenseDialog: StateFlow<Boolean> = _showRemoveExpenseDialog.asStateFlow()

    private val _showCameraScannerDialog = MutableStateFlow(false)
    val showCameraScannerDialog: StateFlow<Boolean> = _showCameraScannerDialog.asStateFlow()

    // Filtered & Sorted In-Stock Phones
    val filteredInStockPhones: StateFlow<List<PhoneEntity>> = combine(
        inStockPhones,
        _searchQuery,
        _selectedCategory,
        _selectedPtaFilter,
        _selectedSortOption
    ) { list, query, category, pta, sort ->
        var result = list

        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            result = result.filter {
                it.model.lowercase().contains(q) ||
                        it.brand.lowercase().contains(q) ||
                        it.imei.lowercase().contains(q) ||
                        it.color.lowercase().contains(q) ||
                        it.category.lowercase().contains(q)
            }
        }

        if (category.isNotBlank() && category != "All") {
            result = result.filter { it.category.equals(category, ignoreCase = true) }
        }

        if (pta.isNotBlank() && pta != "All PTA") {
            result = result.filter { it.ptaStatus.contains(pta, ignoreCase = true) }
        }

        when (sort) {
            "Price: High to Low" -> result.sortedByDescending { it.askingPrice }
            "Price: Low to High" -> result.sortedBy { it.askingPrice }
            "Model: A to Z" -> result.sortedBy { it.model }
            else -> result.sortedByDescending { it.id }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Sales (by query)
    val filteredSales: StateFlow<List<SaleEntity>> = combine(
        allSales,
        _searchQuery
    ) { sales, query ->
        if (query.isBlank()) sales else {
            val q = query.trim().lowercase()
            sales.filter {
                it.billNumber.lowercase().contains(q) ||
                        it.customerName.lowercase().contains(q) ||
                        it.imei.lowercase().contains(q) ||
                        it.phoneSummary.lowercase().contains(q)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Actions
    fun setPage(page: NomiPage) {
        _currentPage.value = page
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun openRightSidebar() {
        _isRightSidebarOpen.value = true
    }

    fun closeRightSidebar() {
        _isRightSidebarOpen.value = false
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(cat: String) {
        _selectedCategory.value = cat
    }

    fun setSelectedPta(pta: String) {
        _selectedPtaFilter.value = pta
    }

    fun setSelectedSort(sort: String) {
        _selectedSortOption.value = sort
    }

    fun openFilterDialog() {
        _showFilterDialog.value = true
    }

    fun closeFilterDialog() {
        _showFilterDialog.value = false
    }

    fun resetFilters() {
        _selectedCategory.value = "All"
        _selectedPtaFilter.value = "All PTA"
        _selectedSortOption.value = "Newest First"
        _searchQuery.value = ""
    }

    // Category
    fun addNewCategory(name: String) {
        viewModelScope.launch {
            repository.addCategory(name)
        }
    }

    fun deleteCategory(category: ProductCategoryEntity) {
        viewModelScope.launch {
            repository.deleteCategory(category)
            if (_selectedCategory.value == category.name) {
                _selectedCategory.value = "All"
            }
        }
    }

    // Add / Edit Product
    fun openAddProductDialog(phone: PhoneEntity? = null) {
        editingPhone = phone
        _showAddEditProductDialog.value = true
    }

    fun closeAddProductDialog() {
        _showAddEditProductDialog.value = false
        editingPhone = null
    }

    fun saveProduct(phone: PhoneEntity) {
        viewModelScope.launch {
            if (phone.id > 0) {
                repository.updatePhone(phone)
            } else {
                repository.addPhone(phone)
            }
            closeAddProductDialog()
        }
    }

    fun deleteProduct(phone: PhoneEntity) {
        viewModelScope.launch {
            repository.deletePhoneById(phone.id)
        }
    }

    // Create Bill
    fun openCreateBillDialog(phone: PhoneEntity? = null) {
        preSelectedPhoneForSale = phone
        _showCreateBillDialog.value = true
    }

    fun closeCreateBillDialog() {
        _showCreateBillDialog.value = false
        preSelectedPhoneForSale = null
    }

    fun createBill(sale: SaleEntity, phoneId: Long) {
        viewModelScope.launch {
            val saleId = repository.createSale(sale, phoneId)
            closeCreateBillDialog()
            // Open receipt view
            _activeReceiptSale.value = sale.copy(id = saleId)
        }
    }

    fun closeReceiptDialog() {
        _activeReceiptSale.value = null
    }

    fun openReceiptForSale(sale: SaleEntity) {
        _activeReceiptSale.value = sale
    }

    fun deleteBill(sale: SaleEntity) {
        viewModelScope.launch {
            repository.deleteSale(sale.id, sale.phoneId)
        }
    }

    // Purchase
    fun openNewPurchaseDialog() {
        _showNewPurchaseDialog.value = true
    }

    fun closeNewPurchaseDialog() {
        _showNewPurchaseDialog.value = false
    }

    fun recordPurchase(purchase: PurchaseEntity, phone: PhoneEntity) {
        viewModelScope.launch {
            repository.recordPurchaseWithPhone(purchase, phone)
            closeNewPurchaseDialog()
        }
    }

    fun deletePurchase(purchase: PurchaseEntity) {
        viewModelScope.launch {
            repository.deletePurchase(purchase.id)
        }
    }

    // Expense
    fun openAddExpenseDialog() {
        _showAddExpenseDialog.value = true
    }

    fun closeAddExpenseDialog() {
        _showAddExpenseDialog.value = false
    }

    fun saveExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.addExpense(expense)
            closeAddExpenseDialog()
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpenseById(expense.id)
        }
    }

    // Return
    fun openProcessReturnDialog() {
        _showProcessReturnDialog.value = true
    }

    fun closeProcessReturnDialog() {
        _showProcessReturnDialog.value = false
    }

    fun processReturn(saleReturn: SaleReturnEntity, phoneId: Long, saleId: Long) {
        viewModelScope.launch {
            repository.processReturn(saleReturn, phoneId, saleId)
            closeProcessReturnDialog()
        }
    }

    // Removal Modals
    fun openRemoveProductDialog() {
        _showRemoveProductDialog.value = true
    }

    fun closeRemoveProductDialog() {
        _showRemoveProductDialog.value = false
    }

    fun openRemoveBillDialog() {
        _showRemoveBillDialog.value = true
    }

    fun closeRemoveBillDialog() {
        _showRemoveBillDialog.value = false
    }

    fun openRemoveExpenseDialog() {
        _showRemoveExpenseDialog.value = true
    }

    fun closeRemoveExpenseDialog() {
        _showRemoveExpenseDialog.value = false
    }

    // Camera Barcode Scanner
    fun openCameraScanner() {
        _showCameraScannerDialog.value = true
    }

    fun closeCameraScanner() {
        _showCameraScannerDialog.value = false
    }

    fun onBarcodeScanned(code: String) {
        _searchQuery.value = code
        closeCameraScanner()
    }
}
