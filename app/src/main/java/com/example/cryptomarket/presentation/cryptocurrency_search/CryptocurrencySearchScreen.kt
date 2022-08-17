package com.example.cryptomarket.presentation.cryptocurrency_search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cryptomarket.domain.model.Coin
import com.example.cryptomarket.presentation.common.StatusBarColor

@Composable
fun CryptoCurrencySearchScreen(
    viewModel: CryptocurrencySearchViewModel = hiltViewModel(),
    navigateToDetails: (String, String, String) -> Unit
) {
    val state = viewModel.state.value
    StatusBarColor()
    CryptoCurrencySearchScreen(
        searchQuery = state.searchQuery,
        coins = state.coins,
        isLoading = state.isLoading,
        error = state.error,
        searchCoins = { query ->
            viewModel.onEvent(
                CryptocurrencySearchEvent.SearchCoins(query = query)
            )
        },
        clearSearchQuery = {
            viewModel.onEvent(
                CryptocurrencySearchEvent.ClearSearchQuery
            )
        },
        navigateToDetails = navigateToDetails
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoCurrencySearchScreen(
    searchQuery: String,
    coins: List<Coin>?,
    isLoading: Boolean,
    error: String?,
    searchCoins: (String) -> Unit,
    clearSearchQuery: () -> Unit,
    navigateToDetails: (String, String, String) -> Unit
) {
    val keyboardFocusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchCoins(it)
            },
            maxLines = 1,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            placeholder = {
                Text(text = "Search")
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "")
            },
            trailingIcon = {
                if (searchQuery.isNotBlank()) Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "",
                    modifier = Modifier.clickable {
                        clearSearchQuery()
                    }
                ) else null
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { keyboardFocusManager.clearFocus() }
            )
        )

        error?.let {
            Spacer(modifier = Modifier.height(20.dp))
            Box(

                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 50.dp)
            ) {
                Text(text = it)
            }
        }

        coins?.let {
            Spacer(modifier = Modifier.height(10.dp))
            CoinsList(coins = it, navigateToDetails = navigateToDetails)
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}


@Composable
fun CoinsList(
    coins: List<Coin>,
    navigateToDetails: (String, String, String) -> Unit
) {
    LazyColumn {
        items(coins) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navigateToDetails(it.id, it.name, it.symbol)
                    }
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp, vertical = 15.dp)
                    )
                    {
                        Text(text = it.name)
                    }
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                    )
                }
            }
        }
    }
}