package com.example.beerbicep.data.mapper

import com.example.beerbicep.Constant.IMAGE_URL
import com.example.beerbicep.data.local.AmountEntity
import com.example.beerbicep.data.local.BeerEntity
import com.example.beerbicep.data.local.HopEntity
import com.example.beerbicep.data.local.IngredientsEntity
import com.example.beerbicep.data.local.MaltEntity
import com.example.beerbicep.data.remote.AmountDto
import com.example.beerbicep.data.remote.BeerDto
import com.example.beerbicep.data.remote.HopDto
import com.example.beerbicep.data.remote.IngredientsDto
import com.example.beerbicep.data.remote.MaltDto

fun BeerDto.toBeerEntity(): BeerEntity {
    val formattedId = "%03d".format(id)

    val constructedImageUrl = "$IMAGE_URL/$formattedId.png"
    return BeerEntity(
        id = id,
        name = name,
        tagLine = tagLine,
        description = description,
        alcoholByVolume = alcoholByVolume,
        bitternessUnit = bitternessUnit,
        imageUrl = constructedImageUrl,
        bestFoods = bestFoods,
        ingredients = ingredients.toIngredientsEntity(),

        )
}

fun AmountDto.toAmountEntity(): AmountEntity {
    return AmountEntity(
        unit = unit,
        value = value
    )
}

// Hop & Malt
fun HopDto.toHopEntity(): HopEntity {
    return HopEntity(
        add = add,
        amount = amount.toAmountEntity(), // <-- Use the mapper
        attribute = attribute,
        name = name
    )
}

fun MaltDto.toMaltEntity(): MaltEntity {
    return MaltEntity(
        amount = amount.toAmountEntity(), // <-- Use the mapper
        name = name
    )
}

// Ingredients
fun IngredientsDto.toIngredientsEntity(): IngredientsEntity {
    return IngredientsEntity(
        hops = hops.map { it.toHopEntity() }, // <-- Map the list
        malt = malt.map { it.toMaltEntity() }, // <-- Map the list
        yeast = yeast
    )
}