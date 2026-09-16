package com.example.ui.screens.admin

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.AdminWalletConfigEntity
import com.example.data.model.GameAdminConfig
import com.example.data.model.RemoteCustomGame
import com.example.data.model.RemoteGameConfig
import com.example.data.remote.GitHubCredentials
import com.example.data.remote.GitHubRemoteService
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.R
import com.example.data.db.DepositRequestEntity
import com.example.data.db.RegisteredAccountEntity
import com.example.data.db.WithdrawRequestEntity
import com.example.data.model.CloudDepositRecord
import com.example.data.model.CloudPlayerRecord
import com.example.data.model.CloudWithdrawRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GitHubControlTabContent(
    adminConfig: GameAdminConfig,
    walletConfig: AdminWalletConfigEntity?,
    allAccounts: List<RegisteredAccountEntity> = emptyList(),
    allDeposits: List<DepositRequestEntity> = emptyList(),
    allWithdraws: List<WithdrawRequestEntity> = emptyList(),
    onConfigUpdatedFromRemote: (RemoteGameConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var credentials by remember { mutableStateOf(GitHubRemoteService.loadCredentials(context)) }
    var ownerInput by remember { mutableStateOf(credentials.owner) }
    var repoInput by remember { mutableStateOf(credentials.repo) }
    var branchInput by remember { mutableStateOf(credentials.branch) }
    var filePathInput by remember { mutableStateOf(credentials.filePath) }
    var tokenInput by remember { mutableStateOf(credentials.token) }
    var customRawUrlInput by remember { mutableStateOf(credentials.customRawUrl) }

    // App Branding & Profile Controls
    var appTitleInput by remember { mutableStateOf("King Game") }
    var appSubtitleInput by remember { mutableStateOf("ROYAL CASINO") }
    var appLogoPreset by remember { mutableStateOf("KING_CROWN") }
    var appLogoUrlInput by remember { mutableStateOf("") }
    var profileBannerUrlInput by remember { mutableStateOf("") }
    var defaultAvatarInput by remember { mutableStateOf("👑") }
    var supportContactInput by remember { mutableStateOf("01303347372") }
    var welcomeBonusInput by remember { mutableStateOf("500") }

    // Emergency and Remote Controls
    var remoteMaintenanceMode by remember { mutableStateOf(false) }
    var maintenanceMsgInput by remember {
        mutableStateOf("সার্ভার রক্ষণাবেক্ষণ কাজের জন্য সাময়িক বন্ধ আছে। কিছুক্ষণ পর আবার চেষ্টা করুন।")
    }
    var remoteNoticeInput by remember {
        mutableStateOf("🎉 কিং গেমে স্বাগতম! প্রতিদিনের ডেইলি মিশন ও বোনাস উপভোগ করুন।")
    }

    var isPublishing by remember { mutableStateOf(false) }
    var isFetching by remember { mutableStateOf(false) }
    var isDiagnosing by remember { mutableStateOf(false) }
    var diagnosticResult by remember { mutableStateOf<GitHubRemoteService.GitHubDiagnosticResult?>(null) }
    var showDiagnosticModal by remember { mutableStateOf(false) }

    var statusFeedback by remember { mutableStateOf<String?>(null) }
    var isSuccessStatus by remember { mutableStateOf(true) }
    var lastSyncTimestamp by remember { mutableStateOf(GitHubRemoteService.getLastSyncTime(context)) }

    // Load initial cached values
    LaunchedEffect(Unit) {
        val cached = GitHubRemoteService.getCachedConfig(context)
        remoteMaintenanceMode = cached.maintenanceMode
        if (cached.maintenanceMessage.isNotBlank()) maintenanceMsgInput = cached.maintenanceMessage
        if (cached.announcementNotice.isNotBlank()) remoteNoticeInput = cached.announcementNotice
        if (cached.appTitle.isNotBlank()) appTitleInput = cached.appTitle
        if (cached.appSubtitle.isNotBlank()) appSubtitleInput = cached.appSubtitle
        if (cached.appLogoPreset.isNotBlank()) appLogoPreset = cached.appLogoPreset
        if (cached.appLogoUrl.isNotBlank()) appLogoUrlInput = cached.appLogoUrl
        if (cached.profileBannerUrl.isNotBlank()) profileBannerUrlInput = cached.profileBannerUrl
        if (cached.defaultPlayerAvatar.isNotBlank()) defaultAvatarInput = cached.defaultPlayerAvatar
        if (cached.supportContact.isNotBlank()) supportContactInput = cached.supportContact
        if (cached.welcomeBonusAmount > 0) welcomeBonusInput = cached.welcomeBonusAmount.toInt().toString()
    }

    val currentRawUrl = remember(ownerInput, repoInput, branchInput, filePathInput, customRawUrlInput) {
        if (customRawUrlInput.isNotBlank()) {
            customRawUrlInput.trim()
        } else {
            "https://raw.githubusercontent.com/${ownerInput.trim()}/${repoInput.trim()}/${branchInput.trim()}/${filePathInput.trim()}"
        }
    }

    val timeFormatter = remember { SimpleDateFormat("dd MMM yyyy, hh:mm:ss a", Locale.getDefault()) }

    fun copyToClipboard(text: String, label: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
    }

    fun buildCurrentPayload(): RemoteGameConfig {
        val currentWallet = walletConfig ?: AdminWalletConfigEntity()
        val currentCached = GitHubRemoteService.getCachedConfig(context)

        // Merge accounts
        val mergedPlayers = currentCached.cloudPlayers.toMutableList()
        allAccounts.forEach { acc ->
            val idx = mergedPlayers.indexOfFirst { it.phone.trim() == acc.userPhone.trim() }
            val rec = CloudPlayerRecord(
                phone = acc.userPhone.trim(),
                name = acc.playerName,
                balance = acc.balance,
                level = acc.level,
                isBlocked = acc.status == "BLOCKED",
                lastUpdated = System.currentTimeMillis()
            )
            if (idx >= 0) mergedPlayers[idx] = rec else mergedPlayers.add(rec)
        }

        // Merge deposits
        val mergedDeposits = currentCached.cloudDeposits.toMutableList()
        allDeposits.forEach { dep ->
            val idx = mergedDeposits.indexOfFirst { it.id == dep.id }
            val rec = CloudDepositRecord(
                id = dep.id,
                phone = dep.userPhone.trim(),
                amount = dep.amount,
                method = dep.method,
                trxId = dep.trxId,
                status = dep.status,
                submittedAt = dep.submittedAt
            )
            if (idx >= 0) mergedDeposits[idx] = rec else mergedDeposits.add(rec)
        }

        // Merge withdraws
        val mergedWithdraws = currentCached.cloudWithdraws.toMutableList()
        allWithdraws.forEach { wd ->
            val idx = mergedWithdraws.indexOfFirst { it.id == wd.id }
            val rec = CloudWithdrawRecord(
                id = wd.id,
                phone = wd.userPhone.trim(),
                amount = wd.amount,
                method = wd.method,
                status = wd.status,
                submittedAt = wd.submittedAt
            )
            if (idx >= 0) mergedWithdraws[idx] = rec else mergedWithdraws.add(rec)
        }

        return RemoteGameConfig(
            version = 1,
            lastUpdated = System.currentTimeMillis(),
            appTitle = appTitleInput.trim().ifBlank { "King Game" },
            appSubtitle = appSubtitleInput.trim().ifBlank { "ROYAL CASINO" },
            appLogoPreset = appLogoPreset.trim().ifBlank { "KING_CROWN" },
            appLogoUrl = appLogoUrlInput.trim(),
            profileBannerUrl = profileBannerUrlInput.trim(),
            defaultPlayerAvatar = defaultAvatarInput.trim().ifBlank { "👑" },
            supportContact = supportContactInput.trim().ifBlank { "01303347372" },
            welcomeBonusAmount = welcomeBonusInput.toDoubleOrNull() ?: 500.0,
            clientSyncToken = tokenInput.trim(),
            maintenanceMode = remoteMaintenanceMode,
            maintenanceMessage = maintenanceMsgInput.trim(),
            announcementNotice = remoteNoticeInput.trim(),

            // Math
            globalWinRatio = adminConfig.winRatioPercent,
            superAceWinRatio = adminConfig.superAceWinRatio,
            deadMansBulletWinRatio = adminConfig.deadMansBulletWinRatio,
            gatesOfOlympusWinRatio = adminConfig.gatesOfOlympusWinRatio,
            sweetBonanzaWinRatio = adminConfig.sweetBonanzaWinRatio,
            maxCascadeSteps = adminConfig.maxCascadeSteps,
            goldenWildChance = adminConfig.goldenWildChance,
            bigJokerChance = adminConfig.bigJokerChance,
            rtpMultiplierBonus = adminConfig.rtpMultiplierBonus,

            // Financial
            nagadWallet = currentWallet.nagadWallet,
            bkashWallet = currentWallet.bkashWallet,
            rocketWallet = currentWallet.rocketWallet,
            upayWallet = currentWallet.upayWallet,
            minDeposit = currentWallet.minDeposit,
            maxDeposit = currentWallet.maxDeposit,
            minWithdraw = 500.0,
            maxWithdraw = 25000.0,

            // Web
            websitePortalUrl = currentWallet.websitePortalUrl,
            isWebAppModeEnabled = currentWallet.isWebAppModeEnabled,

            // Cloud records
            cloudPlayers = mergedPlayers,
            cloudDeposits = mergedDeposits,
            cloudWithdraws = mergedWithdraws
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF140A28)),
                border = BorderStroke(
                    1.5.dp,
                    Brush.linearGradient(listOf(Color(0xFF38BDF8), Color(0xFF818CF8), Color(0xFFC084FC)))
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0284C7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Cloud,
                                    contentDescription = "GitHub Cloud",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "গিটহাব ক্লাউড রিমোট কন্ট্রোল",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "খেলোয়াড়দের সব অ্যাপ গিটহাব ডাটা দিয়ে পরিচালিত",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Last Sync status
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (lastSyncTimestamp > 0) Color(0xFF065F46) else Color(0xFF334155))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (lastSyncTimestamp > 0) "লাইভ ক্লাউড" else "সিঙ্ক নেই",
                                color = if (lastSyncTimestamp > 0) Color(0xFF34D399) else Color(0xFF94A3B8),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0x22FFFFFF))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "ব্যবহার বিধি: আপনি এডমিন অ্যাপ থেকে যা পরিবর্তন করবেন, তা এই গিটহাবে জমা হবে। খেলোয়াড়দের অ্যাপ ওপেন হলেই গিটহাব থেকে সরাসরি লাইভ উইন রেট, ডিপোজিট নাম্বার এবং সেটিংস গ্রহণ করবে। প্লেয়ারের অ্যাপে কোনো এডমিন প্যানেল থাকবে না।",
                        color = Color(0xFFE2E8F0),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    if (lastSyncTimestamp > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "সর্বশেষ সিঙ্ক: ${timeFormatter.format(Date(lastSyncTimestamp))}",
                            color = Color(0xFF38BDF8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

// GitHub Repository Credentials & Token Input (Clean, Unified Top Card)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_github_credentials_setup"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF160B30)),
                border = BorderStroke(1.5.dp, Color(0xFFF59E0B))
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
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD97706)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "🔑 গিটহাব টোকেন ও রিপোজিটরি সেটআপ",
                                    color = Color(0xFFFDE68A),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "PAT টোকেন ও রিপো দিয়ে অনলাইন কন্ট্রোল চালু করুন",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Personal Access Token (PAT) - Top Priority
                    OutlinedTextField(
                        value = tokenInput,
                        onValueChange = { tokenInput = it },
                        label = { Text("GitHub Personal Access Token (PAT) 🔑") },
                        placeholder = { Text("ghp_xxxxxxxxxxxxxxxxxxxx") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_github_pat_token"),
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = Color(0xFFEAB308))
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFF59E0B),
                            unfocusedBorderColor = Color(0xFF64748B)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = ownerInput,
                            onValueChange = { ownerInput = it },
                            label = { Text("GitHub Owner / ইউজারনেম") },
                            placeholder = { Text("যেমন: hekak") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_github_owner"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFEAB308),
                                unfocusedBorderColor = Color(0xFF475569)
                            )
                        )

                        OutlinedTextField(
                            value = repoInput,
                            onValueChange = { repoInput = it },
                            label = { Text("Repository নাম") },
                            placeholder = { Text("যেমন: king-game-system") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_github_repo"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFEAB308),
                                unfocusedBorderColor = Color(0xFF475569)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = branchInput,
                            onValueChange = { branchInput = it },
                            label = { Text("Branch (শাখা)") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFEAB308),
                                unfocusedBorderColor = Color(0xFF475569)
                            )
                        )

                        OutlinedTextField(
                            value = filePathInput,
                            onValueChange = { filePathInput = it },
                            label = { Text("File Path (ফাইলের নাম)") },
                            modifier = Modifier.weight(1.5f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFEAB308),
                                unfocusedBorderColor = Color(0xFF475569)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Custom RAW URL or Direct Link field (with Auto-Fill / Reset Button)
                    OutlinedTextField(
                        value = customRawUrlInput,
                        onValueChange = { customRawUrlInput = it },
                        label = { Text("কাস্টম RAW লিংক (ফাঁকা রাখলে স্বয়ংক্রিয় লিঙ্ক তৈরি হবে)") },
                        placeholder = { Text("স্বয়ংক্রিয় তৈরি লিঙ্ক ব্যবহার করতে ফাঁকা রাখুন") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            if (customRawUrlInput.isNotBlank()) {
                                IconButton(onClick = { customRawUrlInput = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear URL",
                                        tint = Color(0xFFF87171)
                                    )
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFEAB308),
                            unfocusedBorderColor = Color(0xFF475569)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "💡 গুরুত্বপূর্ণ: আপনার ইউজারনেম (${ownerInput.ifBlank { "hekak" }}) ও রিপো (${repoInput.ifBlank { "king-game-system" }}) অনুযায়ী সরাসরি লিঙ্ক স্বয়ংক্রিয়ভাবে তৈরি হয়। কাস্টম লিংকে ভুল লিঙ্ক থাকলে ৪MD বা ৪০৪ এরর হতে পারে। তাই কাস্টম ফিল্ডটি ফাঁকা রাখা সবচেয়ে নিরাপদ।",
                        color = Color(0xFFCBD5E1),
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Save settings button
                    Button(
                        onClick = {
                            val creds = GitHubCredentials(
                                owner = ownerInput.trim(),
                                repo = repoInput.trim(),
                                branch = branchInput.trim(),
                                filePath = filePathInput.trim(),
                                token = tokenInput.trim(),
                                customRawUrl = customRawUrlInput.trim()
                            )
                            credentials = creds
                            GitHubRemoteService.saveCredentials(context, creds)
                            Toast.makeText(context, "✅ গিটহাব সেটিংস সেভ হয়েছে!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("btn_save_github_credentials")
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("টোকেন ও সেটিংস সেভ করুন", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }

        // =====================================================================
        // App Branding, Logo & Profile Control Card (Admin Remote Management)
        // =====================================================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_branding_logo_control"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF13092A)),
                border = BorderStroke(1.5.dp, Color(0xFFEAB308))
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
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEAB308).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👑", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "অ্যাপ লোগো ও ব্র্যান্ডিং কন্ট্রোল",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "ইউজার অ্যাপের লোগো, নাম ও প্রোফাইল ব্যানার নিয়ন্ত্রণ",
                                    color = Color(0xFFFDE68A),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFEAB308))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "LIVE REMOTE",
                                color = Color.Black,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "এডমিন এখানে যে লোগো, খেলার নাম, প্রোফাইল ব্যানার ও সাপোর্ট নম্বর সেট করবেন, তা সকল ইউজার ডিভাইসে তাৎক্ষণিক লাইভ হয়ে যাবে।",
                        color = Color(0xFFCBD5E1),
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // App Title & Subtitle Fields
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = appTitleInput,
                            onValueChange = { appTitleInput = it },
                            label = { Text("অ্যাপের নাম (Title)") },
                            placeholder = { Text("King Game") },
                            modifier = Modifier.weight(1.2f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFEAB308),
                                unfocusedBorderColor = Color(0xFF475569)
                            )
                        )

                        OutlinedTextField(
                            value = appSubtitleInput,
                            onValueChange = { appSubtitleInput = it },
                            label = { Text("সাবটাইটেল") },
                            placeholder = { Text("ROYAL CASINO") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFEAB308),
                                unfocusedBorderColor = Color(0xFF475569)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // App Logo Preset Selector
                    Text("লোগো প্রিসেট বেছে নিন:", color = Color(0xFFFDE68A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    val logoPresets = listOf(
                        "KING_CROWN" to "👑 ক্রাউন",
                        "GOLDEN_ACE" to "🃏 গোল্ডেন এস",
                        "DIAMOND_VIP" to "💎 ডায়মন্ড",
                        "LUCKY_777" to "🎰 লাকি ৭৭৭",
                        "FIRE_DRAGON" to "🔥 ড্রাগন"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        logoPresets.forEach { (presetKey, presetLabel) ->
                            val isSelected = appLogoPreset == presetKey
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0xFFEAB308) else Color(0xFF1E1438))
                                    .border(1.dp, if (isSelected) Color(0xFFF59E0B) else Color(0xFF475569), RoundedCornerShape(8.dp))
                                    .clickable { appLogoPreset = presetKey }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = presetLabel,
                                    color = if (isSelected) Color.Black else Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Custom Online App Logo URL Input
                    OutlinedTextField(
                        value = appLogoUrlInput,
                        onValueChange = { appLogoUrlInput = it },
                        label = { Text("কাস্টম অনলাইন লোগো URL (ঐচ্ছিক)") },
                        placeholder = { Text("https://example.com/logo.png") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            if (appLogoUrlInput.isNotBlank()) {
                                IconButton(onClick = { appLogoUrlInput = "" }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFFF87171))
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFEAB308),
                            unfocusedBorderColor = Color(0xFF475569)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Profile Banner Image URL Input
                    OutlinedTextField(
                        value = profileBannerUrlInput,
                        onValueChange = { profileBannerUrlInput = it },
                        label = { Text("ইউজার প্রোফাইল ব্যানার URL (ঐচ্ছিক)") },
                        placeholder = { Text("https://example.com/banner.jpg") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            if (profileBannerUrlInput.isNotBlank()) {
                                IconButton(onClick = { profileBannerUrlInput = "" }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFFF87171))
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFEAB308),
                            unfocusedBorderColor = Color(0xFF475569)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Default Avatar Emojis
                    Text("ডিফল্ট প্লেয়ার অবতার:", color = Color(0xFFFDE68A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    val avatars = listOf("👑", "🦁", "🐯", "🤠", "🥷", "💎", "🎲", "🏆")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        avatars.forEach { avatarEmoji ->
                            val isSelected = defaultAvatarInput == avatarEmoji
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color(0xFFEAB308) else Color(0xFF1E1438))
                                    .border(1.5.dp, if (isSelected) Color(0xFFF59E0B) else Color(0xFF475569), CircleShape)
                                    .clickable { defaultAvatarInput = avatarEmoji },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(avatarEmoji, fontSize = 16.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Support Helpline & Welcome Bonus
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = supportContactInput,
                            onValueChange = { supportContactInput = it },
                            label = { Text("সাপোর্ট হটলাইন / ফোন") },
                            modifier = Modifier.weight(1.2f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFEAB308),
                                unfocusedBorderColor = Color(0xFF475569)
                            )
                        )

                        OutlinedTextField(
                            value = welcomeBonusInput,
                            onValueChange = { welcomeBonusInput = it.filter { ch -> ch.isDigit() } },
                            label = { Text("ওয়েলকাম বোনাস ৳") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFEAB308),
                                unfocusedBorderColor = Color(0xFF475569)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Live Visual Preview Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0D061E))
                            .border(1.dp, Color(0xFFEAB308).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text("👁️ ইউজার অ্যাপ লাইভ প্রিভিউ:", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Header Preview
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF1E1038))
                                            .border(1.dp, Color(0xFFEAB308), RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (appLogoUrlInput.isNotBlank()) {
                                            AsyncImage(
                                                model = appLogoUrlInput,
                                                contentDescription = "Logo Preview",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            val emoji = when (appLogoPreset) {
                                                "GOLDEN_ACE" -> "🃏"
                                                "DIAMOND_VIP" -> "💎"
                                                "LUCKY_777" -> "🎰"
                                                "FIRE_DRAGON" -> "🔥"
                                                else -> "👑"
                                            }
                                            Text(emoji, fontSize = 20.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = appTitleInput.ifBlank { "King Game" },
                                            color = Color(0xFFFFD700),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = appSubtitleInput.ifBlank { "ROYAL CASINO" },
                                            color = Color(0xFFFBBF24),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF1E1038))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("৳ 2,500.00", color = Color(0xFF34D399), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }

                            if (profileBannerUrlInput.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                ) {
                                    AsyncImage(
                                        model = profileBannerUrlInput,
                                        contentDescription = "Banner Preview",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Publish Branding Button
                    Button(
                        onClick = {
                            if (tokenInput.isBlank()) {
                                statusFeedback = "ব্র্যান্ডিং অনলাইনে পাঠাতে অনুগ্রহ করে উপরে GitHub PAT দিন।"
                                isSuccessStatus = false
                                return@Button
                            }
                            coroutineScope.launch {
                                isPublishing = true
                                statusFeedback = null
                                val updatedCreds = GitHubCredentials(
                                    owner = ownerInput.trim(),
                                    repo = repoInput.trim(),
                                    branch = branchInput.trim(),
                                    filePath = filePathInput.trim(),
                                    token = tokenInput.trim(),
                                    customRawUrl = customRawUrlInput.trim()
                                )
                                credentials = updatedCreds
                                GitHubRemoteService.saveCredentials(context, updatedCreds)
                                val payload = buildCurrentPayload()
                                val result = GitHubRemoteService.publishConfigToGitHub(
                                    context = context,
                                    creds = updatedCreds,
                                    config = payload
                                )
                                isPublishing = false
                                if (result.isSuccess) {
                                    statusFeedback = "🎨 অ্যাপ ব্র্যান্ডিং ও লোগো সফলভাবে গিটহাবে পাবলিশ ও লাইভ করা হয়েছে!"
                                    isSuccessStatus = true
                                    lastSyncTimestamp = System.currentTimeMillis()
                                    onConfigUpdatedFromRemote(payload)
                                    Toast.makeText(context, "ব্র্যান্ডিং অনলাইনে লাইভ হয়েছে!", Toast.LENGTH_LONG).show()
                                } else {
                                    statusFeedback = result.exceptionOrNull()?.message ?: "পাবলিশ ব্যর্থ হয়েছে"
                                    isSuccessStatus = false
                                }
                            }
                        },
                        enabled = !isPublishing && !isFetching,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEAB308)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("btn_publish_branding")
                    ) {
                        Text("🎨 ব্র্যান্ডিং সংরক্ষণ ও গিটহাবে লাইভ করুন", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                }
            }
        }

        // Live Action Buttons Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF130D28)),
                border = BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "⚡ এক ক্লিকে লাইভ সিঙ্ক্রোনাইজেশন",
                        color = Color(0xFFFBBF24),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Push to GitHub Button
                        Button(
                            onClick = {
                                if (tokenInput.isBlank()) {
                                    statusFeedback = "অনুগ্রহ করে নিচে আপনার GitHub Personal Access Token (PAT) দিন।"
                                    isSuccessStatus = false
                                    return@Button
                                }
                                coroutineScope.launch {
                                    isPublishing = true
                                    statusFeedback = null
                                    val updatedCreds = GitHubCredentials(
                                        owner = ownerInput.trim(),
                                        repo = repoInput.trim(),
                                        branch = branchInput.trim(),
                                        filePath = filePathInput.trim(),
                                        token = tokenInput.trim(),
                                        customRawUrl = customRawUrlInput.trim()
                                    )
                                    credentials = updatedCreds
                                    val payload = buildCurrentPayload()
                                    val result = GitHubRemoteService.publishConfigToGitHub(
                                        context = context,
                                        creds = updatedCreds,
                                        config = payload
                                    )
                                    isPublishing = false
                                    if (result.isSuccess) {
                                        statusFeedback = result.getOrNull()
                                        isSuccessStatus = true
                                        lastSyncTimestamp = System.currentTimeMillis()
                                        Toast.makeText(context, "সফলভাবে গিটহাবে পাবলিশ হয়েছে!", Toast.LENGTH_LONG).show()
                                    } else {
                                        statusFeedback = result.exceptionOrNull()?.message ?: "পাবলিশ ব্যর্থ হয়েছে"
                                        isSuccessStatus = false
                                    }
                                }
                            },
                            enabled = !isPublishing && !isFetching,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_publish_github")
                        ) {
                            if (isPublishing) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("গিটহাবে পাঠান", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Pull from GitHub Button
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isFetching = true
                                    statusFeedback = null
                                    val updatedCreds = GitHubCredentials(
                                        owner = ownerInput.trim(),
                                        repo = repoInput.trim(),
                                        branch = branchInput.trim(),
                                        filePath = filePathInput.trim(),
                                        token = tokenInput.trim(),
                                        customRawUrl = customRawUrlInput.trim()
                                    )
                                    credentials = updatedCreds
                                    GitHubRemoteService.saveCredentials(context, updatedCreds)
                                    val result = GitHubRemoteService.fetchRemoteConfig(context)
                                    isFetching = false
                                    // Refresh local credentials state in case fallback updated customRawUrl
                                    val refreshedCreds = GitHubRemoteService.loadCredentials(context)
                                    credentials = refreshedCreds
                                    customRawUrlInput = refreshedCreds.customRawUrl

                                    if (result.isSuccess) {
                                        val remoteConfig = result.getOrThrow()
                                        remoteMaintenanceMode = remoteConfig.maintenanceMode
                                        maintenanceMsgInput = remoteConfig.maintenanceMessage
                                        remoteNoticeInput = remoteConfig.announcementNotice
                                        if (remoteConfig.appTitle.isNotBlank()) appTitleInput = remoteConfig.appTitle
                                        if (remoteConfig.appSubtitle.isNotBlank()) appSubtitleInput = remoteConfig.appSubtitle
                                        if (remoteConfig.appLogoPreset.isNotBlank()) appLogoPreset = remoteConfig.appLogoPreset
                                        if (remoteConfig.appLogoUrl.isNotBlank()) appLogoUrlInput = remoteConfig.appLogoUrl
                                        if (remoteConfig.profileBannerUrl.isNotBlank()) profileBannerUrlInput = remoteConfig.profileBannerUrl
                                        if (remoteConfig.defaultPlayerAvatar.isNotBlank()) defaultAvatarInput = remoteConfig.defaultPlayerAvatar
                                        if (remoteConfig.supportContact.isNotBlank()) supportContactInput = remoteConfig.supportContact
                                        if (remoteConfig.welcomeBonusAmount > 0) welcomeBonusInput = remoteConfig.welcomeBonusAmount.toInt().toString()
                                        lastSyncTimestamp = System.currentTimeMillis()
                                        statusFeedback = "✅ গিটহাব থেকে লাইভ কনফিগারেশন সফলভাবে গৃহীত হয়েছে!"
                                        isSuccessStatus = true
                                        onConfigUpdatedFromRemote(remoteConfig)
                                        Toast.makeText(context, "লাইভ ডাটা সিঙ্ক সম্পন্ন!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        val err = result.exceptionOrNull()?.message ?: "গিটহাব থেকে ডাটা পাওয়া যায়নি"
                                        statusFeedback = "❌ এরর: $err"
                                        isSuccessStatus = false
                                    }
                                }
                            },
                            enabled = !isPublishing && !isFetching,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_fetch_github")
                        ) {
                            if (isFetching) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("গিটহাব সিঙ্ক", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Diagnostic and Copy Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    isDiagnosing = true
                                    val updatedCreds = GitHubCredentials(
                                        owner = ownerInput.trim(),
                                        repo = repoInput.trim(),
                                        branch = branchInput.trim(),
                                        filePath = filePathInput.trim(),
                                        token = tokenInput.trim(),
                                        customRawUrl = customRawUrlInput.trim()
                                    )
                                    credentials = updatedCreds
                                    GitHubRemoteService.saveCredentials(context, updatedCreds)
                                    val diag = GitHubRemoteService.diagnoseGitHub(context, updatedCreds)
                                    diagnosticResult = diag
                                    isDiagnosing = false
                                    showDiagnosticModal = true
                                }
                            },
                            enabled = !isPublishing && !isFetching && !isDiagnosing,
                            border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFBBF24)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("btn_diagnose_github")
                        ) {
                            if (isDiagnosing) {
                                CircularProgressIndicator(color = Color(0xFFFBBF24), modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("সংযোগ টেস্ট", color = Color(0xFFFBBF24), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                val payload = buildCurrentPayload()
                                copyToClipboard(payload.toJsonString(indent = 2), "Config JSON")
                            },
                            border = BorderStroke(1.dp, Color(0xFF8B5CF6)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC4B5FD)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("btn_copy_full_json")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = Color(0xFFC4B5FD), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("JSON কপি করুন", color = Color(0xFFC4B5FD), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Status Message Display
                    if (statusFeedback != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSuccessStatus) Color(0xFF064E3B) else Color(0xFF7F1D1D))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = statusFeedback ?: "",
                                color = if (isSuccessStatus) Color(0xFFA7F3D0) else Color(0xFFFECACA),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Live Raw URL Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0E0720)),
                border = BorderStroke(1.dp, Color(0xFF475569))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "🔗 খেলোয়াড় অ্যাপের লাইভ গিটহাব Raw URL",
                            color = Color(0xFF38BDF8),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        IconButton(
                            onClick = { copyToClipboard(currentRawUrl, "GitHub Raw URL") },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy URL", tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF060310))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = currentRawUrl,
                            color = Color(0xFFFDE047),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "ইউজারদের অ্যাপ এই লিঙ্ক থেকেই স্বয়ংক্রিয়ভাবে ডাটা লোড করে।",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp
                    )

                    if (customRawUrlInput.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = {
                                    customRawUrlInput = ""
                                    val creds = credentials.copy(customRawUrl = "")
                                    credentials = creds
                                    GitHubRemoteService.saveCredentials(context, creds)
                                    Toast.makeText(context, "কাস্টম লিংক রিসেট করা হয়েছে! এখন অটো-লিংক চালু।", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFEAB308)),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color(0xFFFDE047), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("অটো জেনারেটেড লিংকে ফিরুন", color = Color(0xFFFDE047), fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Emergency Remote Controls (Maintenance & Announcements)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF180A2E)),
                border = BorderStroke(1.dp, Color(0xFFEC4899).copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🚨 রিমোট জরুরি কন্ট্রোল (সব প্লেয়ার অ্যাপে প্রযোজ্য)",
                        color = Color(0xFFF472B6),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Remote Maintenance Mode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (remoteMaintenanceMode) Color(0x33EF4444) else Color(0x11FFFFFF))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "সার্ভার মেইনটেন্যান্স লক (Emergency Lock)",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (remoteMaintenanceMode) "চালু আছে: সব খেলোয়াড় অ্যাপ লক থাকবে" else "বন্ধ আছে: স্বাভাবিক খেলা চলবে",
                                color = if (remoteMaintenanceMode) Color(0xFFF87171) else Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = remoteMaintenanceMode,
                            onCheckedChange = { remoteMaintenanceMode = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFEF4444),
                                checkedTrackColor = Color(0xFF7F1D1D),
                                uncheckedThumbColor = Color(0xFF94A3B8),
                                uncheckedTrackColor = Color(0xFF334155)
                            )
                        )
                    }

                    if (remoteMaintenanceMode) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = maintenanceMsgInput,
                            onValueChange = { maintenanceMsgInput = it },
                            label = { Text("মেইনটেন্যান্স নোটিশ (প্লেয়ার যা দেখতে পাবে)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFEF4444),
                                unfocusedBorderColor = Color(0xFF7F1D1D)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Remote Announcement Notice
                    OutlinedTextField(
                        value = remoteNoticeInput,
                        onValueChange = { remoteNoticeInput = it },
                        label = { Text("রিমোট ব্যানার নোটিশ (প্লেয়ারদের হোম স্ক্রিনে দেখাবে)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0xFF475569)
                        )
                    )
                }
            }
        }

        // Live JSON Preview Card
        item {
            var showJsonPreview by remember { mutableStateOf(false) }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B0518)),
                border = BorderStroke(1.dp, Color(0x33FFFFFF))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📄 লাইভ JSON ডাটা পে-লোড প্রিভিউ",
                            color = Color(0xFFA5B4FC),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Button(
                            onClick = { showJsonPreview = !showJsonPreview },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFFFFF)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (showJsonPreview) "লুকান" else "দেখুন",
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        }
                    }

                    if (showJsonPreview) {
                        val currentPayload = buildCurrentPayload().toJsonString(indent = 2)
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF03010A))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = currentPayload,
                                color = Color(0xFF6EE7B7),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { copyToClipboard(currentPayload, "JSON Payload") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF374151)),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("JSON কপি করুন", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showDiagnosticModal && diagnosticResult != null) {
        val diag = diagnosticResult!!
        AlertDialog(
            onDismissRequest = { showDiagnosticModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (diag.isSuccess) Icons.Default.Check else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (diag.isSuccess) Color(0xFF10B981) else Color(0xFFF59E0B)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("গিটহাব সংযোগ ডায়াগনস্টিক", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        Text(
                            text = diag.summaryTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (diag.isSuccess) Color(0xFF34D399) else Color(0xFFFBBF24)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFF374151))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("যাচাইকরণ ফলাফল:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    items(diag.details.size) { idx ->
                        Text(
                            text = diag.details[idx],
                            fontSize = 12.sp,
                            color = Color(0xFFE2E8F0),
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }

                    if (diag.actionSteps.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFF374151))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("প্রয়োজনীয় করণীয় পদক্ষেপ:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF87171))
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                        items(diag.actionSteps.size) { idx ->
                            Text(
                                text = "👉 ${diag.actionSteps[idx]}",
                                fontSize = 12.sp,
                                color = Color(0xFFFCA5A5),
                                modifier = Modifier.padding(vertical = 3.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDiagnosticModal = false }) {
                    Text("ঠিক আছে", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xFF1E1B4B),
            textContentColor = Color(0xFFE2E8F0)
        )
    }
}
