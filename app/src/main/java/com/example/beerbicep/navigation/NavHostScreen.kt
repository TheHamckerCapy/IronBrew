package com.example.beerbicep.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SettingsVoice
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.beerbicep.AdditionalComponents.AnimatedNavigationBar
import com.example.beerbicep.AdditionalComponents.ButtonData
import com.example.beerbicep.R
import com.example.beerbicep.presentation.Detail.DetailScreen
import com.example.beerbicep.presentation.Favourites.FavScreen
import com.example.beerbicep.presentation.Home.HomeEvents
import com.example.beerbicep.presentation.Home.HomeScreen
import com.example.beerbicep.presentation.Home.HomeViewModel
import kotlinx.coroutines.launch


@Composable
fun NavScreen(
    modifier: Modifier = Modifier
) {
    val buttons = remember {
        listOf(
            ButtonData(
                text = "Home",
                icon = R.drawable.home_app
            ),
            ButtonData(
                text = "Favs",
                icon = R.drawable.bookmarks_24px
            )
        )
    }

    val navController = rememberNavController()

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route
    val bottomBarVisibility = currentRoute != "detail/{beerId}"
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val homeViewModel: HomeViewModel = hiltViewModel()
    val homeState by homeViewModel.state.collectAsState()
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))

                // Drawer Header
                Text(
                    text = "TTS Settings",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 24.dp)
                )
                HorizontalDivider()

                // Pitch Slider
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SettingsVoice, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text("Pitch: ${String.format("%.1f", homeState.ttsSettings.pitch)}")
                    }
                    Slider(
                        value = homeState.ttsSettings.pitch,
                        onValueChange = { homeViewModel.onEvent(HomeEvents.OnPitchChange(it)) },
                        valueRange = 0.5f..2.0f,
                        steps = 15
                    )
                }

                // Rate Slider
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text("Speed: ${String.format("%.1f", homeState.ttsSettings.rate)}")
                    Slider(
                        value = homeState.ttsSettings.rate,
                        onValueChange = { homeViewModel.onEvent(HomeEvents.OnRateChange(it)) },
                        valueRange = 0.5f..2.0f,
                        steps = 15
                    )
                }
            }
        },
        drawerState = drawerState,
        gesturesEnabled = currentRoute=="home",
    ) {
        Scaffold(
        bottomBar = {
            if (bottomBarVisibility) {
                // Determine selected index based on route
                val selectedIndex = when (currentRoute) {
                    "home" -> 0
                    "favorites" -> 1
                    else -> 0 // Default or handle appropriately
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                ) {
                    AnimatedNavigationBar(
                        buttons = buttons,
                        barColor = Color.Black,
                        circleColor = Color.Green, // The floating circle
                        selectedColor = Color.Black, // Icon inside circle
                        unselectedColor = Color.Gray, // Icons on bar
                        selectedIndex = selectedIndex,
                        onItemClick = { index ->
                            val route = if (index == 0) "home" else "favorites"
                            if (currentRoute != route) {
                                navController.navigate(route) {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(it)
                .consumeWindowInsets(it)
        ) {
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable(
                    route = "home"
                ) {
                    HomeScreen(
                        onBeerClick = { beerId ->
                            navController.navigate("detail/$beerId")
                        },
                        onFavoritesClick = {
                            navController.navigate("favorites")
                        },
                        onDrawerClick = {
                            scope.launch { drawerState.open() }
                        }
                    )
                }
                composable(
                    route = "detail/{beerId}",
                    arguments = listOf(navArgument("beerId") { type = NavType.IntType })
                ) {
                    DetailScreen(
                        navController=navController
                    )
                }
                composable(
                    route = "favorites"
                ) {
                    FavScreen(
                        onBeerClick = { beerId ->
                            navController.navigate("detail/$beerId")
                        },
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }
            }

        }

    }
    }





}
