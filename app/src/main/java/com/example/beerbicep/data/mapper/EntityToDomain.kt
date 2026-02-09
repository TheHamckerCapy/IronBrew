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

fun BeerEntity.toBeerDomain(): BeerDomain {
    return BeerDomain(
        id = id,
        name = name,
        tagLine = tagLine,
        description = description,
        alcoholByVolume = alcoholByVolume,
        bitternessUnit = bitternessUnit,
        imageUrl = imageUrl,
        bestFoods = bestFoods,
        ingredients = ingredients.toIngredients(),
        isFavorite = isFavourite
    )
}

fun AmountEntity.toAmount(): Amount {
    return Amount(
        unit = unit,
        value = value
    )
}


fun HopEntity.toHop(): Hop {
    return Hop(
        add = add,
        amount = amount.toAmount(),
        attribute = attribute,
        name = name
    )
}

fun MaltEntity.toMalt(): Malt {
    return Malt(
        amount = amount.toAmount(),
        name = name
    )
}


fun IngredientsEntity.toIngredients(): Ingredients {
    return Ingredients(
        hops = hops.map { it.toHop() },
        malt = malt.map { it.toMalt() },
        yeast = yeast
    )
}