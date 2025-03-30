package com.example.nutripal.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.nutripal.data.local.dao.FoodEntryDao
import com.example.nutripal.data.local.entity.FoodEntry
import java.util.Date

@Database(
    entities = [FoodEntry::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverters::class)
abstract class NutriPalDatabase : RoomDatabase() {
    abstract fun foodEntryDao(): FoodEntryDao
}

class DateConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}