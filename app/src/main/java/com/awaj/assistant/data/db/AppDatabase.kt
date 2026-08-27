package com.awaj.assistant.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.awaj.assistant.data.models.AppAliasEntity
import com.awaj.assistant.data.models.CommandLog
import com.awaj.assistant.data.models.RoutineEntity

@Database(
    entities = [CommandLog::class, AppAliasEntity::class, RoutineEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun commandLogDao(): CommandLogDao
    abstract fun appAliasDao(): AppAliasDao
    abstract fun routineDao(): RoutineDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "awaj_assistant_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
