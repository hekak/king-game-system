package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        DailyProgressEntity::class,
        LeaderboardEntryEntity::class,
        UserProfileEntity::class,
        DepositRequestEntity::class,
        AdminWalletConfigEntity::class,
        WithdrawRequestEntity::class,
        RegisteredAccountEntity::class,
        RegisteredDeviceEntity::class,
        CustomGameEntity::class,
        FavoriteGameEntity::class,
        SubAdminEntity::class,
        DeletedGameEntity::class
    ],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_6_7 = object : androidx.room.migration.Migration(6, 7) {
            override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
                try {
                    database.execSQL("ALTER TABLE user_profile ADD COLUMN lastDailyRewardClaimTime INTEGER NOT NULL DEFAULT 0")
                    database.execSQL("ALTER TABLE user_profile ADD COLUMN dailyRewardStreak INTEGER NOT NULL DEFAULT 0")
                    database.execSQL("ALTER TABLE user_profile ADD COLUMN totalDailyRewardsClaimed REAL NOT NULL DEFAULT 0.0")
                } catch (_: Exception) {}
            }
        }

        private val MIGRATION_7_8 = object : androidx.room.migration.Migration(7, 8) {
            override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
                try {
                    database.execSQL("ALTER TABLE user_profile ADD COLUMN requiredTurnover REAL NOT NULL DEFAULT 0.0")
                    database.execSQL("ALTER TABLE user_profile ADD COLUMN completedTurnover REAL NOT NULL DEFAULT 0.0")
                    database.execSQL("ALTER TABLE user_profile ADD COLUMN pendingTurnover REAL NOT NULL DEFAULT 0.0")
                    database.execSQL("ALTER TABLE registered_accounts ADD COLUMN requiredTurnover REAL NOT NULL DEFAULT 0.0")
                    database.execSQL("ALTER TABLE registered_accounts ADD COLUMN completedTurnover REAL NOT NULL DEFAULT 0.0")
                    database.execSQL("ALTER TABLE registered_accounts ADD COLUMN pendingTurnover REAL NOT NULL DEFAULT 0.0")
                    database.execSQL("CREATE TABLE IF NOT EXISTS deleted_games (gameId TEXT NOT NULL PRIMARY KEY, gameTitle TEXT NOT NULL, deletedAt INTEGER NOT NULL)")
                } catch (_: Exception) {}
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "super_ace_database.db"
                )
                    .addMigrations(MIGRATION_6_7, MIGRATION_7_8)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
