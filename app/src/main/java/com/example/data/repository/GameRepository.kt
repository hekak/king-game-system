package com.example.data.repository

import com.example.data.db.AdminWalletConfigEntity
import com.example.data.db.AppDao
import com.example.data.db.DailyProgressEntity
import com.example.data.db.DepositRequestEntity
import com.example.data.db.LeaderboardEntryEntity
import com.example.data.db.RegisteredAccountEntity
import com.example.data.db.RegisteredDeviceEntity
import com.example.data.db.UserProfileEntity
import com.example.data.db.WithdrawRequestEntity
import com.example.data.db.CustomGameEntity
import com.example.data.db.DeletedGameEntity
import com.example.data.db.FavoriteGameEntity
import com.example.data.db.SubAdminEntity
import com.example.data.model.DailyMission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max

class GameRepository(private val appDao: AppDao) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun getTodayDateString(): String = dateFormat.format(Date())

    val userProfileFlow: Flow<UserProfileEntity> = appDao.getUserProfileFlow().map { profile ->
        profile ?: UserProfileEntity()
    }

    fun getTodayProgressFlow(): Flow<DailyProgressEntity> =
        appDao.getDailyProgressFlow(getTodayDateString()).map { progress ->
            progress ?: DailyProgressEntity(date = getTodayDateString())
        }

    val leaderboardFlow: Flow<List<LeaderboardEntryEntity>> = appDao.getLeaderboardFlow()

    val walletConfigFlow: Flow<AdminWalletConfigEntity> = appDao.getWalletConfigFlow().map { config ->
        config ?: AdminWalletConfigEntity(
            id = 1,
            nagadWallet = "01303347372",
            bkashWallet = "01303347372",
            rocketWallet = "01303347372",
            upayWallet = "01303347372",
            minDeposit = 100.0,
            maxDeposit = 20000.0
        )
    }

    val allDepositRequestsFlow: Flow<List<DepositRequestEntity>> = appDao.getAllDepositRequestsFlow()

    fun getDepositRequestsByUserFlow(phone: String): Flow<List<DepositRequestEntity>> =
        appDao.getDepositRequestsByUserFlow(phone)

    val pendingDepositCountFlow: Flow<Int> = appDao.getPendingDepositCountFlow()

    val allWithdrawRequestsFlow: Flow<List<WithdrawRequestEntity>> = appDao.getAllWithdrawRequestsFlow()

    fun getWithdrawRequestsByUserFlow(phone: String): Flow<List<WithdrawRequestEntity>> =
        appDao.getWithdrawRequestsByUserFlow(phone)

    val pendingWithdrawCountFlow: Flow<Int> = appDao.getPendingWithdrawCountFlow()

    suspend fun initializeIfEmpty() = withContext(Dispatchers.IO) {
        val today = getTodayDateString()
        var profile = appDao.getUserProfile()
        if (profile == null) {
            profile = UserProfileEntity(
                id = 1,
                playerName = "Player",
                userPhone = "",
                isRegistered = false,
                avatarEmoji = "👑",
                balance = 0.0,
                totalScore = 0,
                xp = 0,
                level = 1,
                consecutiveDays = 1,
                lastActiveDate = today
            )
            appDao.insertOrUpdateUserProfile(profile)
        } else {
            // Check approved deposits to strictly respect initial 0 balance rule
            val approvedSum = appDao.getApprovedDepositSum()
            val todayProgress = appDao.getDailyProgress(today)
            val totalSpins = todayProgress?.spinsCount ?: 0
            if (approvedSum == 0.0 && profile.totalDailyRewardsClaimed == 0.0) {
                // If user has never deposited and never claimed daily reward, balance must be 0
                if (profile.balance != 0.0) {
                    profile = profile.copy(balance = 0.0)
                    appDao.insertOrUpdateUserProfile(profile)
                }
            } else if (totalSpins == 0 && profile.totalDailyRewardsClaimed == 0.0) {
                // If user deposited but hasn't played spins yet, ensure balance equals deposited amount exactly
                if (profile.balance != approvedSum) {
                    profile = profile.copy(balance = approvedSum)
                    appDao.insertOrUpdateUserProfile(profile)
                }
            }

            if (profile.lastActiveDate != today) {
                // Check streak
                val newStreak = profile.consecutiveDays + 1
                profile = profile.copy(consecutiveDays = newStreak, lastActiveDate = today)
                appDao.insertOrUpdateUserProfile(profile)
            }
        }

        // Calibrate turnover requirement if needed (deposits + bonuses must be played before withdrawal)
        val currentProfile = appDao.getUserProfile()
        if (currentProfile != null) {
            if (currentProfile.requiredTurnover == 0.0 && currentProfile.pendingTurnover == 0.0) {
                val totalTurnoverSource = currentProfile.lifetimeDeposit + currentProfile.totalDailyRewardsClaimed
                val totalPlayed = currentProfile.lifetimeBet
                if (totalTurnoverSource > 0.0) {
                    val remaining = (totalTurnoverSource - totalPlayed).coerceAtLeast(0.0)
                    appDao.setTurnoverState(
                        required = totalTurnoverSource,
                        completed = totalPlayed,
                        pending = remaining
                    )
                } else if (currentProfile.balance > 0.0) {
                    appDao.setTurnoverState(
                        required = currentProfile.balance,
                        completed = 0.0,
                        pending = currentProfile.balance
                    )
                }
            }
        }

        var walletConfig = appDao.getWalletConfig()
        if (walletConfig == null) {
            walletConfig = AdminWalletConfigEntity(
                id = 1,
                nagadWallet = "01303347372",
                bkashWallet = "01303347372",
                rocketWallet = "01303347372",
                upayWallet = "01303347372",
                minDeposit = 100.0,
                maxDeposit = 20000.0
            )
            appDao.insertOrUpdateWalletConfig(walletConfig)
        }

        var todayProgress = appDao.getDailyProgress(today)
        if (todayProgress == null) {
            todayProgress = DailyProgressEntity(date = today)
            appDao.insertOrUpdateDailyProgress(todayProgress)
        }

        val lbCount = appDao.getLeaderboardCount()
        if (lbCount == 0) {
            val initialLeaderboard = listOf(
                LeaderboardEntryEntity(rank = 1, playerName = "DragonKing", avatarEmoji = "🐉", points = 88450, maxWinMultiplier = 420.0),
                LeaderboardEntryEntity(rank = 2, playerName = "VegasQueen", avatarEmoji = "💎", points = 65200, maxWinMultiplier = 280.0),
                LeaderboardEntryEntity(rank = 3, playerName = "GoldenAce", avatarEmoji = "🃏", points = 51900, maxWinMultiplier = 210.0),
                LeaderboardEntryEntity(rank = 4, playerName = "LuckyAce (You)", avatarEmoji = "👑", points = profile.totalScore, maxWinMultiplier = 35.0, isCurrentUser = true),
                LeaderboardEntryEntity(rank = 5, playerName = "JackpotHunter", avatarEmoji = "💰", points = 44100, maxWinMultiplier = 160.0),
                LeaderboardEntryEntity(rank = 6, playerName = "CardMaster", avatarEmoji = "♠️", points = 37800, maxWinMultiplier = 140.0),
                LeaderboardEntryEntity(rank = 7, playerName = "HighRoller88", avatarEmoji = "🔥", points = 31200, maxWinMultiplier = 115.0),
                LeaderboardEntryEntity(rank = 8, playerName = "CasinoStar", avatarEmoji = "⭐", points = 25600, maxWinMultiplier = 90.0),
                LeaderboardEntryEntity(rank = 9, playerName = "RoyalFlush", avatarEmoji = "🏰", points = 19400, maxWinMultiplier = 75.0),
                LeaderboardEntryEntity(rank = 10, playerName = "SpinWizard", avatarEmoji = "🪄", points = 14200, maxWinMultiplier = 50.0)
            )
            appDao.insertLeaderboardEntries(initialLeaderboard)
        }

        // Initialize default Sub-Admins / Agents if none exist
        val existingSubAdmins = appDao.getAllSubAdmins()
        if (existingSubAdmins.isEmpty()) {
            val portalUrl = walletConfig.websitePortalUrl.ifBlank { "https://royalslots-bd.com" }
            val defaultSubAdmins = listOf(
                SubAdminEntity(
                    id = "SA_101",
                    name = "রফিক হাসান (ঢাকা এজেন্ট)",
                    phone = "01711223344",
                    referralCode = "AGENT01",
                    referralLink = if (portalUrl.endsWith("/")) "${portalUrl}?ref=AGENT01" else "${portalUrl}/?ref=AGENT01",
                    commissionPercent = 5.0,
                    status = "ACTIVE",
                    notes = "ঢাকা রিজিয়ন মাস্টার সাব-এডমিন",
                    createdAt = System.currentTimeMillis() - 86400000L * 5
                ),
                SubAdminEntity(
                    id = "SA_102",
                    name = "তানভীর আহমেদ (চট্টগ্রাম এজেন্ট)",
                    phone = "01822334455",
                    referralCode = "VIP77",
                    referralLink = if (portalUrl.endsWith("/")) "${portalUrl}?ref=VIP77" else "${portalUrl}/?ref=VIP77",
                    commissionPercent = 6.0,
                    status = "ACTIVE",
                    notes = "চট্টগ্রাম ও খুলনা জোন সুপার এজেন্ট",
                    createdAt = System.currentTimeMillis() - 86400000L * 2
                )
            )
            defaultSubAdmins.forEach { appDao.insertSubAdmin(it) }
        }
    }

    suspend fun registerUser(phone: String, name: String) = withContext(Dispatchers.IO) {
        val finalName = if (name.isNotBlank()) name.trim() else "User_${phone.takeLast(4)}"
        val now = System.currentTimeMillis()
        appDao.registerUserPhoneWithDevice(phone.trim(), finalName, "device_legacy", "Android", now)
    }

    suspend fun registerUserWithDevice(
        phone: String,
        name: String,
        deviceId: String,
        deviceModel: String,
        subAdminReferralCode: String = ""
    ): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val cleanPhone = phone.trim()
        val cleanDeviceId = deviceId.trim()
        val cleanRef = subAdminReferralCode.trim().uppercase()
        val finalName = if (name.isNotBlank()) name.trim() else "User_${cleanPhone.takeLast(4)}"

        // 1. Check if phone is already registered to a different device
        val existingAccount = appDao.getRegisteredAccount(cleanPhone)
        if (existingAccount != null && existingAccount.deviceId != cleanDeviceId) {
            return@withContext Pair(
                false,
                "এই মোবাইল নম্বর (${cleanPhone}) দিয়ে ইতিমধ্যে একটি অ্যাকাউন্ট তৈরি করা হয়েছে! একই নম্বর দিয়ে একাধিক ডিভাইসে অ্যাকাউন্ট তৈরি করা সম্পূর্ণ নিষিদ্ধ।"
            )
        }

        // 2. Check if this device already registered a different phone
        val existingDevice = appDao.getRegisteredDevice(cleanDeviceId)
        if (existingDevice != null && existingDevice.registeredPhone != cleanPhone) {
            return@withContext Pair(
                false,
                "এই ফোন (${deviceModel}) দিয়ে ইতিমধ্যে ${existingDevice.registeredPhone} নম্বরে একটি অ্যাকাউন্ট তৈরি করা হয়েছে! প্রতিটি ডিভাইস দিয়ে শুধুমাত্র ১টি অ্যাকাউন্ট তৈরি করার নিয়ম রয়েছে।"
            )
        }

        // 3. Validate Sub-Admin Referral Code if entered
        var matchedRefCode = ""
        if (cleanRef.isNotBlank()) {
            val subAdmin = appDao.getSubAdminByReferralCode(cleanRef)
            if (subAdmin != null) {
                matchedRefCode = subAdmin.referralCode
            } else {
                matchedRefCode = cleanRef
            }
        }

        // 4. Register account & lock device
        val now = System.currentTimeMillis()
        val currentProfile = appDao.getUserProfile() ?: UserProfileEntity()
        appDao.insertRegisteredAccount(
            RegisteredAccountEntity(
                userPhone = cleanPhone,
                playerName = finalName,
                deviceId = cleanDeviceId,
                deviceModel = deviceModel,
                registeredAt = now,
                balance = currentProfile.balance,
                totalScore = currentProfile.totalScore,
                xp = currentProfile.xp,
                level = currentProfile.level,
                avatarEmoji = currentProfile.avatarEmoji,
                consecutiveDays = currentProfile.consecutiveDays,
                lastActiveDate = currentProfile.lastActiveDate,
                lifetimeDeposit = currentProfile.lifetimeDeposit,
                lifetimeWithdraw = currentProfile.lifetimeWithdraw,
                lifetimeBet = currentProfile.lifetimeBet,
                lifetimeWon = currentProfile.lifetimeWon,
                referredBySubAdmin = matchedRefCode,
                requiredTurnover = currentProfile.requiredTurnover,
                completedTurnover = currentProfile.completedTurnover,
                pendingTurnover = currentProfile.pendingTurnover
            )
        )
        appDao.insertRegisteredDevice(
            RegisteredDeviceEntity(
                deviceId = cleanDeviceId,
                registeredPhone = cleanPhone,
                deviceModel = deviceModel,
                registeredAt = now
            )
        )
        if (matchedRefCode.isNotBlank()) {
            appDao.registerUserPhoneWithDeviceAndReferral(cleanPhone, finalName, cleanDeviceId, deviceModel, now, matchedRefCode)
        } else {
            appDao.registerUserPhoneWithDevice(cleanPhone, finalName, cleanDeviceId, deviceModel, now)
        }
        Pair(true, "অভিনন্দন! আপনার অ্যাকাউন্ট ও ডিভাইস সফলভাবে লক ও ভেরিফাই করা হয়েছে।")
    }

    suspend fun loginOrRecoverUserWithPhone(
        phone: String,
        deviceId: String,
        deviceModel: String
    ): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val cleanPhone = phone.trim()
        val cleanDeviceId = deviceId.trim()
        val existingAccount = appDao.getRegisteredAccount(cleanPhone)
        if (existingAccount == null) {
            return@withContext Pair(
                false,
                "এই মোবাইল নম্বর (${cleanPhone}) দিয়ে পূর্বে কোনো অ্যাকাউন্ট পাওয়া যায়নি। অনুগ্রহ করে 'নতুন রেজিস্ট্রেশন' ট্যাবে গিয়ে অ্যাকাউন্ট তৈরি করুন।"
            )
        }

        // Restore entire player profile from the saved record!
        val restoredProfile = UserProfileEntity(
            id = 1,
            playerName = existingAccount.playerName,
            userPhone = cleanPhone,
            isRegistered = true,
            avatarEmoji = existingAccount.avatarEmoji.ifBlank { "👑" },
            balance = existingAccount.balance,
            totalScore = existingAccount.totalScore,
            xp = existingAccount.xp,
            level = max(1, existingAccount.level),
            consecutiveDays = max(1, existingAccount.consecutiveDays),
            lastActiveDate = getTodayDateString(),
            deviceId = cleanDeviceId,
            deviceModel = deviceModel,
            registeredAt = existingAccount.registeredAt,
            lifetimeDeposit = existingAccount.lifetimeDeposit,
            lifetimeWithdraw = existingAccount.lifetimeWithdraw,
            lifetimeBet = existingAccount.lifetimeBet,
            lifetimeWon = existingAccount.lifetimeWon,
            requiredTurnover = existingAccount.requiredTurnover,
            completedTurnover = existingAccount.completedTurnover,
            pendingTurnover = existingAccount.pendingTurnover
        )
        appDao.insertOrUpdateUserProfile(restoredProfile)

        // Relink to current device
        appDao.insertRegisteredAccount(
            existingAccount.copy(
                deviceId = cleanDeviceId,
                deviceModel = deviceModel
            )
        )
        appDao.insertRegisteredDevice(
            RegisteredDeviceEntity(
                deviceId = cleanDeviceId,
                registeredPhone = cleanPhone,
                deviceModel = deviceModel,
                registeredAt = System.currentTimeMillis()
            )
        )

        val balanceDisplay = if (existingAccount.balance % 1.0 == 0.0) {
            String.format(Locale.US, "%,d", existingAccount.balance.toLong())
        } else {
            String.format(Locale.US, "%,.2f", existingAccount.balance)
        }

        Pair(
            true,
            "স্বাগতম ${existingAccount.playerName}! আপনার পূর্বের অ্যাকাউন্ট ও 🪙 ${balanceDisplay} ব্যালেন্স সফলভাবে ফিরিয়ে আনা হয়েছে।"
        )
    }

    suspend fun syncRegisteredAccountSnapshot() = withContext(Dispatchers.IO) {
        val profile = appDao.getUserProfile() ?: return@withContext
        if (profile.userPhone.isNotBlank() && profile.isRegistered) {
            val acc = appDao.getRegisteredAccount(profile.userPhone)
            if (acc != null) {
                appDao.insertRegisteredAccount(
                    acc.copy(
                        playerName = profile.playerName,
                        balance = profile.balance,
                        totalScore = profile.totalScore,
                        xp = profile.xp,
                        level = profile.level,
                        avatarEmoji = profile.avatarEmoji,
                        consecutiveDays = profile.consecutiveDays,
                        lastActiveDate = profile.lastActiveDate,
                        lifetimeDeposit = profile.lifetimeDeposit,
                        lifetimeWithdraw = profile.lifetimeWithdraw,
                        lifetimeBet = profile.lifetimeBet,
                        lifetimeWon = profile.lifetimeWon,
                        requiredTurnover = profile.requiredTurnover,
                        completedTurnover = profile.completedTurnover,
                        pendingTurnover = profile.pendingTurnover
                    )
                )
            }
        }
    }

    val allRegisteredAccountsFlow: Flow<List<RegisteredAccountEntity>> = appDao.getAllRegisteredAccountsFlow()
    val allRegisteredAccounts: Flow<List<RegisteredAccountEntity>> get() = allRegisteredAccountsFlow

    suspend fun getApprovedDepositSum(): Double = withContext(Dispatchers.IO) {
        appDao.getApprovedDepositSum()
    }

    suspend fun getApprovedWithdrawSum(): Double = withContext(Dispatchers.IO) {
        appDao.getApprovedWithdrawSum()
    }

    suspend fun getApprovedDepositCount(): Int = withContext(Dispatchers.IO) {
        appDao.getApprovedDepositCount()
    }

    suspend fun getApprovedWithdrawCount(): Int = withContext(Dispatchers.IO) {
        appDao.getApprovedWithdrawCount()
    }

    suspend fun submitDepositRequest(
        userPhone: String,
        amount: Double,
        method: String,
        channel: String = "PASSPAY",
        adminWalletNumber: String,
        trxId: String,
        screenshotUri: String? = null
    ): Long = withContext(Dispatchers.IO) {
        val request = DepositRequestEntity(
            userPhone = userPhone,
            amount = amount,
            method = method,
            channel = channel,
            adminWalletNumber = adminWalletNumber,
            trxId = trxId,
            screenshotUri = screenshotUri,
            status = "PENDING",
            submittedAt = System.currentTimeMillis()
        )
        appDao.insertDepositRequest(request)
    }

    suspend fun approveDepositRequest(requestId: Long): Boolean = withContext(Dispatchers.IO) {
        val req = appDao.getDepositRequestById(requestId) ?: return@withContext false
        if (req.status != "PENDING") return@withContext false
        appDao.updateDepositRequestStatus(requestId, "APPROVED", System.currentTimeMillis())
        appDao.addProfileBalance(req.amount)
        appDao.addLifetimeDeposit(req.amount)
        appDao.addTurnoverRequirement(req.amount)
        syncRegisteredAccountSnapshot()
        true
    }

    suspend fun rejectDepositRequest(requestId: Long, reason: String = "Rejected by Admin"): Boolean = withContext(Dispatchers.IO) {
        val req = appDao.getDepositRequestById(requestId) ?: return@withContext false
        if (req.status != "PENDING") return@withContext false
        appDao.updateDepositRequestStatus(requestId, "REJECTED", System.currentTimeMillis())
        true
    }

    suspend fun submitWithdrawRequest(
        userPhone: String,
        amount: Double,
        method: String
    ): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        if (amount <= 0) {
            return@withContext Pair(false, "উইথড্রো পয়েন্ট অবশ্যই শূন্যের চেয়ে বেশি হতে হবে।")
        }
        val profile = appDao.getUserProfile()
        val currentBalance = profile?.balance ?: 0.0
        if (currentBalance < amount) {
            return@withContext Pair(false, "আপনার ব্যালেন্সে পর্যাপ্ত পয়েন্ট নেই। বর্তমান ব্যালেন্স: ${String.format(Locale.US, "%,.2f", currentBalance)}")
        }

        // Turnover Wagering Rule: User must wager through deposited & bonus points before withdrawing
        val pendingTurnover = profile?.pendingTurnover ?: 0.0
        if (pendingTurnover > 0.0) {
            val pendingStr = String.format(Locale.US, "%,.2f", pendingTurnover)
            return@withContext Pair(
                false,
                "টার্নওভার অপূর্ণ! আপনি জমা ও বোনাস নেওয়ার পর সম্পূর্ণ পয়েন্ট গেম খেলেননি। উইথড্র করতে আরও ৳${pendingStr} পয়েন্ট গেম খেলা বাকি আছে।"
            )
        }

        // Deduct points immediately
        appDao.deductProfileBalance(amount)
        syncRegisteredAccountSnapshot()

        val request = WithdrawRequestEntity(
            userPhone = userPhone.trim(),
            amount = amount,
            method = method,
            status = "PENDING",
            submittedAt = System.currentTimeMillis()
        )
        appDao.insertWithdrawRequest(request)
        Pair(true, "আপনার ${String.format("%.0f", amount)} পয়েন্ট উইথড্রো রিকোয়েস্ট সফলভাবে জমা হয়েছে। এডমিন অনুমোদনের পর টাকা পাঠানো হবে।")
    }

    suspend fun approveWithdrawRequest(requestId: Long): Boolean = withContext(Dispatchers.IO) {
        val req = appDao.getWithdrawRequestById(requestId) ?: return@withContext false
        if (req.status != "PENDING") return@withContext false
        appDao.updateWithdrawRequestStatus(requestId, "APPROVED", System.currentTimeMillis())
        appDao.addLifetimeWithdraw(req.amount)
        syncRegisteredAccountSnapshot()
        true
    }

    suspend fun rejectWithdrawRequest(
        requestId: Long,
        reason: String = "এডমিন কর্তৃক বাতিল",
        refundToUser: Boolean = true
    ): Boolean = withContext(Dispatchers.IO) {
        val req = appDao.getWithdrawRequestById(requestId) ?: return@withContext false
        if (req.status != "PENDING") return@withContext false
        appDao.updateWithdrawRequestStatus(requestId, "REJECTED", System.currentTimeMillis(), reason)
        if (refundToUser) {
            appDao.addProfileBalance(req.amount)
            syncRegisteredAccountSnapshot()
        }
        true
    }

    suspend fun deductPlayerBalance(amount: Double): Double = withContext(Dispatchers.IO) {
        if (amount <= 0) return@withContext 0.0
        appDao.deductProfileBalance(amount)
        val profile = appDao.getUserProfile()
        profile?.balance ?: 0.0
    }

    suspend fun getWalletConfig(): AdminWalletConfigEntity? = withContext(Dispatchers.IO) {
        appDao.getWalletConfig()
    }

    suspend fun updateWalletConfig(config: AdminWalletConfigEntity) = withContext(Dispatchers.IO) {
        appDao.insertOrUpdateWalletConfig(config)
    }

    suspend fun recordSpin(
        bet: Double,
        win: Double,
        comboLevel: Int,
        freeGamesTriggered: Boolean,
        wildsCreated: Int
    ): UserProfileEntity = withContext(Dispatchers.IO) {
        val today = getTodayDateString()
        val currentProgress = appDao.getDailyProgress(today) ?: DailyProgressEntity(date = today)
        val profile = appDao.getUserProfile() ?: UserProfileEntity()

        val pointsEarned = (win * 10).toLong() + (if (win > 0) 50 else 10)
        val xpEarned = (bet * 5).toLong() + (win * 2).toLong()

        val newSpinsCount = currentProgress.spinsCount + 1
        val newTotalBet = currentProgress.totalBet + bet
        val newTotalWon = currentProgress.totalWon + win
        val newHighestWin = max(currentProgress.highestWin, win)
        val newMaxCombo = max(currentProgress.maxCombo, comboLevel)
        val newFreeGamesCount = currentProgress.freeGamesTriggered + if (freeGamesTriggered) 1 else 0
        val newWildsCreated = currentProgress.wildsCreated + wildsCreated
        val newDailyPoints = currentProgress.pointsEarned + pointsEarned

        val updatedProgress = currentProgress.copy(
            spinsCount = newSpinsCount,
            totalBet = newTotalBet,
            totalWon = newTotalWon,
            highestWin = newHighestWin,
            maxCombo = newMaxCombo,
            freeGamesTriggered = newFreeGamesCount,
            wildsCreated = newWildsCreated,
            pointsEarned = newDailyPoints
        )
        appDao.insertOrUpdateDailyProgress(updatedProgress)

        // Update user profile (balance, xp, level, totalScore)
        val newBalance = (profile.balance - bet + win).coerceAtLeast(0.0)
        val newTotalScore = profile.totalScore + pointsEarned
        val newXp = profile.xp + xpEarned
        val xpNeededForNextLevel = profile.level * 1000L
        var newLevel = profile.level
        var remainingXp = newXp
        if (remainingXp >= xpNeededForNextLevel) {
            newLevel++
            remainingXp -= xpNeededForNextLevel
        }

        val newCompletedTurnover = profile.completedTurnover + bet
        val newPendingTurnover = (profile.pendingTurnover - bet).coerceAtLeast(0.0)

        val updatedProfile = profile.copy(
            balance = newBalance,
            totalScore = newTotalScore,
            xp = remainingXp,
            level = newLevel,
            lastActiveDate = today,
            lifetimeBet = profile.lifetimeBet + bet,
            lifetimeWon = profile.lifetimeWon + win,
            completedTurnover = newCompletedTurnover,
            pendingTurnover = newPendingTurnover
        )
        appDao.insertOrUpdateUserProfile(updatedProfile)

        // Update leaderboard score for current user
        updateLeaderboardScore(newTotalScore, if (bet > 0) win / bet else 0.0)
        syncRegisteredAccountSnapshot()

        updatedProfile
    }

    private suspend fun updateLeaderboardScore(userPoints: Long, winMultiplier: Double) {
        val userEntry = appDao.getCurrentUserLeaderboardEntry()
        if (userEntry != null) {
            val updated = userEntry.copy(
                points = userPoints,
                maxWinMultiplier = max(userEntry.maxWinMultiplier, winMultiplier),
                updatedAt = System.currentTimeMillis()
            )
            appDao.updateLeaderboardEntry(updated)
        }
    }

    suspend fun reloadBalance(amount: Double = 2000.0) = withContext(Dispatchers.IO) {
        val profile = appDao.getUserProfile() ?: UserProfileEntity()
        val updated = profile.copy(
            balance = profile.balance + amount,
            requiredTurnover = profile.requiredTurnover + amount,
            pendingTurnover = profile.pendingTurnover + amount
        )
        appDao.insertOrUpdateUserProfile(updated)
        syncRegisteredAccountSnapshot()
    }

    suspend fun claimMissionReward(missionId: Int, rewardPoints: Long, rewardXp: Long) = withContext(Dispatchers.IO) {
        val today = getTodayDateString()
        val progress = appDao.getDailyProgress(today) ?: DailyProgressEntity(date = today)
        val profile = appDao.getUserProfile() ?: UserProfileEntity()

        val updatedProgress = when (missionId) {
            1 -> progress.copy(mission1Claimed = true)
            2 -> progress.copy(mission2Claimed = true)
            3 -> progress.copy(mission3Claimed = true)
            4 -> progress.copy(mission4Claimed = true)
            5 -> progress.copy(mission5Claimed = true)
            else -> progress
        }
        appDao.insertOrUpdateDailyProgress(updatedProgress)

        val newBalance = profile.balance + rewardPoints
        val newScore = profile.totalScore + rewardPoints
        val newXp = profile.xp + rewardXp
        val updatedProfile = profile.copy(
            balance = newBalance,
            totalScore = newScore,
            xp = newXp,
            requiredTurnover = profile.requiredTurnover + rewardPoints,
            pendingTurnover = profile.pendingTurnover + rewardPoints
        )
        appDao.insertOrUpdateUserProfile(updatedProfile)
        updateLeaderboardScore(newScore, 0.0)
        syncRegisteredAccountSnapshot()
    }

    suspend fun setExactBalance(amount: Double) = withContext(Dispatchers.IO) {
        val profile = appDao.getUserProfile() ?: UserProfileEntity()
        val updated = profile.copy(balance = amount.coerceAtLeast(0.0))
        appDao.insertOrUpdateUserProfile(updated)
        syncRegisteredAccountSnapshot()
    }

    suspend fun deductBalance(amount: Double): Double = withContext(Dispatchers.IO) {
        val profile = appDao.getUserProfile() ?: UserProfileEntity()
        val newBalance = (profile.balance - amount).coerceAtLeast(0.0)
        val updated = profile.copy(balance = newBalance)
        appDao.insertOrUpdateUserProfile(updated)
        syncRegisteredAccountSnapshot()
        newBalance
    }

    suspend fun setPlayerLevel(level: Int, xp: Long) = withContext(Dispatchers.IO) {
        val profile = appDao.getUserProfile() ?: UserProfileEntity()
        val updated = profile.copy(level = level.coerceAtLeast(1), xp = xp.coerceAtLeast(0L))
        appDao.insertOrUpdateUserProfile(updated)
    }

    suspend fun resetPlayerProfile() = withContext(Dispatchers.IO) {
        val today = getTodayDateString()
        val current = appDao.getUserProfile()
        val resetProfile = UserProfileEntity(
            id = 1,
            playerName = current?.playerName ?: "Player",
            userPhone = current?.userPhone ?: "",
            isRegistered = current?.isRegistered ?: false,
            avatarEmoji = current?.avatarEmoji ?: "👑",
            balance = 0.0,
            totalScore = 0,
            xp = 0,
            level = 1,
            consecutiveDays = 1,
            lastActiveDate = today
        )
        appDao.insertOrUpdateUserProfile(resetProfile)
    }

    suspend fun clearDailyStats() = withContext(Dispatchers.IO) {
        val today = getTodayDateString()
        appDao.insertOrUpdateDailyProgress(DailyProgressEntity(date = today))
    }

    suspend fun resetMissions() = withContext(Dispatchers.IO) {
        val today = getTodayDateString()
        val progress = appDao.getDailyProgress(today) ?: DailyProgressEntity(date = today)
        val reset = progress.copy(
            mission1Claimed = false,
            mission2Claimed = false,
            mission3Claimed = false,
            mission4Claimed = false,
            mission5Claimed = false
        )
        appDao.insertOrUpdateDailyProgress(reset)
    }

    suspend fun resetLeaderboard() = withContext(Dispatchers.IO) {
        appDao.clearAllLeaderboard()
        val profile = appDao.getUserProfile() ?: UserProfileEntity()
        val initialLeaderboard = listOf(
            LeaderboardEntryEntity(rank = 1, playerName = "DragonKing", avatarEmoji = "🐉", points = 88450, maxWinMultiplier = 420.0),
            LeaderboardEntryEntity(rank = 2, playerName = "VegasQueen", avatarEmoji = "💎", points = 65200, maxWinMultiplier = 280.0),
            LeaderboardEntryEntity(rank = 3, playerName = "GoldenAce", avatarEmoji = "🃏", points = 51900, maxWinMultiplier = 210.0),
            LeaderboardEntryEntity(rank = 4, playerName = "LuckyAce (You)", avatarEmoji = "👑", points = profile.totalScore, maxWinMultiplier = 35.0, isCurrentUser = true),
            LeaderboardEntryEntity(rank = 5, playerName = "JackpotHunter", avatarEmoji = "💰", points = 44100, maxWinMultiplier = 160.0),
            LeaderboardEntryEntity(rank = 6, playerName = "CardMaster", avatarEmoji = "♠️", points = 37800, maxWinMultiplier = 140.0),
            LeaderboardEntryEntity(rank = 7, playerName = "HighRoller88", avatarEmoji = "🔥", points = 31200, maxWinMultiplier = 115.0),
            LeaderboardEntryEntity(rank = 8, playerName = "CasinoStar", avatarEmoji = "⭐", points = 25600, maxWinMultiplier = 90.0),
            LeaderboardEntryEntity(rank = 9, playerName = "RoyalFlush", avatarEmoji = "🏰", points = 19400, maxWinMultiplier = 75.0),
            LeaderboardEntryEntity(rank = 10, playerName = "SpinWizard", avatarEmoji = "🪄", points = 14200, maxWinMultiplier = 50.0)
        )
        appDao.insertLeaderboardEntries(initialLeaderboard)
    }

    fun buildDailyMissions(progress: DailyProgressEntity): List<DailyMission> {
        return listOf(
            DailyMission(
                id = 1,
                title = "Spin Master",
                description = "Complete 15 spins on Super Ace",
                target = 15,
                current = progress.spinsCount,
                rewardPoints = 500,
                rewardXp = 200,
                isClaimed = progress.mission1Claimed
            ),
            DailyMission(
                id = 2,
                title = "Combo Striker",
                description = "Achieve a x2 or higher Combo Multiplier",
                target = 2,
                current = progress.maxCombo,
                rewardPoints = 600,
                rewardXp = 250,
                isClaimed = progress.mission2Claimed
            ),
            DailyMission(
                id = 3,
                title = "Joker's Magic",
                description = "Transform 3 Golden Cards into Wild Jokers",
                target = 3,
                current = progress.wildsCreated,
                rewardPoints = 800,
                rewardXp = 350,
                isClaimed = progress.mission3Claimed
            ),
            DailyMission(
                id = 4,
                title = "High Roller Win",
                description = "Score at least 100 points in a single spin",
                target = 100,
                current = progress.highestWin.toInt(),
                rewardPoints = 1000,
                rewardXp = 400,
                isClaimed = progress.mission4Claimed
            ),
            DailyMission(
                id = 5,
                title = "Free Games Hunter",
                description = "Trigger Free Games by landing 3+ Scatters",
                target = 1,
                current = progress.freeGamesTriggered,
                rewardPoints = 1500,
                rewardXp = 600,
                isClaimed = progress.mission5Claimed
            )
        )
    }

    // Custom Games (Admin added games / links / APIs)
    val allCustomGamesFlow: Flow<List<CustomGameEntity>> = appDao.getAllCustomGamesFlow()
    val activeCustomGamesFlow: Flow<List<CustomGameEntity>> = appDao.getActiveCustomGamesFlow()

    suspend fun addCustomGame(
        title: String,
        provider: String,
        subtitle: String,
        gameUrlOrApi: String,
        badge: String,
        maxWin: String,
        rtp: String,
        themeColorHex: Long = 0xFF8B5CF6
    ): CustomGameEntity = withContext(Dispatchers.IO) {
        val id = "game_" + System.currentTimeMillis()
        val entity = CustomGameEntity(
            id = id,
            title = title.trim(),
            provider = provider.trim().ifBlank { "Custom Provider" },
            subtitle = subtitle.trim().ifBlank { "নতুন গেম" },
            gameUrlOrApi = gameUrlOrApi.trim(),
            badge = badge.trim().ifBlank { "🆕 NEW" },
            maxWin = maxWin.trim().ifBlank { "10,000x" },
            themeColorHex = themeColorHex,
            rtp = rtp.trim().ifBlank { "97.0%" },
            isNewGame = true,
            isActive = true,
            createdAt = System.currentTimeMillis()
        )
        appDao.insertCustomGame(entity)
        entity
    }

    suspend fun deleteCustomGame(id: String) = withContext(Dispatchers.IO) {
        appDao.deleteCustomGame(id)
    }

    suspend fun toggleCustomGameActive(id: String, isActive: Boolean) = withContext(Dispatchers.IO) {
        appDao.toggleCustomGameActive(id, isActive)
    }

    // Favorite Games (User Starred Games)
    val favoriteGameIdsFlow: Flow<Set<String>> = appDao.getFavoriteGameIdsFlow().map { it.toSet() }

    // Deleted Games Management (Admin Game Delete & Restore)
    val allDeletedGamesFlow: Flow<List<DeletedGameEntity>> = appDao.getAllDeletedGamesFlow()
    val deletedGameIdsFlow: Flow<List<String>> = appDao.getDeletedGameIdsFlow()

    suspend fun markGameDeleted(gameId: String, gameTitle: String) = withContext(Dispatchers.IO) {
        appDao.insertDeletedGame(
            DeletedGameEntity(
                gameId = gameId,
                gameTitle = gameTitle,
                deletedAt = System.currentTimeMillis()
            )
        )
        // If it is a custom game, also remove it from active custom games
        appDao.deleteCustomGame(gameId)
    }

    suspend fun restoreDeletedGame(gameId: String) = withContext(Dispatchers.IO) {
        appDao.restoreDeletedGame(gameId)
    }

    suspend fun toggleFavoriteGame(gameId: String): Boolean = withContext(Dispatchers.IO) {
        val count = appDao.isGameFavoriteCount(gameId)
        if (count > 0) {
            appDao.removeGameFavorite(gameId)
            false
        } else {
            appDao.setGameFavorite(FavoriteGameEntity(gameId = gameId, isFavorite = true))
            true
        }
    }

    // Sub-Admin Management & Tracking
    val allSubAdminsFlow: Flow<List<SubAdminEntity>> = appDao.getAllSubAdminsFlow()

    suspend fun getAllSubAdmins(): List<SubAdminEntity> = withContext(Dispatchers.IO) {
        appDao.getAllSubAdmins()
    }

    suspend fun getSubAdminByReferralCode(code: String): SubAdminEntity? = withContext(Dispatchers.IO) {
        appDao.getSubAdminByReferralCode(code.trim().uppercase())
    }

    fun getRegisteredAccountsBySubAdminFlow(code: String): Flow<List<RegisteredAccountEntity>> =
        appDao.getRegisteredAccountsBySubAdminFlow(code.trim().uppercase())

    suspend fun addSubAdmin(
        name: String,
        phone: String,
        referralCode: String,
        commissionPercent: Double = 5.0,
        notes: String = ""
    ): SubAdminEntity = withContext(Dispatchers.IO) {
        val cleanCode = referralCode.trim().uppercase()
        val id = "SA_" + (System.currentTimeMillis() % 100000)
        val config = appDao.getWalletConfig()
        val portalUrl = config?.websitePortalUrl?.ifBlank { "https://royalslots-bd.com" } ?: "https://royalslots-bd.com"
        val refLink = if (portalUrl.endsWith("/")) "${portalUrl}?ref=$cleanCode" else "${portalUrl}/?ref=$cleanCode"

        val entity = SubAdminEntity(
            id = id,
            name = name.trim(),
            phone = phone.trim(),
            referralCode = cleanCode,
            referralLink = refLink,
            commissionPercent = commissionPercent,
            status = "ACTIVE",
            notes = notes.trim(),
            createdAt = System.currentTimeMillis()
        )
        appDao.insertSubAdmin(entity)
        entity
    }

    suspend fun deleteSubAdmin(id: String) = withContext(Dispatchers.IO) {
        appDao.deleteSubAdmin(id)
    }

    suspend fun toggleSubAdminStatus(id: String, status: String) = withContext(Dispatchers.IO) {
        appDao.updateSubAdminStatus(id, status)
    }

    suspend fun updateWebsiteAndSecurityConfig(
        url: String,
        isWebMode: Boolean,
        codeProtect: Boolean
    ) = withContext(Dispatchers.IO) {
        appDao.updateWebsiteAndSecurityConfig(url.trim(), isWebMode, codeProtect)
    }

    suspend fun claimDailyReward(): DailyRewardClaimResult = withContext(Dispatchers.IO) {
        val profile = appDao.getUserProfile() ?: UserProfileEntity()
        val now = System.currentTimeMillis()
        val cycleMs = DailyRewardConfig.REWARD_CYCLE_MS
        val streakResetMs = DailyRewardConfig.STREAK_RESET_MS
        val lastClaim = profile.lastDailyRewardClaimTime

        if (lastClaim > 0L && (now - lastClaim) < cycleMs) {
            val nextClaimTime = lastClaim + cycleMs
            return@withContext DailyRewardClaimResult(
                success = false,
                rewardAmount = 0.0,
                streakDay = profile.dailyRewardStreak.coerceIn(1, 7),
                message = "আজকের ফ্রি রিওয়ার্ড ইতিমধ্যে দাবি করা হয়েছে। পরবর্তী রিওয়ার্ড আনলক হতে সময় বাকি আছে।",
                nextClaimTime = nextClaimTime,
                updatedProfile = profile
            )
        }

        // Determine new streak
        val newStreak = if (lastClaim == 0L || (now - lastClaim) <= streakResetMs) {
            val next = profile.dailyRewardStreak + 1
            if (next > 7) 1 else next
        } else {
            1
        }

        val rewardAmount = DailyRewardConfig.getAmountForDay(newStreak)
        val updatedProfile = profile.copy(
            balance = profile.balance + rewardAmount,
            totalDailyRewardsClaimed = profile.totalDailyRewardsClaimed + rewardAmount,
            lastDailyRewardClaimTime = now,
            dailyRewardStreak = newStreak,
            requiredTurnover = profile.requiredTurnover + rewardAmount,
            pendingTurnover = profile.pendingTurnover + rewardAmount
        )

        appDao.insertOrUpdateUserProfile(updatedProfile)
        syncRegisteredAccountSnapshot()

        DailyRewardClaimResult(
            success = true,
            rewardAmount = rewardAmount,
            streakDay = newStreak,
            message = "🎉 অভিনন্দন! আপনি ডে $newStreak এর ৳${rewardAmount.toInt()} ফ্রি ভার্চুয়াল কয়েন পেয়েছেন!",
            nextClaimTime = now + cycleMs,
            updatedProfile = updatedProfile
        )
    }

    suspend fun getUserProfile(): UserProfileEntity? = withContext(Dispatchers.IO) {
        appDao.getUserProfile()
    }

    suspend fun getAllRegisteredAccountsList(): List<RegisteredAccountEntity> = withContext(Dispatchers.IO) {
        appDao.getAllRegisteredAccounts()
    }

    suspend fun getAllDepositRequestsList(): List<DepositRequestEntity> = withContext(Dispatchers.IO) {
        appDao.getAllDepositRequests()
    }

    suspend fun getAllWithdrawRequestsList(): List<WithdrawRequestEntity> = withContext(Dispatchers.IO) {
        appDao.getAllWithdrawRequests()
    }

    suspend fun updatePlayerBalanceByPhone(phone: String, newBalance: Double) = withContext(Dispatchers.IO) {
        val sanitizedPhone = phone.trim()
        val account = appDao.getRegisteredAccount(sanitizedPhone)
        if (account != null) {
            val updatedAccount = account.copy(balance = newBalance.coerceAtLeast(0.0))
            appDao.insertRegisteredAccount(updatedAccount)
        }
        val currentProfile = appDao.getUserProfile()
        if (currentProfile != null && currentProfile.userPhone == sanitizedPhone) {
            val updatedProfile = currentProfile.copy(balance = newBalance.coerceAtLeast(0.0))
            appDao.insertOrUpdateUserProfile(updatedProfile)
        }
    }

    suspend fun updatePlayerLevelByPhone(phone: String, newLevel: Int) = withContext(Dispatchers.IO) {
        val sanitizedPhone = phone.trim()
        val account = appDao.getRegisteredAccount(sanitizedPhone)
        if (account != null) {
            val updatedAccount = account.copy(level = newLevel.coerceIn(1, 100))
            appDao.insertRegisteredAccount(updatedAccount)
        }
        val currentProfile = appDao.getUserProfile()
        if (currentProfile != null && currentProfile.userPhone == sanitizedPhone) {
            val updatedProfile = currentProfile.copy(level = newLevel.coerceIn(1, 100))
            appDao.insertOrUpdateUserProfile(updatedProfile)
        }
    }

    suspend fun togglePlayerBlockByPhone(phone: String): Boolean = withContext(Dispatchers.IO) {
        val sanitizedPhone = phone.trim()
        val account = appDao.getRegisteredAccount(sanitizedPhone) ?: return@withContext false
        val newStatus = if (account.status == "BLOCKED") "ACTIVE" else "BLOCKED"
        val updated = account.copy(status = newStatus)
        appDao.insertRegisteredAccount(updated)
        newStatus == "BLOCKED"
    }

    suspend fun addManualPlayerAccount(
        phone: String,
        name: String,
        initialBalance: Double = 0.0,
        level: Int = 1
    ) = withContext(Dispatchers.IO) {
        val sanitizedPhone = phone.trim()
        val entity = RegisteredAccountEntity(
            userPhone = sanitizedPhone,
            playerName = name.trim().ifBlank { "Player" },
            deviceId = "manual_admin_${System.currentTimeMillis()}",
            deviceModel = "Admin Created",
            registeredAt = System.currentTimeMillis(),
            status = "ACTIVE",
            balance = initialBalance.coerceAtLeast(0.0),
            level = level.coerceIn(1, 100)
        )
        appDao.insertRegisteredAccount(entity)
    }
}

data class DailyRewardDay(
    val dayNumber: Int,
    val amount: Double,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val isSpecial: Boolean = false
)

object DailyRewardConfig {
    const val REWARD_CYCLE_HOURS = 24L
    const val REWARD_CYCLE_MS = 24L * 60L * 60L * 1000L
    const val STREAK_RESET_MS = 48L * 60L * 60L * 1000L

    val days = listOf(
        DailyRewardDay(1, 100.0, "Day 1", "স্বাগতম কয়েন", "🪙"),
        DailyRewardDay(2, 150.0, "Day 2", "সিলভার স্ট্যাশ", "💰"),
        DailyRewardDay(3, 250.0, "Day 3", "গোল্ডেন ব্যাগ", "✨"),
        DailyRewardDay(4, 400.0, "Day 4", "রয়েল ফরচুন", "💎"),
        DailyRewardDay(5, 600.0, "Day 5", "ডায়মন্ড চেস্ট", "🏆"),
        DailyRewardDay(6, 850.0, "Day 6", "ড্রাগন ট্রেজার", "🔥"),
        DailyRewardDay(7, 1500.0, "Day 7", "কিং মেগা ক্রাউন VIP", "👑", isSpecial = true)
    )

    fun getAmountForDay(day: Int): Double {
        val clamped = ((day - 1) % 7) + 1
        return days.firstOrNull { it.dayNumber == clamped }?.amount ?: 100.0
    }
}

data class DailyRewardClaimResult(
    val success: Boolean,
    val rewardAmount: Double,
    val streakDay: Int,
    val message: String,
    val nextClaimTime: Long,
    val updatedProfile: UserProfileEntity
)

