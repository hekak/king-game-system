package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfileFlow(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfile(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserProfile(profile: UserProfileEntity)

    // Daily Progress
    @Query("SELECT * FROM daily_progress WHERE date = :date")
    fun getDailyProgressFlow(date: String): Flow<DailyProgressEntity?>

    @Query("SELECT * FROM daily_progress WHERE date = :date")
    suspend fun getDailyProgress(date: String): DailyProgressEntity?

    @Query("SELECT * FROM daily_progress ORDER BY date DESC LIMIT 7")
    fun getRecentDailyProgress(): Flow<List<DailyProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDailyProgress(progress: DailyProgressEntity)

    // Leaderboard
    @Query("SELECT * FROM leaderboard_entries WHERE tournamentId = :tournamentId ORDER BY points DESC")
    fun getLeaderboardFlow(tournamentId: String = "daily_slot_tournament"): Flow<List<LeaderboardEntryEntity>>

    @Query("SELECT COUNT(*) FROM leaderboard_entries WHERE tournamentId = :tournamentId")
    suspend fun getLeaderboardCount(tournamentId: String = "daily_slot_tournament"): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboardEntries(entries: List<LeaderboardEntryEntity>)

    @Update
    suspend fun updateLeaderboardEntry(entry: LeaderboardEntryEntity)

    @Query("SELECT * FROM leaderboard_entries WHERE tournamentId = :tournamentId AND isCurrentUser = 1 LIMIT 1")
    suspend fun getCurrentUserLeaderboardEntry(tournamentId: String = "daily_slot_tournament"): LeaderboardEntryEntity?

    // Admin Operations
    @Query("DELETE FROM daily_progress WHERE date = :date")
    suspend fun clearDailyProgress(date: String)

    @Query("DELETE FROM leaderboard_entries")
    suspend fun clearAllLeaderboard()

    @Query("UPDATE user_profile SET balance = :balance WHERE id = 1")
    suspend fun setProfileBalance(balance: Double)

    @Query("UPDATE user_profile SET balance = balance + :amount WHERE id = 1")
    suspend fun addProfileBalance(amount: Double)

    @Query("UPDATE user_profile SET balance = CASE WHEN balance >= :amount THEN balance - :amount ELSE 0.0 END WHERE id = 1")
    suspend fun deductProfileBalance(amount: Double)

    @Query("UPDATE user_profile SET userPhone = :phone, isRegistered = 1, playerName = :name, deviceId = :deviceId, deviceModel = :deviceModel, registeredAt = :timestamp WHERE id = 1")
    suspend fun registerUserPhoneWithDevice(phone: String, name: String, deviceId: String, deviceModel: String, timestamp: Long)

    @Query("UPDATE user_profile SET userPhone = :phone, isRegistered = 1, playerName = :name, deviceId = :deviceId, deviceModel = :deviceModel, registeredAt = :timestamp, referredBySubAdmin = :referredBy WHERE id = 1")
    suspend fun registerUserPhoneWithDeviceAndReferral(phone: String, name: String, deviceId: String, deviceModel: String, timestamp: Long, referredBy: String)

    @Query("UPDATE user_profile SET lifetimeDeposit = lifetimeDeposit + :amount WHERE id = 1")
    suspend fun addLifetimeDeposit(amount: Double)

    @Query("UPDATE user_profile SET lifetimeWithdraw = lifetimeWithdraw + :amount WHERE id = 1")
    suspend fun addLifetimeWithdraw(amount: Double)

    @Query("UPDATE user_profile SET lifetimeBet = lifetimeBet + :bet, lifetimeWon = lifetimeWon + :won WHERE id = 1")
    suspend fun addLifetimeSpin(bet: Double, won: Double)

    // Registered Device & Account Unique Lock
    @Query("SELECT * FROM registered_accounts WHERE userPhone = :phone LIMIT 1")
    suspend fun getRegisteredAccount(phone: String): RegisteredAccountEntity?

    @Query("SELECT * FROM registered_devices WHERE deviceId = :deviceId LIMIT 1")
    suspend fun getRegisteredDevice(deviceId: String): RegisteredDeviceEntity?

    @Query("SELECT * FROM registered_accounts ORDER BY registeredAt DESC")
    fun getAllRegisteredAccountsFlow(): Flow<List<RegisteredAccountEntity>>

    @Query("SELECT * FROM registered_accounts ORDER BY registeredAt DESC")
    suspend fun getAllRegisteredAccounts(): List<RegisteredAccountEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegisteredAccount(account: RegisteredAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegisteredDevice(device: RegisteredDeviceEntity)

    @Query("UPDATE user_profile SET level = :level, xp = :xp WHERE id = 1")
    suspend fun setProfileLevel(level: Int, xp: Long)

    // Deposit Requests
    @Query("SELECT * FROM deposit_requests ORDER BY submittedAt DESC")
    fun getAllDepositRequestsFlow(): Flow<List<DepositRequestEntity>>

    @Query("SELECT * FROM deposit_requests ORDER BY submittedAt DESC")
    suspend fun getAllDepositRequests(): List<DepositRequestEntity>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM deposit_requests WHERE status = 'APPROVED'")
    suspend fun getApprovedDepositSum(): Double

    @Query("SELECT COUNT(*) FROM deposit_requests WHERE status = 'APPROVED'")
    suspend fun getApprovedDepositCount(): Int

    @Query("SELECT * FROM deposit_requests WHERE userPhone = :phone ORDER BY submittedAt DESC")
    fun getDepositRequestsByUserFlow(phone: String): Flow<List<DepositRequestEntity>>

    @Query("SELECT COUNT(*) FROM deposit_requests WHERE status = 'PENDING'")
    fun getPendingDepositCountFlow(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDepositRequest(request: DepositRequestEntity): Long

    @Query("UPDATE deposit_requests SET status = :status, processedAt = :processedAt WHERE id = :id")
    suspend fun updateDepositRequestStatus(id: Long, status: String, processedAt: Long)

    @Query("SELECT * FROM deposit_requests WHERE id = :id LIMIT 1")
    suspend fun getDepositRequestById(id: Long): DepositRequestEntity?

    // Admin Wallet Configuration
    @Query("SELECT * FROM admin_wallet_config WHERE id = 1")
    fun getWalletConfigFlow(): Flow<AdminWalletConfigEntity?>

    @Query("SELECT * FROM admin_wallet_config WHERE id = 1")
    suspend fun getWalletConfig(): AdminWalletConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateWalletConfig(config: AdminWalletConfigEntity)

    // Withdraw Requests
    @Query("SELECT * FROM withdraw_requests ORDER BY submittedAt DESC")
    fun getAllWithdrawRequestsFlow(): Flow<List<WithdrawRequestEntity>>

    @Query("SELECT * FROM withdraw_requests ORDER BY submittedAt DESC")
    suspend fun getAllWithdrawRequests(): List<WithdrawRequestEntity>

    @Query("SELECT * FROM withdraw_requests WHERE userPhone = :phone ORDER BY submittedAt DESC")
    fun getWithdrawRequestsByUserFlow(phone: String): Flow<List<WithdrawRequestEntity>>

    @Query("SELECT COUNT(*) FROM withdraw_requests WHERE status = 'PENDING'")
    fun getPendingWithdrawCountFlow(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawRequest(request: WithdrawRequestEntity): Long

    @Query("UPDATE withdraw_requests SET status = :status, processedAt = :processedAt, rejectionReason = :reason WHERE id = :id")
    suspend fun updateWithdrawRequestStatus(id: Long, status: String, processedAt: Long, reason: String? = null)

    @Query("SELECT * FROM withdraw_requests WHERE id = :id LIMIT 1")
    suspend fun getWithdrawRequestById(id: Long): WithdrawRequestEntity?

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM withdraw_requests WHERE status = 'APPROVED'")
    suspend fun getApprovedWithdrawSum(): Double

    @Query("SELECT COUNT(*) FROM withdraw_requests WHERE status = 'APPROVED'")
    suspend fun getApprovedWithdrawCount(): Int

    // Custom Games (Admin added games / links / APIs)
    @Query("SELECT * FROM custom_games ORDER BY createdAt DESC")
    fun getAllCustomGamesFlow(): Flow<List<CustomGameEntity>>

    @Query("SELECT * FROM custom_games WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getActiveCustomGamesFlow(): Flow<List<CustomGameEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomGame(game: CustomGameEntity)

    @Query("DELETE FROM custom_games WHERE id = :id")
    suspend fun deleteCustomGame(id: String)

    @Query("UPDATE custom_games SET isActive = :isActive WHERE id = :id")
    suspend fun toggleCustomGameActive(id: String, isActive: Boolean)

    // Favorite Games (User Starred Games)
    @Query("SELECT gameId FROM favorite_games WHERE isFavorite = 1")
    fun getFavoriteGameIdsFlow(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setGameFavorite(entity: FavoriteGameEntity)

    @Query("SELECT COUNT(*) FROM favorite_games WHERE gameId = :gameId")
    suspend fun isGameFavoriteCount(gameId: String): Int

    @Query("DELETE FROM favorite_games WHERE gameId = :gameId")
    suspend fun removeGameFavorite(gameId: String)

    // Sub-Admins Management
    @Query("SELECT * FROM sub_admins ORDER BY createdAt DESC")
    fun getAllSubAdminsFlow(): Flow<List<SubAdminEntity>>

    @Query("SELECT * FROM sub_admins ORDER BY createdAt DESC")
    suspend fun getAllSubAdmins(): List<SubAdminEntity>

    @Query("SELECT * FROM sub_admins WHERE referralCode = :code LIMIT 1")
    suspend fun getSubAdminByReferralCode(code: String): SubAdminEntity?

    @Query("SELECT * FROM sub_admins WHERE id = :id LIMIT 1")
    suspend fun getSubAdminById(id: String): SubAdminEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubAdmin(subAdmin: SubAdminEntity)

    @Update
    suspend fun updateSubAdmin(subAdmin: SubAdminEntity)

    @Query("DELETE FROM sub_admins WHERE id = :id")
    suspend fun deleteSubAdmin(id: String)

    @Query("UPDATE sub_admins SET status = :status WHERE id = :id")
    suspend fun updateSubAdminStatus(id: String, status: String)

    // Sub-Admin related users
    @Query("SELECT * FROM registered_accounts WHERE referredBySubAdmin = :subAdminCode ORDER BY registeredAt DESC")
    fun getRegisteredAccountsBySubAdminFlow(subAdminCode: String): Flow<List<RegisteredAccountEntity>>

    @Query("SELECT * FROM registered_accounts WHERE referredBySubAdmin = :subAdminCode ORDER BY registeredAt DESC")
    suspend fun getRegisteredAccountsBySubAdmin(subAdminCode: String): List<RegisteredAccountEntity>

    @Query("SELECT COUNT(*) FROM registered_accounts WHERE referredBySubAdmin = :subAdminCode")
    suspend fun getSubAdminPlayerCount(subAdminCode: String): Int

    @Query("UPDATE admin_wallet_config SET websitePortalUrl = :url, isWebAppModeEnabled = :isWebMode, codeProtectionActive = :codeProtect WHERE id = 1")
    suspend fun updateWebsiteAndSecurityConfig(url: String, isWebMode: Boolean, codeProtect: Boolean)

    // Turnover Requirement Operations
    @Query("UPDATE user_profile SET requiredTurnover = requiredTurnover + :amount, pendingTurnover = pendingTurnover + :amount WHERE id = 1")
    suspend fun addTurnoverRequirement(amount: Double)

    @Query("UPDATE user_profile SET completedTurnover = completedTurnover + :betAmount, pendingTurnover = CASE WHEN pendingTurnover >= :betAmount THEN pendingTurnover - :betAmount ELSE 0.0 END WHERE id = 1")
    suspend fun recordTurnoverBet(betAmount: Double)

    @Query("UPDATE user_profile SET requiredTurnover = :required, completedTurnover = :completed, pendingTurnover = :pending WHERE id = 1")
    suspend fun setTurnoverState(required: Double, completed: Double, pending: Double)

    // Deleted Games Operations (Admin Game Management)
    @Query("SELECT * FROM deleted_games ORDER BY deletedAt DESC")
    fun getAllDeletedGamesFlow(): Flow<List<DeletedGameEntity>>

    @Query("SELECT gameId FROM deleted_games")
    fun getDeletedGameIdsFlow(): Flow<List<String>>

    @Query("SELECT gameId FROM deleted_games")
    suspend fun getDeletedGameIds(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeletedGame(deletedGame: DeletedGameEntity)

    @Query("DELETE FROM deleted_games WHERE gameId = :gameId")
    suspend fun restoreDeletedGame(gameId: String)
}

