package com.example.beerbicep.domain



data class BeerDomain(

    val id: Int,
    val name: String,
    val tagLine: String,
    val description: String,
    val alcoholByVolume: Double,
    val bitternessUnit: Double?,
    val imageUrl: String?,
    val bestFoods: List<String>,
    val ingredients: Ingredients,
    val isFavorite: Boolean,
) {

}


data class Ingredients(
    val hops: List<Hop>,
    val malt: List<Malt>,
    val yeast: String?
)



data class Hop(
    val add: String,
    val amount: Amount,
    val attribute: String,
    val name: String
)

data class Malt(
    val amount: Amount,
    val name: String
)

data class Amount(
    val unit: String,
    val value: Double
)
