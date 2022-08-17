package com.example.cryptomarket.presentation.exchanges

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.example.cryptomarket.domain.model.Exchange
import com.example.cryptomarket.presentation.common.StatusBarColor
import com.example.cryptomarket.presentation.common.TopAppBar
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch

@Composable
fun ExchangesScreen(
    viewModel: ExchangesViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val exchangesData = viewModel.exchangesData.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }

    StatusBarColor()
    ExchangesScreen(
        exchangesData = exchangesData,
        isLoading = state.isLoading,
        refresh = state.refresh,
        error = state.error,
        snackbarHostState = snackbarHostState,
        loadingExchanges = { loading ->
            viewModel.onEvent(
                ExchangesEvent.LoadingExchanges(loading = loading)
            )
        },
        onRefresh = { refresh ->
            viewModel.onEvent(
                ExchangesEvent.OnRefresh(refresh = refresh)
            )
        },
        onError = { error ->
            viewModel.onEvent(
                ExchangesEvent.OnError(error = error)
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangesScreen(
    exchangesData: LazyPagingItems<Exchange>,
    isLoading: Boolean,
    refresh: Boolean,
    error: String?,
    snackbarHostState: SnackbarHostState,
    loadingExchanges: (Boolean) -> Unit,
    onRefresh: (Boolean) -> Unit,
    onError: (String) -> Unit
) {
    LaunchedEffect(refresh) {
        if (refresh) {
            onRefresh(false)
            exchangesData.refresh()
        }
    }

    val loadState = exchangesData.loadState
    when {
        loadState.refresh is LoadState.Error -> {
            loadingExchanges(false)
            val e = exchangesData.loadState.refresh as LoadState.Error
            e.error.message?.let { onError(it) }
        }
        loadState.append is LoadState.Error -> {
            loadingExchanges(false)
            val e = exchangesData.loadState.append as LoadState.Error
            e.error.message?.let { onError(it) }
        }
        loadState.refresh is LoadState.Loading -> {
            loadingExchanges(true)
        }
        loadState.append is LoadState.Loading -> {
            loadingExchanges(true)
        }
        loadState.refresh is LoadState.NotLoading -> {
            loadingExchanges(false)
        }
        loadState.append is LoadState.NotLoading -> {
            loadingExchanges(false)
        }
    }

    val scope = rememberCoroutineScope()
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                LaunchedEffect(error) {
                    error?.let {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = it,
                                withDismissAction = true,
                                duration = SnackbarDuration.Long
                            )
                        }
                    }
                }
                TopAppBar(title = "Crypto Exchanges")
                ExchangesListHeader()
                ExchangesList(
                    exchangesData = exchangesData,
                    isLoading = isLoading,
                    onRefresh = onRefresh,
                )
            }
        }
    )
}

@Composable
fun ExchangesListHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .width(30.dp)
        ) {
            Text(
                text = "#",
                fontWeight = FontWeight.SemiBold
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Exchange",
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp, end = 20.dp)
            )
            Text(
                text = "Trust Score",
                fontWeight = FontWeight.SemiBold,
            )
        }
    }

}

@Composable
fun ExchangesList(
    exchangesData: LazyPagingItems<Exchange>,
    isLoading: Boolean,
    onRefresh: (Boolean) -> Unit,
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isLoading),
        onRefresh = {
            onRefresh(true)
        }
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(exchangesData) { exchange ->
                if (exchange != null) {
                    ExchangeItem(exchange = exchange)
                }
            }
        }

    }
}

@Composable
fun ExchangeItem(exchange: Exchange) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .width(30.dp)
        ) {
            Text(
                text = exchange.trustScoreRank.toString(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
            )
        }
        AsyncImage(
            model = exchange.image,
            contentDescription = "",
            modifier = Modifier
                .width(35.dp)
                .height(35.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = exchange.name.toString(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp, end = 20.dp)
            )
            Text(
                text = "${exchange.trustScore.toString()}/10",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
            )
        }
    }
}
