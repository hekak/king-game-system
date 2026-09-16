package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.db.LeaderboardEntryEntity
import java.util.Locale

@Composable
fun LeaderboardDialog(
    leaderboardEntries: List<LeaderboardEntryEntity>,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(1) } // 0: Info, 1: Rank, 2: Reward, 3: Rules
    val tabs = listOf("Info", "Rank", "Reward", "Rules")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F0A1E))
                .border(
                    2.dp,
                    Brush.verticalGradient(
                        listOf(Color(0xFFF59E0B), Color(0xFF78350F), Color(0xFF1E0A3A))
                    ),
                    RoundedCornerShape(16.dp)
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF2E1065), Color(0xFF1E0A3A), Color(0xFF0F0728))
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "JILI",
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Tournament",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Tournament",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(30.dp)
                            .testTag("close_leaderboard_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFFCBD5E1)
                        )
                    }
                }

                // Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF1A103C),
                    contentColor = Color(0xFFFFD700),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color(0xFFFFD700)
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Black else FontWeight.Medium,
                                    color = if (selectedTab == index) Color(0xFFFFD700) else Color(0xFF94A3B8)
                                )
                            }
                        )
                    }
                }

                // Tournament Countdown Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF28104E))
                        .padding(vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "SLOT_Tournament ",
                            color = Color(0xFFFDE68A),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Ends In: 09h : 37m : 18s",
                            color = Color(0xFFF87171),
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }

                // Content Area
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                ) {
                    when (selectedTab) {
                        0 -> TournamentInfoTab()
                        1 -> TournamentRankTab(leaderboardEntries)
                        2 -> TournamentRewardTab()
                        3 -> TournamentRulesTab()
                    }
                }
            }
        }
    }
}

@Composable
private fun TournamentRankTab(entries: List<LeaderboardEntryEntity>) {
    val currentUserEntry = entries.find { it.isCurrentUser }

    Column(modifier = Modifier.fillMaxSize()) {
        // Current user quick rank bar
        if (currentUserEntry != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF3B1E6D)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFD700))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "#${currentUserEntry.rank}",
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = currentUserEntry.avatarEmoji, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "${currentUserEntry.playerName} (You)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Max Multiplier: ${String.format(Locale.US, "%.1fx", currentUserEntry.maxWinMultiplier)}",
                                color = Color(0xFFFDE68A),
                                fontSize = 10.sp
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = String.format(Locale.US, "%,d", currentUserEntry.points),
                            color = Color(0xFFFFE082),
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "PTS",
                            color = Color(0xFFF59E0B),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Leaderboard List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(entries) { entry ->
                LeaderboardRow(entry = entry)
            }
        }
    }
}

@Composable
private fun LeaderboardRow(entry: LeaderboardEntryEntity) {
    val rankBadge = when (entry.rank) {
        1 -> "🥇"
        2 -> "🥈"
        3 -> "🥉"
        else -> "#${entry.rank}"
    }

    val rankColor = when (entry.rank) {
        1 -> Color(0xFFFFD700)
        2 -> Color(0xFFE2E8F0)
        3 -> Color(0xFFCD7F32)
        else -> Color(0xFF94A3B8)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (entry.isCurrentUser) Color(0x33F59E0B) else Color(0x22FFFFFF))
            .border(
                1.dp,
                if (entry.isCurrentUser) Color(0xFFFFD700) else Color(0x11FFFFFF),
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier.size(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = rankBadge,
                    fontSize = if (entry.rank <= 3) 16.sp else 12.sp,
                    fontWeight = FontWeight.Black,
                    color = rankColor
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
            Text(text = entry.avatarEmoji, fontSize = 18.sp)
            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = entry.playerName,
                    color = if (entry.isCurrentUser) Color(0xFFFFD700) else Color.White,
                    fontWeight = if (entry.isCurrentUser) FontWeight.Black else FontWeight.SemiBold,
                    fontSize = 12.sp
                )
                Text(
                    text = "High: ${String.format(Locale.US, "%.1fx", entry.maxWinMultiplier)}",
                    color = Color(0xFF94A3B8),
                    fontSize = 9.sp
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = String.format(Locale.US, "%,d", entry.points),
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Text(
                text = "points",
                color = Color(0xFF94A3B8),
                fontSize = 8.sp
            )
        }
    }
}

@Composable
private fun TournamentRewardTab() {
    val rewards = listOf(
        Triple("🥇 Rank 1", "50,000 PTS + Crown Badge", Color(0xFFFFD700)),
        Triple("🥈 Rank 2", "30,000 PTS + Silver Badge", Color(0xFFCBD5E1)),
        Triple("🥉 Rank 3", "15,000 PTS + Bronze Badge", Color(0xFFCD7F32)),
        Triple("⭐ Rank 4 - 5", "8,000 PTS", Color(0xFFFDE68A)),
        Triple("✨ Rank 6 - 10", "4,000 PTS", Color(0xFFE2E8F0)),
        Triple("🎟️ All Participants", "500 Participation PTS", Color(0xFF94A3B8))
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(rewards) { (tier, prize, color) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x22FFFFFF))
                    .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tier,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = prize,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun TournamentRulesTab() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Calculation and Rules",
                color = Color(0xFFF59E0B),
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "• The leaderboard is updated every spin based on your cumulative points won.\n" +
                        "• If players have the same score, the ranking will be determined by the system-recorded timestamp indicating who reached the score earlier.\n" +
                        "• Point calculation is based on the win values and multipliers displayed in the actual game.\n" +
                        "• Event rewards will be automatically delivered to the player's balance upon tournament conclusion.",
                color = Color(0xFFE2E8F0),
                fontSize = 11.5.sp,
                lineHeight = 17.sp
            )
        }

        item {
            Text(
                text = "Scoring Mechanics",
                color = Color(0xFFF59E0B),
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "1. Every winning round adds 10x the win amount as tournament points.\n" +
                        "2. Triggering Combo Multipliers (x2, x3, x5, x10) multiplies your round score proportionally!\n" +
                        "3. Free Game spins contribute fully to your tournament rank with boosted multipliers!",
                color = Color(0xFFE2E8F0),
                fontSize = 11.5.sp,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun TournamentInfoTab() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Tournament Details",
            color = Color(0xFFFFD700),
            fontWeight = FontWeight.Black,
            fontSize = 14.sp
        )
        Text(
            text = "Welcome to the Daily Super Ace Championship! Compete against global players on the 1024-ways slot reels. Every cascade, combo, and free game increases your points.",
            color = Color(0xFFCBD5E1),
            fontSize = 11.5.sp,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0x33F59E0B))
                .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(8.dp))
                .padding(10.dp)
        ) {
            Column {
                Text(
                    text = "🏆 Tournament Status: ACTIVE",
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Reset Cycle: Daily 24 Hours",
                    color = Color.White,
                    fontSize = 11.sp
                )
                Text(
                    text = "Qualifying Bet: Any Bet Level (0.5+ pts)",
                    color = Color.White,
                    fontSize = 11.sp
                )
            }
        }
    }
}
