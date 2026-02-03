package com.example.beerbicep.presentation.Home


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.beerbicep.AdditionalComponents.BeerItem
import com.example.beerbicep.AdditionalComponents.BuildImageSlider
import com.example.beerbicep.AdditionalComponents.CustomTopAppBar
import com.example.beerbicep.Resource_Class
import com.example.beerbicep.domain.BeerDomain


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onBeerClick: (Int) -> Unit,
    onFavoritesClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val beerPagingItems = viewModel.beerPagingFlow.collectAsLazyPagingItems()
    val context = LocalContext.current
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResult by viewModel.searchResult.collectAsState()
    var isSearchMode by remember { mutableStateOf(false) }
    val isRefreshing = beerPagingItems.loadState.refresh is LoadState.Loading
    // Handle Paging Error Toasts (Optional UX enhancement)
    LaunchedEffect(key1 = beerPagingItems.loadState) {
        if (beerPagingItems.loadState.refresh is LoadState.Error) {
            Toast.makeText(
                context,
                "Error: ${(beerPagingItems.loadState.refresh as LoadState.Error).error.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState()
    )
    Scaffold(
        topBar = {
            CustomTopAppBar(
                scrollBehavior = scrollBehavior,
                searchQuery = searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                isSearchMode = isSearchMode,
                onSearchModeChange = { isSearchMode = it }
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isSearchMode && searchQuery.isNotBlank()) {
                SearchResultList(
                    searchResult = searchResult,
                    onEvent = { event ->
                        when (event) {
                            is HomeEvents.OnBeerClick -> onBeerClick(event.id)
                            else -> viewModel.onEvent(event)
                        }
                    }
                )
            } else {
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        beerPagingItems.refresh()
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    BeerListContent(
                        beerPagingItems = beerPagingItems,
                        onEvent = { event ->
                            when (event) {
                                is HomeEvents.OnBeerClick -> {
                                    onBeerClick(event.id)
                                }

                                is HomeEvents.ToggleFav -> {
                                    viewModel.onEvent(event)
                                }

                                HomeEvents.Refresh -> {
                                    beerPagingItems.refresh()
                                }
                            }
                        }
                    )
                }


            }


        }
    }
}

@Composable
fun SearchResultList(searchResult: Resource_Class<List<BeerDomain>>, onEvent: (HomeEvents) -> Unit) {

    when (searchResult) {
        is Resource_Class.Error<*> -> {
            ErrorItem(
                message = searchResult.message ?: "search failed",
                onClickRetry = { onEvent(HomeEvents.Refresh) }
            )
        }

        is Resource_Class.Loading<*> -> {
            LoadingView(modifier = Modifier.fillMaxSize())
        }

        is Resource_Class.Success<*> -> {

            val beers = searchResult.data ?: emptyList()
            if (beers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No beers found", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(beers) { beer ->
                        BeerItem(
                            beer = beer,
                            onClick = { onEvent(HomeEvents.OnBeerClick(beer.id)) },
                            onToggleFavorite = { onEvent(HomeEvents.ToggleFav(beer)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BeerListContent(
    beerPagingItems: LazyPagingItems<BeerDomain>,
    onEvent: (HomeEvents) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. The List of Beers
        item { BuildImageSlider() }
        items(
            count = beerPagingItems.itemCount,
            key = beerPagingItems.itemKey { it.id }
        ) { index ->
            val beer = beerPagingItems[index]
            if (beer != null) {
                BeerItem(
                    beer = beer,
                    onClick = { onEvent(HomeEvents.OnBeerClick(id = beer.id)) },
                    onToggleFavorite = { onEvent(HomeEvents.ToggleFav(beerDomain = beer)) }
                )
            }
        }

        // 2. Handling Load States
        beerPagingItems.apply {
            when {
                // Initial Load Loading
                loadState.refresh is LoadState.Loading -> {
                    item { LoadingView(modifier = Modifier.fillParentMaxSize()) }
                }

                // Initial Load Error
                loadState.refresh is LoadState.Error -> {
                    val e = beerPagingItems.loadState.refresh as LoadState.Error
                    item {
                        ErrorItem(
                            message = e.error.localizedMessage ?: "Unknown Error",
                            modifier = Modifier.fillParentMaxSize(),
                            onClickRetry = { retry() }
                        )
                    }
                }

                // Append Loading (Loading more at bottom)
                loadState.append is LoadState.Loading -> {
                    item { LoadingItem() }
                }

                // Append Error (Error at bottom)
                loadState.append is LoadState.Error -> {
                    val e = beerPagingItems.loadState.append as LoadState.Error
                    item {
                        ErrorItem(
                            message = e.error.localizedMessage ?: "Error loading more",
                            onClickRetry = { retry() }
                        )
                    }
                }
            }
        }
    }
}


// --- Helper Composables for Loading/Error States ---

