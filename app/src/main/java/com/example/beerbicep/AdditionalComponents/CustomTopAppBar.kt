package com.example.beerbicep.AdditionalComponents

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
/*
composable for top app bar with search field . if  isSearchMode is true , search field is displayed else title is displayed
ondrawer callback is for opening side navigation drawer
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    isSearchMode: Boolean,
    onSearchModeChange: (Boolean) -> Unit,
    onDrawerOpen: () -> Unit
) {
    TopAppBar(
        modifier = modifier
            .padding(horizontal = 8.dp),
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        windowInsets = WindowInsets(top = 0.dp),
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp)
            ) {
                AnimatedContent(
                    targetState = isSearchMode,
                    transitionSpec = {
                        fadeIn() + slideInHorizontally { it } togetherWith
                                fadeOut() + slideOutHorizontally { -it }
                    },
                    label = "searchTransition"
                ) { searching ->

                    if (searching) {
                        SearchField(
                            query = searchQuery,
                            onQueryChange = onQueryChange
                        )
                    } else {
                        Text(
                            text = "Brewery",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        },
        navigationIcon = {

            IconButton(
                onClick = onDrawerOpen
            ) {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = null,
                    modifier = Modifier

                        .size(27.dp)
                )
            }

        },
        actions = {
            if (!isSearchMode) {
                IconButton(onClick = { onSearchModeChange(true) }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
                Icon(
                    imageVector = Icons.Rounded.Notifications,
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                )
                Icon(
                    imageVector = Icons.Rounded.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                )
            } else {
                IconButton(onClick = {
                    onQueryChange("")
                    onSearchModeChange(false)
                }) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

        }

    )
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit
) {
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, null)
                Spacer(Modifier.width(8.dp))

                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier.weight(1f)
                ) {
                    if (query.isEmpty()) {
                        Text(
                            "Search beers...",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    innerTextField()
                }
            }
        }
    )
}
