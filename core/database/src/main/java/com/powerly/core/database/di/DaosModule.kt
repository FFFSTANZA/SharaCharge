package com.SharaSpot.core.database.di


import com.SharaSpot.core.database.AppDatabase
import com.SharaSpot.core.database.dao.CountriesDao
import org.koin.core.annotation.Single

@Single
fun provideCountriesDao(
    database: AppDatabase
): CountriesDao {
    return database.countriesDao()
}