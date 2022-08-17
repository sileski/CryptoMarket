package com.example.cryptomarket.presentation.market

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.example.cryptomarket.domain.filter_options.CoinsFilterCategories
import com.example.cryptomarket.domain.filter_options.CoinsFilterOption
import com.example.cryptomarket.domain.model.Coin
import com.example.cryptomarket.presentation.common.BackPressHandler
import com.example.cryptomarket.presentation.common.StatusBarColor
import com.example.cryptomarket.presentation.common.TopAppBar
import com.google.accompanist.pager.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MarketScreen(
    viewModel: MarketViewModel = hiltViewModel(),
    changesInWatchlist: Boolean? = null,
    navigateToDetails: (String, String, String) -> Unit
) {
    val state = viewModel.state.value

    LaunchedEffect(key1 = true) {
        changesInWatchlist?.let {
            if (it) {
                viewModel.changeInWatchlist()
            }
        }
    }

    val showFilterOptionsMenuAnimation = remember {
        mutableStateOf(false)
    }
    LaunchedEffect(state.showFilterOptionsMenu) {
        showFilterOptionsMenuAnimation.value = state.showFilterOptionsMenu
    }

    val pagerState = rememberPagerState()
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            viewModel.onEvent(
                MarketEvent.PagerPageChange(page)
            )
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val pagerPages = listOf(
        "Cryptocurrencies", "Watchlist"
    )

    BackPressHandler(onBackPressed = {
        if (state.showFilterOptionsMenu) {
            viewModel.onEvent(
                MarketEvent.CloseFilterOptionsMenu
            )
        }
    })

    StatusBarColor()
    MarketScreen(
        viewModel = viewModel,
        state = state,
        coinsData = viewModel.coinsData,
        watchlistData = viewModel.watchlistData,
        pagerState = pagerState,
        snackbarHostState = snackbarHostState,
        pagerPages = pagerPages,
        showFilterOptionsMenuAnimation = showFilterOptionsMenuAnimation,
        refreshCoins = state.refreshCoins,
        refreshWatchlist = state.refreshWatchlist,
        isLoadingCoins = state.isLoadingCoins,
        isLoadingWatchlist = state.isLoadingWatchlist,
        watchlistCoinsIds = state.watchlistCoinsIds,
        navigateToDetails = navigateToDetails,
        getSelectedCurrencyOptionTitle = viewModel.getCurrencyOptions()[state.selectedCurrencyOption].toString(),
        getSelectedSortOptionTitle = viewModel.getSortingOptions()[state.selectedSortOption].toString(),
        getSelectedOrderOptionTitle = viewModel.getOrderingOptions()[state.selectedOrderOption].toString(),
        getSelectedPricePercentageChangeOptionTitle = viewModel.getPricePercentageChangeOptions()[state.selectedPricePercentageChangeOption].toString(),
        openFilterOptionsMenu = { selectedCoinsFilterCategory ->
            viewModel.onEvent(
                MarketEvent.OpenFilterOptionsMenu(selectedCoinsFilterCategory = selectedCoinsFilterCategory)
            )
        },
        getCurrencyFormat = { price ->
            viewModel.getCurrencyFormat(price = price)
        },
        refresh = { refresh ->
            viewModel.onEvent(
                MarketEvent.Refresh(refresh = refresh)
            )
        },
        loadingCoins = { loading ->
            viewModel.onEvent(
                MarketEvent.LoadingCoins(loading = loading)
            )
        },
        addRemoveCoinFromWatchlist = { coin ->
            viewModel.onEvent(
                MarketEvent.AddRemoveCoinWatchList(coin = coin)
            )
        },
        removeCoinFromWatchList = { coin ->
            viewModel.onEvent(
                MarketEvent.RemoveCoinFromWatchList(coin = coin)
            )
        },
        getPricePercentageChange = { coin ->
            viewModel.getPricePercentageChange(coin = coin)
        },
        loadingWatchlist = { loading ->
            viewModel.onEvent(
                MarketEvent.LoadingWatchlist(loading = loading)
            )
        },
        onError = { error ->
            viewModel.onEvent(
                MarketEvent.OnError(error = error)
            )
        }
    )
}

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MarketScreen(
    viewModel: MarketViewModel,
    state: MarketState,
    coinsData: Flow<PagingData<Coin>>,
    watchlistData: Flow<PagingData<Coin>>,
    pagerState: PagerState,
    snackbarHostState: SnackbarHostState,
    pagerPages: List<String>,
    showFilterOptionsMenuAnimation: MutableState<Boolean>,
    refreshCoins: Boolean,
    refreshWatchlist: Boolean,
    isLoadingCoins: Boolean,
    isLoadingWatchlist: Boolean,
    watchlistCoinsIds: List<String>,
    navigateToDetails: (String, String, String) -> Unit,
    getSelectedCurrencyOptionTitle: String,
    getSelectedSortOptionTitle: String,
    getSelectedOrderOptionTitle: String,
    getSelectedPricePercentageChangeOptionTitle: String,
    openFilterOptionsMenu: (CoinsFilterCategories) -> Unit,
    getCurrencyFormat: (Double) -> String,
    refresh: (Boolean) -> Unit,
    loadingCoins: (Boolean) -> Unit,
    addRemoveCoinFromWatchlist: (Coin) -> Unit,
    removeCoinFromWatchList: (Coin) -> Unit,
    getPricePercentageChange: (Coin) -> String,
    loadingWatchlist: (Boolean) -> Unit,
    onError: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                LaunchedEffect(state.error) {
                    state.error?.let {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = it,
                                withDismissAction = true,
                                duration = SnackbarDuration.Long
                            )
                        }
                    }
                }
                TopAppBar(title = "Crypto Market")
                androidx.compose.material.TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                        )
                    },
                    backgroundColor = MaterialTheme.colorScheme.background
                ) {
                    val coroutine = rememberCoroutineScope()
                    pagerPages.forEachIndexed { index, title ->
                        Tab(
                            text = { Text(title) },
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutine.launch { pagerState.animateScrollToPage(index) }
                            },
                        )
                    }
                }
                FilterOptions(
                    getSelectedCurrencyOptionTitle = { getSelectedCurrencyOptionTitle },
                    getSelectedSortOptionTitle = { getSelectedSortOptionTitle },
                    getSelectedOrderOptionTitle = { getSelectedOrderOptionTitle },
                    getSelectedPricePercentageChangeOptionTitle = { getSelectedPricePercentageChangeOptionTitle },
                    openFilterOptionsMenu = openFilterOptionsMenu
                )
                HorizontalPager(count = pagerPages.size, state = pagerState) { page ->
                    when (page) {
                        0 -> {
                            CryptocurrenciesPage(
                                coinsData = coinsData,
                                refreshCoins = refreshCoins,
                                isLoadingCoins = isLoadingCoins,
                                watchlistCoinsIds = watchlistCoinsIds,
                                getCurrencyFormat = getCurrencyFormat,
                                loadCoins = loadingCoins,
                                refresh = refresh,
                                onError = onError,
                                navigateToDetails = navigateToDetails,
                                addRemoveCoinFromWatchlist = addRemoveCoinFromWatchlist,
                                getPricePercentageChange = getPricePercentageChange
                            )
                        }
                        1 -> {
                            WatchlistPage(
                                watchlistData = watchlistData,
                                refreshWatchlist = refreshWatchlist,
                                watchlistCoinsIds = watchlistCoinsIds,
                                isLoadingWatchlist = isLoadingWatchlist,
                                navigateToDetails = navigateToDetails,
                                loadWatchlist = loadingWatchlist,
                                onError = onError,
                                getCurrencyFormat = getCurrencyFormat,
                                refresh = refresh,
                                removeCoinFromWatchList = removeCoinFromWatchList,
                                getPricePercentageChange = getPricePercentageChange
                            )
                        }
                    }
                }
            }
        }
    )

    if (state.showFilterOptionsMenu) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray.copy(alpha = 0.5f))
        ) {
            Box(modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clickable {
                    viewModel.onEvent(
                        MarketEvent.CloseFilterOptionsMenu
                    )
                }
            )
            when (state.selectedFilterOptionCategory) {
                CoinsFilterCategories.CURRENCY -> {
                    OptionsMenu(
                        categoryTitle = "Currency",
                        showAnimation = showFilterOptionsMenuAnimation,
                        beforeSelectedOption = state.selectedCurrencyOption,
                        optionsItems = viewModel.getCurrencyOptions(),
                        filterOptionsItemSelected = { option ->
                            viewModel.onEvent(
                                MarketEvent.FilterOptionsItemSelected(option = option)
                            )
                        }
                    )
                }
                CoinsFilterCategories.SORT_BY -> {
                    OptionsMenu(
                        categoryTitle = "Sort By",
                        showAnimation = showFilterOptionsMenuAnimation,
                        beforeSelectedOption = state.selectedSortOption,
                        optionsItems = viewModel.getSortingOptions(),
                        filterOptionsItemSelected = { option ->
                            viewModel.onEvent(
                                MarketEvent.FilterOptionsItemSelected(option = option)
                            )
                        }
                    )
                }
                CoinsFilterCategories.ORDER_BY -> {
                    OptionsMenu(
                        categoryTitle = "Order By",
                        showAnimation = showFilterOptionsMenuAnimation,
                        beforeSelectedOption = state.selectedOrderOption,
                        optionsItems = viewModel.getOrderingOptions(),
                        filterOptionsItemSelected = { option ->
                            viewModel.onEvent(
                                MarketEvent.FilterOptionsItemSelected(option = option)
                            )
                        }
                    )
                }
                CoinsFilterCategories.PRICE_PERCENTAGE_CHANGE -> {
                    OptionsMenu(
                        categoryTitle = "Price Change Timeline",
                        showAnimation = showFilterOptionsMenuAnimation,
                        beforeSelectedOption = state.selectedPricePercentageChangeOption,
                        optionsItems = viewModel.getPricePercentageChangeOptions(),
                        filterOptionsItemSelected = { option ->
                            viewModel.onEvent(
                                MarketEvent.FilterOptionsItemSelected(option = option)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CryptocurrenciesPage(
    coinsData: Flow<PagingData<Coin>>,
    refreshCoins: Boolean,
    isLoadingCoins: Boolean,
    refresh: (Boolean) -> Unit,
    loadCoins: (Boolean) -> Unit,
    watchlistCoinsIds: List<String>,
    getCurrencyFormat: (Double) -> String,
    onError: (String) -> Unit,
    navigateToDetails: (String, String, String) -> Unit,
    addRemoveCoinFromWatchlist: (Coin) -> Unit,
    getPricePercentageChange: (Coin) -> String
) {

    val coinsData = coinsData.collectAsLazyPagingItems()

    LaunchedEffect(refreshCoins) {
        if (refreshCoins) {
            refresh(false)
            coinsData.refresh()
        }
    }

    val loadState = coinsData.loadState
    when {
        loadState.refresh is LoadState.Error -> {
            loadCoins(false)
            val e = coinsData.loadState.refresh as LoadState.Error
            e.error.message?.let { onError(it) }
        }
        loadState.append is LoadState.Error -> {
            loadCoins(false)
            val e = coinsData.loadState.append as LoadState.Error
            e.error.message?.let { onError(it) }
        }
        loadState.refresh is LoadState.Loading -> {
            loadCoins(true)
        }
        loadState.append is LoadState.Loading -> {
            loadCoins(true)
        }
        loadState.refresh is LoadState.NotLoading -> {
            loadCoins(false)
        }
        loadState.append is LoadState.NotLoading -> {
            loadCoins(false)
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        CoinsList(
            coins = coinsData,
            isLoading = isLoadingCoins,
            watchlistCoinsIds = watchlistCoinsIds,
            onRefresh = {
                refresh(true)
            },
            addRemoveCoinFromWatchlist = { coin ->
                addRemoveCoinFromWatchlist(coin)
            },
            navigateToDetails = navigateToDetails,
            getCurrencyFormat = getCurrencyFormat,
            getPricePercentageChange = getPricePercentageChange
        )
    }
}


@Composable
fun CoinsList(
    coins: LazyPagingItems<Coin>,
    isLoading: Boolean,
    watchlistCoinsIds: List<String>,
    onRefresh: () -> Unit,
    addRemoveCoinFromWatchlist: (Coin) -> Unit,
    navigateToDetails: (String, String, String) -> Unit,
    getCurrencyFormat: (Double) -> String,
    getPricePercentageChange: (Coin) -> String
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isLoading),
        onRefresh = {
            onRefresh()
        }
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(
                items = coins,
            ) { coin ->
                if (coin != null) {
                    CoinItem(
                        coin = coin,
                        watchlistCoinsIds = watchlistCoinsIds,
                        removeCoinFromWatchList = {
                            addRemoveCoinFromWatchlist(coin)
                        },
                        navigateToDetails = navigateToDetails,
                        getCurrencyFormat = getCurrencyFormat,
                        getPricePercentageChange = getPricePercentageChange
                    )
                }
            }
        }
    }
}

@Composable
fun WatchlistPage(
    watchlistData: Flow<PagingData<Coin>>,
    refreshWatchlist: Boolean,
    refresh: (Boolean) -> Unit,
    isLoadingWatchlist: Boolean,
    loadWatchlist: (Boolean) -> Unit,
    onError: (String) -> Unit,
    watchlistCoinsIds: List<String>,
    navigateToDetails: (String, String, String) -> Unit,
    removeCoinFromWatchList: (Coin) -> Unit,
    getCurrencyFormat: (Double) -> String,
    getPricePercentageChange: (Coin) -> String
) {
    val watchlistData = watchlistData.collectAsLazyPagingItems()

    LaunchedEffect(refreshWatchlist) {
        if (refreshWatchlist) {
            watchlistData.refresh()
            refresh(false)
        }
    }

    val loadState = watchlistData.loadState
    when {
        loadState.refresh is LoadState.Error -> {
            loadWatchlist(false)
            val e = watchlistData.loadState.refresh as LoadState.Error
            e.error.message?.let { onError(it) }
        }
        loadState.append is LoadState.Error -> {
            loadWatchlist(false)
            val e = watchlistData.loadState.append as LoadState.Error
            e.error.message?.let { onError(it) }
        }
        loadState.refresh is LoadState.Loading -> {
            loadWatchlist(true)
        }
        loadState.append is LoadState.Loading -> {
            loadWatchlist(true)
        }
        loadState.refresh is LoadState.NotLoading -> {
            loadWatchlist(false)
        }
        loadState.append is LoadState.NotLoading -> {
            loadWatchlist(false)
        }
    }


    Column(modifier = Modifier.fillMaxSize()) {
        if (watchlistCoinsIds.isEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(text = "Watchlist is empty")
            }
        } else {
            WatchlistCoins(
                isLoading = isLoadingWatchlist,
                watchlistCoins = watchlistData,
                watchlistCoinsIds = watchlistCoinsIds,
                navigateToDetails = navigateToDetails,
                getCurrencyFormat = getCurrencyFormat,
                refresh = refresh,
                removeCoinFromWatchList = removeCoinFromWatchList,
                getPricePercentageChange = getPricePercentageChange
            )
        }
    }
}

@Composable
fun WatchlistCoins(
    isLoading: Boolean,
    watchlistCoins: LazyPagingItems<Coin>,
    watchlistCoinsIds: List<String>,
    getCurrencyFormat: (Double) -> String,
    navigateToDetails: (String, String, String) -> Unit,
    removeCoinFromWatchList: (Coin) -> Unit,
    refresh: (Boolean) -> Unit,
    getPricePercentageChange: (Coin) -> String
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isLoading),
        onRefresh = {
            refresh(true)
        }
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(watchlistCoins) { coin ->
                if (coin != null) {
                    CoinItem(
                        coin = coin,
                        watchlistCoinsIds = watchlistCoinsIds,
                        removeCoinFromWatchList = {
                            removeCoinFromWatchList(coin)
                        },
                        navigateToDetails = navigateToDetails,
                        getCurrencyFormat = getCurrencyFormat,
                        getPricePercentageChange = getPricePercentageChange
                    )
                }
            }
        }
    }
}

@Composable
fun CoinItem(
    coin: Coin,
    watchlistCoinsIds: List<String>,
    removeCoinFromWatchList: () -> Unit,
    navigateToDetails: (String, String, String) -> Unit,
    getCurrencyFormat: (Double) -> String,
    getPricePercentageChange: (Coin) -> String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navigateToDetails(coin.id, coin.name, coin.symbol)
            },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "",
                tint = if (watchlistCoinsIds.contains(coin.id)) MaterialTheme.colorScheme.primary else Color.LightGray,
                modifier = Modifier.clickable {
                    removeCoinFromWatchList()
                })
            Spacer(modifier = Modifier.width(3.dp))
            Box(
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
            ) {
                AsyncImage(
                    model = coin.image,
                    contentDescription = "",
                    modifier = Modifier
                )
            }
            Column(modifier = Modifier.padding(start = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = coin.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 20.dp),
                    )
                    Text(
                        text = getCurrencyFormat(coin.currentPrice),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = coin.symbol.uppercase(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 20.dp)
                    )
                    val percentageChange = getPricePercentageChange(coin)
                    Text(
                        text = percentageChange,
                        color = if (percentageChange.removeSuffix("%")
                                .toDouble() >= 0.0
                        ) Color.Green else Color.Red,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}


@Composable
fun FilterOptions(
    getSelectedCurrencyOptionTitle: () -> String,
    getSelectedSortOptionTitle: () -> String,
    getSelectedOrderOptionTitle: () -> String,
    getSelectedPricePercentageChangeOptionTitle: () -> String,
    openFilterOptionsMenu: (CoinsFilterCategories) -> Unit,
) {
    val optionsList = listOf(
        CoinsFilterOption(
            selectedOption = getSelectedCurrencyOptionTitle(),
            filterCategory = CoinsFilterCategories.CURRENCY
        ),
        CoinsFilterOption(
            selectedOption = getSelectedSortOptionTitle(),
            filterCategory = CoinsFilterCategories.SORT_BY
        ),
        CoinsFilterOption(
            selectedOption = getSelectedOrderOptionTitle(),
            filterCategory = CoinsFilterCategories.ORDER_BY
        ),
        CoinsFilterOption(
            selectedOption = getSelectedPricePercentageChangeOptionTitle(),
            filterCategory = CoinsFilterCategories.PRICE_PERCENTAGE_CHANGE
        ),
    )
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items(optionsList) {
            FilterItem(
                selectedOption = it.selectedOption,
                filterCategory = it.filterCategory,
                openFilterOptionsMenu = openFilterOptionsMenu
            )
        }
    }
}

@Composable
fun FilterItem(
    selectedOption: String,
    filterCategory: CoinsFilterCategories,
    openFilterOptionsMenu: (CoinsFilterCategories) -> Unit
) {
    Surface(
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.clickable {
                openFilterOptionsMenu(filterCategory)
            }
        ) {
            Text(
                text = selectedOption,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun OptionsMenu(
    categoryTitle: String,
    showAnimation: MutableState<Boolean>,
    beforeSelectedOption: String,
    optionsItems: Map<String, String>,
    filterOptionsItemSelected: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    AnimatedVisibility(
        visible = showAnimation.value,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(durationMillis = 600)
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = configuration.screenHeightDp.dp / 2.5f),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            shadowElevation = 12.dp
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = categoryTitle,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                LazyColumn {
                    items(optionsItems.toList()) {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                filterOptionsItemSelected(it.first)
                            }) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = it.second,
                                    fontSize = 18.sp,
                                    color = if (it.first == beforeSelectedOption) Color.Blue else MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
