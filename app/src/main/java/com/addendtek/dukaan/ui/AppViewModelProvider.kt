package com.addendtek.dukaan.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.addendtek.dukaan.DukaanApplication
import com.addendtek.dukaan.ui.home.HomeViewModel
import com.addendtek.dukaan.ui.home.ProductSearchViewModel
import com.addendtek.dukaan.ui.viewmodel.AppSettingsViewModel
import com.addendtek.dukaan.ui.viewmodel.CategoryEditViewModel
import com.addendtek.dukaan.ui.viewmodel.CategoryEntryViewModel
import com.addendtek.dukaan.ui.viewmodel.ProductDetailsViewModel
import com.addendtek.dukaan.ui.viewmodel.ProductEditViewModel
import com.addendtek.dukaan.ui.viewmodel.ProductEntryViewModel
import com.addendtek.dukaan.ui.viewmodel.ProductHistoryViewModel
import com.addendtek.dukaan.ui.viewmodel.ProductListViewModel
import com.addendtek.dukaan.ui.viewmodel.PurchaseAndSalesViewModel
import com.addendtek.dukaan.ui.viewmodel.PurchaseBillDetailsViewModel
import com.addendtek.dukaan.ui.viewmodel.PurchaseBillViewModel
import com.addendtek.dukaan.ui.viewmodel.PurchaseBillsListViewModel
import com.addendtek.dukaan.ui.viewmodel.PurchaseListViewModel
import com.addendtek.dukaan.ui.viewmodel.QuantityTypeEditViewModel
import com.addendtek.dukaan.ui.viewmodel.QuantityTypeEntryViewModel
import com.addendtek.dukaan.ui.viewmodel.SalesBillDetailsViewModel
import com.addendtek.dukaan.ui.viewmodel.SalesBillViewModel
import com.addendtek.dukaan.ui.viewmodel.SalesBillsListViewModel
import com.addendtek.dukaan.ui.viewmodel.SalesListViewModel
import com.addendtek.dukaan.ui.viewmodel.SummaryViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Dukaan app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(
                categoryRepository = dukaanApplication().container.categoryRepository,
                purchaseBillRepository = dukaanApplication().container.purchaseBillRepository,
                salesBillRepository = dukaanApplication().container.salesBillRepository,
                productRepository = dukaanApplication().container.productRepository
            )
        }

        initializer {
            ProductSearchViewModel(
                productRepository = dukaanApplication().container.productRepository
            )
        }

        initializer {
            ProductListViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                productRepository = dukaanApplication().container.productRepository,
                categoryRepository = dukaanApplication().container.categoryRepository,

            )
        }
        initializer {
            ProductDetailsViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                productRepository = dukaanApplication().container.productRepository,
                salesRepository = dukaanApplication().container.salesRepository,
                purchaseRepository = dukaanApplication().container.purchaseRepository,
                quantityTypeRepository = dukaanApplication().container.quantityTypeRepository,
                salesBillRepository = dukaanApplication().container.salesBillRepository,
                purchaseBillRepository = dukaanApplication().container.purchaseBillRepository
                )
        }
        initializer {
            SalesBillsListViewModel(
                salesBillRepository = dukaanApplication().container.salesBillRepository,
                salesRepository = dukaanApplication().container.salesRepository,
                productRepository = dukaanApplication().container.productRepository,
                quantityTypeRepository = dukaanApplication().container.quantityTypeRepository
            )
        }

        initializer {
            PurchaseBillsListViewModel(
                purchaseBillRepository = dukaanApplication().container.purchaseBillRepository,
                purchaseRepository = dukaanApplication().container.purchaseRepository,
                productRepository = dukaanApplication().container.productRepository,
                quantityTypeRepository = dukaanApplication().container.quantityTypeRepository
            )
        }

        initializer {
            SummaryViewModel(
                purchaseBillRepository = dukaanApplication().container.purchaseBillRepository,
                salesBillRepository = dukaanApplication().container.salesBillRepository,
                productRepository = dukaanApplication().container.productRepository
            )
        }

        initializer {
            PurchaseListViewModel(
                productPurchaseRepository = dukaanApplication().container.productPurchaseRepository,
            )

        }
        initializer {
            ProductHistoryViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                productSalesRepository = dukaanApplication().container.productSalesRepository
            )
        }

        initializer {
            PurchaseAndSalesViewModel(
                productPurchaseRepository = dukaanApplication().container.productPurchaseRepository,
                purchaseRepository = dukaanApplication().container.purchaseRepository
            )
        }
        initializer {
            SalesListViewModel(dukaanApplication().container.productSalesRepository)
        }

        initializer {
            AppSettingsViewModel(
                userPreferencesRepository = dukaanApplication().container.userPreferencesRepository,
                categoryRepository = dukaanApplication().container.categoryRepository,
                quantityTypeRepository = dukaanApplication().container.quantityTypeRepository,
                productRepository = dukaanApplication().container.productRepository,
                purchaseRepository = dukaanApplication().container.purchaseRepository,
                salesRepository = dukaanApplication().container.salesRepository,
                purchaseBillRepository = dukaanApplication().container.purchaseBillRepository,
                salesBillRepository = dukaanApplication().container.salesBillRepository
            )
        }

        initializer {
            CategoryEntryViewModel(dukaanApplication().container.categoryRepository)
        }

        initializer {
            CategoryEditViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                dukaanApplication().container.categoryRepository
            )
        }

        initializer {
            QuantityTypeEditViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                dukaanApplication().container.quantityTypeRepository
            )
        }

        initializer {
            QuantityTypeEntryViewModel(dukaanApplication().container.quantityTypeRepository)
        }

        initializer {
            ProductEntryViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                productRepository = dukaanApplication().container.productRepository,
                purchasesRepository = dukaanApplication().container.purchaseRepository,
                quantityTypeRepository = dukaanApplication().container.quantityTypeRepository,
                purchaseBillRepository = dukaanApplication().container.purchaseBillRepository
            )
        }
        initializer {
            PurchaseBillViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                productRepository = dukaanApplication().container.productRepository,
                purchasesRepository = dukaanApplication().container.purchaseRepository,
                purchaseBillRepository = dukaanApplication().container.purchaseBillRepository
            )
        }

        initializer {
            SalesBillViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                productRepository = dukaanApplication().container.productRepository,
                salesRepository = dukaanApplication().container.salesRepository,
                salesBillRepository = dukaanApplication().container.salesBillRepository
            )
        }

        initializer {
            ProductEditViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                productRepository = dukaanApplication().container.productRepository,
                quantityTypeRepository = dukaanApplication().container.quantityTypeRepository
            )
        }

        initializer {
            PurchaseBillDetailsViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                purchaseBillRepository = dukaanApplication().container.purchaseBillRepository
            )
        }
        initializer {
            SalesBillDetailsViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                salesBillRepository = dukaanApplication().container.salesBillRepository
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [DukaanApplication].
 */
fun CreationExtras.dukaanApplication(): DukaanApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as DukaanApplication)