package com.addendtek.dukaan.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.addendtek.dukaan.data.dao.CategoryDao
import com.addendtek.dukaan.data.dao.ProductDao
import com.addendtek.dukaan.data.dao.ProductPurchasesDao
import com.addendtek.dukaan.data.dao.ProductSalesDao
import com.addendtek.dukaan.data.dao.PurchaseBillDao
import com.addendtek.dukaan.data.dao.PurchasesDao
import com.addendtek.dukaan.data.dao.QuantityTypeDao
import com.addendtek.dukaan.data.dao.SalesBillDao
import com.addendtek.dukaan.data.dao.SalesDao
import com.addendtek.dukaan.data.entities.Category
import com.addendtek.dukaan.data.entities.Product
import com.addendtek.dukaan.data.entities.PurchaseBill
import com.addendtek.dukaan.data.entities.Purchases
import com.addendtek.dukaan.data.entities.QuantityType
import com.addendtek.dukaan.data.entities.Sales
import com.addendtek.dukaan.data.entities.SalesBill
import com.addendtek.dukaan.data.relations.Converters


@Database(
    entities = [Category::class, QuantityType::class, Product::class, Purchases::class, Sales::class, PurchaseBill::class, SalesBill::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class DukaanDatabase :  RoomDatabase(){
    abstract fun categoryDao() : CategoryDao
    abstract fun quantityTypeDao() : QuantityTypeDao
    abstract fun productDao() : ProductDao
    abstract fun purchasesDao() : PurchasesDao
    abstract fun salesDao() : SalesDao
    abstract fun productPurchasesDao() : ProductPurchasesDao
    abstract fun productSalesDao() : ProductSalesDao
    abstract fun purchaseBillDao() : PurchaseBillDao
    abstract fun salesBillDao() : SalesBillDao


    companion object {
        @Volatile
        private var Instance: DukaanDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE purchase_bill ADD COLUMN billAddress TEXT DEFAULT '' NOT NULL ")
                db.execSQL("ALTER TABLE sales_bill ADD COLUMN billAddress TEXT DEFAULT '' NOT NULL")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE product_master ADD COLUMN barCode INTEGER DEFAULT '' NOT NULL ")
            }
        }

        fun getDatabase(context: Context): DukaanDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, DukaanDatabase::class.java, "dukaan_database")
                    //.fallbackToDestructiveMigration()
                    .createFromAsset("database/dukaan_database_data.db")
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}