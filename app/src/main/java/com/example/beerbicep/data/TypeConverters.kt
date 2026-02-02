package com.example.beerbicep.data

import androidx.room.TypeConverter
import com.example.beerbicep.data.local.IngredientsEntity
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class BeerConverters {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @TypeConverter
    fun fromStringList(value: String): List<String>? {
        val listType = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter = moshi.adapter<List<String>>(listType)
        return adapter.fromJson(value)
    }

    @TypeConverter
    fun toStringList(list: List<String>): String {
        val listType = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter = moshi.adapter<List<String>>(listType)
        return adapter.toJson(list)
    }

    @TypeConverter
    fun fromIngredientsEntity(value: IngredientsEntity): String {
        val adapter = moshi.adapter(IngredientsEntity::class.java)
        return adapter.toJson(value)
    }

    @TypeConverter
    fun toIngredientsEntity(value: String): IngredientsEntity? {
        val adapter = moshi.adapter(IngredientsEntity::class.java)
        return adapter.fromJson(value)
    }
}