package com.SharaSpot.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.SharaSpot.core.database.dao.CountriesDao
import com.SharaSpot.core.database.model.CountryEntity

@Database(
    entities = [CountryEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun countriesDao(): CountriesDao
}