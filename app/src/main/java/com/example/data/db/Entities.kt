package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_progress")
data class DailyProgressEntity(
    @PrimaryKey val date: String, // format YYYY-MM-DD
    val spinsCount: Int = 0,
    val totalBet: Double = 0.0,
    val totalWon: Double = 0.0,
    val highestWin: Double = 0.0,
    val freeGamesTriggered: Int = 0,
    val wildsCreated: Int = 0,
    val maxCombo: Int = 1,
    val pointsEarned: Long = 0,
    val mission1Claimed: Boolean = false,
    val mission2Claimed: Boolean = false,
    val mission3Claimed: Boolean = false,
    val mission4Claimed: Boolean = false,
    val mission5Claimed: Boolean = false
)

@Entity(tableName = "leaderboard_entries")
data class LeaderboardEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tournamentId: String = "daily_slot_tournament",
    val rank: Int,
    val playerName: String,
    val avatarEmoji: String,
    val points: Long,
    val maxWinMultiplier: Double,
    val isCurrentUser: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val playerName: String = "Player",
    val userPhone: String = "",
    val isRegistered: Boolean = false,
    val avatarEmoji: String = "👑",
    val balance: Double = 0.0,
    val totalScore: Long = 0,
    val xp: Long = 0,
    val level: Int = 1,
    val consecutiveDays: Int = 1,
    val lastActiveDate: String = "",
    val deviceId: String = "",
    val deviceModel: String = "",
    val registeredAt: Long = 0L,
    val lifetimeDeposit: Double = 0.0,
    val lifetimeWithdraw: Double = 0.0,
    val lifetimeBet: Double = 0.0,
    val lifetimeWon: Double = 0.0,
    val referredBySubAdmin: String = "",
    val lastDailyRewardClaimTime: Long = 0L,
    val dailyRewardStreak: Int = 0,
    val totalDailyRewardsClaimed: Double = 0.0,
    val requiredTurnover: Double = 0.0,
    val completedTurnover: Double = 0.0,
    val pendingTurnover: Double = 0.0
)

@Entity(tableName = "registered_accounts")
data class RegisteredAccountEntity(
    @PrimaryKey val userPhone: String,
    val playerName: String,
    val deviceId: String,
    val deviceModel: String,
    val registeredAt: Long = System.currentTimeMillis(),
    val status: String = "ACTIVE",
    val balance: Double = 0.0,
    val totalScore: Long = 0,
    val xp: Long = 0,
    val level: Int = 1,
    val avatarEmoji: String = "👑",
    val consecutiveDays: Int = 1,
    val lastActiveDate: String = "",
    val lifetimeDeposit: Double = 0.0,
    val lifetimeWithdraw: Double = 0.0,
    val lifetimeBet: Double = 0.0,
    val lifetimeWon: Double = 0.0,
    val referredBySubAdmin: String = "",
    val requiredTurnover: Double = 0.0,
    val completedTurnover: Double = 0.0,
    val pendingTurnover: Double = 0.0
)

@Entity(tableName = "registered_devices")
data class RegisteredDeviceEntity(
    @PrimaryKey val deviceId: String,
    val registeredPhone: String,
    val deviceModel: String,
    val registeredAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "deposit_requests")
data class DepositRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userPhone: String,
    val amount: Double,
    val method: String, // "NAGAD", "BKASH", "BKASH_SEND_MONEY", "ROCKET", "UPAY"
    val channel: String = "PASSPAY", // "PASSPAY", "H88PAY", "HRPAY"
    val adminWalletNumber: String,
    val trxId: String,
    val screenshotUri: String? = null,
    val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
    val rejectionReason: String? = null,
    val submittedAt: Long = System.currentTimeMillis(),
    val processedAt: Long? = null
)

@Entity(tableName = "admin_wallet_config")
data class AdminWalletConfigEntity(
    @PrimaryKey val id: Int = 1,
    val nagadWallet: String = "01303347372",
    val bkashWallet: String = "01303347372",
    val rocketWallet: String = "01303347372",
    val upayWallet: String = "01303347372",
    val minDeposit: Double = 100.0,
    val maxDeposit: Double = 20000.0,
    val websitePortalUrl: String = "https://example.com/casino",
    val isWebAppModeEnabled: Boolean = false,
    val codeProtectionActive: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "sub_admins")
data class SubAdminEntity(
    @PrimaryKey val id: String, // e.g. "SA_01" or UUID
    val name: String,
    val phone: String,
    val referralCode: String, // e.g. "AGENT01" (unique, uppercase)
    val referralLink: String = "",
    val commissionPercent: Double = 5.0,
    val status: String = "ACTIVE", // "ACTIVE", "BLOCKED"
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "withdraw_requests")
data class WithdrawRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userPhone: String,
    val amount: Double,
    val method: String, // "NAGAD", "BKASH", "ROCKET", "UPAY"
    val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
    val rejectionReason: String? = null,
    val submittedAt: Long = System.currentTimeMillis(),
    val processedAt: Long? = null
)

@Entity(tableName = "custom_games")
data class CustomGameEntity(
    @PrimaryKey val id: String,
    val title: String,
    val provider: String = "Custom Provider",
    val subtitle: String = "নতুন যুক্ত গেম",
    val gameUrlOrApi: String = "",
    val badge: String = "🆕 NEW",
    val maxWin: String = "10,000x",
    val themeColorHex: Long = 0xFF8B5CF6,
    val rtp: String = "97.0%",
    val isNewGame: Boolean = true,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorite_games")
data class FavoriteGameEntity(
    @PrimaryKey val gameId: String,
    val isFavorite: Boolean = true,
    val favoritedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "deleted_games")
data class DeletedGameEntity(
    @PrimaryKey val gameId: String,
    val gameTitle: String = "",
    val deletedAt: Long = System.currentTimeMillis()
)
