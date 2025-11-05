package com.SharaSpot.core.database.di


import android.content.Context
import androidx.room.Room
import com.SharaSpot.core.database.AppDatabase
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.SharaSpot.core.database")
class DatabaseModule


@Single
fun provideAppDatabase(
    applicationContext: Context
): AppDatabase {
    return Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java,
        "SharaSpot-database"
    ).fallbackToDestructiveMigration(true).build()
}