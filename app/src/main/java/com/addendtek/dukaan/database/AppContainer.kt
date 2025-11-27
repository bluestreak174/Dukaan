package com.addendtek.dukaan.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.addendtek.dukaan.data.repositories.CategoryRepository
import com.addendtek.dukaan.data.repositories.OfflineCategoryRepository
import com.addendtek.dukaan.data.repositories.OfflineProductPurchaseRepository
import com.addendtek.dukaan.data.repositories.OfflineProductRepository
import com.addendtek.dukaan.data.repositories.OfflineProductSalesRepository
import com.addendtek.dukaan.data.repositories.OfflinePurchaseBillRepository
import com.addendtek.dukaan.data.repositories.OfflinePurchasesRepository
import com.addendtek.dukaan.data.repositories.OfflineQuantityTypeRepository
import com.addendtek.dukaan.data.repositories.OfflineSalesBillRepository
import com.addendtek.dukaan.data.repositories.OfflineSalesRepository
import com.addendtek.dukaan.data.repositories.ProductPurchaseRepository
import com.addendtek.dukaan.data.repositories.ProductRepository
import com.addendtek.dukaan.data.repositories.ProductSalesRepository
import com.addendtek.dukaan.data.repositories.PurchaseBillRepository
import com.addendtek.dukaan.data.repositories.PurchaseRepository
import com.addendtek.dukaan.data.repositories.QuantityTypeRepository
import com.addendtek.dukaan.data.repositories.SalesBillRepository
import com.addendtek.dukaan.data.repositories.SalesRepository
import com.addendtek.dukaan.data.repositories.UserPreferencesRepository

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
    val userPreferencesRepository: UserPreferencesRepository
}

private const val APP_PREFERENCE_NAME = "app_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = APP_PREFERENCE_NAME
)
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
    override val userPreferencesRepository: UserPreferencesRepository
        get() = UserPreferencesRepository(context.dataStore)
}