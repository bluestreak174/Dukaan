package com.addendtek.dukaan.ui.home

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.addendtek.dukaan.DukaanTopAppBar
import com.addendtek.dukaan.R
import com.addendtek.dukaan.data.entities.Category
import com.addendtek.dukaan.ui.AppViewModelProvider
import com.addendtek.dukaan.ui.navigation.NavigationDestination
import com.addendtek.dukaan.ui.utils.HelpShowCase
import com.addendtek.dukaan.ui.utils.ShowcaseProperty
import com.addendtek.dukaan.ui.viewmodel.AppSettingsViewModel

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

/**
 * Entry route for Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateToCategoryList: () -> Unit = {},
    navigateToQuantityTypeList: () -> Unit = {},
    navigateToItemList: (Int) -> Unit = {},
    navigateToImageEntry: () -> Unit = {},
    navigateToPurchaseList: () -> Unit = {},
    navigateToSalesList: () -> Unit = {},
    navigateToPurchaseBill: () -> Unit = {},
    navigateToSalesBill: () -> Unit = {},
    navigateToSummary: () -> Unit = {},
    navigateToBillsPurchase: () -> Unit = {},
    navigateToBillsSales: () -> Unit = {},
    navigateToPurchaseSales: () -> Unit = {},
    navigateToSettings: () -> Unit = {},
    navToProductSearch: () -> Unit = {},
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    appSettingsViewModel: AppSettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val homeUiState by  viewModel.homeUiState.collectAsState()
    val stockValueUiState by viewModel.stockValueUiState.collectAsState()
    val pBillUiState by viewModel.purchasesUiState.collectAsStateWithLifecycle()
    val sBillUiState by viewModel.salesUiState.collectAsStateWithLifecycle()
    val appNamePreferencesState by appSettingsViewModel.appNamePrefState.collectAsState()

    val appName = appNamePreferencesState.appName.takeIf { it.isNotBlank() } ?: stringResource(
        R.string.app_name,
    )

    val appHelpPrefState by appSettingsViewModel.appHelpPrefState.collectAsStateWithLifecycle()

    val monthSummary = stringResource(R.string.home_summary_tooltip) + stringResource(
        R.string.home_month_buy,
        pBillUiState.totalBill.total,
        pBillUiState.totalBill.cash,
        pBillUiState.totalBill.upi
    ) + "\n" +
            stringResource(
                R.string.home_month_sell,
                sBillUiState.totalBill.total,
                sBillUiState.totalBill.cash,
                sBillUiState.totalBill.upi

            ) + "\n" +
            stringResource(R.string.stock,stockValueUiState)
    val targets = remember {
        mutableStateMapOf<String, ShowcaseProperty>()
    }

    var showMenuHelpIntro by remember {
        mutableStateOf(false)
    }

    var showHomeHelp by remember {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            DukaanTopAppBar(
                onCategoryClick = navigateToCategoryList,
                onQuantityTypeClick = navigateToQuantityTypeList,
                onImageEntryClick = navigateToImageEntry,
                onPurchaseListClick = navigateToPurchaseList,
                onSalesListClick = navigateToSalesList,
                onPurchaseBillClick = navigateToPurchaseBill,
                onSalesBillClick = navigateToSalesBill,
                onSummaryClick = navigateToSummary,
                onPbillsClick = navigateToBillsPurchase,
                onSbillsClick = navigateToBillsSales,
                onPurchaseSalesClick = navigateToPurchaseSales,
                onSettingsClick = navigateToSettings,
                onSearchClick = navToProductSearch,
                //title = stringResource(HomeDestination.titleRes),
                title = appName,
                titleSummary = monthSummary,
                canNavigateBack = false,
                scrollBehavior = scrollBehavior,
                modifier = Modifier,
                updateTargets = { targets.putAll(it) }
            )
            if(showMenuHelpIntro){
                val uniqueTargets = targets.values.sortedBy { it.index }
                var currentTargetIndex by remember { mutableStateOf(0) }

                val currentTarget =
                    if (uniqueTargets.isNotEmpty() && currentTargetIndex < uniqueTargets.size) uniqueTargets[currentTargetIndex] else null

                currentTarget?.let {
                    HelpShowCase(
                        target = it,
                        dismissOnClickOutside = true,
                        ) {
                        if (++currentTargetIndex >= uniqueTargets.size) {
                            showMenuHelpIntro = false
                        }
                    }
                }
            }


        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Gray),
                contentAlignment = Alignment.Center
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    AppVersionDisplay()

                    /*
                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                    }
                    Column (
                        modifier = Modifier.weight(0.2f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CloseAppButton()
                    }*/
                }
            }
        }

    ) { innerPadding ->

        HomeBody(
            catList = homeUiState.categoryList.sortedBy { it.name },
            //catList = catList,
            onGroupClick = navigateToItemList,
            showHomeHelp = appHelpPrefState.isAppHelpEnabled,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding,
            updateShowMenuHelp = { showMenuHelpIntro = it },
            updateShowHomeHelp = { showHomeHelp = it },
        )

    }

}

@Composable
fun CloseAppButton(
    modifier: Modifier = Modifier
) {
    val activity = (LocalContext.current as? Activity)
    IconButton(
        onClick = {
            activity?.finish() // This finishes the current activity, effectively closing the app if it's the only one
        },
        modifier = Modifier
            .padding(0.dp)
            .width(24.dp)
            .height(24.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.settings_power),
            contentDescription = stringResource(R.string.close_app),
            tint = Color.Red
        )
    }

}

fun getAppVersion(context: Context): String? {
    try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return packageInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return "N/A" // Fallback in case of error
}
@Composable
fun AppVersionDisplay(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appVersion = getAppVersion(context)

    Text(
        text = "Dukaan Version: $appVersion",
        color = Color.DarkGray,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun HomeBody(
    catList: List<Category>,
    onGroupClick: (Int) -> Unit,
    showHomeHelp: Boolean,
    updateShowHomeHelp: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    updateShowMenuHelp: (Boolean) -> Unit,
) {
    val targets = remember {
        mutableStateMapOf<Int, ShowcaseProperty>()
    }
    ProductGroup(
        catList = catList,
        onGroupClick = { onGroupClick(it.id) },
        targets = targets,
        modifier = modifier,
        contentPadding = contentPadding
    )

    if(showHomeHelp){
        val uniqueTargets = targets.values.sortedBy { it.index }

        if(uniqueTargets.isNotEmpty() ){
            HelpShowCase(
                target = uniqueTargets[0],
                dismissOnClickOutside = true,
                onShowCaseCompleted = {
                    updateShowHomeHelp(false)
                    updateShowMenuHelp(true)
                }
            )
        }
    }


}

@Composable
fun ProductGroup(
    catList: List<Category>,
    onGroupClick: (Category) -> Unit,
    targets: SnapshotStateMap<Int, ShowcaseProperty>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
){
    val catHelpTitle = stringResource(R.string.product_list_title)
    val catHelpSubTitle = stringResource(R.string.help_list_of_products)

    Column(modifier = Modifier.padding(top = 10.dp)) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(140.dp),
            modifier = modifier.padding(horizontal = 4.dp),
            contentPadding = contentPadding,
        ) {
            itemsIndexed(items = catList) { index, item ->
                GroupCard(
                    category = item,
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                        .aspectRatio(1.5f)
                        .clickable { onGroupClick(item) }
                        .testTag(stringResource(R.string.category_products))
                        .onGloballyPositioned { coordinates ->
                            if ( coordinates.isAttached) {
                                targets[item.id] = ShowcaseProperty(
                                    index = 1,
                                    coordinates = coordinates,
                                    title = catHelpTitle,
                                    subTitle = catHelpSubTitle
                                )
                            }

                        }

                )
            }
        }
    }







}



@Composable
fun GroupCard(
    category: Category,
    modifier: Modifier = Modifier,
) {

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            val iconColor = Color(0xFF2AC38C)
            val resourceId: Int
            when(category.id) {
               1 -> resourceId = R.drawable.ic_cookie
               2 -> resourceId = R.drawable.ic_chips
               3 -> resourceId = R.drawable.ic_candy
               4 -> resourceId = R.drawable.ic_book_pen
               5 -> resourceId = R.drawable.ic_cigar
               6 -> resourceId = R.drawable.ic_egg
               7 -> resourceId = R.drawable.ic_soap
               8 -> resourceId = R.drawable.ic_toothpaste
               9 -> resourceId = R.drawable.ic_facecream
               10 -> resourceId = R.drawable.ic_cooking
               11 -> resourceId = R.drawable.ic_food_bowl
               12 -> resourceId = R.drawable.ic_coffee
               13 -> resourceId = R.drawable.ic_temple
               14 -> resourceId = R.drawable.ic_drinks
               else -> resourceId = 0
           }
            if(resourceId != 0) {
                Image(
                    painter = painterResource(resourceId),
                    contentDescription = stringResource(R.string.category_products),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(70.dp)
                        .width(70.dp)
                        //.padding(4.dp)
                )
            } else {
                Icon(
                    imageVector = Filled.LocalMall ,
                    contentDescription = stringResource(R.string.category_products),
                    modifier = Modifier
                        .height(70.dp)
                        .width(70.dp)
                        .padding(16.dp),
                    tint = iconColor
                )

            }
            Text(
                text = "${category.name} ",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(4.dp)
                    .weight(1f)

            )

        }
    }
}



