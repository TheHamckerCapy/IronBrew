package com.example.beerbicep.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.beerbicep.R
import com.example.beerbicep.presentation.Detail.DetailScreen
import com.example.beerbicep.presentation.Favourites.FavScreen
import com.example.beerbicep.presentation.Home.HomeScreen

data class NavItem(
    val route: String,
    val icon: Int
)

@Composable
fun NavScreen(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route
    val bottomBarVisibility = currentRoute!="detail/{beerId}"

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


    Scaffold (
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
    ){
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
                        }
                    )
                }
                composable(
                    route = "detail/{beerId}",
                    arguments = listOf(navArgument("beerId") { type = NavType.IntType })
                ) {
                    DetailScreen(

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


/*
@Composable
fun CustomNavigationBar(
    navController: NavController,
    items: List<NavItem>,
    modifier: Modifier = Modifier
) {



    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = modifier
                .width(230.dp)
                .height(75.dp)
                .clip(CircleShape)
                .background(Color.Black)
                .shadow(elevation = 10.dp, shape = CircleShape),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                IconButton(
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true


                        }
                    },


                    ) {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = null,
                        tint = if (currentRoute == item.route) Color.Green else Color.LightGray,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }

}

 */