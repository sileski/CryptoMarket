package com.example.cryptomarket.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cryptomarket.presentation.common.StatusBarColor

@ExperimentalMaterial3Api
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    when (navBackStackEntry?.destination?.route) {
        BottomBarScreen.Market.route -> {
            BottomNavigationBarVisibility.showBottomNavigationBar.value = true
        }
        BottomBarScreen.Search.route -> {
            BottomNavigationBarVisibility.showBottomNavigationBar.value = true
        }
        BottomBarScreen.Exchanges.route -> {
            BottomNavigationBarVisibility.showBottomNavigationBar.value = true
        }
        DetailsScreen.Overview.route -> {
            BottomNavigationBarVisibility.showBottomNavigationBar.value = false
        }
    }

    StatusBarColor()
    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                BottomNavigationBarVisibility.showBottomNavigationBar.value,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = 800)
                ),
                exit = ExitTransition.None
            ) {
                BottomBar(
                    navController = navController,
                    currentDestination = currentDestination
                )
            }
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            BottomNavGraph(navController = navController)
        }
    }
}

@Composable
fun BottomBar(
    navController: NavHostController,
    currentDestination: NavDestination?
) {
    val screens = listOf(
        BottomBarScreen.Market,
        BottomBarScreen.Search,
        BottomBarScreen.Exchanges
    )

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        screens.forEach { screen ->
            AddItem(
                screen = screen,
                currentDestination = currentDestination,
                navController = navController
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    NavigationBarItem(label = {
        Text(text = screen.title)
    },
        icon = {
            Icon(painter = painterResource(id = screen.icon), contentDescription = "")
        },
        selected = currentDestination?.hierarchy?.any {
            it.route == screen.route
        } == true,
        onClick = {
            navController.navigate(screen.route) {
                popUpTo(BottomBarScreen.Search.route) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    )
}