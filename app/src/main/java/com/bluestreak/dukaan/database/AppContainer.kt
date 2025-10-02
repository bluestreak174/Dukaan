package com.bluestreak.dukaan.database

import android.content.Context
import com.bluestreak.dukaan.data.repositories.CategoryRepository
import com.bluestreak.dukaan.data.repositories.OfflineCategoryRepository
import com.bluestreak.dukaan.data.repositories.OfflineProductPurchaseRepository
import com.bluestreak.dukaan.data.repositories.OfflineProductRepository
import com.bluestreak.dukaan.data.repositories.OfflineProductSalesRepository
import com.bluestreak.dukaan.data.repositories.OfflinePurchaseBillRepository
import com.bluestreak.dukaan.data.repositories.OfflinePurchasesRepository
import com.bluestreak.dukaan.data.repositories.OfflineQuantityTypeRepository
import com.bluestreak.dukaan.data.repositories.OfflineSalesBillRepository
import com.bluestreak.dukaan.data.repositories.OfflineSalesRepository
import com.bluestreak.dukaan.data.repositories.ProductPurchaseRepository
import com.bluestreak.dukaan.data.repositories.ProductRepository
import com.bluestreak.dukaan.data.repositories.ProductSalesRepository
import com.bluestreak.dukaan.data.repositories.PurchaseBillRepository
import com.bluestreak.dukaan.data.repositories.PurchaseRepository
import com.bluestreak.dukaan.data.repositories.QuantityTypeRepository
import com.bluestreak.dukaan.data.repositories.SalesBillRepository
import com.bluestreak.dukaan.data.repositories.SalesRepository

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val categoryRepository: CategoryRepository
    val quantityTypeRepository: QuantityTypeRepository
    val productRepository: ProductRepository
    val purchaseRepository: PurchaseRepository
    val salesRepository: SalesRepository
    val productPurchaseRepository: ProductPurchaseRepository
    val productSalesRepository: ProductSalesRepository
    val purchaseBillRepository: PurchaseBillRepository
    val salesBillRepository: SalesBillRepository
}

/**
 * [AppContainer] implementation that provides instance sof [OfflineRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    override val categoryRepository: CategoryRepository by lazy {
        OfflineCategoryRepository(DukaanDatabase.getDatabase(context).categoryDao())
    }
    override val quantityTypeRepository: QuantityTypeRepository by lazy {
        OfflineQuantityTypeRepository(DukaanDatabase.getDatabase(context).quantityTypeDao())
    }
    override val productRepository: ProductRepository by lazy {
        OfflineProductRepository(DukaanDatabase.getDatabase(context).productDao())
    }
    override val purchaseRepository: PurchaseRepository by lazy {
        OfflinePurchasesRepository(DukaanDatabase.getDatabase(context).purchasesDao())
    }
    override val salesRepository: SalesRepository by lazy {
        OfflineSalesRepository(DukaanDatabase.getDatabase(context).salesDao())
    }
    override val productPurchaseRepository: ProductPurchaseRepository by lazy {
        OfflineProductPurchaseRepository(DukaanDatabase.getDatabase(context).productPurchasesDao())
    }
    override val productSalesRepository: ProductSalesRepository by lazy {
        OfflineProductSalesRepository(DukaanDatabase.getDatabase(context).productSalesDao())
    }
    override val purchaseBillRepository: PurchaseBillRepository by lazy {
        OfflinePurchaseBillRepository(DukaanDatabase.getDatabase(context).purchaseBillDao())
    }
    override val salesBillRepository: SalesBillRepository by lazy {
        OfflineSalesBillRepository(DukaanDatabase.getDatabase(context).salesBillDao())
    }

}