package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [WaterLog::class, UserSettings::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun waterDao(): WaterDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Existing rows already configured default to 1 so returning users skip onboarding.
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE user_settings ADD COLUMN onboardingCompleted INTEGER NOT NULL DEFAULT 1"
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE user_settings ADD COLUMN quietHoursStart INTEGER NOT NULL DEFAULT 22"
                )
                db.execSQL(
                    "ALTER TABLE user_settings ADD COLUMN quietHoursEnd INTEGER NOT NULL DEFAULT 7"
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE user_settings ADD COLUMN emergencyContactPhone TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "settiflow_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
