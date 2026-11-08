package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "phones")
data class PhoneEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String = "iPhone", // "iPhone", "Android", or custom category
    val brand: String = "Apple",
    val model: String,
    val color: String = "Space Black",
    val imei: String = "", // Single IMEI field (IMEI 2 removed)
    val storage: String = "128 GB",
    val ram: String = "N/A",
    val batteryHealth: Int? = null, // 50% to 100% for iPhones; null for Android
    val ptaStatus: String = "PTA Approved", // PTA Approved, Non-PTA, JV, Bypass
    val condition: String = "10/10 Like New",
    val costPrice: Double = 0.0,
    val askingPrice: Double = 0.0,
    val status: String = "In Stock", // "In Stock", "Sold", "Returned" (auto-managed)
    val supplierName: String = "",
    val notes: String = "",
    val dateAdded: Long = System.currentTimeMillis()
)

@Entity(tableName = "product_categories")
data class ProductCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val iconName: String = "phone",
    val isDefault: Boolean = false
)

@Entity(tableName = "sales")
data class SaleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val billNumber: String,
    val phoneId: Long,
    val phoneSummary: String,
    val imei: String,
    val customerName: String,
    val customerPhone: String = "",
    val salePrice: Double,
    val downPayment: Double,
    val dueBalance: Double,
    val profit: Double,
    val paymentMethod: String = "Cash",
    val status: String = "Paid", // "Paid", "Partial", "Returned"
    val saleDate: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "sale_returns")
data class SaleReturnEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val saleId: Long,
    val phoneId: Long,
    val customerName: String,
    val phoneSummary: String,
    val refundAmount: Double,
    val reason: String,
    val returnDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "purchases")
data class PurchaseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val phoneId: Long? = null,
    val phoneSummary: String,
    val supplierName: String,
    val supplierPhone: String = "",
    val purchasePrice: Double,
    val paidAmount: Double,
    val dueBalance: Double,
    val paymentMethod: String = "Cash",
    val purchaseDate: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String, // Rent, Utilities, Salary, Refreshments, Parts, Misc
    val amount: Double,
    val paymentMethod: String = "Cash",
    val date: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "suppliers")
data class SupplierEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String = "",
    val totalPurchases: Double = 0.0,
    val totalPaid: Double = 0.0
)
