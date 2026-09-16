package com.example.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.CustomGameEntity
import com.example.data.db.DeletedGameEntity
import com.example.data.model.VideoGameCatalog
import com.example.ui.viewmodel.AdminViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GamesManagementTabContent(
    allGames: List<CustomGameEntity>,
    deletedGames: List<DeletedGameEntity>,
    deletedGameIds: List<String>,
    onTestGame: (CustomGameEntity) -> Unit,
    viewModel: AdminViewModel
) {
    val context = LocalContext.current
    var selectedSubSection by remember { mutableIntStateOf(0) } // 0: Custom Games, 1: Catalog Games, 2: Deleted Archive
    var catalogSearchQuery by remember { mutableStateOf("") }

    var gameTitle by remember { mutableStateOf("") }
    var providerName by remember { mutableStateOf("") }
    var gameSubtitle by remember { mutableStateOf("") }
    var gameUrlOrApi by remember { mutableStateOf("") }
    var badgeText by remember { mutableStateOf("NEW") }
    var maxWinText by remember { mutableStateOf("10,000x MAX WIN") }
    var rtpText by remember { mutableStateOf("97.0%") }

    val badges = listOf("NEW", "HOT", "JACKPOT", "LIVE", "VIP", "EXCLUSIVE")

    val deletedSet = remember(deletedGameIds) { deletedGameIds.toSet() }

    val activeCatalogGames = remember(deletedSet, catalogSearchQuery) {
        VideoGameCatalog.allGames.filter { game ->
            val notDeleted = !deletedSet.contains(game.id) && !deletedSet.contains(game.title)
            val matchesSearch = catalogSearchQuery.isBlank() ||
                    game.title.contains(catalogSearchQuery, ignoreCase = true) ||
                    game.provider.contains(catalogSearchQuery, ignoreCase = true)
            notDeleted && matchesSearch
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_custom_games_tab"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Main Sub-Tab Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF160B30))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Sub-tab 0
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedSubSection == 0) Color(0xFF7C3AED) else Color.Transparent)
                        .clickable { selectedSubSection = 0 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🌐 কাস্টম ও API (${allGames.size})",
                        color = if (selectedSubSection == 0) Color.White else Color(0xFF94A3B8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }

                // Sub-tab 1
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedSubSection == 1) Color(0xFFDC2626) else Color.Transparent)
                        .clickable { selectedSubSection = 1 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎰 ক্যাটালগ ডিলিট (${activeCatalogGames.size})",
                        color = if (selectedSubSection == 1) Color.White else Color(0xFF94A3B8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }

                // Sub-tab 2
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedSubSection == 2) Color(0xFF059669) else Color.Transparent)
                        .clickable { selectedSubSection = 2 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🗑️ ডিলিটকৃত গেম (${deletedGames.size})",
                        color = if (selectedSubSection == 2) Color.White else Color(0xFF94A3B8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // ==========================================
        // SUB-SECTION 0: Custom Games Management
        // ==========================================
        if (selectedSubSection == 0) {
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
                                text = "💡 সুবিধা: এখানে যে কোনো গেম লিংক যুক্ত করলে তা সরাসরি প্লেয়ার অ্যাপের হোম স্ক্রিনে ভেসে উঠবে। আপনি যেকোনো সময় বন্ধ বা চিরতরে ডিলিট করতে পারবেন।",
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

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val presets = listOf(
                                Triple("Aviator Crash", "Spribe", "https://spribe.co/games/aviator"),
                                Triple("Crazy Time", "Evolution", "https://evolution.com/games/crazy-time"),
                                Triple("Mega Roulette", "Pragmatic Play", "https://pragmaticplay.com/roulette"),
                                Triple("Blackjack VIP", "Ezugi", "https://ezugi.com/blackjack")
                            )
                            presets.forEach { (pTitle, pProv, pUrl) ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF2E1065))
                                        .border(1.dp, Color(0xFF8B5CF6), RoundedCornerShape(8.dp))
                                        .clickable {
                                            gameTitle = pTitle
                                            providerName = pProv
                                            gameSubtitle = "$pProv এর বিশ্ববিখ্যাত $pTitle গেম"
                                            gameUrlOrApi = pUrl
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "+ $pTitle",
                                        color = Color(0xFFE9D5FF),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = gameTitle,
                            onValueChange = { gameTitle = it },
                            label = { Text("গেমের নাম (Game Title)") },
                            placeholder = { Text("যেমন: Aviator, Crazy Time, Roulette...") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        OutlinedTextField(
                            value = providerName,
                            onValueChange = { providerName = it },
                            label = { Text("প্রোভাইডার নাম (Game Provider)") },
                            placeholder = { Text("যেমন: JILI, PG Soft, Pragmatic Play, Spribe...") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        OutlinedTextField(
                            value = gameUrlOrApi,
                            onValueChange = { gameUrlOrApi = it },
                            label = { Text("গেম ওয়েব লিংক / API URL (Game URL)") },
                            placeholder = { Text("https://example.com/game/launch?token=...") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

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
                        text = "📋 বর্তমান লাইভ কাস্টম গেম তালিকা",
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
                                        text = "🎮 টেস্ট প্লে",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Button(
                                    onClick = {
                                        viewModel.deleteGame(game.id, game.title)
                                        viewModel.deleteCustomGame(game.id)
                                        Toast.makeText(context, "'${game.title}' গেমটি স্থায়ীভাবে মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show()
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
                                        text = "গেম মুছুন",
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

        // ==========================================
        // SUB-SECTION 1: Catalog & Native Games Delete
        // ==========================================
        else if (selectedSubSection == 1) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF260D17)),
                    border = BorderStroke(1.dp, Color(0xFFDC2626)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "⚠️", fontSize = 20.sp)
                            Column {
                                Text(
                                    text = "ক্যাসিনো ও স্লট গেম ডিলিট করার কন্ট্রোল",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "যেকোনো গেম ডিলিট করলে তা প্লেয়ার অ্যাপের হোম স্ক্রিন, ক্যাটাগরি ও সার্চ থেকে অবিলম্বে গায়েব হয়ে যাবে।",
                                    color = Color(0xFFFCA5A5),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        OutlinedTextField(
                            value = catalogSearchQuery,
                            onValueChange = { catalogSearchQuery = it },
                            placeholder = { Text("গেমের নাম বা প্রোভাইডার দিয়ে খুঁজুন (যেমন: Super Ace, Fortune...)") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFFFCA5A5)) },
                            trailingIcon = {
                                if (catalogSearchQuery.isNotEmpty()) {
                                    IconButton(onClick = { catalogSearchQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                                    }
                                }
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFEF4444),
                                unfocusedBorderColor = Color(0xFF7F1D1D)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "লাইভ ক্যাটালগ গেম (${activeCatalogGames.size} টি উপলব্ধ)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "ডিলিট করতে লাল বাটনে চাপুন",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                }
            }

            if (activeCatalogGames.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF160B24)),
                        border = BorderStroke(1.dp, Color(0xFF334155)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "কোনো গেম পাওয়া যায়নি অথবা সব গেম ইতোমধ্যে ডিলিট করা হয়েছে।",
                                color = Color(0xFF94A3B8),
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(activeCatalogGames, key = { it.id }) { game ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B0F2A)),
                        border = BorderStroke(1.dp, Color(0xFF4C1D95)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_catalog_game_${game.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = game.title,
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp
                                    )
                                    if (game.isHot) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFFEF4444))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text("HOT", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${game.provider} • Max Win: ${game.maxWin} • ${game.badge}",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "ID: ${game.id} | ক্যাটাগরি: ${game.category.displayName}",
                                    color = Color(0xFF8B5CF6),
                                    fontSize = 10.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    viewModel.deleteGame(game.id, game.title)
                                    Toast.makeText(context, "'${game.title}' গেমটি সফলভাবে ডিলিট করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ডিলিট করুন", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // SUB-SECTION 2: Deleted Games Archive & Restore
        // ==========================================
        else if (selectedSubSection == 2) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF092E20)),
                    border = BorderStroke(1.dp, Color(0xFF10B981)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "♻️", fontSize = 20.sp)
                            Column {
                                Text(
                                    text = "ডিলিটকৃত গেমের তালিকা ও পুনরুদ্ধার আর্কাইভ",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "যেসব গেম ডিলিট করা হয়েছে তা এখানে সংরক্ষিত থাকে। 'পুনরুদ্ধার' বাটনে চাপলে পুনরায় প্লেয়ারদের সামনে চলে আসবে।",
                                    color = Color(0xFFA7F3D0),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            if (deletedGames.isEmpty()) {
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
                                .padding(30.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🎉", fontSize = 32.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "কোনো গেম ডিলিট করা নেই!",
                                    color = Color(0xFF34D399),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "সব ক্যাসিনো ও কাস্টম গেম বর্তমানে প্লেয়ারদের জন্য সক্রিয় রয়েছে।",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            } else {
                items(deletedGames, key = { it.gameId }) { del ->
                    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                    val deletedTime = dateFormat.format(Date(del.deletedAt))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF240E1B)),
                        border = BorderStroke(1.dp, Color(0xFFDC2626)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_deleted_game_${del.gameId}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = del.gameTitle,
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "ID: ${del.gameId}",
                                    color = Color(0xFFFCA5A5),
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "ডিলিট করার সময়: $deletedTime",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    viewModel.restoreGame(del.gameId, del.gameTitle)
                                    Toast.makeText(context, "'${del.gameTitle}' গেমটি পুনরুদ্ধার করে প্লেয়ারদের জন্য পুনরায় চালু করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("পুনরুদ্ধার করুন", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
