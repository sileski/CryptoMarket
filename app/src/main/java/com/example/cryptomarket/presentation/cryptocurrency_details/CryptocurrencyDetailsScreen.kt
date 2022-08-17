package com.example.cryptomarket.presentation.cryptocurrency_details

import android.graphics.Paint
import android.os.Build
import android.text.Spanned
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cryptomarket.domain.model.CoinDetails
import com.example.cryptomarket.domain.model.CoinOhlc
import com.example.cryptomarket.presentation.common.BackPressHandler
import com.example.cryptomarket.presentation.common.StatusBarColor
import com.example.cryptomarket.presentation.ui.theme.Green
import com.example.cryptomarket.util.ChartDaysFilterFilterOptionKeys
import kotlinx.coroutines.launch
import java.time.format.TextStyle
import java.util.*

@Composable
fun CryptocurrencyDetailsScreen(
    viewModel: CryptocurrencyDetailsViewModel = hiltViewModel(),
    coinId: String,
    coinName: String,
    coinSymbol: String,
    navigateBack: (Boolean?) -> Unit
) {
    val state = viewModel.state.value
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        viewModel.getCoinDetails(coinId = coinId)
        viewModel.getChartData(coinId = coinId)
        viewModel.checkIfCoinIsInWatchlist(coinId = coinId)
    }

    BackPressHandler(onBackPressed = {
        navigateBack(state.initialIsCoinInWatchlist != state.isCoinInWatchlist)
    })

    StatusBarColor()
    CryptocurrencyDetailsScreen(
        snackbarHostState = snackbarHostState,
        coinName = coinName,
        coinDetails = state.coinDetails,
        chartData = state.chartData,
        isLoadingChartData = state.isLoadingChart,
        isCoinInWatchlist = state.isCoinInWatchlist,
        initialIsCoinInWatchlist = state.initialIsCoinInWatchlist,
        errorChart = state.errorChart,
        errorDetails = state.errorDetails,
        getChartDaysFilterOptions = viewModel.getChartDaysFilterOptions(),
        selectedChartDaysFilterOption = state.selectedChartDaysFilterOption,
        priceDifference = state.priceDifference,
        navigateBack = navigateBack,
        getCoinPrice = {
            viewModel.getCoinPrice()
        },
        convertFromHtmlToText = { htmlText ->
            viewModel.convertFromHtmlToText(text = htmlText)
        },
        addRemoveCoinFromWatchList = {
            viewModel.onEvent(
                CryptocurrencyDetailsEvent.AddRemoveCoinWatchList(
                    coinId = coinId,
                    coinName = coinName,
                    coinSymbol = coinSymbol
                )
            )
        },
        selectChartDaysFilterOption = { item ->
            viewModel.onEvent(
                CryptocurrencyDetailsEvent.SelectedDaysFilterOption(
                    daysFilterOption = item,
                    coinId = coinId
                )
            )
        },
        getCurrencyFormat = { price ->
            viewModel.getCurrencyFormat(price = price)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptocurrencyDetailsScreen(
    snackbarHostState: SnackbarHostState,
    coinName: String,
    coinDetails: CoinDetails?,
    chartData: List<CoinOhlc>?,
    isCoinInWatchlist: Boolean,
    initialIsCoinInWatchlist: Boolean?,
    isLoadingChartData: Boolean,
    errorChart: String?,
    errorDetails: String?,
    getChartDaysFilterOptions: Map<String, String>,
    selectedChartDaysFilterOption: String,
    priceDifference: Double,
    convertFromHtmlToText: (String) -> Spanned?,
    navigateBack: (Boolean?) -> Unit,
    addRemoveCoinFromWatchList: () -> Unit,
    getCoinPrice: () -> String,
    selectChartDaysFilterOption: (String) -> Unit,
    getCurrencyFormat: (Double) -> String
) {
    val scope = rememberCoroutineScope()
    Column {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = coinName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navigateBack(initialIsCoinInWatchlist != isCoinInWatchlist)
                        }
                        ) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            addRemoveCoinFromWatchList()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                tint = if (isCoinInWatchlist) MaterialTheme.colorScheme.primary else Color.LightGray,
                                contentDescription = ""
                            )
                        }
                    },
                    modifier = Modifier.shadow(elevation = 8.dp)
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            content = {
                Column(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    LaunchedEffect(errorDetails) {
                        errorDetails?.let {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = it,
                                    withDismissAction = true,
                                    duration = SnackbarDuration.Long
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    CurrentPriceAndPriceChange(
                        getCoinPrice = getCoinPrice,
                        priceDifference = priceDifference
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    ChartDaysFilterOptions(
                        getChartDaysFilterOptions = getChartDaysFilterOptions,
                        selectedChartDaysFilterOption = selectedChartDaysFilterOption,
                        selectChartDaysFilterOption = selectChartDaysFilterOption,
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (!isLoadingChartData) {
                            if (chartData != null) {
                                CryptoChart(
                                    chartData = chartData, modifier = Modifier
                                        .fillMaxWidth()
                                        .height(350.dp)
                                        .align(CenterHorizontally),
                                    selectedChartDaysFilterOption = selectedChartDaysFilterOption,
                                    getCurrencyFormat = getCurrencyFormat
                                )
                            } else {
                                errorChart?.let {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(350.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = it)
                                    }
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(350.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    AboutCoin(
                        coinName = coinName,
                        coinDetails = coinDetails,
                        convertFromHtmlToText = convertFromHtmlToText
                    )
                }
            }
        )
    }
}

@Composable
fun CurrentPriceAndPriceChange(
    getCoinPrice: () -> String,
    priceDifference: Double,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = getCoinPrice(),
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp
        )
        Card(
            shape = RoundedCornerShape(6.dp),
            colors = CardDefaults.cardColors(
                containerColor = (if (priceDifference >= 0.0) Green.copy(alpha = 0.8f) else Color.Red.copy(
                    alpha = 0.6f
                ))
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (priceDifference >= 0.0) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    tint = MaterialTheme.colorScheme.background,
                    contentDescription = ""
                )
                Text(
                    text = "${priceDifference}%",
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.padding(5.dp)
                )
            }
        }

    }
}

@Composable
fun ChartDaysFilterOptions(
    getChartDaysFilterOptions: Map<String, String>,
    selectedChartDaysFilterOption: String,
    selectChartDaysFilterOption: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items(getChartDaysFilterOptions.toList()) { item ->
            DaysOption(
                title = item.second,
                isSelected = item.first == selectedChartDaysFilterOption,
                onDaySelected = {
                    selectChartDaysFilterOption(item.first)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaysOption(title: String, isSelected: Boolean, onDaySelected: () -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
        onClick = {
            onDaySelected()
        }
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CryptoChart(
    chartData: List<CoinOhlc>,
    selectedChartDaysFilterOption: String,
    chartColor: Color = Color.Green,
    getCurrencyFormat: (Double) -> String,
    modifier: Modifier
) {
    val space = 25f
    val chartColor = remember {
        chartColor.copy(alpha = 0.5f)
    }
    val highestPrice = remember(chartData) {
        (chartData.maxOfOrNull { it.close }?.plus(0))?.toDouble() ?: 0.0
    }
    val lowestPrice = remember(chartData) {
        chartData.minOfOrNull { it.close }?.toDouble() ?: 0.0
    }

    val density = LocalDensity.current
    val isDarkTheme = isSystemInDarkTheme()
    val textPaint = remember(density) {
        Paint().apply {
            color = if (isDarkTheme) android.graphics.Color.WHITE else android.graphics.Color.BLACK
            textAlign = Paint.Align.LEFT
            textSize = density.run { 10.sp.toPx() }
        }
    }

    val animateFloat = remember { androidx.compose.animation.core.Animatable(0f) }
    LaunchedEffect(animateFloat) {
        animateFloat.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
        )
    }

    Canvas(modifier = modifier) {
        val spacePerItem = (size.width - space) / chartData.size
        val step = chartData.size / 7
        (chartData.indices step step).forEach { i ->
            val data = chartData[i]
            val hour = data.date.hour
            val minute = if (data.date.minute < 10) {
                "0${data.date.minute}"
            } else {
                data.date.minute
            }
            val day = data.date.dayOfMonth
            val month = data.date.monthValue
            val monthName =
                data.date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            val year = data.date.year
            val text = when (selectedChartDaysFilterOption) {
                ChartDaysFilterFilterOptionKeys.HOURS_24 -> {
                    "$hour:$minute"
                }
                ChartDaysFilterFilterOptionKeys.DAYS_7 -> {
                    "$day"
                }
                ChartDaysFilterFilterOptionKeys.DAYS_14 -> {
                    "$day"
                }
                ChartDaysFilterFilterOptionKeys.MONTH_1 -> {
                    "$day"
                }
                ChartDaysFilterFilterOptionKeys.MONTH_3 -> {
                    "$day/$month"
                }
                ChartDaysFilterFilterOptionKeys.MONTH_6 -> {
                    "$day/$month"
                }
                ChartDaysFilterFilterOptionKeys.YEAR_1 -> {
                    "$monthName"
                }
                ChartDaysFilterFilterOptionKeys.MAX -> {
                    "$year"
                }
                else -> {
                    "$day"
                }
            }
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    text,
                    space + i * spacePerItem,
                    size.height + 10,
                    textPaint
                )
            }
        }
        val priceStep = (highestPrice - lowestPrice) / 6f
        val upperValueNumberOfDigits = highestPrice.toString().length
        val pricePositionX = upperValueNumberOfDigits * 5f
        (0..5).forEach { i ->
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    getCurrencyFormat(lowestPrice + priceStep * i),
                    pricePositionX,
                    size.height - space - i * size.height / 6f,
                    textPaint
                )
            }
        }
        var lastX = 0f
        val strokePath = Path().apply {
            val height = size.height
            for (i in chartData.indices) {
                val data = chartData[i]
                val nextData = chartData.getOrNull(i + 1) ?: chartData.last()
                val leftRatio = (data.close - lowestPrice) / (highestPrice - lowestPrice)
                val rightRatio = (nextData.close - lowestPrice) / (highestPrice - lowestPrice)

                val x1 = space + i * spacePerItem
                val y1 = height - space - (leftRatio * height).toFloat()
                val x2 = space + (i + 1) * spacePerItem
                val y2 = height - space - (rightRatio * height).toFloat()
                if (i == 0) {
                    moveTo(x1, y1)
                }
                lastX = (x1 + x2) / 2f
                quadraticBezierTo(
                    x1 * animateFloat.value,
                    y1 * animateFloat.value,
                    lastX * animateFloat.value,
                    (y1 + y2) / 2f * animateFloat.value
                )
            }
        }
        val fillPath = android.graphics.Path(strokePath.asAndroidPath())
            .asComposePath()
            .apply {
                lineTo(lastX, size.height - space)
                lineTo(space, size.height - space)
                close()
            }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    chartColor,
                    Color.Transparent
                ),
                endY = size.height - space
            )
        )
        drawPath(
            path = strokePath,
            color = chartColor,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}

@Composable
fun AboutCoin(
    coinName: String,
    coinDetails: CoinDetails?,
    convertFromHtmlToText: (String) -> Spanned?
) {
    Column {
        val isTextExpanded = remember {
            mutableStateOf(false)
        }
        val isDarkTheme = isSystemInDarkTheme()
        coinDetails?.description?.en?.let { htmlText ->
            if (htmlText.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .clickable {
                            isTextExpanded.value = !isTextExpanded.value
                        }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "About $coinName",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                        )
                        Icon(
                            imageVector = if (isTextExpanded.value) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = ""
                        )
                    }
                }
                if (isTextExpanded.value) {
                    AndroidView(
                        factory = { context -> TextView(context) },
                        update = { textView ->
                            textView.text = "${convertFromHtmlToText(htmlText)}"
                            textView.maxLines = if (!isTextExpanded.value) 6 else Int.MAX_VALUE
                            textView.isClickable = true
                            textView.setOnClickListener {
                                isTextExpanded.value = !isTextExpanded.value
                            }
                            textView.setTextColor(if (isDarkTheme) Color.White.toArgb() else Color.Black.toArgb())
                        },
                        modifier = Modifier.padding(horizontal = 15.dp)
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
            }
        }
    }
}