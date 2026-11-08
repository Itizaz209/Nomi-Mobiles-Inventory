package com.example.data.repository

import com.example.data.local.NomiDao
import com.example.data.model.ExpenseEntity
import com.example.data.model.PhoneEntity
import com.example.data.model.ProductCategoryEntity
import com.example.data.model.PurchaseEntity
import com.example.data.model.SaleEntity
import com.example.data.model.SaleReturnEntity
import com.example.data.model.SupplierEntity
import kotlinx.coroutines.flow.Flow

class NomiRepository(private val dao: NomiDao) {

    // Phones
    val allPhones: Flow<List<PhoneEntity>> = dao.getAllPhones()
    val inStockPhones: Flow<List<PhoneEntity>> = dao.getInStockPhones()

    suspend fun getPhoneById(id: Long): PhoneEntity? = dao.getPhoneById(id)
    suspend fun getInStockPhoneByImei(imei: String): PhoneEntity? = dao.getInStockPhoneByImei(imei.trim())
    suspend fun addPhone(phone: PhoneEntity): Long = dao.insertPhone(phone)
    suspend fun updatePhone(phone: PhoneEntity) = dao.updatePhone(phone)
    suspend fun deletePhoneById(id: Long) = dao.deletePhoneById(id)

    // Categories
    val categories: Flow<List<ProductCategoryEntity>> = dao.getAllCategories()
    suspend fun addCategory(name: String, iconName: String = "category"): Long {
        return dao.insertCategory(
            ProductCategoryEntity(
                name = name.trim(),
                iconName = iconName,
                isDefault = false
            )
        )
    }
    suspend fun deleteCategory(category: ProductCategoryEntity) = dao.deleteCategory(category)
    suspend fun deleteCategoryById(id: Long) = dao.deleteCategoryById(id)

    // Sales / Bills
    val allSales: Flow<List<SaleEntity>> = dao.getAllSales()
    suspend fun createSale(sale: SaleEntity, phoneId: Long): Long {
        return dao.createSaleTransaction(sale, phoneId)
    }
    suspend fun deleteSale(saleId: Long, phoneId: Long) {
        dao.deleteSaleTransaction(saleId, phoneId)
    }

    // Returns
    val allReturns: Flow<List<SaleReturnEntity>> = dao.getAllReturns()
    suspend fun processReturn(saleReturn: SaleReturnEntity, phoneId: Long, saleId: Long): Long {
        return dao.processReturnTransaction(saleReturn, phoneId, saleId)
    }

    // Purchases
    val allPurchases: Flow<List<PurchaseEntity>> = dao.getAllPurchases()
    suspend fun recordPurchaseWithPhone(purchase: PurchaseEntity, phone: PhoneEntity): Long {
        return dao.createPurchaseWithPhone(purchase, phone)
    }
    suspend fun deletePurchase(id: Long) = dao.deletePurchaseById(id)

    // Expenses
    val allExpenses: Flow<List<ExpenseEntity>> = dao.getAllExpenses()
    suspend fun addExpense(expense: ExpenseEntity): Long = dao.insertExpense(expense)
    suspend fun deleteExpenseById(id: Long) = dao.deleteExpenseById(id)

    // Suppliers
    val allSuppliers: Flow<List<SupplierEntity>> = dao.getAllSuppliers()
    val supplierNames: Flow<List<String>> = dao.getDistinctSupplierNames()
    suspend fun addSupplier(name: String, phone: String = "") {
        if (name.isNotBlank()) {
            dao.insertSupplier(SupplierEntity(name = name.trim(), phone = phone.trim()))
        }
    }
}
