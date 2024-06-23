package com.example.spirala1

import androidx.room.Database
import androidx.room.TypeConverters
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Biljka::class, BiljkaBitmap::class], version = 1)
@TypeConverters(Converters::class)
abstract class BiljkaDatabase : RoomDatabase() {
    abstract fun biljkaDao(): BiljkaDAO

    companion object {
        @Volatile
        private var INSTANCE: BiljkaDatabase? = null

        fun getDatabase(context: Context): BiljkaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BiljkaDatabase::class.java,
                    "biljke-db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}