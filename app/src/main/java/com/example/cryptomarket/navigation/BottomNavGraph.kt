package com.example.cryptomarket.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cryptomarket.presentation.cryptocurrency_details.CryptocurrencyDetailsScreen
import com.example.cryptomarket.presentation.cryptocurrency_search.CryptoCurrencySearchScreen
import com.example.cryptomarket.presentation.exchanges.ExchangesScreen
import com.example.cryptomarket.presentation.market.MarketScreen
import com.example.cryptomarket.util.CHANGES_IN_WATCHLIST

@Composable
fun BottomNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = BottomBarScreen.Market.route) {
        composable(route = BottomBarScreen.Market.route) {
            val changesInWatchlist =
                navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
                    CHANGES_IN_WATCHLIST
                )?.value
            MarketScreen(
                changesInWatchlist = changesInWatchlist
            ) { coinId, coinName, coinSymbol ->
                navController.navigate(
                    DetailsScreen.Overview.passArguments(
                        coinId = coinId,
                        coinName = coinName,
                        coinSymbol = coinSymbol
                    )
                )
            }
        }
        composable(route = BottomBarScreen.Search.route) {
            CryptoCurrencySearchScreen { coinId, coinName, coinSymbol ->
                navController.navigate(
                    DetailsScreen.Overview.passArguments(
                        coinId = coinId,
                        coinName = coinName,
                        coinSymbol = coinSymbol
                    )
                )
            }
        }
        composable(route = BottomBarScreen.Exchanges.route) {
            ExchangesScreen()
        }
        detailsNavGraph(navController)
    }
}

fun NavGraphBuilder.detailsNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.DETAILS,
        startDestination = DetailsScreen.Overview.route
    ) {
        composable(
            route = DetailsScreen.Overview.route,
            arguments = listOf(
                navArgument("$DETAILS_ID_ARGUMENT_KEY") {
                    type = NavType.StringType
                },
                navArgument("$DETAILS_NAME_ARGUMENT_KEY") {
                    type = NavType.StringType
                },
                navArgument("$DETAILS_SYMBOL_ARGUMENT_KEY") {
                    type = NavType.StringType
                },
            )
        ) {
            CryptocurrencyDetailsScreen(
                coinId = it.arguments?.getString("$DETAILS_ID_ARGUMENT_KEY").toString(),
                coinName = it.arguments?.getString("$DETAILS_NAME_ARGUMENT_KEY").toString(),
                coinSymbol = it.arguments?.getString("$DETAILS_SYMBOL_ARGUMENT_KEY").toString(),
            ) { changesInWatchlist ->
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    CHANGES_IN_WATCHLIST,
                    changesInWatchlist
                )
                navController.popBackStack()
            }
        }
    }
}

