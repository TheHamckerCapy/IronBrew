package com.example.beerbicep.presentation.Home


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.LocalDrink
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.beerbicep.Resource
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
                onSearchModeChange = { isSearchMode=it }
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
            } else{
                Column {
                    BuildImageSlider()
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
fun SearchResultList(searchResult: Resource<List<BeerDomain>>, onEvent: (HomeEvents) -> Unit) {

    when (searchResult) {
        is Resource.Error<*> -> {
            ErrorItem(
                message = searchResult.message ?: "search failed",
                onClickRetry = { onEvent(HomeEvents.Refresh) }
            )
        }

        is Resource.Loading<*> -> {
            LoadingView(modifier = Modifier.fillMaxSize())
        }

        is Resource.Success<*> -> {

            val beers = searchResult.data ?: emptyList()
            if (beers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    Text("No beers found", color = Color.Gray)
                }
            }else{
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

@Composable
fun BeerItem(
    beer: BeerDomain,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min) // Important for layout consistency
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.Top // Align to top to handle long descriptions
        ) {
            // Beer Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(beer.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = beer.name,
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White), // White background for transparent PNGs
                contentScale = ContentScale.Fit,
                error = rememberVectorPainter(Icons.Outlined.LocalDrink) // Simple error placeholder
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Beer Details
            Column(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxHeight()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Name
                    Text(
                        text = beer.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Favorite Button
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (beer.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Toggle Favorite",
                            tint = if (beer.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Tagline
                Text(
                    text = beer.tagLine,
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))


                // ABV Badge
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = "ABV: ${beer.alcoholByVolume}%",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

// --- Helper Composables for Loading/Error States ---

@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun LoadingItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            strokeWidth = 2.dp
        )
    }
}

@Composable
fun ErrorItem(
    message: String,
    modifier: Modifier = Modifier,
    onClickRetry: () -> Unit
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onClickRetry) {
            Text("Retry")
        }
    }
}
