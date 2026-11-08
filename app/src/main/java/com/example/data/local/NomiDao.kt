package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.ExpenseEntity
import com.example.data.model.PhoneEntity
import com.example.data.model.ProductCategoryEntity
import com.example.data.model.PurchaseEntity
import com.example.data.model.SaleEntity
import com.example.data.model.SaleReturnEntity
import com.example.data.model.SupplierEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NomiDao {

    // --- PHONES (INVENTORY) ---
    @Query("SELECT * FROM phones ORDER BY id DESC")
    fun getAllPhones(): Flow<List<PhoneEntity>>

    @Query("SELECT * FROM phones WHERE status = 'In Stock' ORDER BY id DESC")
    fun getInStockPhones(): Flow<List<PhoneEntity>>

    @Query("SELECT * FROM phones WHERE id = :id LIMIT 1")
    suspend fun getPhoneById(id: Long): PhoneEntity?

    @Query("SELECT * FROM phones WHERE imei = :imei AND status = 'In Stock' LIMIT 1")
    suspend fun getInStockPhoneByImei(imei: String): PhoneEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhone(phone: PhoneEntity): Long

    @Update
    suspend fun updatePhone(phone: PhoneEntity)

    @Delete
    suspend fun deletePhone(phone: PhoneEntity)

    @Query("DELETE FROM phones WHERE id = :id")
    suspend fun deletePhoneById(id: Long)

    @Query("UPDATE phones SET status = :status WHERE id = :id")
    suspend fun updatePhoneStatus(id: Long, status: String)

    // --- PRODUCT CATEGORIES ---
    @Query("SELECT * FROM product_categories ORDER BY id ASC")
    fun getAllCategories(): Flow<List<ProductCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: ProductCategoryEntity): Long

    @Delete
    suspend fun deleteCategory(category: ProductCategoryEntity)

    @Query("DELETE FROM product_categories WHERE id = :id")
    suspend fun deleteCategoryById(id: Long)

    @Query("SELECT COUNT(*) FROM product_categories")
    suspend fun getCategoryCount(): Int

    // --- SALES (BILLS) ---
    @Query("SELECT * FROM sales ORDER BY id DESC")
    fun getAllSales(): Flow<List<SaleEntity>>

    @Query("SELECT * FROM sales WHERE id = :id LIMIT 1")
    suspend fun getSaleById(id: Long): SaleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: SaleEntity): Long

    @Update
    suspend fun updateSale(sale: SaleEntity)

    @Delete
    suspend fun deleteSale(sale: SaleEntity)

    @Query("DELETE FROM sales WHERE id = :id")
    suspend fun deleteSaleById(id: Long)

    @Query("UPDATE sales SET status = :status WHERE id = :id")
    suspend fun updateSaleStatus(id: Long, status: String)

    // --- SALE RETURNS ---
    @Query("SELECT * FROM sale_returns ORDER BY id DESC")
    fun getAllReturns(): Flow<List<SaleReturnEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReturn(saleReturn: SaleReturnEntity): Long

    // --- PURCHASES ---
    @Query("SELECT * FROM purchases ORDER BY id DESC")
    fun getAllPurchases(): Flow<List<PurchaseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(purchase: PurchaseEntity): Long

    @Query("DELETE FROM purchases WHERE id = :id")
    suspend fun deletePurchaseById(id: Long)

    // --- EXPENSES ---
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpenseById(id: Long)

    // --- SUPPLIERS ---
    @Query("SELECT * FROM suppliers ORDER BY name ASC")
    fun getAllSuppliers(): Flow<List<SupplierEntity>>

    @Query("SELECT DISTINCT name FROM suppliers UNION SELECT DISTINCT supplierName FROM purchases WHERE supplierName != '' UNION SELECT DISTINCT supplierName FROM phones WHERE supplierName != ''")
    fun getDistinctSupplierNames(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplier(supplier: SupplierEntity): Long

    // --- ATOMIC TRANSACTIONS ---
    @Transaction
    suspend fun createSaleTransaction(sale: SaleEntity, phoneId: Long): Long {
        updatePhoneStatus(phoneId, "Sold")
        return insertSale(sale)
    }

    @Transaction
    suspend fun processReturnTransaction(saleReturn: SaleReturnEntity, phoneId: Long, saleId: Long): Long {
        updatePhoneStatus(phoneId, "In Stock")
        updateSaleStatus(saleId, "Returned")
        return insertReturn(saleReturn)
    }

    @Transaction
    suspend fun deleteSaleTransaction(saleId: Long, phoneId: Long) {
        if (phoneId > 0) {
            updatePhoneStatus(phoneId, "In Stock")
        }
        deleteSaleById(saleId)
    }

    @Transaction
    suspend fun createPurchaseWithPhone(purchase: PurchaseEntity, phone: PhoneEntity): Long {
        val phoneId = insertPhone(phone)
        val purchaseWithPhone = purchase.copy(phoneId = phoneId)
        return insertPurchase(purchaseWithPhone)
    }
}
