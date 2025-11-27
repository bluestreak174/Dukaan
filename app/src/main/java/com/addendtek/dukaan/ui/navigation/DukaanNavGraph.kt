package com.addendtek.dukaan.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.addendtek.dukaan.ui.home.AppSettingsDestination
import com.addendtek.dukaan.ui.home.AppSettingsScreen
import com.addendtek.dukaan.ui.home.HomeDestination
import com.addendtek.dukaan.ui.home.HomeScreen
import com.addendtek.dukaan.ui.home.ImageEntryDestination
import com.addendtek.dukaan.ui.home.ImageFileScreen
import com.addendtek.dukaan.ui.product.CategoryEditDestination
import com.addendtek.dukaan.ui.product.CategoryEditScreen
import com.addendtek.dukaan.ui.product.CategoryEntryDestination
import com.addendtek.dukaan.ui.product.CategoryEntryScreen
import com.addendtek.dukaan.ui.product.CategoryListDestination
import com.addendtek.dukaan.ui.product.CategoryListScreen
import com.addendtek.dukaan.ui.product.ProductDetailsDestination
import com.addendtek.dukaan.ui.product.ProductDetailsScreen
import com.addendtek.dukaan.ui.product.ProductEditDestination
import com.addendtek.dukaan.ui.product.ProductEditScreen
import com.addendtek.dukaan.ui.product.ProductEntryDestination
import com.addendtek.dukaan.ui.product.ProductEntryScreen
import com.addendtek.dukaan.ui.product.ProductHistoryDestination
import com.addendtek.dukaan.ui.product.ProductHistoryScreen
import com.addendtek.dukaan.ui.product.ProductListDestination
import com.addendtek.dukaan.ui.product.ProductListScreen
import com.addendtek.dukaan.ui.product.PurchaseBillDestination
import com.addendtek.dukaan.ui.product.PurchaseBillDetailsDestination
import com.addendtek.dukaan.ui.product.PurchaseBillDetailsScreen
import com.addendtek.dukaan.ui.product.PurchaseBillScreen
import com.addendtek.dukaan.ui.product.PurchaseBillsList
import com.addendtek.dukaan.ui.product.PurchaseBillsListDestination
import com.addendtek.dukaan.ui.product.PurchaseListDestination
import com.addendtek.dukaan.ui.product.PurchaseListScreen
import com.addendtek.dukaan.ui.product.PurchaseSaleScreen
import com.addendtek.dukaan.ui.product.PurchaseSaleScreenDestination
import com.addendtek.dukaan.ui.product.QuantityTypeEditDestination
import com.addendtek.dukaan.ui.product.QuantityTypeEditScreen
import com.addendtek.dukaan.ui.product.QuantityTypeEntryDestination
import com.addendtek.dukaan.ui.product.QuantityTypeEntryScreen
import com.addendtek.dukaan.ui.product.QuantityTypeListScreen
import com.addendtek.dukaan.ui.product.QuantityTypeListScreenDestination
import com.addendtek.dukaan.ui.product.SalesBillDestination
import com.addendtek.dukaan.ui.product.SalesBillDetailsDestination
import com.addendtek.dukaan.ui.product.SalesBillDetailsScreen
import com.addendtek.dukaan.ui.product.SalesBillScreen
import com.addendtek.dukaan.ui.product.SalesBillsList
import com.addendtek.dukaan.ui.product.SalesBillsListDestination
import com.addendtek.dukaan.ui.product.SalesListDestination
import com.addendtek.dukaan.ui.product.SalesListScreen
import com.addendtek.dukaan.ui.product.SummaryScreen
import com.addendtek.dukaan.ui.product.SummaryScreenDestination

/**
 * Provides Navigation graph for the application.
 */
@Composable
fun DukaanNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToCategoryList = { navController.navigate(CategoryListDestination.route) },
                navigateToQuantityTypeList = { navController.navigate(QuantityTypeListScreenDestination.route) },
                navigateToItemList = {
                    navController.navigate("${ProductListDestination.route}/${it}")
                },
                navigateToImageEntry = { navController.navigate(ImageEntryDestination.route)},
                navigateToPurchaseList = { navController.navigate(PurchaseListDestination.route)},
                navigateToSalesList = { navController.navigate(SalesListDestination.route)},
                navigateToPurchaseBill = { navController.navigate(PurchaseBillDestination.route)},
                navigateToSalesBill = { navController.navigate(SalesBillDestination.route )},
                navigateToSummary = { navController.navigate(SummaryScreenDestination.route)},
                navigateToBillsPurchase = { navController.navigate(PurchaseBillsListDestination.route)},
                navigateToBillsSales = { navController.navigate(SalesBillsListDestination.route)},
                navigateToPurchaseSales = { navController.navigate(PurchaseSaleScreenDestination.route)},
                navigateToSettings = {navController.navigate(AppSettingsDestination.route)}
            )
        }
        composable(route = CategoryListDestination.route) {
            CategoryListScreen(
                navigateToCategoryEntry = { navController.navigate(CategoryEntryDestination.route)},
                navigateToCategoryEdit = { navController.navigate("${CategoryEditDestination.route}/${it}")},
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
            )
        }
        composable(route = QuantityTypeListScreenDestination.route) {
            QuantityTypeListScreen(
                navigateToQuantityTypeEntry =  { navController.navigate(QuantityTypeEntryDestination.route)},
                navigateToQuantityTypeEdit = { navController.navigate("${QuantityTypeEditDestination.route}/$it")  },
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
            )
        }

        composable(route = SalesBillsListDestination.route){
            SalesBillsList(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                navigateToBillDetails = {
                    navController.navigate("${SalesBillDetailsDestination.route}/${it}")
                }
            )
        }
        composable(route = PurchaseBillsListDestination.route){
            PurchaseBillsList(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                navigateToBillDetails = {
                    navController.navigate("${PurchaseBillDetailsDestination.route}/${it}")
                }
            )
        }
        composable(route = SummaryScreenDestination.route){
            SummaryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
            )
        }
        composable(route = PurchaseSaleScreenDestination.route){
            PurchaseSaleScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
            )
        }
        composable(
            route = PurchaseBillDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(PurchaseBillDetailsDestination.billIdArg) {
                type = NavType.IntType
            })
        ) {
            PurchaseBillDetailsScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
            )
        }
        composable(
            route = SalesBillDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(SalesBillDetailsDestination.billIdArg) {
                type = NavType.IntType
            })
        ) {
            SalesBillDetailsScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
            )
        }
        composable(
            route = ProductListDestination.routeWithArgs,
            arguments = listOf(navArgument(ProductListDestination.catIdArg) {
                type = NavType.IntType
            })
        ) {
            ProductListScreen(
                navigateToProductEntry = {
                    navController.navigate("${ProductEntryDestination.route}/${it}")
                                         },
                navigateToProductUpdate = {
                    navController.navigate("${ProductDetailsDestination.route}/${it}")
                                          },
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
            )
        }

        composable(
            route = ProductEntryDestination.routeWithArgs,
            arguments = listOf(navArgument(ProductEntryDestination.catIdArg) {
                type = NavType.IntType
            })
        ) {
            ProductEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = ProductDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(ProductDetailsDestination.productIdArg) {
                type = NavType.IntType
            })
        ) {
            ProductDetailsScreen(
                navigateToEditProduct = { navController.navigate("${ProductEditDestination.route}/$it")  },
                navigateToProductHistory = { navController.navigate("${ProductHistoryDestination.route}/$it")},
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
            )
        }

        composable(
            route = ProductHistoryDestination.routeWithArgs,
            arguments = listOf(navArgument(ProductHistoryDestination.productIdArg) {
                type = NavType.IntType
            })
        ) {
            ProductHistoryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                navigateToBillDetails = { navController.navigate("${PurchaseBillDetailsDestination.route}/$it") },
                navigateToSalesBillDetails = { navController.navigate("${SalesBillDetailsDestination.route}/$it") }
            )
        }

        composable(
          route = ProductEditDestination.routeWithArgs,
            arguments = listOf(navArgument(ProductEditDestination.productIdArg) {
                type = NavType.IntType
            })
        ) {
            ProductEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
            )
        }

        composable(
            route = CategoryEditDestination.routeWithArgs,
            arguments = listOf(navArgument(CategoryEditDestination.catIdArg){
              type = NavType.IntType
            })
        ) {
            CategoryEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = QuantityTypeEditDestination.routeWithArgs,
            arguments = listOf(navArgument(QuantityTypeEditDestination.qtyTypeIdArg){
                type = NavType.IntType
            })
        ){
            QuantityTypeEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = CategoryEntryDestination.route,
        ) {
            CategoryEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = QuantityTypeEntryDestination.route,
        ) {
            QuantityTypeEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = ImageEntryDestination.route,
        ) {
            ImageFileScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = AppSettingsDestination.route,
        ) {
            AppSettingsScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = PurchaseListDestination.route,
        ) {
            PurchaseListScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
            )
        }

        composable(
            route = SalesListDestination.route,
        ) {
            SalesListScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
            )
        }

        composable(
            route = PurchaseBillDestination.route,
        ) {
            PurchaseBillScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },

            )
        }

        composable(
            route = SalesBillDestination.route,
        ) {
            SalesBillScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

    }
}