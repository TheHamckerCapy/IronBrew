package com.example.beerbicep.data.remote

import com.squareup.moshi.Json

data class BeerDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "tagline") val tagLine: String,
    @Json(name = "description") val description: String,
    @Json(name = "abv") val alcoholByVolume: Double,
    @Json(name = "ibu") val bitternessUnit: Double?,
    @Json(name = "image") val imageUrl: String?,
    @Json(name = "food_pairing") val bestFoods: List<String>,
    @Json(name = "ingredients") val ingredients: IngredientsDto,
)


data class IngredientsDto(
    val hops: List<HopDto>,
    val malt: List<MaltDto>,
    val yeast: String?
)


data class HopDto(
    val add: String,
    val amount: AmountDto,
    val attribute: String,
    val name: String
)

data class MaltDto(
    val amount: AmountDto,
    val name: String
)

data class AmountDto(
    val unit: String,
    val value: Double
)
