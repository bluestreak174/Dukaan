package com.bluestreak.dukaan.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bluestreak.dukaan.DukaanTopAppBar
import com.bluestreak.dukaan.R
import com.bluestreak.dukaan.data.entities.Category


/**
 * Entry route for Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TestScreen(
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
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    //Buy/Sell : Cash/UPI
    //1000/2000 : 1000/2000
    val billsTotal  = stringResource(
        R.string.buy_sell,
        9876543210,
        1234567890
    ) +
            stringResource(
                R.string.cash_upi,
                1234567890,
                9876543210
            )
val catList = List(14, {Category(it,"a")})


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
                //title = stringResource(HomeDestination.titleRes),
                title = billsTotal,
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        },

        ) { innerPadding ->
        HomeBodyTest(
            catList = catList,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding,
        )
    }

}

@Composable
fun HomeBodyTest(
    catList: List<Category>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    ProductGroupTest(
        catList = catList,
        modifier = modifier,
        contentPadding = contentPadding
    )
}

@Composable
fun ProductGroupTest(
    catList: List<Category>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
){
    Column(modifier = Modifier.padding(top = 10.dp)) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(140.dp),
            modifier = modifier.padding(horizontal = 4.dp),
            contentPadding = contentPadding,
        ) {
            items(items = catList, key = { it.id }) { item ->
                GroupCardTest(
                    category = item,
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                        .aspectRatio(1.5f)
                        .clickable { }
                        .testTag(stringResource(R.string.category_products))
                )
            }
        }
    }
}



@Composable
fun GroupCardTest(
    category: Category,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardColors(
            containerColor = Color.LightGray,
            contentColor = Color.Black,
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.Black,
        )
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
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
                    imageVector = Filled.ShoppingCart,
                    contentDescription = stringResource(R.string.category_products),
                    modifier = Modifier
                        .height(70.dp)
                        .width(70.dp)
                        .padding(16.dp)
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
