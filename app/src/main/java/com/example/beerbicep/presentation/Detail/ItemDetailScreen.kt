package com.example.beerbicep.presentation.Detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalPizza
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.SoupKitchen
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.beerbicep.AdditionalComponents.TtsController
import com.example.beerbicep.R
import com.example.beerbicep.domain.BeerDomain
import com.example.beerbicep.ui.theme.my_font_1


@Composable
fun BeerDetailContent(
    tts: TtsController,
    beer: BeerDomain,
    onNavigateUp: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 32.dp)
    ) {
        // --- Header (Back Button & Favorite) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            // Added Favorite Button here in the header for easy access
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (beer.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (beer.isFavorite) Color.Red else Color.White
                )
            }
        }

        // --- Main Section (Title + Stats + Image) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(550.dp)
        ) {
            // 1. The "Frame" Border
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(200.dp)
                    .height(250.dp)
                    .offset(x = 25.dp)
                    .border(BorderStroke(2.dp, Color(0xFF444444)))
            )


            // 2. The Content Row
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                // LEFT COLUMN
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 40.dp)
                ) {
                    Text(
                        text = beer.name,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontFamily = my_font_1,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White,
                        lineHeight = 45.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = beer.tagLine,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontStyle = FontStyle.Italic
                        ),
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(60.dp))

                    StatItem(
                        value = "${beer.alcoholByVolume}%",
                        label = "ABV",
                        icon = null
                    )

                    Spacer(modifier = Modifier.height(32.dp))


                    StatItem(
                        value = beer.bitternessUnit?.toInt()?.toString() ?: "-",
                        label = "Bitterness",
                        icon = Icons.Outlined.WaterDrop
                    )
                }

                // RIGHT COLUMN
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(beer.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = beer.name,
                        modifier = Modifier
                            .fillMaxHeight(0.85f)
                            .offset(y = 50.dp, x = 20.dp)
                            .width(120.dp)
                            .height(140.dp),
                        contentScale = ContentScale.Fit,
                        error = painterResource(R.drawable.testimage3)
                    )
                }
            }
        }

        // --- Bottom Section ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = my_font_1,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.LightGray,
                        fontSize = 30.sp
                    )
                }
                IconButton(
                    onClick = {
                        if (tts.isSpeaking) {
                            tts.stop()
                        } else {
                            tts.speak(beer.description)
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (tts.isSpeaking)
                            Icons.Default.Stop
                        else
                            Icons.Default.VolumeUp,
                        contentDescription = "Speak description",
                        tint = if (tts.isSpeaking) Color.Red else Color.LightGray
                    )
                }
            }


            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = beer.description,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = my_font_1,
                    fontWeight = FontWeight.Light
                ),
                color = Color.LightGray,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(34.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.beerfood),
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Food Pairing",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = my_font_1,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.LightGray,
                    fontSize = 30.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            beer.bestFoods.forEach { pairing ->
                Text(
                    text = "• $pairing",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Serif,
                    color = Color.LightGray,
                    modifier = Modifier.padding(vertical = 2.dp),
                    fontSize = 17.sp
                )
            }
            Spacer(modifier = Modifier.height(34.dp))

            // --- Ingredients Section ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Science,
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Ingredients",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = my_font_1,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.LightGray,
                    fontSize = 30.sp
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            // --- Malt Section ---
            IngredientSubHeader(text = "Malt", icon = Icons.Outlined.SoupKitchen)
            beer.ingredients.malt.forEach { malt ->
                Text(
                    text = "• ${malt.name} (${malt.amount.value} ${malt.amount.unit})",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Serif,
                    color = Color.LightGray,
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Hops Section ---
            IngredientSubHeader(
                text = "Hops",
                icon = Icons.Outlined.LocalPizza
            ) // Using pizza as a placeholder for hop flower
            beer.ingredients.hops.forEach { hop ->
                Text(
                    text = "• ${hop.name} (${hop.amount.value} ${hop.amount.unit}) - ${hop.add} / ${hop.attribute}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Serif,
                    color = Color.LightGray,
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                    fontSize = 16.sp
                )
            }

            // --- Yeast Section ---
            if (beer.ingredients.yeast != null) {
                Spacer(modifier = Modifier.height(16.dp))
                IngredientSubHeader(text = "Yeast", icon = Icons.Outlined.WaterDrop)
                Text(
                    text = "• ${beer.ingredients.yeast}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Serif,
                    color = Color.LightGray,
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                    fontSize = 16.sp
                )
            }

        }
    }
}