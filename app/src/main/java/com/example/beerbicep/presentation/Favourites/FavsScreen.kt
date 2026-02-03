package com.example.beerbicep.presentation.Favourites


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.beerbicep.AdditionalComponents.BeerItem
import com.example.beerbicep.domain.BeerDomain


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavScreen(
    modifier: Modifier = Modifier,
    viewModel: FavViewModel = hiltViewModel(),
    onBeerClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.favBeerState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favs") },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        androidx.compose.material3.Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            FavoriteBeerList(
                beers = state.favBeersList,
                onEvent = { event ->
                    when (event) {
                        is FavEvents.OnBeerClick -> onBeerClick(event.id)
                        is FavEvents.ToggleFav -> viewModel.OnToggleFavorite(event.beerDomain)
                        else -> Unit
                    }
                }
            )
        }

    }

}

@Composable
fun FavoriteBeerList(
    beers: List<BeerDomain>,
    onEvent: (FavEvents) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = beers,
            key = { it.id }
        ) { beer ->
            BeerItem(
                beer = beer,
                onClick = { onEvent(FavEvents.OnBeerClick(id = beer.id)) },
                onToggleFavorite = { onEvent(FavEvents.ToggleFav(beerDomain = beer)) }
            )
        }

        if (beers.isEmpty()) {
            item {
                Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No favorites yet!", color = Color.Gray)
                }
            }
        }
    }
}
