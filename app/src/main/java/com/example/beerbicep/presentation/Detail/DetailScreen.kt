package com.example.beerbicep.presentation.Detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.beerbicep.Resource_Class
import com.example.beerbicep.AdditionalComponents.rememberTextToSpeech
import com.example.beerbicep.ui.theme.my_font_1

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = hiltViewModel()
) {

    val state by viewModel.detailState.collectAsState()
    val tts = rememberTextToSpeech()
    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                (Color(0xFF121212))
            )
    ) {
        when (val resource = state.beerDetail) {
            is Resource_Class.Error<*> -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = resource.message ?: "Unknown Error",
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.onEvent(DetailEvents.RetryDetail) }) {
                        Text("Retry")
                    }
                }
            }

            is Resource_Class.Loading<*> -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            }

            is Resource_Class.Success<*> -> {
                val beer = resource.data
                if (beer != null) {
                    BeerDetailContent(

                        tts = tts,
                        beer = beer,
                        onNavigateUp = { },
                        onToggleFavorite = { viewModel.onEvent(DetailEvents.ToggleFav) }
                    )
                }
            }
        }
    }
}


@Composable
fun IngredientSubHeader(text: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            ),
            color = Color.LightGray
        )
    }
}

@Composable
fun StatItem(
    value: String,
    label: String,
    icon: ImageVector?
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.width(32.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(30.dp)
                )
            } else {
                Text(
                    text = "%",
                    color = Color.LightGray,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Light
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 17.sp,
                fontFamily = my_font_1
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = my_font_1
                ),
                color = Color.White
            )
        }
    }
}

