package com.example.beerbicep.AdditionalComponents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

/*
infinite horizontal carousel for the image slider in actual it creates 1000 pager .
reference - https://medium.com/@somgov0/how-to-build-an-auto-image-slider-in-jetpack-compose-970e07c56db3
 */


@Composable
fun BuildImageSlider(
    modifier: Modifier = Modifier
) {
    val imageUrls = listOf(
        "https://cdn.shopify.com/s/files/1/0786/2996/2024/files/lost_file_name_468_-Edit_2048x2048.jpg?v=1754653626",
        "https://sp-ao.shortpixel.ai/client/to_webp,q_glossy,ret_img,w_1920/https://wicklowwolf.com/wp-content/uploads/2024/10/COJBH-001.jpg",
        "https://www.brewer-world.com/wp-content/uploads/2024/03/nn-BW-Article-Images-Folder-2.png",
        "https://efp.brewdog.com/images/cms/blog/1426759687LEAD.jpg"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        InfiniteHorizontalCarousel(
            items = imageUrls,
            itemSpacing = 10.dp,
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) { imageUrl ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .height(180.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            )
        }
    }
}

@Composable
fun <T> InfiniteHorizontalCarousel(
    items: List<T>,
    itemSpacing: Dp,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T) -> Unit
) {
    if (items.isEmpty()) return

    val repeatedCount = 1000
    val startIndex = (repeatedCount / 2) - ((repeatedCount / 2) % items.size)

    val state = rememberPagerState(initialPage = startIndex) { repeatedCount }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            state.animateScrollToPage(state.currentPage + 1)
        }
    }

    Box(modifier = modifier) {
        HorizontalPager(
            state = state,
            pageSize = PageSize.Fill,
            contentPadding = contentPadding,
            pageSpacing = itemSpacing,
            modifier = Modifier.fillMaxWidth()
        ) { index ->
            val actualIndex = index % items.size
            val item = items[actualIndex]

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        val pageOffset =
                            ((state.currentPage - index) + state.currentPageOffsetFraction).absoluteValue
                        scaleY = lerp(
                            start = 0.85f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    }
            ) {
                itemContent(item)
            }
        }
    }
}
