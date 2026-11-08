package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.ExpenseEntity
import com.example.data.model.PhoneEntity
import com.example.data.model.ProductCategoryEntity
import com.example.data.model.PurchaseEntity
import com.example.data.model.SaleEntity
import com.example.data.model.SaleReturnEntity
import com.example.data.model.SupplierEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        PhoneEntity::class,
        ProductCategoryEntity::class,
        SaleEntity::class,
        SaleReturnEntity::class,
        PurchaseEntity::class,
        ExpenseEntity::class,
        SupplierEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NomiDatabase : RoomDatabase() {

    abstract fun nomiDao(): NomiDao

    companion object {
        @Volatile
        private var INSTANCE: NomiDatabase? = null

        fun getDatabase(context: Context): NomiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NomiDatabase::class.java,
                    "nomi_mobiles_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getDatabase(context).nomiDao()
                                seedDefaultCategories(dao)
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun seedDefaultCategories(dao: NomiDao) {
            if (dao.getCategoryCount() == 0) {
                val defaults = listOf(
                    ProductCategoryEntity(name = "iPhone", iconName = "apple", isDefault = true),
                    ProductCategoryEntity(name = "Android", iconName = "android", isDefault = true),
                    ProductCategoryEntity(name = "Accessories", iconName = "headphones", isDefault = true),
                    ProductCategoryEntity(name = "Tablets", iconName = "tablet", isDefault = true),
                    ProductCategoryEntity(name = "Smart Watches", iconName = "watch", isDefault = true)
                )
                defaults.forEach { dao.insertCategory(it) }
            }
        }
    }
}
