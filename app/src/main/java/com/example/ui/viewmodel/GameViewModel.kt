package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AdminWalletConfigEntity
import com.example.data.db.AppDatabase
import com.example.data.db.DailyProgressEntity
import com.example.data.db.DepositRequestEntity
import com.example.data.db.LeaderboardEntryEntity
import com.example.data.db.UserProfileEntity
import com.example.data.db.WithdrawRequestEntity
import com.example.data.db.CustomGameEntity
import com.example.data.model.BulletCard
import com.example.data.model.BulletSpinResult
import com.example.data.model.CasinoGame
import com.example.data.model.DailyMission
import com.example.data.model.GameAdminConfig
import com.example.data.model.OlympusCell
import com.example.data.model.OlympusSpinResult
import com.example.data.model.SlotCard
import com.example.data.model.SpinResult
import com.example.data.model.SweetCell
import com.example.data.model.SweetSpinResult
import com.example.data.model.SymbolType
import com.example.data.model.RemoteGameConfig
import com.example.data.remote.GitHubRemoteService
import com.example.data.repository.GameAdminRepository
import com.example.data.repository.GameRepository
import com.example.engine.DeadMansBulletEngine
import com.example.engine.GatesOfOlympusEngine
import com.example.engine.SlotEngine
import com.example.engine.SweetBonanzaEngine
import com.example.util.HapticHelper
import com.example.util.SoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TurboSpeed(val spinDurationMs: Long, val stepDelayMs: Long, val label: String) {
    NORMAL(1150L, 850L, "NORMAL"),
    TURBO(650L, 480L, "TURBO"),
    SUPER_TURBO(350L, 250L, "SUPER")
}

enum class AppScreenState {
    HOME_LOBBY,
    GAME_PLAY
}

enum class ActiveModal {
    NONE,
    GAME_LOBBY,
    LEADERBOARD,
    DAILY_PROGRESS,
    PAYTABLE,
    BUY_BONUS,
    BIG_WIN,
    FREE_GAME_SPLASH,
    ADMIN_PANEL,
    DEPOSIT,
    WITHDRAW,
    REGISTER,
    BET_PICKER
}

data class GameUiState(
    val currentScreen: AppScreenState = AppScreenState.HOME_LOBBY,
    val currentGame: CasinoGame = CasinoGame.SUPER_ACE,
    val grid: List<List<SlotCard>> = SlotEngine.generateInitialGrid(),
    val deadMansGrid: List<List<BulletCard>> = DeadMansBulletEngine.generateGrid(),
    val deadMansMultiplier: Int = 1,
    val olympusGrid: List<List<OlympusCell>> = emptyList(),
    val olympusMultiplier: Int = 1,
    val sweetGrid: List<List<SweetCell>> = emptyList(),
    val sweetMultiplier: Int = 1,
    val isSpinning: Boolean = false,
    val currentBet: Double = 2.0,
    val currentWin: Double = 0.0,
    val lastSpinWin: Double = 0.0,
    val comboMultiplier: Int = 1,
    val comboStep: Int = 0,
    val comboBannerText: String = "Match 3+ cards left-to-right to trigger Cascades & Multipliers!",
    val isFreeGameActive: Boolean = false,
    val freeSpinsRemaining: Int = 0,
    val freeGameTotalWin: Double = 0.0,
    val turboSpeed: TurboSpeed = TurboSpeed.NORMAL,
    val autoSpinRemaining: Int = 0,
    val isAutoSpinActive: Boolean = false,
    val activeModal: ActiveModal = ActiveModal.NONE,
    val bigWinAmount: Double = 0.0,
    val bigWinType: String = "BIG WIN",
    val hapticsEnabled: Boolean = true,
    val soundEnabled: Boolean = true
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository
    private val adminRepository: GameAdminRepository = GameAdminRepository.getInstance(application)
    private val haptics: HapticHelper = HapticHelper(application)
    val soundManager: SoundManager = SoundManager.getInstance(application)

    private val _remoteConfig = MutableStateFlow(GitHubRemoteService.getCachedConfig(application))
    val remoteConfig: StateFlow<RemoteGameConfig> = _remoteConfig.asStateFlow()

    private val _isSyncingRemote = MutableStateFlow(false)
    val isSyncingRemote: StateFlow<Boolean> = _isSyncingRemote.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        repository = GameRepository(db.appDao())
        viewModelScope.launch {
            repository.initializeIfEmpty()
            // Sync with live GitHub remote configuration
            fetchAndApplyGitHubConfig()
        }
    }

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

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

    val adminConfig: StateFlow<GameAdminConfig> = adminRepository.configFlow

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

    val allWithdrawRequests: StateFlow<List<WithdrawRequestEntity>> = repository.allWithdrawRequestsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val customGames: StateFlow<List<CustomGameEntity>> = repository.activeCustomGamesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favoriteGameIds: StateFlow<Set<String>> = repository.favoriteGameIdsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    val deletedGameIds: StateFlow<List<String>> = repository.deletedGameIdsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private val _activeWebGame = MutableStateFlow<CustomGameEntity?>(null)
    val activeWebGame: StateFlow<CustomGameEntity?> = _activeWebGame.asStateFlow()

    fun openWebGame(game: CustomGameEntity) {
        _activeWebGame.value = game
        if (_uiState.value.soundEnabled) soundManager.playButtonClick()
    }

    fun closeWebGame() {
        _activeWebGame.value = null
        if (_uiState.value.soundEnabled) soundManager.playButtonClick()
    }

    fun toggleFavorite(gameId: String) {
        viewModelScope.launch {
            val isNowFav = repository.toggleFavoriteGame(gameId)
            if (_uiState.value.hapticsEnabled) haptics.spinTick()
            if (_uiState.value.soundEnabled) {
                if (isNowFav) soundManager.playCoinDrop(1) else soundManager.playButtonClick()
            }
        }
    }

    private var autoSpinJob: Job? = null
    private var spinJob: Job? = null

    fun toggleTurbo() {
        val nextSpeed = when (_uiState.value.turboSpeed) {
            TurboSpeed.NORMAL -> TurboSpeed.TURBO
            TurboSpeed.TURBO -> TurboSpeed.SUPER_TURBO
            TurboSpeed.SUPER_TURBO -> TurboSpeed.NORMAL
        }
        _uiState.value = _uiState.value.copy(turboSpeed = nextSpeed)
        if (_uiState.value.soundEnabled) soundManager.playButtonClick()
    }

    fun toggleHaptics() {
        _uiState.value = _uiState.value.copy(hapticsEnabled = !_uiState.value.hapticsEnabled)
    }

    fun toggleSound() {
        val next = !_uiState.value.soundEnabled
        _uiState.value = _uiState.value.copy(soundEnabled = next)
        soundManager.isEnabled = next
        if (next) soundManager.playButtonClick()
    }

    fun adjustBet(increment: Boolean) {
        if (_uiState.value.isSpinning) return
        val betOptions = listOf(
            1.0, 2.0, 5.0, 10.0, 20.0, 50.0, 100.0, 200.0,
            500.0, 1000.0, 2000.0, 5000.0, 10000.0, 20000.0
        )
        val current = _uiState.value.currentBet
        val currentIndex = betOptions.indexOfFirst { it >= current }
        val nextIndex = if (increment) {
            if (currentIndex == -1) betOptions.size - 1
            else (currentIndex + 1).coerceAtMost(betOptions.size - 1)
        } else {
            if (currentIndex == -1) 0
            else (currentIndex - 1).coerceAtLeast(0)
        }
        _uiState.value = _uiState.value.copy(currentBet = betOptions[nextIndex])
        if (_uiState.value.soundEnabled) soundManager.playButtonClick()
    }

    fun setBet(amount: Double) {
        if (_uiState.value.isSpinning) return
        val safeBet = amount.coerceIn(1.0, 20000.0)
        _uiState.value = _uiState.value.copy(currentBet = safeBet)
        if (_uiState.value.soundEnabled) soundManager.playButtonClick()
    }

    fun selectGame(game: CasinoGame) {
        if (_uiState.value.isSpinning) return
        _uiState.value = _uiState.value.copy(
            currentGame = game,
            comboBannerText = when (game) {
                CasinoGame.SUPER_ACE -> "🃏 Super Ace: 1024 Ways Golden Wild Cascades"
                CasinoGame.DEAD_MANS_BULLET -> "🤠 Dead Man's Bullet: Outlaw Bounty Respins & 9,990x Max Win!"
                CasinoGame.GATES_OF_OLYMPUS -> "⚡ Gates of Olympus: Zeus 500x Thunder Multipliers!"
                CasinoGame.SWEET_BONANZA -> "🍭 Sweet Bonanza: Candy Tumbles & 100x Sugar Bombs!"
            }
        )
        if (_uiState.value.soundEnabled) {
            when (game) {
                CasinoGame.SUPER_ACE -> soundManager.playFreeSpinsTrigger()
                CasinoGame.DEAD_MANS_BULLET -> soundManager.playWesternChord()
                CasinoGame.GATES_OF_OLYMPUS -> soundManager.playThunderStrike()
                CasinoGame.SWEET_BONANZA -> soundManager.playCandyPop()
            }
        }
    }

    fun navigateToGame(game: CasinoGame) {
        selectGame(game)
        _uiState.value = _uiState.value.copy(
            currentScreen = AppScreenState.GAME_PLAY,
            activeModal = ActiveModal.NONE
        )
    }

    fun navigateToHome() {
        stopAutoSpin()
        _uiState.value = _uiState.value.copy(
            currentScreen = AppScreenState.HOME_LOBBY,
            activeModal = ActiveModal.NONE
        )
        if (_uiState.value.soundEnabled) {
            soundManager.playButtonClick()
        }
    }

    fun showModal(modal: ActiveModal) {
        _uiState.value = _uiState.value.copy(activeModal = modal)
    }

    fun showBetPickerDialog() {
        if (_uiState.value.isSpinning) return
        _uiState.value = _uiState.value.copy(activeModal = ActiveModal.BET_PICKER)
    }

    fun dismissModal() {
        _uiState.value = _uiState.value.copy(activeModal = ActiveModal.NONE)
    }

    fun startAutoSpin(count: Int = -1) {
        val currentBet = _uiState.value.currentBet
        val currentBalance = userProfile.value.balance
        if (!_uiState.value.isFreeGameActive && currentBalance < currentBet) {
            _uiState.value = _uiState.value.copy(
                comboBannerText = "⚠️ পয়েন্ট ব্যালেন্স অপর্যাপ্ত! খেলতে পয়েন্ট ডিপোজিট করুন",
                activeModal = ActiveModal.DEPOSIT
            )
            return
        }
        _uiState.value = _uiState.value.copy(
            isAutoSpinActive = true,
            autoSpinRemaining = count
        )
        triggerSpin()
    }

    fun stopAutoSpin() {
        autoSpinJob?.cancel()
        _uiState.value = _uiState.value.copy(
            isAutoSpinActive = false,
            autoSpinRemaining = 0
        )
    }

    fun triggerSpin(forcedScatters: Int = 0) {
        if (_uiState.value.isSpinning) return
        val currentBet = _uiState.value.currentBet
        val currentBalance = userProfile.value.balance
        val isFreeGame = _uiState.value.isFreeGameActive

        if (!isFreeGame && currentBalance < currentBet) {
            _uiState.value = _uiState.value.copy(
                comboBannerText = "⚠️ পয়েন্ট ব্যালেন্স অপর্যাপ্ত! খেলতে পয়েন্ট ডিপোজিট করুন",
                activeModal = ActiveModal.DEPOSIT
            )
            return
        }

        if (_uiState.value.currentGame == CasinoGame.DEAD_MANS_BULLET) {
            spinDeadMansBullet(currentBet)
            return
        }
        if (_uiState.value.currentGame == CasinoGame.GATES_OF_OLYMPUS) {
            spinGatesOfOlympus(currentBet)
            return
        }
        if (_uiState.value.currentGame == CasinoGame.SWEET_BONANZA) {
            spinSweetBonanza(currentBet)
            return
        }

        spinJob = viewModelScope.launch {
            val turbo = _uiState.value.turboSpeed
            val isFree = _uiState.value.isFreeGameActive
            val startingMultiplier = if (isFree) 2 else 1

            if (_uiState.value.hapticsEnabled) haptics.spinTick()
            if (_uiState.value.soundEnabled) soundManager.playSpinStart()

            _uiState.value = _uiState.value.copy(
                isSpinning = true,
                currentWin = 0.0,
                comboStep = 0,
                comboMultiplier = startingMultiplier,
                comboBannerText = if (isFree) "🔥 FREE GAME! Combo Multipliers x2, x4, x6, x10!" else "Reels Spinning..."
            )

            // Spin animation
            val spinDuration = turbo.spinDurationMs
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < spinDuration) {
                // Random temporary symbols for visual blur
                val tempGrid = SlotEngine.generateInitialGrid()
                _uiState.value = _uiState.value.copy(grid = tempGrid)
                delay(60)
            }

            // Calculate actual outcome
            val config = adminConfig.value
            val result: SpinResult = SlotEngine.playSpin(
                currentBet = currentBet,
                isFreeGame = isFree,
                forcedScatters = forcedScatters,
                winRatioPercent = config.superAceWinRatio,
                maxSteps = config.maxCascadeSteps,
                goldenChance = config.goldenWildChance,
                bigJokerChance = config.bigJokerChance,
                rtpMultiplierBonus = config.rtpMultiplierBonus,
                guaranteedHouseProfit = config.guaranteedHouseProfitMode,
                houseProfitMarginPercent = config.houseProfitMarginPercent,
                maxPayoutMultiplierCap = config.maxPayoutMultiplierCap
            )

            if (_uiState.value.hapticsEnabled) haptics.reelStop()
            if (_uiState.value.soundEnabled) soundManager.playReelStop()

            _uiState.value = _uiState.value.copy(grid = result.initialGrid)
            delay(turbo.stepDelayMs / 2)

            var accumulatedSpinWin = 0.0
            var totalWildsInSpin = 0

            // Animate cascade steps if any
            if (result.cascadeSteps.isNotEmpty()) {
                for (step in result.cascadeSteps) {
                    accumulatedSpinWin += step.stepWin
                    totalWildsInSpin += step.transformedWildPositions.size

                    if (_uiState.value.hapticsEnabled) {
                        if (step.transformedWildPositions.isNotEmpty()) {
                            haptics.wildTransformation()
                        } else {
                            haptics.comboHit()
                        }
                    }

                    if (_uiState.value.soundEnabled) {
                        if (step.transformedWildPositions.isNotEmpty()) {
                            soundManager.playWildTransform()
                        } else {
                            soundManager.playCombo(step.stepIndex + 1)
                        }
                    }

                    val wildText = if (step.transformedWildPositions.isNotEmpty()) "🃏 GOLDEN WILD FLIP!" else ""
                    val bannerMsg = "COMBO ${step.stepIndex + 1} (x${step.multiplier}) +${String.format("%.2f", step.stepWin)} $wildText"

                    _uiState.value = _uiState.value.copy(
                        grid = step.grid,
                        comboMultiplier = step.multiplier,
                        comboStep = step.stepIndex + 1,
                        currentWin = accumulatedSpinWin,
                        comboBannerText = bannerMsg
                    )
                    delay(turbo.stepDelayMs)
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    comboBannerText = if (isFree) "Free Spin finished" else "Try again! Golden Cards on reels 2-4 flip to Wilds"
                )
                delay(turbo.stepDelayMs / 2)
            }

            // Enforce house profit margin & guaranteed loss policy (admin permission required for profit)
            val cappedSpinWin = enforceHouseProfitPolicy(accumulatedSpinWin, currentBet)

            // Persist to Room Database
            val effectiveBet = if (isFree) 0.0 else currentBet
            repository.recordSpin(
                bet = effectiveBet,
                win = cappedSpinWin,
                comboLevel = result.finalMultiplier,
                freeGamesTriggered = result.triggeredFreeGames,
                wildsCreated = totalWildsInSpin
            )

            // Check Big Win celebration
            val winMultiplier = if (currentBet > 0) cappedSpinWin / currentBet else 0.0
            if (winMultiplier >= 15.0) {
                if (_uiState.value.hapticsEnabled) haptics.bigWin()
                if (_uiState.value.soundEnabled) soundManager.playBigWin()
                val winType = when {
                    winMultiplier >= 50.0 -> "SUPER MEGA WIN!"
                    winMultiplier >= 30.0 -> "MEGA WIN!"
                    else -> "BIG WIN!"
                }
                _uiState.value = _uiState.value.copy(
                    bigWinAmount = cappedSpinWin,
                    bigWinType = winType,
                    activeModal = ActiveModal.BIG_WIN
                )
            }

            // Handle Free Game transitions
            var nextFreeSpins = _uiState.value.freeSpinsRemaining
            var nextIsFreeGame = _uiState.value.isFreeGameActive
            var nextFreeGameWin = _uiState.value.freeGameTotalWin

            if (isFree) {
                nextFreeGameWin += cappedSpinWin
                nextFreeSpins--
                if (result.triggeredFreeGames) {
                    nextFreeSpins += 5 // Stack 5 extra free spins
                    if (_uiState.value.soundEnabled) soundManager.playFreeSpinsTrigger()
                    _uiState.value = _uiState.value.copy(
                        comboBannerText = "🎉 +5 FREE SPINS ADDED!"
                    )
                }
                if (nextFreeSpins <= 0) {
                    nextIsFreeGame = false
                    if (_uiState.value.soundEnabled) soundManager.playCoinDrop(4)
                    _uiState.value = _uiState.value.copy(
                        comboBannerText = "🏆 Free Games Ended! Total Won: ${String.format("%.2f", nextFreeGameWin)}"
                    )
                }
            } else if (result.triggeredFreeGames) {
                nextIsFreeGame = true
                nextFreeSpins = 10
                nextFreeGameWin = 0.0
                if (_uiState.value.soundEnabled) soundManager.playFreeSpinsTrigger()
                _uiState.value = _uiState.value.copy(
                    activeModal = ActiveModal.FREE_GAME_SPLASH,
                    comboBannerText = "🎉 3+ SCATTERS! 10 FREE GAMES WON!"
                )
            }

            _uiState.value = _uiState.value.copy(
                isSpinning = false,
                currentWin = cappedSpinWin,
                lastSpinWin = cappedSpinWin,
                isFreeGameActive = nextIsFreeGame,
                freeSpinsRemaining = nextFreeSpins,
                freeGameTotalWin = nextFreeGameWin
            )

            // Auto-spin next round handler
            handleAutoSpinNext()
        }
    }

    private fun enforceHouseProfitPolicy(rawWin: Double, currentBet: Double): Double {
        val config = adminConfig.value
        if (config.allowPlayerProfit) {
            return rawWin
        }
        // Admin policy: User cannot profit; for every 100 spent, max 60 won (40 lost). House keeps at least 30-40%
        val profile = userProfile.value
        val prospectiveTotalSpent = profile.lifetimeBet + currentBet
        val maxAllowedLifetimeWin = prospectiveTotalSpent * config.maxPlayerReturnRatio // default 0.60
        val remainingWinQuota = (maxAllowedLifetimeWin - profile.lifetimeWon).coerceAtLeast(0.0)
        val maxSingleSpinWin = currentBet * config.maxPlayerReturnRatio
        val maxAllowedWin = minOf(maxSingleSpinWin, remainingWinQuota)

        return rawWin.coerceAtMost(maxAllowedWin)
    }

    private suspend fun handleAutoSpinNext() {
        if (_uiState.value.isAutoSpinActive) {
            val balance = userProfile.value.balance
            val bet = _uiState.value.currentBet
            val isFreeGame = _uiState.value.isFreeGameActive
            if (!isFreeGame && balance < bet) {
                stopAutoSpin()
                _uiState.value = _uiState.value.copy(
                    comboBannerText = "⚠️ পয়েন্ট ব্যালেন্স অপর্যাপ্ত! অটো বেটিং বন্ধ করা হয়েছে"
                )
                return
            }
            val remaining = _uiState.value.autoSpinRemaining
            if (remaining > 1) {
                _uiState.value = _uiState.value.copy(autoSpinRemaining = remaining - 1)
            } else if (remaining == 1) {
                stopAutoSpin()
                return
            }
            delay(350)
            triggerSpin()
        }
    }

    private fun spinDeadMansBullet(currentBet: Double) {
        spinJob = viewModelScope.launch {
            val turbo = _uiState.value.turboSpeed
            if (_uiState.value.hapticsEnabled) haptics.spinTick()
            if (_uiState.value.soundEnabled) {
                soundManager.playRevolverSpin()
                soundManager.playGunCock()
            }

            _uiState.value = _uiState.value.copy(
                isSpinning = true,
                currentWin = 0.0,
                deadMansMultiplier = 1,
                comboBannerText = "🤠 Revolver spinning... Bullet Respins ready!"
            )

            val spinDuration = turbo.spinDurationMs
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < spinDuration) {
                val temp = DeadMansBulletEngine.generateGrid()
                _uiState.value = _uiState.value.copy(deadMansGrid = temp)
                delay(60)
            }

            val config = adminConfig.value
            val result = DeadMansBulletEngine.playSpin(
                currentBet = currentBet,
                winRatioPercent = config.deadMansBulletWinRatio,
                rtpBonus = config.rtpMultiplierBonus,
                guaranteedHouseProfit = config.guaranteedHouseProfitMode,
                houseProfitMarginPercent = config.houseProfitMarginPercent,
                maxPayoutMultiplierCap = config.maxPayoutMultiplierCap
            )

            if (_uiState.value.hapticsEnabled) haptics.reelStop()
            if (_uiState.value.soundEnabled) soundManager.playReelStop()

            val finalWin = enforceHouseProfitPolicy(result.totalWin, currentBet)

            _uiState.value = _uiState.value.copy(
                deadMansGrid = result.grid,
                deadMansMultiplier = result.finalMultiplier,
                currentWin = finalWin,
                lastSpinWin = finalWin,
                comboBannerText = if (finalWin > 0) {
                    if (result.triggeredRespins) "🔥 OUTLAW BOUNTY RESPIN! +${String.format("%.2f", finalWin)} pts"
                    else "🎯 ${result.winType}: +${String.format("%.2f", finalWin)} pts"
                } else "Western Showdown - No match. Spin again!"
            )

            if (finalWin > 0 && _uiState.value.soundEnabled) {
                soundManager.playGunshotRicochet()
                if (result.triggeredRespins) {
                    delay(250)
                    soundManager.playWesternChord()
                }
            }

            repository.recordSpin(
                bet = currentBet,
                win = finalWin,
                comboLevel = result.finalMultiplier,
                freeGamesTriggered = result.triggeredRespins,
                wildsCreated = if (result.triggeredRespins) 1 else 0
            )

            val mult = if (currentBet > 0) finalWin / currentBet else 0.0
            if (mult >= 15.0) {
                if (_uiState.value.hapticsEnabled) haptics.bigWin()
                if (_uiState.value.soundEnabled) soundManager.playBigWin()
                _uiState.value = _uiState.value.copy(
                    bigWinAmount = finalWin,
                    bigWinType = if (mult >= 50.0) "OUTLAW JACKPOT!" else "DEAD MAN BIG WIN!",
                    activeModal = ActiveModal.BIG_WIN
                )
            }

            _uiState.value = _uiState.value.copy(isSpinning = false)
            handleAutoSpinNext()
        }
    }

    private fun spinGatesOfOlympus(currentBet: Double) {
        spinJob = viewModelScope.launch {
            val turbo = _uiState.value.turboSpeed
            if (_uiState.value.hapticsEnabled) haptics.spinTick()
            if (_uiState.value.soundEnabled) soundManager.playSpinStart()

            _uiState.value = _uiState.value.copy(
                isSpinning = true,
                currentWin = 0.0,
                olympusMultiplier = 1,
                comboBannerText = "⚡ Zeus gathers lightning... 500x Multipliers incoming!"
            )

            val spinDuration = turbo.spinDurationMs
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < spinDuration) {
                val temp = GatesOfOlympusEngine.playSpin(currentBet, 0).grid
                _uiState.value = _uiState.value.copy(olympusGrid = temp)
                delay(60)
            }

            val config = adminConfig.value
            val result = GatesOfOlympusEngine.playSpin(
                currentBet = currentBet,
                winRatioPercent = config.gatesOfOlympusWinRatio,
                rtpBonus = config.rtpMultiplierBonus,
                guaranteedHouseProfit = config.guaranteedHouseProfitMode,
                houseProfitMarginPercent = config.houseProfitMarginPercent,
                maxPayoutMultiplierCap = config.maxPayoutMultiplierCap
            )

            if (_uiState.value.hapticsEnabled) haptics.reelStop()
            if (_uiState.value.soundEnabled) soundManager.playReelStop()

            if (result.zeusMultiplier > 1 && _uiState.value.soundEnabled) {
                soundManager.playThunderStrike()
            }

            val finalWin = enforceHouseProfitPolicy(result.totalWin, currentBet)

            _uiState.value = _uiState.value.copy(
                olympusGrid = result.grid,
                olympusMultiplier = result.zeusMultiplier,
                currentWin = finalWin,
                lastSpinWin = finalWin,
                comboBannerText = if (finalWin > 0) {
                    "⚡ ZEUS THUNDER HIT! +${String.format("%.2f", finalWin)} pts (x${result.zeusMultiplier})"
                } else "Gates of Olympus - Spin again for Zeus Multipliers!"
            )

            if (finalWin > 0 && _uiState.value.soundEnabled) {
                soundManager.playCoinDrop(3)
            }

            repository.recordSpin(
                bet = currentBet,
                win = finalWin,
                comboLevel = result.zeusMultiplier,
                freeGamesTriggered = result.triggeredFreeSpins,
                wildsCreated = 0
            )

            val mult = if (currentBet > 0) finalWin / currentBet else 0.0
            if (mult >= 15.0) {
                if (_uiState.value.hapticsEnabled) haptics.bigWin()
                if (_uiState.value.soundEnabled) soundManager.playBigWin()
                _uiState.value = _uiState.value.copy(
                    bigWinAmount = finalWin,
                    bigWinType = "ZEUS MEGA WIN!",
                    activeModal = ActiveModal.BIG_WIN
                )
            }

            _uiState.value = _uiState.value.copy(isSpinning = false)
            handleAutoSpinNext()
        }
    }

    private fun spinSweetBonanza(currentBet: Double) {
        spinJob = viewModelScope.launch {
            val turbo = _uiState.value.turboSpeed
            if (_uiState.value.hapticsEnabled) haptics.spinTick()
            if (_uiState.value.soundEnabled) soundManager.playSpinStart()

            _uiState.value = _uiState.value.copy(
                isSpinning = true,
                currentWin = 0.0,
                sweetMultiplier = 1,
                comboBannerText = "🍭 Candies tumbling... Sugar Bombs up to 100x!"
            )

            val spinDuration = turbo.spinDurationMs
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < spinDuration) {
                val temp = SweetBonanzaEngine.playSpin(currentBet, 0).grid
                _uiState.value = _uiState.value.copy(sweetGrid = temp)
                delay(60)
            }

            val config = adminConfig.value
            val result = SweetBonanzaEngine.playSpin(
                currentBet = currentBet,
                winRatioPercent = config.sweetBonanzaWinRatio,
                rtpBonus = config.rtpMultiplierBonus,
                guaranteedHouseProfit = config.guaranteedHouseProfitMode,
                houseProfitMarginPercent = config.houseProfitMarginPercent,
                maxPayoutMultiplierCap = config.maxPayoutMultiplierCap
            )

            if (_uiState.value.hapticsEnabled) haptics.reelStop()
            if (_uiState.value.soundEnabled) soundManager.playReelStop()

            if (result.totalWin > 0 && _uiState.value.soundEnabled) {
                soundManager.playCandyPop()
            }

            val finalWin = enforceHouseProfitPolicy(result.totalWin, currentBet)

            _uiState.value = _uiState.value.copy(
                sweetGrid = result.grid,
                sweetMultiplier = result.bombMultiplier,
                currentWin = finalWin,
                lastSpinWin = finalWin,
                comboBannerText = if (finalWin > 0) {
                    "🍬 SUGAR BOMB HIT! +${String.format("%.2f", finalWin)} pts (x${result.bombMultiplier})"
                } else "Sweet Bonanza - Spin again for Sugar Candies!"
            )

            repository.recordSpin(
                bet = currentBet,
                win = finalWin,
                comboLevel = result.bombMultiplier,
                freeGamesTriggered = result.triggeredFreeSpins,
                wildsCreated = 0
            )

            val mult = if (currentBet > 0) finalWin / currentBet else 0.0
            if (mult >= 15.0) {
                if (_uiState.value.hapticsEnabled) haptics.bigWin()
                if (_uiState.value.soundEnabled) soundManager.playBigWin()
                _uiState.value = _uiState.value.copy(
                    bigWinAmount = finalWin,
                    bigWinType = "SUGAR BOMB WIN!",
                    activeModal = ActiveModal.BIG_WIN
                )
            }

            _uiState.value = _uiState.value.copy(isSpinning = false)
            handleAutoSpinNext()
        }
    }

    fun buyBonus() {
        val cost = _uiState.value.currentBet * 40.0
        val balance = userProfile.value.balance
        if (balance < cost) {
            _uiState.value = _uiState.value.copy(
                comboBannerText = "Insufficient balance to buy Free Games ($cost pts needed)!"
            )
            return
        }

        viewModelScope.launch {
            repository.recordSpin(
                bet = cost,
                win = 0.0,
                comboLevel = 1,
                freeGamesTriggered = true,
                wildsCreated = 0
            )
            dismissModal()
            triggerSpin(forcedScatters = 3)
        }
    }

    fun claimMission(mission: DailyMission) {
        if (!mission.isCompleted || mission.isClaimed) return
        viewModelScope.launch {
            repository.claimMissionReward(mission.id, mission.rewardPoints, mission.rewardXp)
            if (_uiState.value.hapticsEnabled) haptics.comboHit()
            if (_uiState.value.soundEnabled) soundManager.playCoinDrop(3)
        }
    }

    fun showDepositDialog() {
        showModal(ActiveModal.DEPOSIT)
    }

    fun showWithdrawDialog() {
        showModal(ActiveModal.WITHDRAW)
    }

    fun showRegisterDialog() {
        showModal(ActiveModal.REGISTER)
    }

    fun registerPlayer(phone: String, name: String) {
        viewModelScope.launch {
            repository.registerUser(phone, name)
            dismissModal()
            if (_uiState.value.soundEnabled) soundManager.playCoinDrop(3)
        }
    }

    fun registerPlayerWithDevice(
        phone: String,
        name: String,
        deviceId: String,
        deviceModel: String,
        referralCode: String = "",
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val (success, message) = repository.registerUserWithDevice(
                phone = phone,
                name = name,
                deviceId = deviceId,
                deviceModel = deviceModel,
                subAdminReferralCode = referralCode
            )
            if (success) {
                dismissModal()
                if (_uiState.value.soundEnabled) soundManager.playCoinDrop(3)
            }
            onResult(success, message)
        }
    }

    fun loginOrRecoverPlayer(
        phone: String,
        deviceId: String,
        deviceModel: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val (success, message) = repository.loginOrRecoverUserWithPhone(
                phone = phone,
                deviceId = deviceId,
                deviceModel = deviceModel
            )
            if (success) {
                dismissModal()
                if (_uiState.value.soundEnabled) soundManager.playCoinDrop(3)
            }
            onResult(success, message)
        }
    }

    fun submitDeposit(
        amount: Double,
        method: String,
        adminWalletNumber: String,
        trxId: String,
        channel: String = "PASSPAY",
        screenshotUri: String? = null,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val phone = userProfile.value.userPhone
            repository.submitDepositRequest(
                userPhone = phone,
                amount = amount,
                method = method,
                channel = channel,
                adminWalletNumber = adminWalletNumber,
                trxId = trxId,
                screenshotUri = screenshotUri
            )
            if (_uiState.value.soundEnabled) soundManager.playButtonClick()
            onSuccess()
        }
    }

    fun submitWithdraw(
        amount: Double,
        method: String,
        userPhone: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val (success, message) = repository.submitWithdrawRequest(
                userPhone = userPhone,
                amount = amount,
                method = method
            )
            if (success) {
                if (_uiState.value.soundEnabled) soundManager.playButtonClick()
            }
            onResult(success, message)
        }
    }

    fun reloadBalance() {
        viewModelScope.launch {
            repository.reloadBalance(2500.0)
            if (_uiState.value.hapticsEnabled) haptics.spinTick()
            if (_uiState.value.soundEnabled) soundManager.playCoinDrop(3)
        }
    }

    fun updateAdminConfig(newConfig: GameAdminConfig) {
        adminRepository.updateConfig(newConfig)
    }

    fun setExactBalance(amount: Double) {
        viewModelScope.launch {
            repository.reloadBalance(amount)
        }
    }

    fun getMissions(progress: DailyProgressEntity): List<DailyMission> {
        return repository.buildDailyMissions(progress)
    }

    fun refreshRemoteConfig(onComplete: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            _isSyncingRemote.value = true
            val success = fetchAndApplyGitHubConfig()
            _isSyncingRemote.value = false
            onComplete?.invoke(success)
        }
    }

    private suspend fun fetchAndApplyGitHubConfig(): Boolean {
        return try {
            val result = GitHubRemoteService.fetchRemoteConfig(getApplication())
            if (result.isSuccess) {
                val cfg = result.getOrThrow()
                _remoteConfig.value = cfg
                GitHubRemoteService.syncRemoteConfigToPlayerState(
                    context = getApplication(),
                    config = cfg,
                    repository = repository,
                    adminRepository = adminRepository
                )
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}
