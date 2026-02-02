package com.example.beerbicep.data.mapper

import com.example.beerbicep.data.local.AmountEntity
import com.example.beerbicep.data.local.BeerEntity
import com.example.beerbicep.data.local.HopEntity
import com.example.beerbicep.data.local.IngredientsEntity
import com.example.beerbicep.data.local.MaltEntity
import com.example.beerbicep.domain.Amount
import com.example.beerbicep.domain.BeerDomain
import com.example.beerbicep.domain.Hop
import com.example.beerbicep.domain.Ingredients
import com.example.beerbicep.domain.Malt


fun BeerDomain.toBeerEntity(): BeerEntity {
    return BeerEntity(
        id = id,
        name = name,
        tagLine = tagLine,
        description = description,
        alcoholByVolume = alcoholByVolume,
        bitternessUnit = bitternessUnit,
        imageUrl = imageUrl,
        bestFoods = bestFoods,
        ingredients = ingredients.toIngredientsEntity(),
        isFavourite = isFavorite
    )
}

fun Amount.toAmountEntity(): AmountEntity {
    return AmountEntity(
        unit = unit,
        value = value
    )
}

// Hop & Malt
fun Hop.toHopEntity(): HopEntity {
    return HopEntity(
        add = add,
        amount = amount.toAmountEntity(), // <-- Use the mapper
        attribute = attribute,
        name = name
    )
}

fun Malt.toMaltEntity(): MaltEntity {
    return MaltEntity(
        amount = amount.toAmountEntity(), // <-- Use the mapper
        name = name
    )
}

// Ingredients
fun Ingredients.toIngredientsEntity(): IngredientsEntity {
    return IngredientsEntity(
        hops = hops.map { it.toHopEntity() }, // <-- Map the list
        malt = malt.map { it.toMaltEntity() }, // <-- Map the list
        yeast = yeast
    )
}