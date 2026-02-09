package com.example.beerbicep.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.beerbicep.data.BeerConverters


@Database(entities = [BeerEntity::class, RemoteKey::class], version = 1, exportSchema = false)
@TypeConverters(BeerConverters::class)//from data/type-converters
abstract class BeerDb : RoomDatabase() {
    abstract val beerDao: BeerDao
    abstract val remoteKeyDao: RemoteKeyDao// for pagging's remote-mediator
}