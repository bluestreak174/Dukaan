package com.bluestreak.dukaan.test

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.bluestreak.dukaan.DukaanApp
import com.bluestreak.dukaan.R
import com.bluestreak.dukaan.ui.home.HomeDestination
import com.bluestreak.dukaan.ui.product.CategoryEntryDestination
import com.bluestreak.dukaan.ui.product.ProductListDestination
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DukaanScreenNavigationTest {
    /**
     * Note: To access to an empty activity, the code uses ComponentActivity instead of
     * MainActivity.
     */
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var navController: TestNavHostController

    @Before
    fun setupDukaanNavHost() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            DukaanApp(navController = navController)
        }
    }

    @Test
    fun dukaanNavHost_verifyStartDestination() {
        navController.assertCurrentRouteName(HomeDestination.route)
    }

    @Test
    fun dukaanNavHost_verifyBackNavigationNotShownOnHomeScreen() {
        val backText = composeTestRule.activity.getString(R.string.back_button)
        composeTestRule.onNodeWithContentDescription(backText).assertDoesNotExist()
    }
    @Test
    fun dukaanNavHost_clickOnCategory_navigatesToProductListScreen() {
        val catText = composeTestRule.activity.getString(R.string.category_products)
        composeTestRule.onNodeWithTag(catText).performClick()
        navController.assertCurrentRouteName(ProductListDestination.route)
    }
    @Test
    fun dukaanNavHost_clickMenuCategory_navigatesToCategoryScreen() {
        val menuText = composeTestRule.activity.getString(R.string.more_options_test)
        composeTestRule.onNodeWithTag(menuText).performClick()
        val catText = composeTestRule.activity.getString(R.string.menu_category_type_test)
        composeTestRule.onNodeWithTag(catText).performClick()
        navController.assertCurrentRouteName(CategoryEntryDestination.route)
    }

}