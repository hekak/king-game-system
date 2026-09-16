package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Cloud
import com.example.data.db.AdminWalletConfigEntity
import com.example.data.db.CustomGameEntity
import com.example.data.db.DailyProgressEntity
import com.example.data.db.DeletedGameEntity
import com.example.data.db.DepositRequestEntity
import com.example.data.db.LeaderboardEntryEntity
import com.example.data.db.RegisteredAccountEntity
import com.example.data.db.SubAdminEntity
import com.example.data.db.UserProfileEntity
import com.example.data.db.WithdrawRequestEntity
import com.example.data.model.GameAdminConfig
import com.example.ui.dialogs.WebGameDialog
import com.example.ui.screens.admin.AdminMobileOtpGate
import com.example.ui.screens.admin.CloudDeploymentGuideTabContent
import com.example.ui.screens.admin.GamesManagementTabContent
import com.example.ui.screens.admin.GitHubControlTabContent
import com.example.ui.screens.admin.SubAdminsTabContent
import com.example.ui.screens.admin.WebSecurityTabContent
import com.example.ui.viewmodel.AdminViewModel
import androidx.compose.material.icons.filled.CloudSync
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

enum class AdminTab(val title: String, val subtitle: String, val icon: ImageVector) {
    CLOUD_GUIDE("ক্লাউড ও পাবলিশ গাইড", "Firebase & GitHub Guide", Icons.Default.CloudSync),
    GITHUB_CONTROL("গিটহাব ক্লাউড সিঙ্ক", "GitHub Cloud Control", Icons.Default.Cloud),
    WITHDRAW("উইথড্রো রিকোয়েস্ট", "Cash Out Requests", Icons.Default.Payments),
    DEPOSIT("ওয়ালেট ও ডিপোজিট", "Wallet & Deposit", Icons.Default.AccountBalanceWallet),
    SUB_ADMINS("সাব-এডমিন ও এজেন্ট", "Agents & Referrals", Icons.Default.Group),
    WEB_SECURITY("ওয়েবসাইট ও সুরক্ষা", "Web Portal & Obfuscation", Icons.Default.Language),
    CUSTOM_GAMES("গেম ম্যানেজমেন্ট ও ডিলিট", "Games & Deletion", Icons.Default.SportsEsports),
    PROFIT_LOSS("লাভ-ক্ষতি ও অডিট", "P&L & Statements", Icons.Default.MonetizationOn),
    ACCOUNTS("ইউজার ও ডিভাইস", "Users & Devices", Icons.Default.Security),
    ENGINE("উইন রেট ও ইঞ্জিন", "Win Ratio & Math", Icons.Default.Tune),
    PLAYER("প্লেয়ার ব্যালেন্স", "Chips & Balance", Icons.Default.Wallet),
    ANALYTICS("লাইভ অ্যানালিটিক্স", "Live Telemetry", Icons.Default.Analytics),
    TOURNAMENT("টুর্নামেন্ট", "Leaderboard", Icons.Default.EmojiEvents),
    AUDIO("সাউন্ড স্টুডিও", "SFX Studio", Icons.Default.VolumeUp)
}

@Composable
fun AdminAppScreen(
    viewModel: AdminViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val config by viewModel.adminConfig.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val progress by viewModel.todayProgress.collectAsState()
    val leaderboard by viewModel.leaderboard.collectAsState()
    val walletConfig by viewModel.walletConfig.collectAsState()
    val allDeposits by viewModel.allDepositRequests.collectAsState()
    val pendingDepositCount by viewModel.pendingDepositCount.collectAsState()
    val allWithdraws by viewModel.allWithdrawRequests.collectAsState()
    val pendingWithdrawCount by viewModel.pendingWithdrawCount.collectAsState()
    val allRegisteredAccounts by viewModel.allRegisteredAccounts.collectAsState()
    val allCustomGames by viewModel.allCustomGames.collectAsState()
    val allDeletedGames by viewModel.allDeletedGames.collectAsState()
    val deletedGameIds by viewModel.deletedGameIds.collectAsState()
    val allSubAdmins by viewModel.allSubAdmins.collectAsState()
    val statusMsg by viewModel.statusMessage.collectAsState()

    var activeAdminWebGame by remember { mutableStateOf<CustomGameEntity?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by remember { mutableStateOf(AdminTab.CLOUD_GUIDE) }
    var isAdminAuthenticated by remember { mutableStateOf(false) }
    var adminPhoneInput by remember { mutableStateOf("01303347372") }
    var isAdminOtpSent by remember { mutableStateOf(false) }
    var adminGeneratedOtp by remember { mutableStateOf("") }
    var adminEnteredOtp by remember { mutableStateOf("") }
    var adminOtpTimer by remember { mutableIntStateOf(60) }
    var isAdminTimerRunning by remember { mutableStateOf(false) }
    var adminOtpError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isAdminTimerRunning, adminOtpTimer) {
        if (isAdminTimerRunning && adminOtpTimer > 0) {
            kotlinx.coroutines.delay(1000L)
            adminOtpTimer--
        } else if (adminOtpTimer == 0) {
            isAdminTimerRunning = false
        }
    }

    LaunchedEffect(statusMsg) {
        statusMsg?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF090414),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (!isAdminAuthenticated) {
            AdminMobileOtpGate(
                adminPhone = adminPhoneInput,
                onPhoneChange = { if (!isAdminOtpSent) adminPhoneInput = it.take(11) },
                isOtpSent = isAdminOtpSent,
                onSendOtp = {
                    val cleaned = adminPhoneInput.trim()
                    if (cleaned.length != 11 || !cleaned.startsWith("01")) {
                        adminOtpError = "সঠিক ১১ ডিজিটের মোবাইল নম্বর দিন।"
                    } else {
                        adminGeneratedOtp = (100000..999999).random().toString()
                        isAdminOtpSent = true
                        adminOtpTimer = 60
                        isAdminTimerRunning = true
                        adminOtpError = null
                    }
                },
                enteredOtp = adminEnteredOtp,
                onOtpChange = { adminEnteredOtp = it.filter { ch -> ch.isDigit() }.take(6) },
                generatedOtp = adminGeneratedOtp,
                isTimerRunning = isAdminTimerRunning,
                timerSeconds = adminOtpTimer,
                errorMessage = adminOtpError,
                onResendOtp = {
                    adminGeneratedOtp = (100000..999999).random().toString()
                    adminOtpTimer = 60
                    isAdminTimerRunning = true
                    adminOtpError = null
                },
                onChangeNumber = {
                    isAdminOtpSent = false
                    adminEnteredOtp = ""
                    adminGeneratedOtp = ""
                    isAdminTimerRunning = false
                    adminOtpError = null
                },
                onVerifyOtp = {
                    if (adminEnteredOtp.trim().length != 6) {
                        adminOtpError = "অনুগ্রহ করে সম্পূর্ণ ৬ ডিজিটের ওটিপি কোড লিখুন।"
                    } else if (adminEnteredOtp.trim() != adminGeneratedOtp) {
                        adminOtpError = "ভুল ওটিপি কোড! অনুগ্রহ করে সঠিক ৬ ডিজিটের কোড দিন।"
                    } else {
                        adminOtpError = null
                        isAdminAuthenticated = true
                        viewModel.soundManager.playBigWin()
                    }
                },
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF0F0624), Color(0xFF070312), Color(0xFF030107))
                        )
                    )
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // App Header
                AdminTopHeader(
                    onOpenGitHubSync = {
                        selectedTab = AdminTab.GITHUB_CONTROL
                    },
                    onLockAdmin = {
                        isAdminAuthenticated = false
                        isAdminOtpSent = false
                        adminEnteredOtp = ""
                        adminGeneratedOtp = ""
                        isAdminTimerRunning = false
                    }
                )

            // Navigation Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = Color(0xFF140A2E),
                contentColor = Color(0xFFEAB308),
                edgePadding = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                AdminTab.values().forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = {
                            selectedTab = tab
                            viewModel.soundManager.playButtonClick()
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (selectedTab == tab) Color(0xFFFBBF24) else Color(0xFF94A3B8)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = tab.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == tab) Color(0xFFFBBF24) else Color(0xFF94A3B8)
                                )
                                if (tab == AdminTab.WITHDRAW && pendingWithdrawCount > 0) {
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "$pendingWithdrawCount",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                                if (tab == AdminTab.DEPOSIT && pendingDepositCount > 0) {
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(Color(0xFFEF4444))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "$pendingDepositCount",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }
                        },
                        modifier = Modifier.testTag("admin_tab_${tab.name.lowercase()}")
                    )
                }
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .widthIn(max = 640.dp)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                when (selectedTab) {
                    AdminTab.CLOUD_GUIDE -> CloudDeploymentGuideTabContent(
                        onNavigateToGitHubControl = {
                            selectedTab = AdminTab.GITHUB_CONTROL
                        }
                    )
                    AdminTab.GITHUB_CONTROL -> GitHubControlTabContent(
                        adminConfig = config,
                        walletConfig = walletConfig,
                        allAccounts = allRegisteredAccounts,
                        allDeposits = allDeposits,
                        allWithdraws = allWithdraws,
                        onConfigUpdatedFromRemote = { remoteCfg ->
                            viewModel.applyRemoteGameConfig(remoteCfg)
                        }
                    )
                    AdminTab.WITHDRAW -> WithdrawTabContent(
                        allWithdraws = allWithdraws,
                        viewModel = viewModel
                    )
                    AdminTab.DEPOSIT -> DepositTabContent(
                        walletConfig = walletConfig,
                        allDeposits = allDeposits,
                        viewModel = viewModel
                    )
                    AdminTab.SUB_ADMINS -> SubAdminsTabContent(
                        allSubAdmins = allSubAdmins,
                        allAccounts = allRegisteredAccounts,
                        allDeposits = allDeposits,
                        allWithdraws = allWithdraws,
                        walletConfig = walletConfig,
                        viewModel = viewModel
                    )
                    AdminTab.WEB_SECURITY -> WebSecurityTabContent(
                        walletConfig = walletConfig,
                        viewModel = viewModel,
                        onPreviewWebPortal = { previewUrl ->
                            activeAdminWebGame = CustomGameEntity(
                                id = "preview_portal",
                                title = "ওয়েবসাইট পোর্টাল প্রিভিউ",
                                provider = "Official Web Portal",
                                subtitle = "লাইভ টেস্ট",
                                gameUrlOrApi = previewUrl,
                                badge = "PORTAL",
                                maxWin = "Unlimited",
                                themeColorHex = 0xFF0284C7,
                                rtp = "100%",
                                isNewGame = false,
                                isActive = true,
                                createdAt = System.currentTimeMillis()
                            )
                        }
                    )
                    AdminTab.CUSTOM_GAMES -> GamesManagementTabContent(
                        allGames = allCustomGames,
                        deletedGames = allDeletedGames,
                        deletedGameIds = deletedGameIds,
                        onTestGame = { activeAdminWebGame = it },
                        viewModel = viewModel
                    )
                    AdminTab.PROFIT_LOSS -> ProfitLossTabContent(
                        userProfile = userProfile,
                        allDeposits = allDeposits,
                        allWithdraws = allWithdraws,
                        todayProgress = progress,
                        allRegisteredAccounts = allRegisteredAccounts,
                        viewModel = viewModel
                    )
                    AdminTab.ACCOUNTS -> AccountsTabContent(
                        allAccounts = allRegisteredAccounts,
                        userProfile = userProfile,
                        viewModel = viewModel
                    )
                    AdminTab.ENGINE -> EngineTabContent(config = config, viewModel = viewModel)
                    AdminTab.PLAYER -> PlayerTabContent(userProfile = userProfile, viewModel = viewModel)
                    AdminTab.ANALYTICS -> AnalyticsTabContent(progress = progress, viewModel = viewModel)
                    AdminTab.TOURNAMENT -> TournamentTabContent(leaderboard = leaderboard, progress = progress, viewModel = viewModel)
                    AdminTab.AUDIO -> AudioTabContent(viewModel = viewModel)
                }
            }
        }

        // Admin Web / API Game Live Tester Dialog
        activeAdminWebGame?.let { testGame ->
            WebGameDialog(
                game = testGame,
                onDismiss = { activeAdminWebGame = null }
            )
        }
        }
    }
}

@Composable
private fun AdminTopHeader(
    onOpenGitHubSync: () -> Unit,
    onLockAdmin: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF160933))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.5.dp, Brush.linearGradient(listOf(Color(0xFF38BDF8), Color(0xFFEAB308))), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_admin_logo),
                    contentDescription = "Admin App Logo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "King Game Admin",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFDC2626))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "এডমিন কনসোল",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
                Text(
                    text = "খেলোয়াড়ের জয়ের শতকরা হার (Win Rate %) ও গেম সেটিংস",
                    color = Color(0xFFFDE047),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Cloud Sync Shortcut Button
            Button(
                onClick = onOpenGitHubSync,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.testTag("admin_github_cloud_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Cloud,
                        contentDescription = "GitHub Cloud",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ক্লাউড",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Lock Admin Panel Button
            Button(
                onClick = onLockAdmin,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.testTag("admin_lock_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "লক 🔒",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// TAB 0: WITHDRAW REQUESTS MANAGEMENT
// ----------------------------------------------------
@Composable
private fun WithdrawTabContent(
    allWithdraws: List<WithdrawRequestEntity>,
    viewModel: AdminViewModel
) {
    val context = LocalContext.current
    var filterStatus by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    val pendingList = allWithdraws.filter { it.status == "PENDING" }
    val approvedList = allWithdraws.filter { it.status == "APPROVED" }
    val rejectedList = allWithdraws.filter { it.status == "REJECTED" }

    val filteredRequests = when (filterStatus) {
        "PENDING" -> pendingList
        "APPROVED" -> approvedList
        "REJECTED" -> rejectedList
        else -> allWithdraws
    }

    val searchedRequests = remember(filteredRequests, searchQuery) {
        if (searchQuery.isBlank()) {
            filteredRequests
        } else {
            val q = searchQuery.trim().lowercase()
            filteredRequests.filter {
                it.userPhone.lowercase().contains(q) ||
                it.method.lowercase().contains(q) ||
                it.amount.toString().contains(q) ||
                it.id.toString().contains(q)
            }
        }
    }

    val totalPendingPoints = pendingList.sumOf { it.amount }
    val totalApprovedPoints = approvedList.sumOf { it.amount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_withdraw_tab_content"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Summary Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Pending Card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1038)),
                    border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("অপেক্ষমান উইথড্রো", color = Color(0xFFFCD34D), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${pendingList.size} টি আবেদন",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "🪙 ${String.format(Locale.US, "%,.0f", totalPendingPoints)} পয়েন্ট",
                            color = Color(0xFFFDE68A),
                            fontSize = 11.sp
                        )
                    }
                }

                // Approved Card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F261F)),
                    border = BorderStroke(1.dp, Color(0xFF10B981)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("অনুমোদিত উইথড্রো", color = Color(0xFF34D399), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${approvedList.size} টি সম্পন্ন",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "🪙 ${String.format(Locale.US, "%,.0f", totalApprovedPoints)} পয়েন্ট",
                            color = Color(0xFF6EE7B7),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Admin Search Bar for Withdrawals
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF130926)),
                border = BorderStroke(1.dp, Color(0xFF4C1D95)),
                shape = RoundedCornerShape(10.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_withdraw_search_bar"),
                    placeholder = {
                        Text(
                            text = "উইথড্রো খুঁজুন (নম্বর, Trx ID, মেথড বা নাম)...",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { searchQuery = "" },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
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

        // Filter Pills
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val filters = listOf(
                    "ALL" to "সব (${allWithdraws.size})",
                    "PENDING" to "অপেক্ষমান (${pendingList.size})",
                    "APPROVED" to "অনুমোদিত (${approvedList.size})",
                    "REJECTED" to "বাতিল (${rejectedList.size})"
                )
                filters.forEach { (key, label) ->
                    val isSelected = filterStatus == key
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFF10B981) else Color(0xFF160B30))
                            .border(1.dp, if (isSelected) Color(0xFF34D399) else Color(0xFF334155), RoundedCornerShape(8.dp))
                            .clickable { filterStatus = key }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.Black else Color(0xFFCBD5E1),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Empty state
        if (searchedRequests.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = Color(0xFF475569),
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "সার্চ অনুসারে কোনো উইথড্রো রিকোয়েস্ট মেলেনি" else "কোনো উইথড্রো রিকোয়েস্ট পাওয়া যায়নি",
                            color = Color(0xFF94A3B8),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(searchedRequests, key = { it.id }) { req ->
                val statusColor = when (req.status) {
                    "APPROVED" -> Color(0xFF10B981)
                    "REJECTED" -> Color(0xFFEF4444)
                    else -> Color(0xFFF59E0B)
                }
                val methodEmoji = when (req.method) {
                    "NAGAD" -> "🔴 নগদ"
                    "BKASH" -> "🟣 বিকাশ"
                    "ROCKET" -> "🔵 রকেট"
                    "UPAY" -> "🟡 উপায়"
                    else -> req.method
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_withdraw_card_${req.id}"),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF160A2E)),
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = methodEmoji,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "🪙 ${String.format(Locale.US, "%,.0f", req.amount)} পয়েন্ট",
                                    color = Color(0xFFFBBF24),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(statusColor.copy(alpha = 0.2f))
                                    .border(1.dp, statusColor, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = when (req.status) {
                                        "APPROVED" -> "অনুমোদিত (PAID)"
                                        "REJECTED" -> "বাতিল (REJECTED)"
                                        else -> "অপেক্ষমান (PENDING)"
                                    },
                                    color = statusColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // User phone with copy action
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "মোবাইল নম্বর: ",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = req.userPhone,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            IconButton(
                                onClick = {
                                    val clip = ClipData.newPlainText("phone", req.userPhone)
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "নম্বর কপি করা হয়েছে: ${req.userPhone}", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy phone",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Text(
                            text = "আবেদনের সময়: ${dateFormat.format(Date(req.submittedAt))}",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )

                        if (!req.rejectionReason.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "বাতিলের কারণ: ${req.rejectionReason}",
                                color = Color(0xFFFCA5A5),
                                fontSize = 11.sp
                            )
                        }

                        // Action Buttons when PENDING
                        if (req.status == "PENDING") {
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = Color(0xFF334155))
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Approve Button
                                Button(
                                    onClick = {
                                        viewModel.approveWithdraw(req.id)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .height(38.dp)
                                        .testTag("admin_approve_withdraw_${req.id}"),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("অনুমোদন (Paid)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                // Reject & Refund Button
                                Button(
                                    onClick = {
                                        viewModel.rejectWithdraw(req.id, refund = true)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB91C1C)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .height(38.dp)
                                        .testTag("admin_reject_refund_withdraw_${req.id}"),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("বাতিল ও রিফান্ড", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// TAB 1: ENGINE MATH & RTP
// ----------------------------------------------------
@Composable
private fun EngineTabContent(
    config: GameAdminConfig,
    viewModel: AdminViewModel
) {
    var winRatio by remember(config) { mutableIntStateOf(config.winRatioPercent) }
    var superAceRatio by remember(config) { mutableIntStateOf(config.superAceWinRatio) }
    var deadMansRatio by remember(config) { mutableIntStateOf(config.deadMansBulletWinRatio) }
    var olympusRatio by remember(config) { mutableIntStateOf(config.gatesOfOlympusWinRatio) }
    var sweetBonanzaRatio by remember(config) { mutableIntStateOf(config.sweetBonanzaWinRatio) }

    var goldenChance by remember(config) { mutableStateOf(config.goldenWildChance) }
    var bigJokerChance by remember(config) { mutableStateOf(config.bigJokerChance) }
    var maxCascades by remember(config) { mutableIntStateOf(config.maxCascadeSteps) }
    var rtpMultiplier by remember(config) { mutableStateOf(config.rtpMultiplierBonus) }

    var guaranteedProfit by remember(config) { mutableStateOf(config.guaranteedHouseProfitMode) }
    var houseMargin by remember(config) { mutableIntStateOf(config.houseProfitMarginPercent) }
    var maxCap by remember(config) { mutableStateOf(config.maxPayoutMultiplierCap) }
    var allowPlayerProfit by remember(config) { mutableStateOf(config.allowPlayerProfit) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ALLOW PLAYER PROFIT TOGGLE (এডমিন অ্যাপ থেকে সেটিং অন করে দিলে তবেই প্লেয়ার লাভ পাবে, অন্যথায় সর্বদা লসে থাকবে)
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (allowPlayerProfit) Color(0xFF1E1B4B) else Color(0xFF3F0B1A)
                ),
                border = BorderStroke(
                    2.dp,
                    if (allowPlayerProfit) Color(0xFF6366F1) else Color(0xFFDC2626)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (allowPlayerProfit) Color(0x336366F1) else Color(0x33DC2626)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = if (allowPlayerProfit) Color(0xFF818CF8) else Color(0xFFF87171),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "খেলোয়াড়দের নেট লাভের অনুমতি",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = if (allowPlayerProfit) "Player Profit: ACTIVE (উইনিং উন্মুক্ত)" else "Player Profit: LOCKED (সর্বদা এডমিন লাভ)",
                                    color = if (allowPlayerProfit) Color(0xFFA5B4FC) else Color(0xFFFCA5A5),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Switch(
                            checked = allowPlayerProfit,
                            onCheckedChange = { isChecked ->
                                allowPlayerProfit = isChecked
                                viewModel.setAllowPlayerProfit(isChecked)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF6366F1),
                                uncheckedThumbColor = Color(0xFF94A3B8),
                                uncheckedTrackColor = Color(0xFF7F1D1D)
                            ),
                            modifier = Modifier.testTag("admin_allow_player_profit_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (allowPlayerProfit) Color(0x306366F1) else Color(0x30DC2626))
                            .border(0.8.dp, if (allowPlayerProfit) Color(0xFF6366F1) else Color(0xFFDC2626), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (allowPlayerProfit)
                                "🟢 সক্রিয়: এডমিন কর্তৃক খেলোয়াড়দের নেট লাভের অনুমতি দেওয়া হয়েছে। প্লেয়াররা জ্যাকপট জিততে এবং লাভ করতে পারবে।"
                            else
                                "🔒 নিশ্চিত এডমিন লাভ মোড: খেলোয়াড় ১০০ টাকা খরচ করলে সর্বোচ্চ ৬০ টাকা জিতবে এবং ৪০ টাকা এডমিনের নিশ্চিত লাভ থাকবে (অথবা ১০ টাকা খরচ করলে সর্বোচ্চ ৬ টাকা জিতবে)। এডমিন অ্যাপ থেকে এই সেটিং অন না করা পর্যন্ত কোনো প্লেয়ার নেট লাভ করতে পারবে না।",
                            color = if (allowPlayerProfit) Color(0xFFE0E7FF) else Color(0xFFFFE4E6),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // GUARANTEED HOUSE PROFIT MODE (সব সময় এডমিনের লাভ মোড)
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (guaranteedProfit) Color(0xFF062817) else Color(0xFF1A102E)
                ),
                border = BorderStroke(
                    2.dp,
                    if (guaranteedProfit) Color(0xFF10B981) else Color(0xFF6B21A8)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (guaranteedProfit) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0x33A855F7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = if (guaranteedProfit) Color(0xFF34D399) else Color(0xFFA855F7),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "সব সময় এডমিনের লাভ মোড",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Guaranteed House Profit Engine",
                                    color = if (guaranteedProfit) Color(0xFFA7F3D0) else Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Switch(
                            checked = guaranteedProfit,
                            onCheckedChange = { isChecked ->
                                guaranteedProfit = isChecked
                                viewModel.setGuaranteedHouseProfitMode(isChecked)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF10B981),
                                uncheckedThumbColor = Color(0xFF94A3B8),
                                uncheckedTrackColor = Color(0xFF334155)
                            ),
                            modifier = Modifier.testTag("admin_guaranteed_profit_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (guaranteedProfit) Color(0x3010B981) else Color(0x20A855F7))
                            .border(0.8.dp, if (guaranteedProfit) Color(0xFF10B981) else Color(0xFF6B21A8), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (guaranteedProfit)
                                "🛡️ সক্রিয়: গেম ইঞ্জিন স্বয়ংক্রিয়ভাবে $houseMargin% এডমিন মার্জিন বজায় রাখবে এবং সর্বোচ্চ পেআউট ${maxCap.toInt()}x এ ক্যাপ করবে। কোনো একক স্পিন বা দীর্ঘমেয়াদে এডমিনের লোকসান হবে না।"
                            else
                                "⚠️ নিষ্ক্রিয়: স্ট্যান্ডার্ড স্লট অ্যালগরিদম সক্রিয়। সাধারণ র্যান্ডম ও ভলাটিলিটি অনুযায়ী প্লেয়াররা বড় জ্যাকপট পেতে পারে।",
                            color = if (guaranteedProfit) Color(0xFFD1FAE5) else Color(0xFFCBD5E1),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }

                    if (guaranteedProfit) {
                        Spacer(modifier = Modifier.height(14.dp))

                        // House Profit Margin Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "এডমিনের সংরক্ষিত লাভ মার্জিন",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$houseMargin%",
                                color = Color(0xFF34D399),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Slider(
                            value = houseMargin.toFloat(),
                            onValueChange = {
                                houseMargin = it.roundToInt()
                                viewModel.setHouseProfitMargin(houseMargin)
                            },
                            valueRange = 15f..50f,
                            steps = 7,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF10B981),
                                activeTrackColor = Color(0xFF10B981),
                                inactiveTrackColor = Color(0xFF1E3A2F)
                            )
                        )

                        Text(
                            text = "প্লেয়ারদের সর্বোচ্চ তাত্ত্বিক জেতার সম্ভাবনা: ${(100 - houseMargin)}% (এডমিন সর্বদা $houseMargin% এগিয়ে থাকবে)",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Max Multiplier Cap Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "একক স্পিনে সর্বোচ্চ পেআউট ক্যাপ",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${maxCap.toInt()}x বাজি",
                                color = Color(0xFFFBBF24),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Slider(
                            value = maxCap.toFloat(),
                            onValueChange = {
                                maxCap = it.toDouble()
                                viewModel.setMaxPayoutMultiplierCap(maxCap)
                            },
                            valueRange = 10f..60f,
                            steps = 5,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFFBBF24),
                                activeTrackColor = Color(0xFFFBBF24),
                                inactiveTrackColor = Color(0xFF334155)
                            )
                        )
                    }
                }
            }
        }
        // Master Win Rate for ALL Games
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1038)),
                border = BorderStroke(2.dp, Color(0xFFEAB308)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "🌐 মাস্টার উইন রেট (সকল গেম)",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFEAB308).copy(alpha = 0.25f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "MASTER RTP",
                                        color = Color(0xFFFDE047),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                            Text(
                                text = "অ্যাপের সব কটি গেমের জেতার রেশিও একসাথে সেট করুন (৫% - ৯৫%)",
                                color = Color(0xFFE2E8F0),
                                fontSize = 11.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFEAB308))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "$winRatio%",
                                color = Color.Black,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = winRatio.toFloat(),
                        onValueChange = {
                            winRatio = it.roundToInt()
                            superAceRatio = winRatio
                            deadMansRatio = winRatio
                            olympusRatio = winRatio
                            sweetBonanzaRatio = winRatio
                            viewModel.setAllGamesWinRatio(winRatio)
                        },
                        valueRange = 5f..95f,
                        steps = 17,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFEAB308),
                            activeTrackColor = Color(0xFFEAB308),
                            inactiveTrackColor = Color(0xFF334155)
                        ),
                        modifier = Modifier.testTag("admin_engine_win_ratio_slider")
                    )

                    // Quick presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        listOf(10, 25, 50, 75, 90).forEach { preset ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (winRatio == preset) Color(0xFFEAB308) else Color(0xFF2E1065))
                                    .border(1.dp, if (winRatio == preset) Color(0xFFFDE047) else Color(0xFF6D28D9), RoundedCornerShape(6.dp))
                                    .clickable {
                                        winRatio = preset
                                        superAceRatio = preset
                                        deadMansRatio = preset
                                        olympusRatio = preset
                                        sweetBonanzaRatio = preset
                                        viewModel.setAllGamesWinRatio(preset)
                                        viewModel.soundManager.playButtonClick()
                                    }
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$preset%",
                                    color = if (winRatio == preset) Color.Black else Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section Title: Individual Game Controls
        item {
            Text(
                text = "🎮 প্রতিটি খেলার আলাদা জেতার রেশিও কন্ট্রোল",
                color = Color(0xFFFBBF24),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
            )
        }

        // GAME 1: Super Ace
        item {
            GameWinRatioCard(
                gameEmoji = "🃏",
                gameName = "Super Ace (সুপার এস)",
                description = "কার্ড এলিমিনেশন ও গোল্ডেন কার্ড মাল্টিপ্লায়ার স্লট",
                themeColor = Color(0xFFEAB308),
                badgeColor = Color(0xFFFEF08A),
                currentRatio = superAceRatio,
                onRatioChanged = { ratio ->
                    superAceRatio = ratio
                    viewModel.setSuperAceWinRatio(ratio)
                },
                testTagPrefix = "super_ace"
            )
        }

        // GAME 2: Dead Man's Bullet
        item {
            GameWinRatioCard(
                gameEmoji = "🤠",
                gameName = "Dead Man's Bullet (ডেড ম্যানস বুলেট)",
                description = "ওয়াইল্ড ওয়েস্ট রিভলভার স্পিন ও আউটল শ্যুটআউট",
                themeColor = Color(0xFFEF4444),
                badgeColor = Color(0xFFFECACA),
                currentRatio = deadMansRatio,
                onRatioChanged = { ratio ->
                    deadMansRatio = ratio
                    viewModel.setDeadMansBulletWinRatio(ratio)
                },
                testTagPrefix = "dead_mans"
            )
        }

        // GAME 3: Gates of Olympus
        item {
            GameWinRatioCard(
                gameEmoji = "⚡",
                gameName = "Gates of Olympus (গেটস অব অলিম্পাস)",
                description = "জিউসের বিদ্যুৎ চমক ও ৫০০x পর্যন্ত গ্লোবাল মাল্টিপ্লায়ার",
                themeColor = Color(0xFF38BDF8),
                badgeColor = Color(0xFFBAE6FD),
                currentRatio = olympusRatio,
                onRatioChanged = { ratio ->
                    olympusRatio = ratio
                    viewModel.setGatesOfOlympusWinRatio(ratio)
                },
                testTagPrefix = "olympus"
            )
        }

        // GAME 4: Sweet Bonanza
        item {
            GameWinRatioCard(
                gameEmoji = "🍭",
                gameName = "Sweet Bonanza (সুইট বোনানজা)",
                description = "ক্যান্ডি টাম্বল পে ও ১০০x পর্যন্ত সুগার বম্ব",
                themeColor = Color(0xFFEC4899),
                badgeColor = Color(0xFFFBCFE8),
                currentRatio = sweetBonanzaRatio,
                onRatioChanged = { ratio ->
                    sweetBonanzaRatio = ratio
                    viewModel.setSweetBonanzaWinRatio(ratio)
                },
                testTagPrefix = "sweet_bonanza"
            )
        }

        item {
            // Golden Card Wild Transform Chance
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF160B30)),
                border = BorderStroke(1.dp, Color(0xFF4C1D95)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Golden Card Spawn Rate",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Reels 2, 3, 4 cards flip to Wilds upon match",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = "${(goldenChance * 100).roundToInt()}%",
                            color = Color(0xFFFBBF24),
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }

                    Slider(
                        value = goldenChance,
                        onValueChange = {
                            goldenChance = it
                            viewModel.setGoldenWildChance(it)
                        },
                        valueRange = 0.05f..0.60f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFBBF24),
                            activeTrackColor = Color(0xFFFBBF24),
                            inactiveTrackColor = Color(0xFF334155)
                        )
                    )
                }
            }
        }

        item {
            // Big Joker Card Chance
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF160B30)),
                border = BorderStroke(1.dp, Color(0xFF4C1D95)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Big Joker Transformation Chance",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Transforms into Big Joker and splashes adjacent symbols into Wilds",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = "${(bigJokerChance * 100).roundToInt()}%",
                            color = Color(0xFFA855F7),
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }

                    Slider(
                        value = bigJokerChance,
                        onValueChange = {
                            bigJokerChance = it
                            viewModel.setBigJokerChance(it)
                        },
                        valueRange = 0.05f..0.50f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFA855F7),
                            activeTrackColor = Color(0xFFA855F7),
                            inactiveTrackColor = Color(0xFF334155)
                        )
                    )
                }
            }
        }

        item {
            // Max Cascades & RTP Multiplier
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF160B30)),
                border = BorderStroke(1.dp, Color(0xFF4C1D95)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Max Cascades: $maxCascades steps",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "RTP Multiplier: ${String.format("%.2f", rtpMultiplier)}x",
                            color = Color(0xFF34D399),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Slider(
                        value = maxCascades.toFloat(),
                        onValueChange = {
                            maxCascades = it.roundToInt()
                            viewModel.setMaxCascadeSteps(maxCascades)
                        },
                        valueRange = 3f..15f,
                        steps = 11,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF34D399),
                            activeTrackColor = Color(0xFF34D399),
                            inactiveTrackColor = Color(0xFF334155)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.resetConfigToDefaults()
                                viewModel.soundManager.playButtonClick()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF374151)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Reset to 30% Default", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val updated = GameAdminConfig(
                                    winRatioPercent = winRatio,
                                    goldenWildChance = goldenChance,
                                    bigJokerChance = bigJokerChance,
                                    maxCascadeSteps = maxCascades,
                                    rtpMultiplierBonus = rtpMultiplier
                                )
                                viewModel.updateConfig(updated)
                                viewModel.soundManager.playButtonClick()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Save All Changes", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// TAB 2: PLAYER & CHIPS (ECONOMY)
// ----------------------------------------------------
@Composable
private fun PlayerTabContent(
    userProfile: UserProfileEntity,
    viewModel: AdminViewModel
) {
    var customBalanceInput by remember { mutableStateOf("") }
    var customDeductInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Profile Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF160B30)),
                border = BorderStroke(1.dp, Color(0xFFEAB308)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = userProfile.avatarEmoji, fontSize = 32.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = userProfile.playerName,
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Level ${userProfile.level} • XP: ${userProfile.xp}/${userProfile.level * 1000}",
                                    color = Color(0xFFFBBF24),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Current Chips",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                            Text(
                                text = String.format("%.2f", userProfile.balance),
                                color = Color(0xFFEAB308),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }

        item {
            // Quick Grant Chips
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF160B30)),
                border = BorderStroke(1.dp, Color(0xFF4C1D95)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Instant Chip Payout (Grant to Player)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val amounts = listOf(1000.0, 5000.0, 25000.0, 100000.0, 1000000.0)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        amounts.forEach { amt ->
                            val label = when {
                                amt >= 1000000 -> "+1M"
                                amt >= 1000 -> "+${(amt / 1000).toInt()}K"
                                else -> "+$amt"
                            }
                            Button(
                                onClick = {
                                    viewModel.addBalance(amt)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }

        item {
            // Quick Deduct Points Card (এডমিন প্লেয়ারের পয়েন্ট কেটে নিতে পারবে)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF230B1E)),
                border = BorderStroke(1.dp, Color(0xFFEF4444)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.RemoveCircle,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "প্লেয়ারের পয়েন্ট কেটে নিন (Deduct Points)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            text = "বর্তমান: ${String.format(Locale.US, "%,.0f", userProfile.balance)}",
                            color = Color(0xFFFCA5A5),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick deduct buttons
                    val deductAmounts = listOf(100.0, 500.0, 1000.0, 5000.0, 20000.0)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        deductAmounts.forEach { amt ->
                            val label = when {
                                amt >= 1000 -> "-${(amt / 1000).toInt()}K"
                                else -> "-${amt.toInt()}"
                            }
                            Button(
                                onClick = {
                                    viewModel.deductPlayerPoints(amt)
                                    viewModel.soundManager.playButtonClick()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF991B1B)),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Custom deduct input + action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customDeductInput,
                            onValueChange = { customDeductInput = it },
                            placeholder = { Text("কেটে নেওয়ার পরিমাণ (যেমন 500)", color = Color.Gray, fontSize = 12.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFEF4444),
                                unfocusedBorderColor = Color(0xFF6B7280)
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val amount = customDeductInput.toDoubleOrNull()
                                if (amount != null && amount > 0) {
                                    viewModel.deductPlayerPoints(amount)
                                    customDeductInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(52.dp)
                        ) {
                            Text("কেটে নিন", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Clear to 0 button
                    Button(
                        onClick = {
                            viewModel.deductPlayerPoints(userProfile.balance)
                            viewModel.soundManager.playButtonClick()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF450A0A)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp)
                    ) {
                        Text("সব পয়েন্ট কেটে শূন্য (০) করুন (Zero Balance)", color = Color(0xFFFCA5A5), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            // Set Exact Balance & Level
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF160B30)),
                border = BorderStroke(1.dp, Color(0xFF4C1D95)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Set Exact Balance",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customBalanceInput,
                            onValueChange = { customBalanceInput = it },
                            placeholder = { Text("e.g. 50000", color = Color.Gray, fontSize = 13.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFEAB308),
                                unfocusedBorderColor = Color(0xFF4B5563)
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val amount = customBalanceInput.toDoubleOrNull()
                                if (amount != null) {
                                    viewModel.setExactBalance(amount)
                                    customBalanceInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(52.dp)
                        ) {
                            Text("Set", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Quick Set Level",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(1, 5, 10, 25, 50).forEach { lvl ->
                            Button(
                                onClick = {
                                    viewModel.setPlayerLevel(lvl)
                                    viewModel.soundManager.playButtonClick()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C1D95)),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(text = "Lv $lvl", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            viewModel.resetPlayerProfile()
                            viewModel.soundManager.playButtonClick()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7F1D1D)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Reset Profile to Default (0 points, Lv 1)", color = Color(0xFFFCA5A5), fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// TAB 3: REAL-TIME ANALYTICS
// ----------------------------------------------------
@Composable
private fun AnalyticsTabContent(
    progress: DailyProgressEntity,
    viewModel: AdminViewModel
) {
    val totalBet = progress.totalBet
    val totalWon = progress.totalWon
    val realizedRtp = if (totalBet > 0) (totalWon / totalBet * 100) else 0.0

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF160B30)),
                border = BorderStroke(1.dp, Color(0xFF4C1D95)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Session Performance (${progress.date})",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricBox(
                            title = "Total Spins",
                            value = "${progress.spinsCount}",
                            color = Color(0xFF38BDF8),
                            modifier = Modifier.weight(1f)
                        )
                        MetricBox(
                            title = "Realized RTP",
                            value = "${String.format("%.1f", realizedRtp)}%",
                            color = if (realizedRtp > 96.0) Color(0xFF34D399) else Color(0xFFFBBF24),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricBox(
                            title = "Total Wagered",
                            value = String.format("%.2f", totalBet),
                            color = Color(0xFFE2E8F0),
                            modifier = Modifier.weight(1f)
                        )
                        MetricBox(
                            title = "Total Paid Out",
                            value = String.format("%.2f", totalWon),
                            color = Color(0xFFEAB308),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricBox(
                            title = "Highest Win",
                            value = String.format("%.2f", progress.highestWin),
                            color = Color(0xFFA855F7),
                            modifier = Modifier.weight(1f)
                        )
                        MetricBox(
                            title = "Max Combo",
                            value = "x${progress.maxCombo}",
                            color = Color(0xFFF43F5E),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricBox(
                            title = "Free Games Won",
                            value = "${progress.freeGamesTriggered}",
                            color = Color(0xFF38BDF8),
                            modifier = Modifier.weight(1f)
                        )
                        MetricBox(
                            title = "Wilds Created",
                            value = "${progress.wildsCreated}",
                            color = Color(0xFFFBBF24),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            viewModel.clearDailyTelemetry()
                            viewModel.soundManager.playButtonClick()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF475569)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Clear Today's Telemetry", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricBox(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0F0624))
            .border(1.dp, Color(0xFF2D1B69), RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(text = title, color = Color(0xFF94A3B8), fontSize = 10.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, color = color, fontSize = 15.sp, fontWeight = FontWeight.Black)
        }
    }
}

// ----------------------------------------------------
// TAB 4: TOURNAMENT & MISSIONS
// ----------------------------------------------------
@Composable
private fun TournamentTabContent(
    leaderboard: List<LeaderboardEntryEntity>,
    progress: DailyProgressEntity,
    viewModel: AdminViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tournament Standings",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp
                )
                Button(
                    onClick = {
                        viewModel.resetLeaderboard()
                        viewModel.soundManager.playButtonClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C1D95)),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Reset Leaderboard", fontSize = 11.sp)
                }
            }
        }

        items(leaderboard) { entry ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (entry.isCurrentUser) Color(0x33EAB308) else Color(0xFF160B30))
                    .border(
                        1.dp,
                        if (entry.isCurrentUser) Color(0xFFEAB308) else Color(0xFF2D1B69),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "#${entry.rank}",
                        color = when (entry.rank) {
                            1 -> Color(0xFFFFD700)
                            2 -> Color(0xFFC0C0C0)
                            3 -> Color(0xFFCD7F32)
                            else -> Color(0xFF94A3B8)
                        },
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        modifier = Modifier.width(32.dp)
                    )
                    Text(text = entry.avatarEmoji, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = entry.playerName,
                        color = if (entry.isCurrentUser) Color(0xFFFBBF24) else Color.White,
                        fontWeight = if (entry.isCurrentUser) FontWeight.Black else FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }

                Text(
                    text = "${entry.points} pts",
                    color = Color(0xFFEAB308),
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF160B30)),
                border = BorderStroke(1.dp, Color(0xFF4C1D95)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Missions Controls",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Button(
                            onClick = {
                                viewModel.resetDailyMissions()
                                viewModel.soundManager.playButtonClick()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA)),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Reset Missions Status", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// TAB 5: AUDIO STUDIO & SFX TEST
// ----------------------------------------------------
@Composable
private fun AudioTabContent(
    viewModel: AdminViewModel
) {
    var isMuted by remember { mutableStateOf(!viewModel.soundManager.isEnabled) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF160B30)),
                border = BorderStroke(1.dp, Color(0xFF4C1D95)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Master Sound Engine",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Synthesized real-time 16-bit PCM arcade audio",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }

                        Switch(
                            checked = !isMuted,
                            onCheckedChange = { checked ->
                                isMuted = !checked
                                viewModel.toggleMasterSound(checked)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFEAB308),
                                checkedTrackColor = Color(0xFF9333EA)
                            )
                        )
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF160B30)),
                border = BorderStroke(1.dp, Color(0xFF4C1D95)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Interactive Sound Test Board",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Tap any button to test and preview the authentic slot machine sound effect in real time",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val soundButtons = listOf(
                        Triple("🎰 Spin Whirr & Ticks", "spin", Color(0xFF3B82F6)),
                        Triple("🛑 Reel Lock / Stop", "stop", Color(0xFF6366F1)),
                        Triple("🤠 Revolver Cylinder Spin (Dead Man's Bullet)", "revolver", Color(0xFFD97706)),
                        Triple("🔫 Colt 45 Hammer Cock (Dead Man's Bullet)", "gun_cock", Color(0xFFB45309)),
                        Triple("💥 Gunshot Blast & Ricochet (Dead Man's Bullet)", "gunshot", Color(0xFFDC2626)),
                        Triple("🎸 Western Outlaw Melody (Dead Man's Bullet)", "western_chord", Color(0xFF92400E)),
                        Triple("⚡ Zeus Thunder Strike (Gates of Olympus)", "thunder", Color(0xFF7C3AED)),
                        Triple("🍬 Sweet Candy Pop (Sweet Bonanza)", "candy_pop", Color(0xFFDB2777)),
                        Triple("✨ Combo 1 (x1 / 523Hz)", "combo_1", Color(0xFF10B981)),
                        Triple("✨ Combo 2 (x2 / 659Hz)", "combo_2", Color(0xFF059669)),
                        Triple("✨ Combo 3 (x3 / 784Hz)", "combo_3", Color(0xFF047857)),
                        Triple("✨ Combo 4+ (x5 / 1046Hz)", "combo_4", Color(0xFF065F46)),
                        Triple("🃏 Golden Wild Transform", "wild", Color(0xFF8B5CF6)),
                        Triple("🪙 Coin Ping / Payout Drop", "coin", Color(0xFFF59E0B)),
                        Triple("🎺 Big Win Fanfare Melody", "big_win", Color(0xFFDC2626)),
                        Triple("🎆 Free Spins Trigger Fanfare", "free_spins", Color(0xFFE11D48)),
                        Triple("🔘 Tactile Button Click", "click", Color(0xFF475569))
                    )

                    soundButtons.forEach { (label, soundKey, btnColor) ->
                        Button(
                            onClick = {
                                viewModel.testSound(soundKey)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = btnColor),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = label,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// Tab: Deposit & Wallet Management
// ----------------------------------------------------
@Composable
private fun DepositTabContent(
    walletConfig: AdminWalletConfigEntity,
    allDeposits: List<DepositRequestEntity>,
    viewModel: AdminViewModel
) {
    val context = LocalContext.current
    var nagadNumber by remember(walletConfig) { mutableStateOf(walletConfig.nagadWallet) }
    var bkashNumber by remember(walletConfig) { mutableStateOf(walletConfig.bkashWallet) }
    var rocketNumber by remember(walletConfig) { mutableStateOf(walletConfig.rocketWallet) }
    var upayNumber by remember(walletConfig) { mutableStateOf(walletConfig.upayWallet) }
    var minDeposit by remember(walletConfig) { mutableStateOf(walletConfig.minDeposit.toInt().toString()) }
    var maxDeposit by remember(walletConfig) { mutableStateOf(walletConfig.maxDeposit.toInt().toString()) }

    val pendingDeposits = remember(allDeposits) { allDeposits.filter { it.status == "PENDING" } }
    val historyDeposits = remember(allDeposits) { allDeposits.filter { it.status != "PENDING" } }
    var depositSearchQuery by remember { mutableStateOf("") }

    val searchedPendingDeposits = remember(pendingDeposits, depositSearchQuery) {
        if (depositSearchQuery.isBlank()) pendingDeposits
        else {
            val q = depositSearchQuery.trim().lowercase()
            pendingDeposits.filter {
                it.userPhone.lowercase().contains(q) ||
                it.trxId.lowercase().contains(q) ||
                it.method.lowercase().contains(q) ||
                it.amount.toString().contains(q) ||
                it.id.toString().contains(q)
            }
        }
    }

    val searchedHistoryDeposits = remember(historyDeposits, depositSearchQuery) {
        if (depositSearchQuery.isBlank()) historyDeposits
        else {
            val q = depositSearchQuery.trim().lowercase()
            historyDeposits.filter {
                it.userPhone.lowercase().contains(q) ||
                it.trxId.lowercase().contains(q) ||
                it.method.lowercase().contains(q) ||
                it.amount.toString().contains(q) ||
                it.id.toString().contains(q)
            }
        }
    }
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 1: Wallet Numbers Configuration
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF160933)),
                border = BorderStroke(1.dp, Color(0xFF8B5CF6)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF6D28D9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = "Wallet Setup",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "সরাসরি ওয়ালেট নম্বর কন্ট্রোল",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "গেম অ্যাপে প্লেয়াররা এই নম্বরগুলোতে ক্যাশ আউট / সেন্ড মানি করবে",
                                color = Color(0xFFC4B5FD),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Nagad
                    OutlinedTextField(
                        value = nagadNumber,
                        onValueChange = { nagadNumber = it },
                        label = { Text("🔴 নগদ (Nagad) নম্বর") },
                        placeholder = { Text("01303347372") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_nagad_number_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFF97316),
                            unfocusedBorderColor = Color(0xFF4C1D95),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // bKash
                    OutlinedTextField(
                        value = bkashNumber,
                        onValueChange = { bkashNumber = it },
                        label = { Text("🟣 বিকাশ (bKash) নম্বর") },
                        placeholder = { Text("01303347372") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_bkash_number_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFEC4899),
                            unfocusedBorderColor = Color(0xFF4C1D95),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Rocket
                    OutlinedTextField(
                        value = rocketNumber,
                        onValueChange = { rocketNumber = it },
                        label = { Text("🔵 রকেট (Rocket) নম্বর") },
                        placeholder = { Text("01303347372") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_rocket_number_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF60A5FA),
                            unfocusedBorderColor = Color(0xFF4C1D95),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Upay
                    OutlinedTextField(
                        value = upayNumber,
                        onValueChange = { upayNumber = it },
                        label = { Text("🟡 উপায় (Upay) নম্বর") },
                        placeholder = { Text("01303347372") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_upay_number_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFBBF24),
                            unfocusedBorderColor = Color(0xFF4C1D95),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Min and Max Deposit Range
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = minDeposit,
                            onValueChange = { minDeposit = it.filter { ch -> ch.isDigit() } },
                            label = { Text("সর্বনিম্ন পয়েন্ট") },
                            placeholder = { Text("100") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF10B981),
                                unfocusedBorderColor = Color(0xFF4C1D95),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        OutlinedTextField(
                            value = maxDeposit,
                            onValueChange = { maxDeposit = it.filter { ch -> ch.isDigit() } },
                            label = { Text("সর্বোচ্চ পয়েন্ট") },
                            placeholder = { Text("20000") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF10B981),
                                unfocusedBorderColor = Color(0xFF4C1D95),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val minVal = minDeposit.toDoubleOrNull() ?: 100.0
                            val maxVal = maxDeposit.toDoubleOrNull() ?: 20000.0
                            viewModel.updateWalletNumbers(
                                nagad = nagadNumber,
                                bkash = bkashNumber,
                                rocket = rocketNumber,
                                upay = upayNumber,
                                minDeposit = minVal,
                                maxDeposit = maxVal
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("admin_save_wallets_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ওয়ালেট নম্বর ও লিমিট সংরক্ষণ করুন",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Deposit Search Bar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF130926)),
                border = BorderStroke(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.6f)),
                shape = RoundedCornerShape(10.dp)
            ) {
                OutlinedTextField(
                    value = depositSearchQuery,
                    onValueChange = { depositSearchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_deposit_search_bar"),
                    placeholder = {
                        Text(
                            text = "ডিপোজিট আবেদন ও হিস্ট্রি খুঁজুন (নম্বর, Trx ID, মেথড)...",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (depositSearchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { depositSearchQuery = "" },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }
        }

        // Section 2: Pending Deposit Requests
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = "📥 অপেক্ষমান ডিপোজিট আবেদন",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (searchedPendingDeposits.isNotEmpty()) Color(0xFFEF4444) else Color(0xFF334155))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${searchedPendingDeposits.size} টি",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (searchedPendingDeposits.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF140A26)),
                    border = BorderStroke(1.dp, Color(0xFF2E1065)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "✨", fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (depositSearchQuery.isNotBlank()) "সার্চ অনুসারে কোনো অপেক্ষমান ডিপোজিট পাওয়া যায়নি" else "কোনো অপেক্ষমান ডিপোজিট আবেদন নেই",
                                color = Color(0xFFA78BFA),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "প্লেয়ার পয়েন্ট লোডের আবেদন সাবমিট করলে এখানে এপ্রুভালের জন্য জমা হবে",
                                color = Color(0xFF6B7280),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        } else {
            items(searchedPendingDeposits, key = { it.id }) { req ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1035)),
                    border = BorderStroke(1.5.dp, Color(0xFFF59E0B)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_pending_deposit_${req.id}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        // Player & Method Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "📱 আইডি:",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = req.userPhone,
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                            }
                            // Method Chip
                            val displayMethod = when (req.method.uppercase()) {
                                "NAGAD", "নগদ" -> "নগদ"
                                "BKASH", "BKASH_SEND_MONEY", "বিকাশ" -> "বিকাশ"
                                "ROCKET", "রকেট" -> "রকেট"
                                "UPAY", "উপায়" -> "উপায়"
                                else -> req.method
                            }
                            val methodColor = when (req.method.uppercase()) {
                                "NAGAD", "নগদ" -> Color(0xFFF97316)
                                "BKASH", "BKASH_SEND_MONEY", "বিকাশ" -> Color(0xFFEC4899)
                                "ROCKET", "রকেট" -> Color(0xFF3B82F6)
                                else -> Color(0xFFEAB308)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(methodColor)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = displayMethod,
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Requested Amount
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "অনুরোধকৃত পয়েন্ট:",
                                color = Color(0xFFE2E8F0),
                                fontSize = 13.sp
                            )
                            Text(
                                text = "🪙 ${String.format(Locale.US, "%,.0f", req.amount)} pts",
                                color = Color(0xFFFFD700),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // TrxID with Copy Button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F061E))
                                .border(1.dp, Color(0xFF4C1D95), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "ট্যান্জাকশন আইডি (TrxID):",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = req.trxId,
                                    color = Color(0xFF38BDF8),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("TrxID", req.trxId)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "TrxID কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy TrxID",
                                    tint = Color(0xFF93C5FD),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Wallet & Timestamp info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "প্রেরিত নম্বর: ${req.adminWalletNumber}",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                            Text(
                                text = dateFormat.format(Date(req.submittedAt)),
                                color = Color(0xFF64748B),
                                fontSize = 10.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Action Buttons: Approve & Reject
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.rejectDeposit(req.id) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .testTag("admin_reject_deposit_${req.id}"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "বাতিল",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            Button(
                                onClick = { viewModel.approveDeposit(req.id) },
                                modifier = Modifier
                                    .weight(1.5f)
                                    .height(42.dp)
                                    .testTag("admin_approve_deposit_${req.id}"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "অনুমোদন করুন",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Completed / Past Deposits
        if (searchedHistoryDeposits.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "📜 বিগত ডিপোজিট হিস্ট্রি (${searchedHistoryDeposits.size} টি)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            items(searchedHistoryDeposits.take(15), key = { it.id }) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF120722)),
                    border = BorderStroke(1.dp, Color(0xFF281347)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = item.userPhone,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                val histMethod = when (item.method.uppercase()) {
                                    "NAGAD", "নগদ" -> "নগদ"
                                    "BKASH", "BKASH_SEND_MONEY", "বিকাশ" -> "বিকাশ"
                                    "ROCKET", "রকেট" -> "রকেট"
                                    "UPAY", "উপায়" -> "উপায়"
                                    else -> item.method
                                }
                                Text(
                                    text = "($histMethod)",
                                    color = Color(0xFFA78BFA),
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = "Trx: ${item.trxId} • ${dateFormat.format(Date(item.submittedAt))}",
                                color = Color(0xFF64748B),
                                fontSize = 10.sp
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "+${String.format(Locale.US, "%,.0f", item.amount)}",
                                color = if (item.status == "APPROVED") Color(0xFF4ADE80) else Color(0xFF94A3B8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (item.status == "APPROVED") Color(0x3310B981) else Color(0x33EF4444)
                                    )
                                    .border(
                                        1.dp,
                                        if (item.status == "APPROVED") Color(0xFF10B981) else Color(0xFFEF4444),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (item.status == "APPROVED") "এপ্রুভড" else "বাতিল",
                                    color = if (item.status == "APPROVED") Color(0xFF34D399) else Color(0xFFF87171),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GameWinRatioCard(
    gameEmoji: String,
    gameName: String,
    description: String,
    themeColor: Color,
    badgeColor: Color,
    currentRatio: Int,
    onRatioChanged: (Int) -> Unit,
    testTagPrefix: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF160B30)),
        border = BorderStroke(1.dp, themeColor.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = gameEmoji, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = gameName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = description,
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(themeColor)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$currentRatio%",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Slider(
                value = currentRatio.toFloat(),
                onValueChange = { onRatioChanged(it.roundToInt()) },
                valueRange = 5f..95f,
                steps = 17,
                colors = SliderDefaults.colors(
                    thumbColor = themeColor,
                    activeTrackColor = themeColor,
                    inactiveTrackColor = Color(0xFF334155)
                ),
                modifier = Modifier.testTag("${testTagPrefix}_win_ratio_slider")
            )

            // Preset buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(10, 25, 50, 75, 90).forEach { preset ->
                    val isSelected = currentRatio == preset
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(5.dp))
                            .background(if (isSelected) themeColor else Color(0xFF0F172A))
                            .border(1.dp, if (isSelected) badgeColor else Color(0xFF334155), RoundedCornerShape(5.dp))
                            .clickable { onRatioChanged(preset) }
                            .padding(vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$preset%",
                            color = if (isSelected) Color.Black else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// TAB: PROFIT & LOSS / AUDIT STATEMENT
// ----------------------------------------------------
@Composable
private fun ProfitLossTabContent(
    userProfile: UserProfileEntity,
    allDeposits: List<DepositRequestEntity>,
    allWithdraws: List<WithdrawRequestEntity>,
    todayProgress: DailyProgressEntity,
    allRegisteredAccounts: List<RegisteredAccountEntity>,
    viewModel: AdminViewModel
) {
    val context = LocalContext.current
    val approvedDeposits = allDeposits.filter { it.status == "APPROVED" }
    val approvedWithdraws = allWithdraws.filter { it.status == "APPROVED" }

    val totalDepositAmount = approvedDeposits.sumOf { it.amount }
    val totalWithdrawAmount = approvedWithdraws.sumOf { it.amount }
    val cashflowNetProfit = totalDepositAmount - totalWithdrawAmount

    val lifetimeBet = userProfile.lifetimeBet
    val lifetimeWon = userProfile.lifetimeWon
    val casinoGrossMargin = lifetimeBet - lifetimeWon
    val totalAdminNetProfit = cashflowNetProfit + casinoGrossMargin

    var previewExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // TOP HERO: Admin Total Net Profit
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (totalAdminNetProfit >= 0) Color(0xFF072714) else Color(0xFF2E0808)
                ),
                border = BorderStroke(
                    2.dp,
                    if (totalAdminNetProfit >= 0) Color(0xFF10B981) else Color(0xFFEF4444)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (totalAdminNetProfit >= 0) Color(0x3310B981) else Color(0x33EF4444)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (totalAdminNetProfit >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                    contentDescription = null,
                                    tint = if (totalAdminNetProfit >= 0) Color(0xFF34D399) else Color(0xFFF87171),
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "এডমিনের মোট নিট লাভ (Total Net Profit)",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "ক্যাশফ্লো প্রফিট + স্লট ক্যাসিনো গ্রস মার্জিন",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Text(
                            text = "${if (totalAdminNetProfit >= 0) "+" else ""}${String.format(Locale.US, "%,.0f", totalAdminNetProfit)} ৳",
                            color = if (totalAdminNetProfit >= 0) Color(0xFF34D399) else Color(0xFFEF4444),
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0x33FFFFFF))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "ক্যাশফ্লো মার্জিন (ডিপোজিট - উইথড্রো):",
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp
                        )
                        Text(
                            text = "${if (cashflowNetProfit >= 0) "+" else ""}${String.format(Locale.US, "%,.0f", cashflowNetProfit)} ৳",
                            color = if (cashflowNetProfit >= 0) Color(0xFF6EE7B7) else Color(0xFFFCA5A5),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "স্লট ইঞ্জিন মার্জিন (টার্নওভার বাজি - উইন):",
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp
                        )
                        Text(
                            text = "${if (casinoGrossMargin >= 0) "+" else ""}${String.format(Locale.US, "%,.0f", casinoGrossMargin)} ৳",
                            color = if (casinoGrossMargin >= 0) Color(0xFF6EE7B7) else Color(0xFFFCA5A5),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 4 KEY KPI CARDS
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Approved Deposits
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF132219)),
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "📥 সর্বমোট ডিপোজিট",
                                color = Color(0xFFA7F3D0),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${String.format(Locale.US, "%,.0f", totalDepositAmount)} ৳",
                                color = Color(0xFF34D399),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "${approvedDeposits.size} টি অনুমোদিত রিকোয়েস্ট",
                                color = Color(0xFF6EE7B7),
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Approved Withdrawals
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF261019)),
                        border = BorderStroke(1.dp, Color(0xFFF43F5E).copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "📤 সর্বমোট উইথড্রো প্রদান",
                                color = Color(0xFFFECDD3),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${String.format(Locale.US, "%,.0f", totalWithdrawAmount)} ৳",
                                color = Color(0xFFFB7185),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "${approvedWithdraws.size} টি পেইড রিকোয়েস্ট",
                                color = Color(0xFFFDA4AF),
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Total Bets Placed
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1834)),
                        border = BorderStroke(1.dp, Color(0xFF818CF8).copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "🎰 প্লেয়ারদের মোট বাজি",
                                color = Color(0xFFC7D2FE),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${String.format(Locale.US, "%,.0f", lifetimeBet)} ৳",
                                color = Color(0xFFA5B4FC),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "মোট টার্নওভার ভলিউম",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Total Won Paid Out
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF221630)),
                        border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "🏆 মোট পেআউট উইন",
                                color = Color(0xFFFDE68A),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${String.format(Locale.US, "%,.0f", lifetimeWon)} ৳",
                                color = Color(0xFFFBBF24),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            val payoutPercent = if (lifetimeBet > 0) (lifetimeWon / lifetimeBet) * 100 else 0.0
                            Text(
                                text = "প্রকৃত পেআউট রেশিও: ${String.format(Locale.US, "%.1f", payoutPercent)}%",
                                color = Color(0xFFFCD34D),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // DOWNLOAD / EXPORT STATEMENT SECTION (ইউজারের তথ্য ও ব্যালেন্স স্টেটমেন্ট ফাইল ডাউনলোড)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1038)),
                border = BorderStroke(2.dp, Color(0xFFFBBF24)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFBBF24).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = Color(0xFFFBBF24),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "এডমিন অডিট ও ব্যালেন্স স্টেটমেন্ট",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "ইউজারের হিসাব ও স্টেটমেন্ট ফাইল ডাউনলোড করুন",
                                    color = Color(0xFFFDE68A),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "এডমিন হিসেবে আপনি ইউজার আইডি, মোবাইল নম্বর, ব্যালেন্স, ডিপোজিট ও উইথড্রো হিস্ট্রির সম্পূর্ণ স্টেটমেন্ট ফাইল আকারে ডাউনলোড ও সেভ করে রাখতে পারেন।",
                        color = Color(0xFFE2E8F0),
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action Buttons: TXT, CSV, Copy
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.exportStatementText(context)
                                viewModel.soundManager.playButtonClick()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("admin_export_statement_txt_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "📄 TXT ফাইল", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.exportStatementCsv(context)
                                viewModel.soundManager.playButtonClick()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("admin_export_statement_csv_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "📊 CSV এক্সেল", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val summary = viewModel.getStatementSummaryText()
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Statement", summary)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "স্টেটমেন্ট ক্লিপবোর্ডে কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                viewModel.soundManager.playButtonClick()
                            },
                            modifier = Modifier
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C1D95)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Toggle Preview
                    Button(
                        onClick = { previewExpanded = !previewExpanded },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E1065)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (previewExpanded) "▲ স্টেটমেন্ট প্রিভিউ লুকান" else "▼ স্টেটমেন্ট প্রিভিউ দেখুন",
                            fontSize = 11.sp,
                            color = Color(0xFFE9D5FF)
                        )
                    }

                    if (previewExpanded) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0D061A))
                                .border(1.dp, Color(0xFF4C1D95), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = viewModel.getStatementSummaryText(),
                                color = Color(0xFFFDE68A),
                                fontSize = 10.sp,
                                lineHeight = 15.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // PLAYER FINANCIAL STATS
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF160A2E)),
                border = BorderStroke(1.dp, Color(0xFF7C3AED)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "👤 বর্তমান সক্রিয় প্লেয়ার প্রোফাইল স্টেটমেন্ট",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "ইউজার নাম:", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        Text(text = userProfile.playerName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "নিবন্ধিত মোবাইল:", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        Text(text = if (userProfile.userPhone.isNotBlank()) userProfile.userPhone else "017XXXXXXXX", color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "চলতি ব্যালেন্স:", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        Text(text = "${String.format(Locale.US, "%,.0f", userProfile.balance)} পয়েন্ট", color = Color(0xFF34D399), fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "প্লেয়ারের মোট ডিপোজিট:", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        Text(text = "${String.format(Locale.US, "%,.0f", userProfile.lifetimeDeposit)} ৳", color = Color.White, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "প্লেয়ারের মোট উইথড্রো:", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        Text(text = "${String.format(Locale.US, "%,.0f", userProfile.lifetimeWithdraw)} ৳", color = Color.White, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "প্লেয়ারের মোট বাজি:", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        Text(text = "${String.format(Locale.US, "%,.0f", userProfile.lifetimeBet)} ৳", color = Color.White, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "প্লেয়ারের মোট উইন:", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        Text(text = "${String.format(Locale.US, "%,.0f", userProfile.lifetimeWon)} ৳", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// TAB: ACCOUNTS & DEVICE LOCK (১ ফোন = ১ একাউন্ট)
// ----------------------------------------------------
@Composable
private fun AccountsTabContent(
    allAccounts: List<RegisteredAccountEntity>,
    userProfile: UserProfileEntity,
    viewModel: AdminViewModel
) {
    val context = LocalContext.current
    val sdf = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

    var accountForBalanceEdit by remember { mutableStateOf<RegisteredAccountEntity?>(null) }
    var newBalanceInput by remember { mutableStateOf("") }
    var showAddPlayerDialog by remember { mutableStateOf(false) }
    var addPhoneInput by remember { mutableStateOf("") }
    var addNameInput by remember { mutableStateOf("") }
    var addBalanceInput by remember { mutableStateOf("500") }
    var isSyncingAll by remember { mutableStateOf(false) }

    // Dialog for Editing Balance
    accountForBalanceEdit?.let { targetAccount ->
        AlertDialog(
            onDismissRequest = { accountForBalanceEdit = null },
            title = { Text("খেলোয়াড়ের ব্যালেন্স পরিবর্তন", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "ইউজার: ${targetAccount.playerName} (${targetAccount.userPhone})",
                        color = Color(0xFFFBBF24),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "বর্তমান ব্যালেন্স: ৳ ${"%.2f".format(targetAccount.balance)}",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newBalanceInput,
                        onValueChange = { newBalanceInput = it },
                        label = { Text("নতুন ব্যালেন্স (৳)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0xFF475569)
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = newBalanceInput.toDoubleOrNull()
                        if (parsed != null && parsed >= 0) {
                            viewModel.updateCloudUserBalance(targetAccount.userPhone, parsed)
                            Toast.makeText(context, "ব্যালেন্স আপডেট ও ক্লাউডে সিঙ্ক করা হয়েছে", Toast.LENGTH_SHORT).show()
                            accountForBalanceEdit = null
                        } else {
                            Toast.makeText(context, "সঠিক ব্যালেন্স সংখ্যা দিন", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text("সংরক্ষণ ও ক্লাউডে পাঠান", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { accountForBalanceEdit = null }) {
                    Text("বাতিল", color = Color(0xFF94A3B8))
                }
            },
            containerColor = Color(0xFF0F1E36)
        )
    }

    // Dialog for Adding Player
    if (showAddPlayerDialog) {
        AlertDialog(
            onDismissRequest = { showAddPlayerDialog = false },
            title = { Text("নতুন খেলোয়াড় যোগ করুন", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = addPhoneInput,
                        onValueChange = { addPhoneInput = it },
                        label = { Text("মোবাইল নম্বর (১১ ডিজিট)") },
                        placeholder = { Text("01XXXXXXXXX") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0xFF475569)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = addNameInput,
                        onValueChange = { addNameInput = it },
                        label = { Text("প্লেয়ারের নাম") },
                        placeholder = { Text("Player One") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0xFF475569)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = addBalanceInput,
                        onValueChange = { addBalanceInput = it },
                        label = { Text("শুরুর ব্যালেন্স (৳)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0xFF475569)
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cleanPhone = addPhoneInput.trim()
                        val cleanName = addNameInput.trim().ifBlank { "Player $cleanPhone" }
                        val bal = addBalanceInput.toDoubleOrNull() ?: 500.0
                        if (cleanPhone.length >= 11) {
                            viewModel.addManualPlayer(cleanPhone, cleanName, bal)
                            Toast.makeText(context, "নতুন প্লেয়ার যুক্ত ও ক্লাউডে সিঙ্ক করা হয়েছে", Toast.LENGTH_SHORT).show()
                            showAddPlayerDialog = false
                            addPhoneInput = ""
                            addNameInput = ""
                        } else {
                            Toast.makeText(context, "সঠিক ১১ ডিজিটের ফোন নম্বর দিন", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8))
                ) {
                    Text("যুক্ত ও ক্লাউড সিঙ্ক", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPlayerDialog = false }) {
                    Text("বাতিল", color = Color(0xFF94A3B8))
                }
            },
            containerColor = Color(0xFF0F1E36)
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Security Header Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E36)),
                border = BorderStroke(2.dp, Color(0xFF38BDF8)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF38BDF8).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "ডিভাইস ও অ্যাকাউন্ট সিকিউরিটি",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "১ মোবাইল + ১ ফোন = ১ অ্যাকাউন্ট নীতি সক্রিয়",
                                    color = Color(0xFFBAE6FD),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF0284C7))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${allAccounts.size} টি ইউজার",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x2038BDF8))
                            .border(0.8.dp, Color(0xFF38BDF8), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "🛡️ সুরক্ষা নিয়মাবলী:\n১. কোনো ইউজার একটি ফোন বা ডিভাইস দিয়ে একবারের বেশি একাউন্ট করতে পারবে না।\n২. একটি মোবাইল নাম্বার দিয়েও শুধুমাত্র ১ টি একাউন্ট তৈরি করা সম্ভব।\n৩. প্রতিটি একাউন্ট তৈরির সময় ফোনের হার্ডওয়্যার ফিঙ্গারপ্রিন্ট ডাটাবেজে লক হয়ে যায়।",
                            color = Color(0xFFE0F2FE),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Buttons: Sync All to Cloud, Add Player, Export Audit
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.syncAllAccountsToCloud()
                                Toast.makeText(context, "সকল ইউজার ডাটা ক্লাউডে সিঙ্ক করা হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = if (isSyncingAll) "সিঙ্ক হচ্ছে..." else "ক্লাউডে সিঙ্ক", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { showAddPlayerDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "প্লেয়ার যোগ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            viewModel.exportStatementCsv(context)
                            viewModel.soundManager.playButtonClick()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "ইউজার তালিকা ও ডিভাইস অডিট ফাইল এক্সপোর্ট", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Account List
        if (allAccounts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF160A2E)),
                    border = BorderStroke(1.dp, Color(0xFF4C1D95)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "কোনো নিবন্ধিত অ্যাকাউন্ট পাওয়া যায়নি",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "প্লেয়াররা অ্যাপে মোবাইল নাম্বার দিয়ে রেজিস্ট্রেশন করলে তাদের ডিভাইস ও আইডি এখানে সংরক্ষিত থাকবে।",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(allAccounts, key = { it.userPhone }) { acc ->
                val isBlocked = acc.status == "BLOCKED"
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_account_card_${acc.userPhone}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlocked) Color(0xFF280A15) else Color(0xFF160A2E)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isBlocked) Color(0xFFEF4444) else Color(0xFF38BDF8).copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (isBlocked) Color(0x33EF4444) else Color(0xFF38BDF8).copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isBlocked) Icons.Default.Block else Icons.Default.PhoneAndroid,
                                        contentDescription = null,
                                        tint = if (isBlocked) Color(0xFFEF4444) else Color(0xFF38BDF8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = acc.playerName,
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = acc.userPhone,
                                        color = Color(0xFFFBBF24),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isBlocked) Color(0x33EF4444) else Color(0x3310B981))
                                    .border(1.dp, if (isBlocked) Color(0xFFEF4444) else Color(0xFF10B981), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (isBlocked) "🚫 BLOCKED" else "🔒 DEVICE LOCKED",
                                    color = if (isBlocked) Color(0xFFF87171) else Color(0xFF34D399),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0x22FFFFFF))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Balance and VIP Level Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "ব্যালেন্স: ", color = Color(0xFF94A3B8), fontSize = 12.sp)
                                Text(
                                    text = "৳ ${"%.2f".format(acc.balance)}",
                                    color = Color(0xFF34D399),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF1E293B))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = "লেভেল: ${acc.level}", color = Color(0xFFFDE68A), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "লকড ফোন মডেল:", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            Text(text = acc.deviceModel, color = Color(0xFFE2E8F0), fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "ডিভাইস হার্ডওয়্যার আইডি:", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            Text(
                                text = if (acc.deviceId.length > 16) "${acc.deviceId.take(16)}..." else acc.deviceId,
                                color = Color(0xFF93C5FD),
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "নিবন্ধনের তারিখ:", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            Text(
                                text = sdf.format(Date(acc.registeredAt)),
                                color = Color(0xFFCBD5E1),
                                fontSize = 10.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Action Buttons: Edit Balance, Block/Unblock, Copy phone
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Edit Balance Button
                            Button(
                                onClick = {
                                    newBalanceInput = acc.balance.toInt().toString()
                                    accountForBalanceEdit = acc
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(30.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "ব্যালেন্স", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            // Block / Unblock Button
                            Button(
                                onClick = {
                                    viewModel.toggleCloudUserBlock(acc.userPhone)
                                    Toast.makeText(
                                        context,
                                        if (isBlocked) "ইউজার আনব্লক ও ক্লাউডে সিঙ্ক করা হয়েছে" else "ইউজার ব্লক ও ক্লাউডে সিঙ্ক করা হয়েছে",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isBlocked) Color(0xFF10B981) else Color(0xFFDC2626)
                                ),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(30.dp)
                            ) {
                                Icon(
                                    imageVector = if (isBlocked) Icons.Default.CheckCircle else Icons.Default.Block,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = if (isBlocked) "আনব্লক" else "ব্লক", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            // Copy phone action
                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("UserPhone", acc.userPhone)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "নম্বর ${acc.userPhone} কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .weight(0.9f)
                                    .height(30.dp)
                            ) {
                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "কপি", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// Tab: Custom Games & API Link Management
// ----------------------------------------------------
@Composable
private fun CustomGamesTabContent(
    allGames: List<CustomGameEntity>,
    onTestGame: (CustomGameEntity) -> Unit,
    viewModel: AdminViewModel
) {
    val context = LocalContext.current

    var gameTitle by remember { mutableStateOf("") }
    var providerName by remember { mutableStateOf("") }
    var gameSubtitle by remember { mutableStateOf("") }
    var gameUrlOrApi by remember { mutableStateOf("") }
    var badgeText by remember { mutableStateOf("NEW") }
    var maxWinText by remember { mutableStateOf("10,000x MAX WIN") }
    var rtpText by remember { mutableStateOf("97.0%") }

    val badges = listOf("NEW", "HOT", "JACKPOT", "LIVE", "VIP", "EXCLUSIVE")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_custom_games_tab"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Info Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF170933)),
                border = BorderStroke(1.dp, Color(0xFF8B5CF6)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF8B5CF6)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsEsports,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "🎮 নতুন গেম লিংক ও API অ্যাডমিন ম্যানেজমেন্ট",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "যেকোনো নতুন ক্যাসিনো, স্লট বা লাইভ গেমের ওয়েব লিংক/API যুক্ত করুন",
                                color = Color(0xFFC4B5FD),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF2E1065).copy(alpha = 0.6f))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "💡 সুবিধা: এখানে যে কোনো গেম লিংক যুক্ত করলে তা সরাসরি প্লেয়ার অ্যাপের হোম স্ক্রিনের সবার উপরে 'NEW' ব্যাজ সহ ভেসে উঠবে। ইউজাররা গেমটি খেলতে ও ফেভারিট স্টার করতে পারবে।",
                            color = Color(0xFFE9D5FF),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Add Game Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF130A28)),
                border = BorderStroke(1.dp, Color(0xFFEAB308)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "➕", fontSize = 18.sp)
                        Text(
                            text = "নতুন গেম যুক্ত করুন (Add & Publish Game)",
                            color = Color(0xFFFDE047),
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )
                    }

                    Text(
                        text = "⚡ দ্রুত এক ক্লিকে টেমপ্লেট নির্বাচন করুন (Quick Presets):",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )

                    // Presets Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val presets = listOf(
                            Triple("🚀 Aviator Crash", "Spribe", "https://spribe.co/games/aviator") to "10,000x MAX WIN",
                            Triple("💣 Mines Gold", "Spribe", "https://spribe.co/games/mines") to "5,000x MAX WIN",
                            Triple("🎡 Crazy Time", "Evolution", "https://www.evolution.com/games/crazy-time/") to "25,000x MAX WIN",
                            Triple("🎲 Plinko Ball", "BGaming", "https://bgaming.com/games/plinko") to "1,000x MAX WIN",
                            Triple("🃏 Teen Patti", "Ezugi", "https://ezugi.com/games/teen-patti/") to "1,000x MAX WIN"
                        )

                        presets.forEach { (meta, maxWin) ->
                            val (name, prov, url) = meta
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF2E1065))
                                    .border(1.dp, Color(0xFF8B5CF6), RoundedCornerShape(8.dp))
                                    .clickable {
                                        gameTitle = name
                                        providerName = prov
                                        gameUrlOrApi = url
                                        gameSubtitle = "$name বাই $prov - লাইভ ক্যাসিনো ও আর্কেড গেম"
                                        maxWinText = maxWin
                                        badgeText = "NEW"
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = name,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0x33FFFFFF))

                    // Title
                    OutlinedTextField(
                        value = gameTitle,
                        onValueChange = { gameTitle = it },
                        label = { Text("গেমের নাম (Title)") },
                        placeholder = { Text("যেমন: Aviator Crash, Mines, Crazy Time") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_game_title_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    // Provider
                    OutlinedTextField(
                        value = providerName,
                        onValueChange = { providerName = it },
                        label = { Text("গেম প্রোভাইডার (Provider)") },
                        placeholder = { Text("যেমন: Spribe, Pragmatic Play, JILI, Custom") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_game_provider_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    // Game Web URL or API Endpoint
                    OutlinedTextField(
                        value = gameUrlOrApi,
                        onValueChange = { gameUrlOrApi = it },
                        label = { Text("গেমের ওয়েব লিংক বা API ইউআরএল (Web / API URL)") },
                        placeholder = { Text("https://example.com/game-frame...") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_game_url_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    // Subtitle / Description
                    OutlinedTextField(
                        value = gameSubtitle,
                        onValueChange = { gameSubtitle = it },
                        label = { Text("সংক্ষিপ্ত বিবরণ / বর্ণনা (Subtitle)") },
                        placeholder = { Text("১০,০০০ গুণ পর্যন্ত মাল্টিপ্লায়ার রকেট ক্যাশ আউট ক্র্যাশ গেম") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    // Badges Selector Row
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "ব্যাজ নির্বাচন করুন (Badge):", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            badges.forEach { b ->
                                val isSel = badgeText == b
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSel) Color(0xFFF59E0B) else Color(0xFF1E1038))
                                        .border(1.dp, if (isSel) Color(0xFFFDE047) else Color(0xFF475569), RoundedCornerShape(6.dp))
                                        .clickable { badgeText = b }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = b,
                                        color = if (isSel) Color.Black else Color(0xFFCBD5E1),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    // Max Win & RTP Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = maxWinText,
                            onValueChange = { maxWinText = it },
                            label = { Text("Max Win") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        OutlinedTextField(
                            value = rtpText,
                            onValueChange = { rtpText = it },
                            label = { Text("RTP") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }

                    // Submit Button
                    Button(
                        onClick = {
                            if (gameTitle.isBlank() || gameUrlOrApi.isBlank()) {
                                Toast.makeText(context, "দয়া করে গেমের নাম এবং ওয়েব লিংক দিন", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            var formattedUrl = gameUrlOrApi.trim()
                            if (!formattedUrl.startsWith("http://") && !formattedUrl.startsWith("https://")) {
                                formattedUrl = "https://$formattedUrl"
                            }
                            viewModel.addCustomGame(
                                title = gameTitle.trim(),
                                provider = if (providerName.isNotBlank()) providerName.trim() else "Custom",
                                subtitle = if (gameSubtitle.isNotBlank()) gameSubtitle.trim() else "${gameTitle.trim()} লাইভ গেম",
                                gameUrlOrApi = formattedUrl,
                                badge = badgeText,
                                maxWin = maxWinText.trim(),
                                rtp = rtpText.trim()
                            )
                            Toast.makeText(context, "'${gameTitle.trim()}' গেমটি সফলভাবে যুক্ত হয়েছে এবং লাইভ করা হয়েছে!", Toast.LENGTH_LONG).show()
                            // Clear inputs
                            gameTitle = ""
                            providerName = ""
                            gameSubtitle = ""
                            gameUrlOrApi = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("admin_save_game_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "🚀 সংরক্ষণ করুন ও প্লেয়ার অ্যাপে লাইভ করুন",
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Section: Existing Custom Games List
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = "📋 বর্তমান লাইভ গেম তালিকা",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (allGames.isNotEmpty()) Color(0xFF10B981) else Color(0xFF334155))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${allGames.size} টি",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (allGames.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF140A26)),
                    border = BorderStroke(1.dp, Color(0xFF2E1065)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🎮", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "কোনো কাস্টম গেম বা API যুক্ত নেই",
                                color = Color(0xFFA78BFA),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "উপরের ফরম থেকে নতুন গেম যুক্ত করুন। যুক্ত করার সাথে সাথে প্লেয়ারদের হোমস্ক্রিনে চলে যাবে।",
                                color = Color(0xFF6B7280),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        } else {
            items(allGames, key = { it.id }) { game ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF190C36)),
                    border = BorderStroke(1.dp, if (game.isActive) Color(0xFF8B5CF6) else Color(0xFF475569)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_custom_game_card_${game.id}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFEAB308))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = game.badge,
                                        color = Color.Black,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                                Text(
                                    text = game.title,
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (game.isActive) "সক্রিয়" else "বন্ধ",
                                    color = if (game.isActive) Color(0xFF34D399) else Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Switch(
                                    checked = game.isActive,
                                    onCheckedChange = { isChecked ->
                                        viewModel.toggleCustomGame(game.id, isChecked)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color(0xFF10B981),
                                        checkedTrackColor = Color(0xFF065F46)
                                    )
                                )
                            }
                        }

                        Text(
                            text = "${game.provider} • ${game.subtitle}",
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "🏆 ${game.maxWin}",
                                color = Color(0xFFFBBF24),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "📊 RTP: ${game.rtp}",
                                color = Color(0xFF34D399),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // URL preview box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF0F0620))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "🔗 ${game.gameUrlOrApi}",
                                color = Color(0xFF38BDF8),
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Action Buttons: Test Play & Delete
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onTestGame(game) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D28D9)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "🎮 টেস্ট প্লে (Test Play)",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = {
                                    viewModel.deleteCustomGame(game.id)
                                    Toast.makeText(context, "'${game.title}' মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444).copy(alpha = 0.2f)),
                                border = BorderStroke(1.dp, Color(0xFFEF4444)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = Color(0xFFFCA5A5),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "মুছুন",
                                    color = Color(0xFFFCA5A5),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
