package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FiberNew
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.db.CustomGameEntity
import com.example.data.model.CasinoGame
import com.example.data.model.VideoGameCatalog
import com.example.data.model.VideoGameCategory
import com.example.data.model.VideoGameModel
import com.example.ui.components.VideoBottomNavTab
import com.example.ui.components.VideoCasinoBottomNav
import com.example.ui.components.VideoCasinoTopBar
import com.example.ui.components.VideoAnnouncementMarquee
import com.example.ui.components.VideoCategoryNavBar
import com.example.ui.components.VideoFloatingSocials
import com.example.ui.components.VideoGameCard3Col
import com.example.ui.components.VideoGameLaunchDialog
import com.example.ui.components.VideoJackpotWinnerSection
import com.example.ui.components.VideoPromoBannerCarousel
import com.example.ui.dialogs.BigWinOverlay
import com.example.ui.dialogs.DailyProgressDialog
import com.example.ui.dialogs.DepositDialog
import com.example.ui.dialogs.LeaderboardDialog
import com.example.ui.dialogs.PaytableRulesDialog
import com.example.ui.dialogs.RegisterDialog
import com.example.ui.dialogs.VideoInviteEarnDialog
import com.example.ui.dialogs.VideoPlayerProfileDialog
import com.example.ui.dialogs.VideoPromotionDialog
import com.example.ui.dialogs.WebGameDialog
import com.example.ui.dialogs.WithdrawDialog
import com.example.ui.viewmodel.ActiveModal
import com.example.ui.viewmodel.GameViewModel

sealed class DisplayGameItem {
    abstract val id: String
    abstract val title: String
    abstract val provider: String
    abstract val isFavorite: Boolean

    data class NativeSlotGame(
        val game: CasinoGame,
        override val isFavorite: Boolean
    ) : DisplayGameItem() {
        override val id: String get() = game.name
        override val title: String get() = game.title
        override val provider: String get() = game.provider
    }

    data class CustomWebGame(
        val entity: CustomGameEntity,
        override val isFavorite: Boolean
    ) : DisplayGameItem() {
        override val id: String get() = entity.id
        override val title: String get() = entity.title
        override val provider: String get() = entity.provider
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
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
    val customGames by viewModel.customGames.collectAsState()
    val favoriteGameIds by viewModel.favoriteGameIds.collectAsState()
    val deletedGameIds by viewModel.deletedGameIds.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val activeWebGame by viewModel.activeWebGame.collectAsState()
    val remoteConfig by viewModel.remoteConfig.collectAsState()
    val isSyncingRemote by viewModel.isSyncingRemote.collectAsState()

    val missions = remember(todayProgress) {
        viewModel.getMissions(todayProgress)
    }
    val claimableCount = missions.count { it.isCompleted && !it.isClaimed }

    var activeCategory by remember { mutableStateOf(VideoGameCategory.HOT) }
    var activeBottomTab by remember { mutableStateOf(VideoBottomNavTab.HOME) }
    var selectedGameForLaunch by remember { mutableStateOf<VideoGameModel?>(null) }
    var selectedSlotProvider by remember { mutableStateOf("ALL") }
    var selectedSlotSubFilter by remember { mutableStateOf("ALL") }
    var showPromotionModal by remember { mutableStateOf(false) }
    var showInviteModal by remember { mutableStateOf(false) }
    var showProfileModal by remember { mutableStateOf(false) }

    // Periodic live sync with remote GitHub config (every 25 seconds) so player app stays controlled online
    LaunchedEffect(Unit) {
        while (true) {
            viewModel.refreshRemoteConfig()
            kotlinx.coroutines.delay(25_000L)
        }
    }

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
                    .widthIn(max = 600.dp)
            ) {
                // Top Header Bar (UUOK / Super Ace Style as seen in video)
                VideoCasinoTopBar(
                    balance = userProfile.balance,
                    onOpenDeposit = { viewModel.showDepositDialog() },
                    onRefreshBalance = { viewModel.refreshRemoteConfig() }
                )

                // Main Scrollable Content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    // 1. Promotional Banner Carousel (50% bonus, iPhone 16 Pro Max draw, 8888 bonus)
                    item {
                        VideoPromoBannerCarousel(
                            onBannerClick = { showPromotionModal = true }
                        )
                    }

                    // 2. Announcement Marquee Ticker
                    item {
                        VideoAnnouncementMarquee(
                            notice = remoteConfig.announcementNotice
                        )
                    }

                    // 3. Category Navigation Bar (Hot, Slots, Fish, Live, Poker, Sports)
                    item {
                        VideoCategoryNavBar(
                            selectedCategory = activeCategory,
                            onSelectCategory = { activeCategory = it }
                        )
                    }

                    // Category Specific View Rendering
                    when (activeCategory) {
                        VideoGameCategory.HOT -> {
                            // Jackpot Winner Rolling Counter (incrementing dynamically)
                            item {
                                VideoJackpotWinnerSection(
                                    onGameClick = { gameName ->
                                        val matched = VideoGameCatalog.allGames.find {
                                            it.title.equals(gameName, ignoreCase = true)
                                        } ?: VideoGameCatalog.allGames.first()
                                        selectedGameForLaunch = matched
                                    }
                                )
                            }

                            // Hot Games Section Header
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "🔥", fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Hot",
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 16.sp
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFF1F0D42))
                                            .border(1.dp, Color(0xFF6B21A8), RoundedCornerShape(12.dp))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "<   All   >",
                                            color = Color(0xFFFBBF24),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // 3-Column Grid of Hot Games
                            val hotGames = VideoGameCatalog.getByCategory(VideoGameCategory.HOT).filterNot {
                                deletedGameIds.contains(it.id) || deletedGameIds.contains(it.title)
                            }
                            val chunkedHot = hotGames.chunked(3)
                            items(chunkedHot) { rowGames ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    rowGames.forEach { game ->
                                        VideoGameCard3Col(
                                            game = game,
                                            isFavorite = favoriteGameIds.contains(game.id),
                                            onToggleFavorite = { viewModel.toggleFavorite(game.id) },
                                            onClick = { selectedGameForLaunch = game },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    repeat(3 - rowGames.size) {
                                        Spacer(modifier = Modifier.weight(1f).padding(4.dp))
                                    }
                                }
                            }
                        }

                        VideoGameCategory.SLOTS -> {
                            // Search Bar
                            item {
                                HomeSearchBar(
                                    searchQuery = searchQuery,
                                    onQueryChange = { viewModel.setSearchQuery(it) }
                                )
                            }

                            // Provider Filters (JL, PG, PP, SW, SPRIBE, BNG)
                            item {
                                val providers = listOf("ALL", "JILI", "PG Soft", "Pragmatic Play", "Skywind Group", "Spribe", "Booongo")
                                LazyRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    items(providers) { prov ->
                                        val isSel = selectedSlotProvider == prov
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSel) Color(0xFFCA8A04) else Color(0xFF1E1038))
                                                .border(1.dp, if (isSel) Color(0xFFFEF08A) else Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                                                .clickable { selectedSlotProvider = prov }
                                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                        ) {
                                            Text(
                                                text = prov,
                                                color = if (isSel) Color.Black else Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSel) FontWeight.Black else FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }

                            // Sub-filters (All, Hot, Recent game, Favorites)
                            item {
                                val subFilters = listOf("ALL" to "All", "HOT" to "Hot", "RECENT" to "Recent game", "FAVORITES" to "Favorites")
                                LazyRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    items(subFilters) { (key, label) ->
                                        val isSel = selectedSlotSubFilter == key
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(if (isSel) Color(0xFF22C55E) else Color(0x22FFFFFF))
                                                .clickable { selectedSlotSubFilter = key }
                                                .padding(horizontal = 12.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = label,
                                                color = if (isSel) Color.Black else Color(0xFFE2E8F0),
                                                fontSize = 10.sp,
                                                fontWeight = if (isSel) FontWeight.Black else FontWeight.Normal
                                            )
                                        }
                                    }
                                }
                            }

                            // Filtered Slot Games
                            val slotGames = VideoGameCatalog.allGames.filter { game ->
                                val notDeleted = !deletedGameIds.contains(game.id) && !deletedGameIds.contains(game.title)
                                val matchesProv = selectedSlotProvider == "ALL" || game.provider.contains(selectedSlotProvider, ignoreCase = true)
                                val matchesSearch = searchQuery.isBlank() || game.title.contains(searchQuery, ignoreCase = true) || game.provider.contains(searchQuery, ignoreCase = true)
                                val matchesSub = when (selectedSlotSubFilter) {
                                    "HOT" -> game.isHot
                                    "FAVORITES" -> favoriteGameIds.contains(game.id)
                                    else -> true
                                }
                                notDeleted && matchesProv && matchesSearch && matchesSub
                            }

                            val chunkedSlots = slotGames.chunked(3)
                            items(chunkedSlots) { rowGames ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    rowGames.forEach { game ->
                                        VideoGameCard3Col(
                                            game = game,
                                            isFavorite = favoriteGameIds.contains(game.id),
                                            onToggleFavorite = { viewModel.toggleFavorite(game.id) },
                                            onClick = { selectedGameForLaunch = game },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    repeat(3 - rowGames.size) {
                                        Spacer(modifier = Modifier.weight(1f).padding(4.dp))
                                    }
                                }
                            }

                            // Pagination Bar (< 1 2 3 ... 17 >)
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    listOf("<", "1", "2", "3", "...", "17", ">").forEach { p ->
                                        Box(
                                            modifier = Modifier
                                                .padding(horizontal = 3.dp)
                                                .size(28.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (p == "1") Color(0xFFCA8A04) else Color(0xFF1E1038))
                                                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(6.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = p,
                                                color = if (p == "1") Color.Black else Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        else -> {
                            // FISH, LIVE, POKER, SPORTS Categories
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = activeCategory.icon, fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${activeCategory.displayName} Games",
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp
                                    )
                                }
                            }

                            val catGames = VideoGameCatalog.getByCategory(activeCategory).filterNot {
                                deletedGameIds.contains(it.id) || deletedGameIds.contains(it.title)
                            }
                            val chunkedCat = catGames.chunked(3)
                            items(chunkedCat) { rowGames ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    rowGames.forEach { game ->
                                        VideoGameCard3Col(
                                            game = game,
                                            isFavorite = favoriteGameIds.contains(game.id),
                                            onToggleFavorite = { viewModel.toggleFavorite(game.id) },
                                            onClick = { selectedGameForLaunch = game },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    repeat(3 - rowGames.size) {
                                        Spacer(modifier = Modifier.weight(1f).padding(4.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Also display Custom Web Games added by Admin
                    val filteredCustomGames = customGames.filterNot {
                        deletedGameIds.contains(it.id) || deletedGameIds.contains(it.title)
                    }
                    if (filteredCustomGames.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "✨", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "এডমিন স্পেশাল গেম (${filteredCustomGames.size}টি)",
                                    color = Color(0xFFFDE047),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                            }
                        }
                        items(filteredCustomGames, key = { it.id }) { cGame ->
                            CustomGameCardItem(
                                game = cGame,
                                isFavorite = favoriteGameIds.contains(cGame.id),
                                onToggleFavorite = { viewModel.toggleFavorite(cGame.id) },
                                onPlay = { viewModel.openWebGame(cGame) }
                            )
                        }
                    }
                }

                // 4. Video Casino Bottom Navigation Bar (5 tabs with Center Golden Button)
                VideoCasinoBottomNav(
                    selectedTab = activeBottomTab,
                    onSelectTab = { tab ->
                        when (tab) {
                            VideoBottomNavTab.HOME -> {
                                activeBottomTab = VideoBottomNavTab.HOME
                            }
                            VideoBottomNavTab.PROMOTION -> {
                                showPromotionModal = true
                            }
                            VideoBottomNavTab.INVITE -> {
                                showInviteModal = true
                            }
                            VideoBottomNavTab.DEPOSIT -> {
                                viewModel.showDepositDialog()
                            }
                            VideoBottomNavTab.PROFILE -> {
                                showProfileModal = true
                            }
                        }
                    }
                )
            }

            // Floating Social Community Buttons (WhatsApp, Telegram, Facebook)
            VideoFloatingSocials(
                onWhatsAppClick = { /* Opens WhatsApp support */ },
                onTelegramClick = { /* Opens Telegram channel */ },
                onFacebookClick = { /* Opens Facebook community */ },
                modifier = Modifier.align(Alignment.BottomEnd)
            )

            // Game Launch Bottom Sheet Dialog (00:50 - 00:52 in video)
            if (selectedGameForLaunch != null) {
                val g = selectedGameForLaunch!!
                VideoGameLaunchDialog(
                    game = g,
                    onPlayReal = {
                        selectedGameForLaunch = null
                        if (g.nativeGameType != null) {
                            viewModel.navigateToGame(g.nativeGameType)
                        } else {
                            val matchedCustom = customGames.find { it.title.equals(g.title, ignoreCase = true) }
                            if (matchedCustom != null) {
                                viewModel.openWebGame(matchedCustom)
                            } else {
                                // Default launch to Super Ace slot engine
                                viewModel.navigateToGame(CasinoGame.SUPER_ACE)
                            }
                        }
                    },
                    onPlayDemo = {
                        selectedGameForLaunch = null
                        if (g.nativeGameType != null) {
                            viewModel.navigateToGame(g.nativeGameType)
                        } else {
                            viewModel.navigateToGame(CasinoGame.SUPER_ACE)
                        }
                    },
                    onDismiss = { selectedGameForLaunch = null }
                )
            }

            // Promotion Modal Dialog
            if (showPromotionModal) {
                VideoPromotionDialog(
                    onClaimBonus = { viewModel.showDepositDialog() },
                    onOpenDeposit = { viewModel.showDepositDialog() },
                    onDismiss = { showPromotionModal = false }
                )
            }

            // Invite & Earn Modal Dialog
            if (showInviteModal) {
                VideoInviteEarnDialog(
                    userProfile = userProfile,
                    agentCommissionPercent = 10.0,
                    onDismiss = { showInviteModal = false }
                )
            }

            // Player Profile Modal Dialog
            if (showProfileModal) {
                VideoPlayerProfileDialog(
                    userProfile = userProfile,
                    soundEnabled = uiState.soundEnabled,
                    onToggleSound = { viewModel.toggleSound() },
                    onOpenDeposit = { viewModel.showDepositDialog() },
                    onOpenWithdraw = { viewModel.showWithdrawDialog() },
                    onOpenMissions = { viewModel.showModal(ActiveModal.DAILY_PROGRESS) },
                    onOpenLeaderboard = { viewModel.showModal(ActiveModal.LEADERBOARD) },
                    onDismiss = { showProfileModal = false }
                )
            }

            // Dialog Overlays
            when (uiState.activeModal) {
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
                ActiveModal.BIG_WIN -> {
                    BigWinOverlay(
                        winAmount = uiState.bigWinAmount,
                        winType = uiState.bigWinType,
                        onDismiss = { viewModel.dismissModal() }
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
                        canDismiss = userProfile.isRegistered,
                        onDismiss = { viewModel.dismissModal() },
                        onRegisterWithDeviceAndReferral = { phone, name, deviceId, deviceModel, referralCode, onResult ->
                            viewModel.registerPlayerWithDevice(phone, name, deviceId, deviceModel, referralCode, onResult)
                        },
                        onLoginOrRecover = { phone, deviceId, deviceModel, onResult ->
                            viewModel.loginOrRecoverPlayer(phone, deviceId, deviceModel, onResult)
                        }
                    )
                }
                else -> {}
            }

            // Automatic Prompt: If player is not registered, prompt registration
            if ((!userProfile.isRegistered || userProfile.userPhone.isEmpty()) && uiState.activeModal == ActiveModal.NONE && !remoteConfig.maintenanceMode) {
                RegisterDialog(
                    initialPhone = userProfile.userPhone,
                    canDismiss = false,
                    onRegisterWithDeviceAndReferral = { phone, name, deviceId, deviceModel, referralCode, onResult ->
                        viewModel.registerPlayerWithDevice(phone, name, deviceId, deviceModel, referralCode, onResult)
                    },
                    onLoginOrRecover = { phone, deviceId, deviceModel, onResult ->
                        viewModel.loginOrRecoverPlayer(phone, deviceId, deviceModel, onResult)
                    }
                )
            }

            // Active Custom Game / API WebView Dialog
            if (activeWebGame != null) {
                WebGameDialog(
                    game = activeWebGame!!,
                    onDismiss = { viewModel.closeWebGame() }
                )
            }

            // Remote Maintenance Mode Overlay from Admin via GitHub
            if (remoteConfig.maintenanceMode) {
                HomeMaintenanceOverlay(
                    message = remoteConfig.maintenanceMessage,
                    isSyncing = isSyncingRemote,
                    onRetry = { viewModel.refreshRemoteConfig() }
                )
            }
        }
    }
}

// ----------------------------------------------------
// Top Header Bar
// ----------------------------------------------------
@Composable
private fun HomeTopBar(
    balance: Double,
    level: Int,
    userPhone: String,
    isRegistered: Boolean,
    claimableCount: Int,
    soundEnabled: Boolean,
    onReloadChips: () -> Unit,
    onOpenWithdraw: () -> Unit,
    onOpenRegistration: () -> Unit,
    onOpenMissions: () -> Unit,
    onOpenLeaderboard: () -> Unit,
    onToggleSound: () -> Unit
) {
    Surface(
        color = Color(0xFF0D051C),
        border = BorderStroke(1.dp, Color(0x33F59E0B))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Player VIP Avatar, ID, Balance & Withdraw
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                // VIP Avatar Badge
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFD97706), Color(0xFFF59E0B), Color(0xFFFEF08A))
                            )
                        )
                        .border(1.5.dp, Color(0xFFFFD700), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "VIP$level",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp
                    )
                }

                // Balance Pill (Clicking triggers Deposit Dialog)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x44000000))
                        .border(1.dp, Color(0x77F59E0B), RoundedCornerShape(20.dp))
                        .clickable { onReloadChips() }
                        .padding(horizontal = 7.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(text = "🪙", fontSize = 11.sp)
                        val displayBalance = if (balance % 1.0 == 0.0) {
                            String.format(java.util.Locale.US, "%,d", balance.toLong())
                        } else {
                            String.format(java.util.Locale.US, "%,.2f", balance)
                        }
                        Text(
                            text = displayBalance,
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        )
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Reload Chips",
                                tint = Color.White,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }

                // Dedicated Player Withdraw Button (উইথড্র)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFBE185D), Color(0xFFE11D48))
                            )
                        )
                        .border(1.dp, Color(0xFFFDA4AF), RoundedCornerShape(20.dp))
                        .clickable { onOpenWithdraw() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "উইথড্র",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                    )
                }
            }

            // Right: Missions, Leaderboard, Sound
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Missions Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x22FFFFFF))
                        .border(1.dp, Color(0x33F59E0B), RoundedCornerShape(8.dp))
                        .clickable { onOpenMissions() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("home_missions_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ListAlt,
                            contentDescription = "Daily Missions",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(15.dp)
                        )
                        if (claimableCount > 0) {
                            Spacer(modifier = Modifier.width(3.dp))
                            Box(
                                modifier = Modifier
                                    .size(15.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$claimableCount",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Leaderboard Button
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(0x22FFFFFF))
                        .border(1.dp, Color(0x33F59E0B), CircleShape)
                        .clickable { onOpenLeaderboard() }
                        .testTag("home_leaderboard_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Leaderboard",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(15.dp)
                    )
                }

                // Sound Toggle
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(0x22FFFFFF))
                        .border(1.dp, Color(0x33F59E0B), CircleShape)
                        .clickable { onToggleSound() }
                        .testTag("home_sound_toggle"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                        contentDescription = "Toggle Sound",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// Hero Carousel / Banner
// ----------------------------------------------------
@Composable
private fun HomeHeroBanner(
    onPlayFeatured: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, Color(0xFFD97706)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E0A3C))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
        ) {
            // Background Artwork
            Image(
                painter = painterResource(id = R.drawable.img_casino_lobby_banner),
                contentDescription = "Casino Lobby Hero Banner",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Dark vignette gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0x66000000),
                                Color(0xCC0D051C),
                                Color(0xFA070312)
                            )
                        )
                    )
            )

            // Content Overlay
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Tag
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFDC2626))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Whatshot,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "HOT FEATURED",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x66000000))
                            .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "MAX 9,990x",
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp
                        )
                    }
                }

                // Title & Description
                Column {
                    Text(
                        text = "🤠 DEAD MAN'S BULLET",
                        color = Color(0xFFFFD700),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Skywind Group • Western Outlaw Bounty Respins",
                        color = Color(0xFFFDE68A),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "রিয়েল রিভলভার স্পিন, গানশট রিকোচেট ও বিগ ক্যাশ প্রাইজ!",
                        color = Color(0xFFE2E8F0),
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // CTA Button
                Button(
                    onClick = onPlayFeatured,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD97706)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "এখনই খেলুন (PLAY NOW)",
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// Game Search Bar
// ----------------------------------------------------
@Composable
private fun HomeSearchBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF130926)),
        border = BorderStroke(1.dp, Color(0xFF6B21A8)),
        shape = RoundedCornerShape(12.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("home_game_search_bar"),
            placeholder = {
                Text(
                    text = "গেমের নাম বা প্রোভাইডার সার্চ করুন...",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Games",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { onQueryChange("") },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear Search",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFFD700),
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }
}

// ----------------------------------------------------
// Filter Chips Row
// ----------------------------------------------------
@Composable
private fun FilterChipsRow(
    selectedFilter: String,
    onSelectFilter: (String) -> Unit,
    favoriteCount: Int = 0,
    newGameCount: Int = 0
) {
    val filters = listOf(
        Pair("ALL", "🌟 সব গেম"),
        Pair("FAVORITES", if (favoriteCount > 0) "⭐ পছন্দের ($favoriteCount)" else "⭐ পছন্দের গেম"),
        Pair("NEW", if (newGameCount > 0) "🆕 নতুন গেম ($newGameCount)" else "🆕 নতুন গেম"),
        Pair("HOT", "🔥 জনপ্রিয়"),
        Pair("WESTERN", "🤠 বাউন্টি"),
        Pair("MEGA", "⚡ মেগা উইন")
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { (key, label) ->
            val isSelected = selectedFilter == key
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isSelected) Brush.horizontalGradient(
                            listOf(Color(0xFFD97706), Color(0xFFF59E0B))
                        ) else Brush.horizontalGradient(
                            listOf(Color(0x22FFFFFF), Color(0x11FFFFFF))
                        )
                    )
                    .border(
                        1.dp,
                        if (isSelected) Color(0xFFFDE047) else Color(0x33F59E0B),
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onSelectFilter(key) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = label,
                    color = if (isSelected) Color.Black else Color(0xFFE2E8F0),
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold
                )
            }
        }
    }
}

// ----------------------------------------------------
// Individual Game Card Item (With Logo & Play Action)
// ----------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GameCardItem(
    game: CasinoGame,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onPlay: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val borderPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // Visual theme definition per game
    val (primaryGradient, borderColor, badgeColor, maxWinText, iconSymbol, banglaHighlight) = when (game) {
        CasinoGame.DEAD_MANS_BULLET -> {
            CardVisualTheme(
                gradient = listOf(Color(0xFF451A03), Color(0xFF78350F), Color(0xFF1E0A3C)),
                border = Color(0xFFF59E0B),
                badge = Color(0xFFD97706),
                maxWin = "9,990x MAX WIN",
                symbol = "🤠",
                bangla = "আউটল রিভলভার, গানশট রিকোচেট ও বাউন্টি কালেক্টর রেস্পিনস"
            )
        }
        CasinoGame.SUPER_ACE -> {
            CardVisualTheme(
                gradient = listOf(Color(0xFF1E1B4B), Color(0xFF3730A3), Color(0xFF170C3A)),
                border = Color(0xFFFFD700),
                badge = Color(0xFF6366F1),
                maxWin = "1,500x MAX WIN",
                symbol = "🃏",
                bangla = "১০২৪ ওয়েজ ক্যাসকেডিং কার্ড, গোল্ডেন ওয়াইল্ড ফ্লিপ ও x১০ কম্বো"
            )
        }
        CasinoGame.GATES_OF_OLYMPUS -> {
            CardVisualTheme(
                gradient = listOf(Color(0xFF172554), Color(0xFF1E40AF), Color(0xFF160A38)),
                border = Color(0xFF38BDF8),
                badge = Color(0xFF2563EB),
                maxWin = "5,000x MAX WIN",
                symbol = "⚡",
                bangla = "জিউসের বজ্রপাত, স্ক্যাটার পে ও ৫০০ গুণ পর্যন্ত থান্ডার মাল্টিপ্লায়ার"
            )
        }
        CasinoGame.SWEET_BONANZA -> {
            CardVisualTheme(
                gradient = listOf(Color(0xFF500724), Color(0xFF831843), Color(0xFF1E0630)),
                border = Color(0xFFF472B6),
                badge = Color(0xFFDB2777),
                maxWin = "21,100x MAX WIN",
                symbol = "🍭",
                bangla = "ক্যান্ডি পপ সাউন্ড, আনলিমিটেড টাম্বল ও ১০০x সুগার বম্ব বিস্ফোরণ"
            )
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onPlay() }
            .testTag("game_card_${game.name.lowercase()}"),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, if (isFavorite) Color(0xFFFFD700) else borderColor.copy(alpha = borderPulse)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF120826))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(primaryGradient))
                .padding(14.dp)
        ) {
            Column {
                // Top Row: Logo Badge & Provider / Max Win Tags / Star Favorite
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Game Logo / Emblem Box
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0x66FFFFFF), Color(0x22000000))
                                    )
                                )
                                .border(1.5.dp, borderColor, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = iconSymbol,
                                fontSize = 28.sp
                            )
                        }

                        Column {
                            // Title
                            Text(
                                text = game.title,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.3.sp
                            )
                            // Provider
                            Text(
                                text = game.provider,
                                color = Color(0xFFFDE68A),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Max Win Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x33000000))
                                .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = maxWinText,
                                color = borderColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Star Favorite Button
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isFavorite) Color(0x33EAB308) else Color(0x1FFFFFFF))
                                .testTag("favorite_button_${game.name.lowercase()}")
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = if (isFavorite) "Starred Favorite" else "Add to Favorites",
                                tint = if (isFavorite) Color(0xFFFFD700) else Color(0xFF94A3B8),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                if (isFavorite) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF854D0E))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFEF08A),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "আপনার পছন্দের তালিকায় সবার শীর্ষে রাখা হয়েছে",
                                color = Color(0xFFFEF08A),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bangla Feature Highlight
                Text(
                    text = banglaHighlight,
                    color = Color(0xFFE2E8F0),
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Feature Tags Row
                val featureTags = when (game) {
                    CasinoGame.DEAD_MANS_BULLET -> listOf("🤠 3x3 Revolver", "🎯 Bullet Cash", "🔥 999x Outlaw Bounty", "RTP ${game.rtp}")
                    CasinoGame.SUPER_ACE -> listOf("🃏 Golden Wild Flip", "📈 x10 Free Game", "✨ 1024 Ways", "RTP ${game.rtp}")
                    CasinoGame.GATES_OF_OLYMPUS -> listOf("⚡ Zeus Thunder Hit", "💎 500x Orbs", "🏛️ Scatter Pays", "RTP ${game.rtp}")
                    CasinoGame.SWEET_BONANZA -> listOf("🍭 Candy Tumbler", "💣 100x Sugar Bomb", "🍬 All Ways", "RTP ${game.rtp}")
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (tag in featureTags) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x2AFFFFFF))
                                .border(0.8.dp, Color(0x44FFFFFF), RoundedCornerShape(6.dp))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = tag,
                                color = Color(0xFFF1F5F9),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Large Play Button
                Button(
                    onClick = onPlay,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("play_button_${game.name.lowercase()}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = borderColor
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "প্রবেশ করুন / খেলুন (PLAY NOW)",
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// Custom Admin Added Game Card Item
// ----------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CustomGameCardItem(
    game: CustomGameEntity,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onPlay: () -> Unit
) {
    val borderColor = if (isFavorite) Color(0xFFFFD700) else Color(0xFF8B5CF6)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onPlay() }
            .testTag("custom_game_card_${game.id}"),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF140A2C))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF2E1065), Color(0xFF1E1B4B), Color(0xFF0F0A1E))
                    )
                )
                .padding(14.dp)
        ) {
            Column {
                // Top Row: Emblem, Title/Provider, Badges, Favorite Star
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(listOf(Color(0xFF7C3AED), Color(0xFF4C1D95)))
                                )
                                .border(1.5.dp, Color(0xFFC4B5FD), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsEsports,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = game.title,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.3.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFE11D48))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = game.badge,
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = "${game.provider} • RTP ${game.rtp}",
                                color = Color(0xFFFDE68A),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x33000000))
                                .border(1.dp, Color(0xFF38BDF8), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = game.maxWin,
                                color = Color(0xFF38BDF8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isFavorite) Color(0x33EAB308) else Color(0x1FFFFFFF))
                                .testTag("favorite_button_${game.id}")
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Favorite Game",
                                tint = if (isFavorite) Color(0xFFFFD700) else Color(0xFF94A3B8),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                if (isFavorite) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF854D0E))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFEF08A),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "আপনার পছন্দের তালিকায় সবার শীর্ষে রাখা হয়েছে",
                                color = Color(0xFFFEF08A),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bangla Subtitle / Highlight
                Text(
                    text = game.subtitle,
                    color = Color(0xFFE2E8F0),
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Tags
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val customTags = listOf("🆕 এডমিন লাইভ গেম", "🌐 ওয়েব / API লিংক", "⚡ ইনস্ট্যান্ট প্লে", "RTP ${game.rtp}")
                    for (tag in customTags) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x2AFFFFFF))
                                .border(0.8.dp, Color(0x44FFFFFF), RoundedCornerShape(6.dp))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = tag,
                                color = Color(0xFFF1F5F9),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Large Play Button
                Button(
                    onClick = onPlay,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("play_custom_game_${game.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7C3AED)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "প্রবেশ করুন / খেলুন (PLAY NOW)",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}

private data class CardVisualTheme(
    val gradient: List<Color>,
    val border: Color,
    val badge: Color,
    val maxWin: String,
    val symbol: String,
    val bangla: String
)

@Composable
fun HomeRemoteNoticeBanner(
    notice: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1038)),
        border = BorderStroke(1.dp, Color(0xFFEAB308).copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEAB308)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Campaign,
                    contentDescription = "Notice",
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = notice,
                color = Color(0xFFFEF08A),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun HomeMaintenanceOverlay(
    message: String,
    isSyncing: Boolean,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xF505020C))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF16092E)),
            border = BorderStroke(1.5.dp, Color(0xFFEF4444))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0x33EF4444)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Maintenance",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "সার্ভার মেইনটেন্যান্স চলছে",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message.ifBlank { "সার্ভার সাময়িক রক্ষণাবেক্ষণ কাজের জন্য বন্ধ রয়েছে। কিছুক্ষণ পর আবার চেষ্টা করুন।" },
                    color = Color(0xFFCBD5E1),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 19.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onRetry,
                    enabled = !isSyncing,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("পুনরায় চেষ্টা করুন", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
