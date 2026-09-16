package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data.model.CasinoGame
import com.example.ui.components.ControlPanel
import com.example.ui.components.DeadMansBulletReelsView
import com.example.ui.components.GameTopBar
import com.example.ui.components.GatesOfOlympusReelsView
import com.example.ui.components.MultiplierHeader
import com.example.ui.components.SlotReelsView
import com.example.ui.components.SweetBonanzaReelsView
import com.example.ui.dialogs.BetPickerDialog
import com.example.ui.dialogs.BigWinOverlay
import com.example.ui.dialogs.BuyBonusDialog
import com.example.ui.dialogs.DailyProgressDialog
import com.example.ui.dialogs.DepositDialog
import com.example.ui.dialogs.FreeGameSplash
import com.example.ui.dialogs.GameSelectDialog
import com.example.ui.dialogs.LeaderboardDialog
import com.example.ui.dialogs.PaytableRulesDialog
import com.example.ui.dialogs.RegisterDialog
import com.example.ui.dialogs.WithdrawDialog
import com.example.ui.viewmodel.ActiveModal
import com.example.ui.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val todayProgress by viewModel.todayProgress.collectAsState()
    val leaderboard by viewModel.leaderboard.collectAsState()
    val walletConfig by viewModel.walletConfig.collectAsState()
    val allDepositRequests by viewModel.allDepositRequests.collectAsState()
    val allWithdrawRequests by viewModel.allWithdrawRequests.collectAsState()

    val missions = remember(todayProgress) {
        viewModel.getMissions(todayProgress)
    }
    val claimableCount = missions.count { it.isCompleted && !it.isClaimed }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF070312)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF0F0624),
                            Color(0xFF090315),
                            Color(0xFF04010A)
                        )
                    )
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 600.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Action Bar
                GameTopBar(
                    currentGame = uiState.currentGame,
                    claimableMissionsCount = claimableCount,
                    soundEnabled = uiState.soundEnabled,
                    onBackToHome = { viewModel.navigateToHome() },
                    onOpenLobby = { viewModel.showModal(ActiveModal.GAME_LOBBY) },
                    onOpenLeaderboard = { viewModel.showModal(ActiveModal.LEADERBOARD) },
                    onOpenDailyProgress = { viewModel.showModal(ActiveModal.DAILY_PROGRESS) },
                    onOpenPaytable = { viewModel.showModal(ActiveModal.PAYTABLE) },
                    onOpenBuyBonus = { viewModel.showModal(ActiveModal.BUY_BONUS) },
                    onToggleSound = { viewModel.toggleSound() }
                )

                // Multiplier Ladder Header
                MultiplierHeader(
                    currentMultiplier = uiState.comboMultiplier,
                    isFreeGame = uiState.isFreeGameActive,
                    bannerText = uiState.comboBannerText
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Casino Reels Grid based on active game
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when (uiState.currentGame) {
                        CasinoGame.SUPER_ACE -> {
                            SlotReelsView(
                                grid = uiState.grid,
                                isSpinning = uiState.isSpinning
                            )
                        }
                        CasinoGame.DEAD_MANS_BULLET -> {
                            DeadMansBulletReelsView(
                                grid = uiState.deadMansGrid,
                                isSpinning = uiState.isSpinning,
                                winMultiplier = uiState.deadMansMultiplier
                            )
                        }
                        CasinoGame.GATES_OF_OLYMPUS -> {
                            GatesOfOlympusReelsView(
                                grid = uiState.olympusGrid,
                                isSpinning = uiState.isSpinning,
                                zeusMultiplier = uiState.olympusMultiplier
                            )
                        }
                        CasinoGame.SWEET_BONANZA -> {
                            SweetBonanzaReelsView(
                                grid = uiState.sweetGrid,
                                isSpinning = uiState.isSpinning,
                                bombMultiplier = uiState.sweetMultiplier
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Interactive Control Panel
                ControlPanel(
                    currentBet = uiState.currentBet,
                    currentWin = uiState.currentWin,
                    balance = userProfile.balance,
                    playerLevel = userProfile.level,
                    isSpinning = uiState.isSpinning,
                    turboSpeed = uiState.turboSpeed,
                    isAutoSpinActive = uiState.isAutoSpinActive,
                    autoSpinRemaining = uiState.autoSpinRemaining,
                    isFreeGame = uiState.isFreeGameActive,
                    freeSpinsRemaining = uiState.freeSpinsRemaining,
                    onSpinClick = { viewModel.triggerSpin() },
                    onBetAdjust = { increment -> viewModel.adjustBet(increment) },
                    onBetClick = { viewModel.showBetPickerDialog() },
                    onTurboClick = { viewModel.toggleTurbo() },
                    onAutoSpinToggle = {
                        if (uiState.isAutoSpinActive) {
                            viewModel.stopAutoSpin()
                        } else {
                            viewModel.startAutoSpin(-1)
                        }
                    },
                    onReloadBalance = { viewModel.showDepositDialog() }
                )
            }

            // Dialog Overlays
            when (uiState.activeModal) {
                ActiveModal.GAME_LOBBY -> {
                    GameSelectDialog(
                        currentGame = uiState.currentGame,
                        onSelectGame = { game -> viewModel.selectGame(game) },
                        onDismiss = { viewModel.dismissModal() },
                        onNavigateHome = { viewModel.navigateToHome() }
                    )
                }
                ActiveModal.LEADERBOARD -> {
                    LeaderboardDialog(
                        leaderboardEntries = leaderboard,
                        onDismiss = { viewModel.dismissModal() }
                    )
                }
                ActiveModal.DAILY_PROGRESS -> {
                    DailyProgressDialog(
                        dailyProgress = todayProgress,
                        userProfile = userProfile,
                        missions = missions,
                        onClaimMission = { mission -> viewModel.claimMission(mission) },
                        onDismiss = { viewModel.dismissModal() }
                    )
                }
                ActiveModal.PAYTABLE -> {
                    PaytableRulesDialog(
                        currentBet = uiState.currentBet,
                        onDismiss = { viewModel.dismissModal() }
                    )
                }
                ActiveModal.BUY_BONUS -> {
                    BuyBonusDialog(
                        currentBet = uiState.currentBet,
                        userBalance = userProfile.balance,
                        onConfirmBuy = { viewModel.buyBonus() },
                        onDismiss = { viewModel.dismissModal() }
                    )
                }
                ActiveModal.BIG_WIN -> {
                    BigWinOverlay(
                        winAmount = uiState.bigWinAmount,
                        winType = uiState.bigWinType,
                        onDismiss = { viewModel.dismissModal() }
                    )
                }
                ActiveModal.FREE_GAME_SPLASH -> {
                    FreeGameSplash(
                        spinsAwarded = 10,
                        onStartFreeSpins = {
                            viewModel.dismissModal()
                            viewModel.triggerSpin()
                        }
                    )
                }
                ActiveModal.DEPOSIT -> {
                    DepositDialog(
                        userPhone = userProfile.userPhone,
                        walletConfig = walletConfig,
                        depositHistory = allDepositRequests.filter { it.userPhone == userProfile.userPhone || userProfile.userPhone.isEmpty() },
                        onSubmitDeposit = { amount, method, adminWallet, trxId ->
                            viewModel.submitDeposit(
                                amount = amount,
                                method = method,
                                adminWalletNumber = adminWallet,
                                trxId = trxId
                            )
                        },
                        onDismiss = { viewModel.dismissModal() }
                    )
                }
                ActiveModal.WITHDRAW -> {
                    WithdrawDialog(
                        userPhone = userProfile.userPhone,
                        currentBalance = userProfile.balance,
                        withdrawHistory = allWithdrawRequests.filter { it.userPhone == userProfile.userPhone || userProfile.userPhone.isEmpty() },
                        requiredTurnover = userProfile.requiredTurnover,
                        completedTurnover = userProfile.completedTurnover,
                        pendingTurnover = userProfile.pendingTurnover,
                        onSubmitWithdraw = { amount, method, phone, onResult ->
                            viewModel.submitWithdraw(
                                amount = amount,
                                method = method,
                                userPhone = phone,
                                onResult = onResult
                            )
                        },
                        onDismiss = { viewModel.dismissModal() }
                    )
                }
                ActiveModal.REGISTER -> {
                    RegisterDialog(
                        initialPhone = userProfile.userPhone,
                        onRegister = { phone, name ->
                            viewModel.registerPlayer(phone, name)
                        }
                    )
                }
                ActiveModal.BET_PICKER -> {
                    BetPickerDialog(
                        currentBet = uiState.currentBet,
                        userBalance = userProfile.balance,
                        onSelectBet = { bet -> viewModel.setBet(bet) },
                        onDismiss = { viewModel.dismissModal() }
                    )
                }
                ActiveModal.ADMIN_PANEL -> {
                    // Hidden in player dashboard - accessible exclusively via dedicated Admin App
                }
                ActiveModal.NONE -> {}
            }
        }
    }
}
