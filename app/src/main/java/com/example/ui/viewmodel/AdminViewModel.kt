package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AdminWalletConfigEntity
import com.example.data.db.AppDatabase
import com.example.data.db.DailyProgressEntity
import com.example.data.db.DepositRequestEntity
import com.example.data.db.LeaderboardEntryEntity
import com.example.data.db.RegisteredAccountEntity
import com.example.data.db.SubAdminEntity
import com.example.data.db.UserProfileEntity
import com.example.data.db.WithdrawRequestEntity
import com.example.data.db.CustomGameEntity
import com.example.data.db.DeletedGameEntity
import com.example.data.model.DailyMission
import com.example.data.model.GameAdminConfig
import com.example.data.model.RemoteGameConfig
import com.example.data.remote.GitHubRemoteService
import com.example.data.repository.GameAdminRepository
import com.example.data.repository.GameRepository
import com.example.util.SoundManager
import com.example.util.StatementExportUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = GameRepository(db.appDao())
    private val adminRepository = GameAdminRepository.getInstance(application)
    val soundManager = SoundManager.getInstance(application)

    val adminConfig: StateFlow<GameAdminConfig> = adminRepository.configFlow

    private val _remoteConfig = MutableStateFlow(GitHubRemoteService.getCachedConfig(application))
    val remoteConfig: StateFlow<RemoteGameConfig> = _remoteConfig.asStateFlow()

    val userProfile: StateFlow<UserProfileEntity> = repository.userProfileFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfileEntity()
        )

    val todayProgress: StateFlow<DailyProgressEntity> = repository.getTodayProgressFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DailyProgressEntity(date = repository.getTodayDateString())
        )

    val leaderboard: StateFlow<List<LeaderboardEntryEntity>> = repository.leaderboardFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val walletConfig: StateFlow<AdminWalletConfigEntity> = repository.walletConfigFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AdminWalletConfigEntity()
        )

    val allDepositRequests: StateFlow<List<DepositRequestEntity>> = repository.allDepositRequestsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val pendingDepositCount: StateFlow<Int> = repository.pendingDepositCountFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val allWithdrawRequests: StateFlow<List<WithdrawRequestEntity>> = repository.allWithdrawRequestsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val pendingWithdrawCount: StateFlow<Int> = repository.pendingWithdrawCountFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val allRegisteredAccounts: StateFlow<List<RegisteredAccountEntity>> = repository.allRegisteredAccountsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allSubAdmins: StateFlow<List<SubAdminEntity>> = repository.allSubAdminsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allCustomGames: StateFlow<List<CustomGameEntity>> = repository.allCustomGamesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allDeletedGames: StateFlow<List<DeletedGameEntity>> = repository.allDeletedGamesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val deletedGameIds: StateFlow<List<String>> = repository.deletedGameIdsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteGame(gameId: String, gameTitle: String) {
        viewModelScope.launch {
            repository.markGameDeleted(gameId, gameTitle)
            _statusMessage.value = "গেম '$gameTitle' সফলভাবে মুছে ফেলা হয়েছে! প্লেয়ার অ্যাপ থেকে এটি সরিয়ে নেওয়া হয়েছে।"
            soundManager.playButtonClick()
        }
    }

    fun restoreGame(gameId: String, gameTitle: String = "") {
        viewModelScope.launch {
            repository.restoreDeletedGame(gameId)
            val name = gameTitle.ifBlank { gameId }
            _statusMessage.value = "গেম '$name' সফলভাবে পুনরুদ্ধার করা হয়েছে! এটি প্লেয়ার অ্যাপে পুনরায় দৃশ্যমান হবে।"
            soundManager.playCoinDrop(1)
        }
    }

    fun addCustomGame(
        title: String,
        provider: String,
        subtitle: String,
        gameUrlOrApi: String,
        badge: String,
        maxWin: String,
        rtp: String
    ) {
        viewModelScope.launch {
            repository.addCustomGame(
                title = title,
                provider = provider,
                subtitle = subtitle,
                gameUrlOrApi = gameUrlOrApi,
                badge = badge,
                maxWin = maxWin,
                rtp = rtp
            )
            _statusMessage.value = "নতুন গেম '${title}' সফলভাবে যুক্ত ও প্লেয়ার অ্যাপে লাইভ করা হয়েছে!"
            soundManager.playCoinDrop(2)
        }
    }

    fun deleteCustomGame(id: String) {
        viewModelScope.launch {
            repository.deleteCustomGame(id)
            _statusMessage.value = "গেমটি মুছে ফেলা হয়েছে।"
            soundManager.playButtonClick()
        }
    }

    fun toggleCustomGame(id: String, isActive: Boolean) {
        viewModelScope.launch {
            repository.toggleCustomGameActive(id, isActive)
            _statusMessage.value = if (isActive) "গেমটি সক্রিয় করা হয়েছে" else "গেমটি নিষ্ক্রিয় করা হয়েছে"
        }
    }

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    fun updateWalletNumbers(
        nagad: String,
        bkash: String,
        rocket: String,
        upay: String,
        minDeposit: Double = 100.0,
        maxDeposit: Double = 20000.0
    ) {
        viewModelScope.launch {
            val updated = walletConfig.value.copy(
                nagadWallet = nagad.trim(),
                bkashWallet = bkash.trim(),
                rocketWallet = rocket.trim(),
                upayWallet = upay.trim(),
                minDeposit = minDeposit,
                maxDeposit = maxDeposit,
                updatedAt = System.currentTimeMillis()
            )
            repository.updateWalletConfig(updated)
            _statusMessage.value = "ওয়ালেট নম্বর সফলভাবে আপডেট করা হয়েছে!"
        }
    }

    fun approveDeposit(requestId: Long) {
        viewModelScope.launch {
            val success = repository.approveDepositRequest(requestId)
            if (success) {
                soundManager.playCoinDrop(4)
                _statusMessage.value = "ডিপোজিট সফলভাবে অনুমোদন করা হয়েছে! প্লেয়ার ব্যালেন্সে পয়েন্ট যুক্ত হয়েছে।"
                updateCloudDepositStatus(requestId, "APPROVED", "অনুমোদিত")
            } else {
                _statusMessage.value = "আবেদন অনুমোদন ব্যর্থ হয়েছে।"
            }
        }
    }

    fun rejectDeposit(requestId: Long, reason: String = "বাতিল করা হয়েছে") {
        viewModelScope.launch {
            val success = repository.rejectDepositRequest(requestId, reason)
            if (success) {
                _statusMessage.value = "ডিপোজিট আবেদন বাতিল করা হয়েছে।"
                updateCloudDepositStatus(requestId, "REJECTED", reason)
            } else {
                _statusMessage.value = "বাতিল করা সম্ভব হয়নি।"
            }
        }
    }

    fun approveWithdraw(requestId: Long) {
        viewModelScope.launch {
            val success = repository.approveWithdrawRequest(requestId)
            if (success) {
                soundManager.playButtonClick()
                _statusMessage.value = "উইথড্রো রিকোয়েস্ট সফলভাবে অনুমোদন করা হয়েছে!"
                updateCloudWithdrawStatus(requestId, "APPROVED", "অনুমোদিত")
            } else {
                _statusMessage.value = "উইথড্রো অনুমোদন ব্যর্থ হয়েছে।"
            }
        }
    }

    fun rejectWithdraw(requestId: Long, refund: Boolean = true, reason: String = "এডমিন কর্তৃক বাতিল") {
        viewModelScope.launch {
            val success = repository.rejectWithdrawRequest(requestId, reason, refund)
            if (success) {
                _statusMessage.value = if (refund) {
                    "উইথড্রো বাতিল করা হয়েছে এবং পয়েন্ট প্লেয়ারকে ফেরত দেওয়া হয়েছে।"
                } else {
                    "উইথড্রো বাতিল করা হয়েছে।"
                }
                updateCloudWithdrawStatus(requestId, "REJECTED", reason)
            } else {
                _statusMessage.value = "বাতিল করা সম্ভব হয়নি।"
            }
        }
    }

    fun deductBalance(amount: Double) {
        viewModelScope.launch {
            repository.deductPlayerBalance(amount)
            soundManager.playButtonClick()
            _statusMessage.value = "প্লেয়ারের ব্যালেন্স থেকে ${String.format("%.0f", amount)} পয়েন্ট কেটে নেওয়া হয়েছে!"
        }
    }

    fun setSuperAceWinRatio(percent: Int) {
        adminRepository.setSuperAceWinRatio(percent.coerceIn(5, 95))
        _statusMessage.value = "Super Ace উইন রেশিও সেট করা হয়েছে: $percent%"
    }

    fun setDeadMansBulletWinRatio(percent: Int) {
        adminRepository.setDeadMansBulletWinRatio(percent.coerceIn(5, 95))
        _statusMessage.value = "Dead Man's Bullet উইন রেশিও সেট করা হয়েছে: $percent%"
    }

    fun setGatesOfOlympusWinRatio(percent: Int) {
        adminRepository.setGatesOfOlympusWinRatio(percent.coerceIn(5, 95))
        _statusMessage.value = "Gates of Olympus উইন রেশিও সেট করা হয়েছে: $percent%"
    }

    fun setSweetBonanzaWinRatio(percent: Int) {
        adminRepository.setSweetBonanzaWinRatio(percent.coerceIn(5, 95))
        _statusMessage.value = "Sweet Bonanza উইন রেশিও সেট করা হয়েছে: $percent%"
    }

    fun setAllGamesWinRatio(percent: Int) {
        adminRepository.setAllGamesWinRatio(percent.coerceIn(5, 95))
        _statusMessage.value = "সব কটি গেমের উইন রেশিও সেট করা হয়েছে: $percent%"
    }

    fun updateConfig(newConfig: GameAdminConfig) {
        adminRepository.updateConfig(newConfig)
        _statusMessage.value = "Engine calibrated: Win Rate ${newConfig.winRatioPercent}%"
    }

    fun setWinRatio(percent: Int) {
        val updated = adminConfig.value.copy(winRatioPercent = percent.coerceIn(5, 95))
        adminRepository.updateConfig(updated)
        _statusMessage.value = "Winning hit ratio set to $percent%"
    }

    fun setGoldenWildChance(chance: Float) {
        val updated = adminConfig.value.copy(goldenWildChance = chance.coerceIn(0.05f, 0.60f))
        adminRepository.updateConfig(updated)
        _statusMessage.value = "Golden Wild chance set to ${(chance * 100).toInt()}%"
    }

    fun setBigJokerChance(chance: Float) {
        val updated = adminConfig.value.copy(bigJokerChance = chance.coerceIn(0.05f, 0.50f))
        adminRepository.updateConfig(updated)
        _statusMessage.value = "Big Joker chance set to ${(chance * 100).toInt()}%"
    }

    fun setMaxCascadeSteps(steps: Int) {
        val updated = adminConfig.value.copy(maxCascadeSteps = steps.coerceIn(3, 15))
        adminRepository.updateConfig(updated)
        _statusMessage.value = "Max cascades set to $steps steps"
    }

    fun setRtpMultiplierBonus(bonus: Float) {
        val updated = adminConfig.value.copy(rtpMultiplierBonus = bonus.coerceIn(0.5f, 3.0f))
        adminRepository.updateConfig(updated)
        _statusMessage.value = "RTP multiplier bonus set to ${String.format("%.2f", bonus)}x"
    }

    fun resetConfigToDefaults() {
        val def = adminRepository.resetToDefaults()
        _statusMessage.value = "Engine config reset to defaults (${def.winRatioPercent}% win rate)"
    }

    fun addBalance(amount: Double) {
        viewModelScope.launch {
            repository.reloadBalance(amount)
            soundManager.playCoinDrop(4)
            _statusMessage.value = "Added +${String.format("%.0f", amount)} chips to player!"
        }
    }

    fun setExactBalance(amount: Double) {
        viewModelScope.launch {
            repository.setExactBalance(amount)
            soundManager.playCoinDrop(3)
            _statusMessage.value = "Player balance updated to ${String.format("%.2f", amount)}"
        }
    }

    fun deductPlayerPoints(amount: Double) {
        viewModelScope.launch {
            val newBal = repository.deductBalance(amount)
            soundManager.playButtonClick()
            _statusMessage.value = "পয়েন্ট কেটে নেওয়া হয়েছে: -${String.format(java.util.Locale.US, "%,.0f", amount)} (নতুন ব্যালেন্স: ${String.format(java.util.Locale.US, "%,.0f", newBal)})"
        }
    }

    fun setPlayerLevel(level: Int) {
        viewModelScope.launch {
            repository.setPlayerLevel(level, 0L)
            _statusMessage.value = "Player level updated to $level"
        }
    }

    fun resetPlayerProfile() {
        viewModelScope.launch {
            repository.resetPlayerProfile()
            _statusMessage.value = "Player profile reset to default (0 points, Level 1)"
        }
    }

    fun clearDailyTelemetry() {
        viewModelScope.launch {
            repository.clearDailyStats()
            _statusMessage.value = "Today's gameplay stats cleared"
        }
    }

    fun resetDailyMissions() {
        viewModelScope.launch {
            repository.resetMissions()
            _statusMessage.value = "Daily missions claim status reset"
        }
    }

    fun resetLeaderboard() {
        viewModelScope.launch {
            repository.resetLeaderboard()
            _statusMessage.value = "Tournament leaderboard reset to default standings"
        }
    }

    fun testSound(soundType: String) {
        when (soundType) {
            "spin" -> soundManager.playSpinStart()
            "stop" -> soundManager.playReelStop()
            "combo_1" -> soundManager.playCombo(1)
            "combo_2" -> soundManager.playCombo(2)
            "combo_3" -> soundManager.playCombo(3)
            "combo_4" -> soundManager.playCombo(4)
            "wild" -> soundManager.playWildTransform()
            "coin" -> soundManager.playCoinDrop(4)
            "big_win" -> soundManager.playBigWin()
            "free_spins" -> soundManager.playFreeSpinsTrigger()
            "click" -> soundManager.playButtonClick()
            "revolver" -> soundManager.playRevolverSpin()
            "gun_cock" -> soundManager.playGunCock()
            "gunshot" -> soundManager.playGunshotRicochet()
            "western_chord" -> soundManager.playWesternChord()
            "thunder" -> soundManager.playThunderStrike()
            "candy_pop" -> soundManager.playCandyPop()
        }
    }

    fun toggleMasterSound(enabled: Boolean) {
        soundManager.isEnabled = enabled
        _statusMessage.value = if (enabled) "Sound FX Enabled" else "Sound FX Muted"
    }

    fun setGuaranteedHouseProfitMode(enabled: Boolean) {
        val current = adminConfig.value
        adminRepository.updateConfig(current.copy(guaranteedHouseProfitMode = enabled))
        _statusMessage.value = if (enabled) "✅ এডমিন লাভ সুরক্ষা (House Profit Guarantee) চালু করা হয়েছে!" else "House Profit Guarantee নিষ্ক্রিয় করা হয়েছে"
    }

    fun setAllowPlayerProfit(allowed: Boolean) {
        val current = adminConfig.value
        adminRepository.updateConfig(current.copy(allowPlayerProfit = allowed))
        _statusMessage.value = if (allowed) "🟢 খেলোয়াড়দের নেট লাভের অনুমতি সক্রিয় করা হয়েছে" else "🔒 প্লেয়ার লাভ বন্ধ (ইউজার সর্বদা লসে থাকবে, সর্বোচ্চ ৬০% রিটার্ন)"
    }

    fun setHouseProfitMargin(margin: Int) {
        val current = adminConfig.value
        adminRepository.updateConfig(current.copy(houseProfitMarginPercent = margin))
        _statusMessage.value = "এডমিনের নিশ্চিত মার্জিন $margin% সেভ করা হয়েছে"
    }

    fun setMaxPayoutMultiplierCap(cap: Double) {
        val current = adminConfig.value
        adminRepository.updateConfig(current.copy(maxPayoutMultiplierCap = cap))
        _statusMessage.value = "সর্বোচ্চ উইন ক্যাপ ${cap.toInt()}x সেট করা হয়েছে"
    }

    fun exportStatementText(context: Context) {
        try {
            val file = StatementExportUtil.exportStatementFile(
                context = context,
                profile = userProfile.value,
                registeredAccounts = allRegisteredAccounts.value,
                deposits = allDepositRequests.value,
                withdraws = allWithdrawRequests.value,
                today = todayProgress.value
            )
            StatementExportUtil.shareStatementFile(context, file)
            _statusMessage.value = "📄 অডিট স্টেটমেন্ট ফাইল সফলভাবে তৈরি হয়েছে (${file.name})"
        } catch (e: Exception) {
            _statusMessage.value = "ফাইল তৈরি ব্যর্থ: ${e.message}"
        }
    }

    fun exportStatementCsv(context: Context) {
        try {
            val file = StatementExportUtil.exportCsvStatementFile(
                context = context,
                profile = userProfile.value,
                registeredAccounts = allRegisteredAccounts.value,
                deposits = allDepositRequests.value,
                withdraws = allWithdrawRequests.value
            )
            StatementExportUtil.shareStatementFile(context, file)
            _statusMessage.value = "📊 CSV এক্সেল স্টেটমেন্ট ফাইল সফলভাবে তৈরি হয়েছে"
        } catch (e: Exception) {
            _statusMessage.value = "CSV তৈরি ব্যর্থ: ${e.message}"
        }
    }

    fun getStatementSummaryText(): String {
        return StatementExportUtil.generateAuditStatement(
            profile = userProfile.value,
            registeredAccounts = allRegisteredAccounts.value,
            deposits = allDepositRequests.value,
            withdraws = allWithdrawRequests.value,
            today = todayProgress.value
        )
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun getMissions(progress: DailyProgressEntity): List<DailyMission> {
        return repository.buildDailyMissions(progress)
    }

    // Sub-Admin Operations
    fun addSubAdmin(
        name: String,
        phone: String,
        referralCode: String,
        commissionPercent: Double = 5.0,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val entity = repository.addSubAdmin(
                name = name,
                phone = phone,
                referralCode = referralCode,
                commissionPercent = commissionPercent,
                notes = notes
            )
            soundManager.playCoinDrop(3)
            _statusMessage.value = "নতুন সাব-এডমিন '${entity.name}' সফলভাবে যুক্ত হয়েছে! কোড: ${entity.referralCode}"
        }
    }

    fun deleteSubAdmin(id: String) {
        viewModelScope.launch {
            repository.deleteSubAdmin(id)
            soundManager.playButtonClick()
            _statusMessage.value = "সাব-এডমিন সফলভাবে মুছে ফেলা হয়েছে।"
        }
    }

    fun toggleSubAdminStatus(id: String, currentStatus: String) {
        viewModelScope.launch {
            val nextStatus = if (currentStatus == "ACTIVE") "SUSPENDED" else "ACTIVE"
            repository.toggleSubAdminStatus(id, nextStatus)
            _statusMessage.value = if (nextStatus == "ACTIVE") "সাব-এডমিন সক্রিয় করা হয়েছে" else "সাব-এডমিন সাময়িকভাবে স্থগিত করা হয়েছে"
        }
    }

    // Website & Security Protection Settings
    fun updateWebsiteAndSecurityConfig(
        url: String,
        isWebMode: Boolean,
        codeProtect: Boolean
    ) {
        viewModelScope.launch {
            repository.updateWebsiteAndSecurityConfig(url, isWebMode, codeProtect)
            _statusMessage.value = "ওয়েবসাইট পোর্টাল ও সোর্স কোড সুরক্ষা সফলভাবে সংরক্ষণ করা হয়েছে!"
            soundManager.playButtonClick()
        }
    }

    // Reports & Statements for Admin and Sub-Admins
    fun exportSubAdminsReport(context: Context) {
        try {
            val file = StatementExportUtil.exportSubAdminsCsvFile(
                context = context,
                subAdmins = allSubAdmins.value,
                accounts = allRegisteredAccounts.value,
                deposits = allDepositRequests.value,
                withdraws = allWithdrawRequests.value
            )
            StatementExportUtil.shareStatementFile(context, file, "সাব-এডমিন ও এজেন্ট রিপোর্ট ডাউনলোড করুন")
            _statusMessage.value = "📊 সাব-এডমিন রিপোর্ট ফাইল প্রস্তুত (${file.name})"
        } catch (e: Exception) {
            _statusMessage.value = "রিপোর্ট তৈরি ব্যর্থ: ${e.message}"
        }
    }

    fun exportAllPlayersReport(context: Context) {
        try {
            val file = StatementExportUtil.exportAllPlayersWithSubAdminCsvFile(
                context = context,
                accounts = allRegisteredAccounts.value,
                subAdmins = allSubAdmins.value,
                deposits = allDepositRequests.value,
                withdraws = allWithdrawRequests.value
            )
            StatementExportUtil.shareStatementFile(context, file, "খেলোয়াড় ও সাব-এডমিন ডাটা ডাউনলোড করুন")
            _statusMessage.value = "👥 খেলোয়াড়দের CSV ডাটা ফাইল ডাউনলোড প্রস্তুত"
        } catch (e: Exception) {
            _statusMessage.value = "রিপোর্ট তৈরি ব্যর্থ: ${e.message}"
        }
    }

    fun exportMasterFullAudit(context: Context) {
        try {
            val file = StatementExportUtil.exportMasterFullCasinoAuditFile(
                context = context,
                profile = userProfile.value,
                subAdmins = allSubAdmins.value,
                accounts = allRegisteredAccounts.value,
                deposits = allDepositRequests.value,
                withdraws = allWithdrawRequests.value,
                today = todayProgress.value
            )
            StatementExportUtil.shareStatementFile(context, file, "মাস্টার ক্যাসিনো অডিট রিপোর্ট ডাউনলোড করুন")
            _statusMessage.value = "👑 মাস্টার ক্যাসিনো অডিট রিপোর্ট ফাইল প্রস্তুত (${file.name})"
        } catch (e: Exception) {
            _statusMessage.value = "মাস্টার রিপোর্ট তৈরি ব্যর্থ: ${e.message}"
        }
    }

    fun applyRemoteGameConfig(config: RemoteGameConfig) {
        _remoteConfig.value = config
        viewModelScope.launch {
            GitHubRemoteService.syncRemoteConfigToPlayerState(
                context = getApplication(),
                config = config,
                repository = repository,
                adminRepository = adminRepository
            )
            _statusMessage.value = "গিটহাব থেকে লাইভ কনফিগারেশন গ্রহণ ও প্রয়োগ করা হয়েছে!"
        }
    }

    fun updateBrandingConfig(
        appTitle: String,
        appSubtitle: String,
        appLogoPreset: String,
        appLogoUrl: String,
        profileBannerUrl: String,
        defaultPlayerAvatar: String,
        supportContact: String,
        welcomeBonus: Double
    ) {
        val current = _remoteConfig.value
        val updated = current.copy(
            appTitle = appTitle.trim().ifBlank { "King Game" },
            appSubtitle = appSubtitle.trim().ifBlank { "ROYAL CASINO" },
            appLogoPreset = appLogoPreset.trim().ifBlank { "KING_CROWN" },
            appLogoUrl = appLogoUrl.trim(),
            profileBannerUrl = profileBannerUrl.trim(),
            defaultPlayerAvatar = defaultPlayerAvatar.trim().ifBlank { "👑" },
            supportContact = supportContact.trim().ifBlank { "01303347372" },
            welcomeBonusAmount = welcomeBonus.coerceAtLeast(0.0),
            lastUpdated = System.currentTimeMillis()
        )
        _remoteConfig.value = updated
        viewModelScope.launch {
            GitHubRemoteService.syncRemoteConfigToPlayerState(
                context = getApplication(),
                config = updated,
                repository = repository,
                adminRepository = adminRepository
            )
            val creds = GitHubRemoteService.loadCredentials(getApplication())
            if (creds.token.isNotBlank()) {
                GitHubRemoteService.publishConfigToGitHub(getApplication(), creds, updated)
            }
            _statusMessage.value = "🎨 ব্র্যান্ডিং ও লোগো সফলভাবে আপডেট ও অনলাইনে সেভ হয়েছে!"
            soundManager.playButtonClick()
        }
    }

    fun updateCloudUserBalance(phone: String, newBalance: Double) {
        viewModelScope.launch {
            val sanitizedPhone = phone.trim()
            repository.updatePlayerBalanceByPhone(sanitizedPhone, newBalance)
            val current = _remoteConfig.value
            val existing = current.cloudPlayers.firstOrNull { it.phone.trim() == sanitizedPhone }
            val updatedPlayers = current.cloudPlayers.filterNot { it.phone.trim() == sanitizedPhone }.toMutableList()
            updatedPlayers.add(
                com.example.data.model.CloudPlayerRecord(
                    phone = sanitizedPhone,
                    name = existing?.name ?: "Player",
                    balance = newBalance.coerceAtLeast(0.0),
                    level = existing?.level ?: 1,
                    isBlocked = existing?.isBlocked ?: false,
                    lastUpdated = System.currentTimeMillis()
                )
            )
            val updatedConfig = current.copy(cloudPlayers = updatedPlayers, lastUpdated = System.currentTimeMillis())
            _remoteConfig.value = updatedConfig
            val creds = GitHubRemoteService.loadCredentials(getApplication())
            if (creds.token.isNotBlank()) {
                GitHubRemoteService.publishConfigToGitHub(getApplication(), creds, updatedConfig)
            }
            _statusMessage.value = "ইউজার $sanitizedPhone এর ব্যালেন্স ৳${String.format(java.util.Locale.US, "%.2f", newBalance)} এ আপডেট ও সিঙ্ক হয়েছে।"
            soundManager.playCoinDrop(3)
        }
    }

    fun updateCloudUserLevel(phone: String, newLevel: Int) {
        viewModelScope.launch {
            val sanitizedPhone = phone.trim()
            repository.updatePlayerLevelByPhone(sanitizedPhone, newLevel)
            val current = _remoteConfig.value
            val existing = current.cloudPlayers.firstOrNull { it.phone.trim() == sanitizedPhone }
            val updatedPlayers = current.cloudPlayers.filterNot { it.phone.trim() == sanitizedPhone }.toMutableList()
            updatedPlayers.add(
                com.example.data.model.CloudPlayerRecord(
                    phone = sanitizedPhone,
                    name = existing?.name ?: "Player",
                    balance = existing?.balance ?: 0.0,
                    level = newLevel.coerceIn(1, 100),
                    isBlocked = existing?.isBlocked ?: false,
                    lastUpdated = System.currentTimeMillis()
                )
            )
            val updatedConfig = current.copy(cloudPlayers = updatedPlayers, lastUpdated = System.currentTimeMillis())
            _remoteConfig.value = updatedConfig
            val creds = GitHubRemoteService.loadCredentials(getApplication())
            if (creds.token.isNotBlank()) {
                GitHubRemoteService.publishConfigToGitHub(getApplication(), creds, updatedConfig)
            }
            _statusMessage.value = "ইউজার $sanitizedPhone এর ভিআইপি লেভেল $newLevel এ আপডেট করা হয়েছে।"
            soundManager.playButtonClick()
        }
    }

    fun toggleCloudUserBlock(phone: String) {
        viewModelScope.launch {
            val sanitizedPhone = phone.trim()
            val isBlocked = repository.togglePlayerBlockByPhone(sanitizedPhone)
            val current = _remoteConfig.value
            val existing = current.cloudPlayers.firstOrNull { it.phone.trim() == sanitizedPhone }
            val updatedPlayers = current.cloudPlayers.filterNot { it.phone.trim() == sanitizedPhone }.toMutableList()
            updatedPlayers.add(
                com.example.data.model.CloudPlayerRecord(
                    phone = sanitizedPhone,
                    name = existing?.name ?: "Player",
                    balance = existing?.balance ?: 0.0,
                    level = existing?.level ?: 1,
                    isBlocked = isBlocked,
                    lastUpdated = System.currentTimeMillis()
                )
            )
            val updatedConfig = current.copy(cloudPlayers = updatedPlayers, lastUpdated = System.currentTimeMillis())
            _remoteConfig.value = updatedConfig
            val creds = GitHubRemoteService.loadCredentials(getApplication())
            if (creds.token.isNotBlank()) {
                GitHubRemoteService.publishConfigToGitHub(getApplication(), creds, updatedConfig)
            }
            _statusMessage.value = if (isBlocked) "ইউজার $sanitizedPhone ব্লক করা হয়েছে।" else "ইউজার $sanitizedPhone আনব্লক করা হয়েছে।"
            soundManager.playButtonClick()
        }
    }

    fun addManualPlayer(phone: String, name: String, initialBalance: Double, level: Int = 1) {
        viewModelScope.launch {
            val sanitizedPhone = phone.trim()
            repository.addManualPlayerAccount(sanitizedPhone, name, initialBalance, level)
            val current = _remoteConfig.value
            val updatedPlayers = current.cloudPlayers.filterNot { it.phone.trim() == sanitizedPhone }.toMutableList()
            updatedPlayers.add(
                com.example.data.model.CloudPlayerRecord(
                    phone = sanitizedPhone,
                    name = name.trim().ifBlank { "Player" },
                    balance = initialBalance.coerceAtLeast(0.0),
                    level = level.coerceIn(1, 100),
                    lastUpdated = System.currentTimeMillis()
                )
            )
            val updatedConfig = current.copy(cloudPlayers = updatedPlayers, lastUpdated = System.currentTimeMillis())
            _remoteConfig.value = updatedConfig
            val creds = GitHubRemoteService.loadCredentials(getApplication())
            if (creds.token.isNotBlank()) {
                GitHubRemoteService.publishConfigToGitHub(getApplication(), creds, updatedConfig)
            }
            _statusMessage.value = "নতুন প্লেয়ার $sanitizedPhone যুক্ত এবং অনলাইনে সিঙ্ক হয়েছে!"
            soundManager.playCoinDrop(3)
        }
    }

    fun syncAllAccountsToCloud() {
        viewModelScope.launch {
            val currentAccounts = allRegisteredAccounts.value
            val current = _remoteConfig.value
            val mergedPlayers = current.cloudPlayers.toMutableList()
            currentAccounts.forEach { acc ->
                val idx = mergedPlayers.indexOfFirst { it.phone.trim() == acc.userPhone.trim() }
                val record = com.example.data.model.CloudPlayerRecord(
                    phone = acc.userPhone.trim(),
                    name = acc.playerName,
                    balance = acc.balance,
                    level = acc.level,
                    isBlocked = acc.status == "BLOCKED",
                    lastUpdated = System.currentTimeMillis()
                )
                if (idx >= 0) {
                    mergedPlayers[idx] = record
                } else {
                    mergedPlayers.add(record)
                }
            }
            val updatedConfig = current.copy(cloudPlayers = mergedPlayers, lastUpdated = System.currentTimeMillis())
            _remoteConfig.value = updatedConfig
            val creds = GitHubRemoteService.loadCredentials(getApplication())
            if (creds.token.isNotBlank()) {
                val res = GitHubRemoteService.publishConfigToGitHub(getApplication(), creds, updatedConfig)
                if (res.isSuccess) {
                    _statusMessage.value = "🚀 সকল ইউজার একাউন্ট (${mergedPlayers.size} টি) সফলভাবে গিটহাবে সিঙ্ক হয়েছে!"
                } else {
                    _statusMessage.value = "লোকালে সিঙ্ক হয়েছে, গিটহাব ত্রুটি: ${res.exceptionOrNull()?.message}"
                }
            } else {
                _statusMessage.value = "টোকেন ছাড়া শুধু লোকালে সিঙ্ক হয়েছে।"
            }
            soundManager.playButtonClick()
        }
    }

    private fun updateCloudDepositStatus(requestId: Long, status: String, note: String) {
        val current = _remoteConfig.value
        val updatedDeposits = current.cloudDeposits.map {
            if (it.id == requestId) it.copy(status = status, adminNote = note) else it
        }
        val targetDeposit = current.cloudDeposits.firstOrNull { it.id == requestId }
        val updatedPlayers = if (targetDeposit != null && status == "APPROVED") {
            current.cloudPlayers.map { player ->
                if (player.phone.trim() == targetDeposit.phone.trim()) {
                    player.copy(
                        balance = player.balance + targetDeposit.amount,
                        totalDeposit = player.totalDeposit + targetDeposit.amount,
                        lastUpdated = System.currentTimeMillis()
                    )
                } else player
            }
        } else current.cloudPlayers
        val updatedConfig = current.copy(
            cloudDeposits = updatedDeposits,
            cloudPlayers = updatedPlayers,
            lastUpdated = System.currentTimeMillis()
        )
        _remoteConfig.value = updatedConfig
        viewModelScope.launch {
            val creds = GitHubRemoteService.loadCredentials(getApplication())
            if (creds.token.isNotBlank()) {
                GitHubRemoteService.publishConfigToGitHub(getApplication(), creds, updatedConfig)
            }
        }
    }

    private fun updateCloudWithdrawStatus(requestId: Long, status: String, note: String) {
        val current = _remoteConfig.value
        val updatedWithdraws = current.cloudWithdraws.map {
            if (it.id == requestId) it.copy(status = status, adminNote = note) else it
        }
        val updatedConfig = current.copy(
            cloudWithdraws = updatedWithdraws,
            lastUpdated = System.currentTimeMillis()
        )
        _remoteConfig.value = updatedConfig
        viewModelScope.launch {
            val creds = GitHubRemoteService.loadCredentials(getApplication())
            if (creds.token.isNotBlank()) {
                GitHubRemoteService.publishConfigToGitHub(getApplication(), creds, updatedConfig)
            }
        }
    }
}
