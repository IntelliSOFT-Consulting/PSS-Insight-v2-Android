package com.intellisoft.pss.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities =
        [
            IndicatorsData::class,
            IndicatorResponse::class,
            Submissions::class,
            Comments::class,
            Organizations::class,
            Image::class],
    version = 2,
    exportSchema = false)
@TypeConverters(Converters::class)
public abstract class PssDatabase : RoomDatabase() {

  abstract fun roomDao(): RoomDao

  companion object {
    // Singleton prevents multiple instances of database opening at the
    // same time.
    @Volatile private var INSTANCE: PssDatabase? = null

    fun getDatabase(context: Context): PssDatabase {
      val tempInstance = INSTANCE
      if (tempInstance != null) {
        return tempInstance
      }
      synchronized(this) {
        val instance =
            Room.databaseBuilder(
                    context.applicationContext, PssDatabase::class.java, "pss_database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        INSTANCE = instance
        return instance
      }
    }
  }
}
