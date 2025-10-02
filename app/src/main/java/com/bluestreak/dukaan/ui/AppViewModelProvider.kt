package com.bluestreak.dukaan.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bluestreak.dukaan.DukaanApplication
import com.bluestreak.dukaan.ui.home.HomeViewModel
import com.bluestreak.dukaan.ui.viewmodel.CategoryEditViewModel
import com.bluestreak.dukaan.ui.viewmodel.CategoryEntryViewModel
import com.bluestreak.dukaan.ui.viewmodel.ProductDetailsViewModel
import com.bluestreak.dukaan.ui.viewmodel.ProductEditViewModel
import com.bluestreak.dukaan.ui.viewmodel.ProductEntryViewModel
import com.bluestreak.dukaan.ui.viewmodel.ProductHistoryViewModel
import com.bluestreak.dukaan.ui.viewmodel.ProductListViewModel
import com.bluestreak.dukaan.ui.viewmodel.PurchaseAndSalesViewModel
import com.bluestreak.dukaan.ui.viewmodel.PurchaseBillDetailsViewModel
import com.bluestreak.dukaan.ui.viewmodel.PurchaseBillViewModel
import com.bluestreak.dukaan.ui.viewmodel.PurchaseBillsListViewModel
import com.bluestreak.dukaan.ui.viewmodel.PurchaseListViewModel
import com.bluestreak.dukaan.ui.viewmodel.QuantityTypeEditViewModel
import com.bluestreak.dukaan.ui.viewmodel.QuantityTypeEntryViewModel
import com.bluestreak.dukaan.ui.viewmodel.SalesBillDetailsViewModel
import com.bluestreak.dukaan.ui.viewmodel.SalesBillViewModel
import com.bluestreak.dukaan.ui.viewmodel.SalesBillsListViewModel
import com.bluestreak.dukaan.ui.viewmodel.SalesListViewModel
import com.bluestreak.dukaan.ui.viewmodel.SummaryViewModel

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
                productPurchaseRepository = dukaanApplication().container.productPurchaseRepository
            )
        }
        initializer {
            SalesListViewModel(dukaanApplication().container.productSalesRepository)
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