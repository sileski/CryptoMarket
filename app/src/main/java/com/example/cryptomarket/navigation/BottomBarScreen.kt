package com.example.cryptomarket.navigation

import com.example.cryptomarket.R

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: Int
) {
    object Market : BottomBarScreen(
        route = "market",
        title = "Market",
        icon = R.drawable.ic_market
    )

    object Search : BottomBarScreen(
        route = "search",
        title = "Search",
        icon = R.drawable.ic_search
    )

    object Exchanges : BottomBarScreen(
        route = "exchanges",
        title = "Exchanges",
        icon = R.drawable.ic_exchange
    )
}
