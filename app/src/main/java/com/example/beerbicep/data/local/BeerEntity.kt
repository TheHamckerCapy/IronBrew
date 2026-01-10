package com.example.beerbicep.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

import androidx.room.TypeConverters
import com.example.beerbicep.data.BeerConverters


@Entity(tableName = "beers")
@TypeConverters(BeerConverters::class)
data class BeerEntity(
@PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val tagLine: String,
    val description: String,
    val alcoholByVolume: Double,
    val bitternessUnit: Double?,
    val imageUrl: String?,
    val bestFoods: List<String>,
    val ingredients: IngredientsEntity,
    val isFavourite: Boolean = false,
)


data class IngredientsEntity(
    val hops: List<HopEntity>,
    val malt: List<MaltEntity>,
    val yeast: String?
)



data class HopEntity(
    val add: String,
    val amount: AmountEntity,
    val attribute: String,
    val name: String
)

data class MaltEntity(
    val amount: AmountEntity,
    val name: String
)

data class AmountEntity(
    val unit: String,
    val value: Double
)
