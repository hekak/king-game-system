package com.example.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.db.DailyProgressEntity
import com.example.data.db.UserProfileEntity
import com.example.data.model.DailyMission
import java.util.Locale

@Composable
fun DailyProgressDialog(
    dailyProgress: DailyProgressEntity,
    userProfile: UserProfileEntity,
    missions: List<DailyMission>,
    onClaimMission: (DailyMission) -> Unit,
    onDismiss: () -> Unit
) {
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
                                listOf(Color(0xFF2E1065), Color(0xFF1E0A3A))
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Daily Progress",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Daily Progress & Missions",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(30.dp)
                            .testTag("close_daily_progress_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFFCBD5E1)
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Streak & Level Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF241442)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x44F59E0B))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = "Streak",
                                        tint = Color(0xFFF97316),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = "${userProfile.consecutiveDays} Day Streak!",
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "Keep spinning daily for score bonuses",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "LV.${userProfile.level}",
                                        color = Color(0xFF22C55E),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${userProfile.xp}/${userProfile.level * 1000} XP",
                                        color = Color(0xFFFDE68A),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }

                    // Daily Statistics Grid
                    item {
                        Text(
                            text = "Today's Statistics (${dailyProgress.date})",
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatCard(
                                title = "Spins Today",
                                value = "${dailyProgress.spinsCount}",
                                icon = "🎰",
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Points Won",
                                value = String.format(Locale.US, "%,.0f", dailyProgress.totalWon),
                                icon = "💰",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatCard(
                                title = "Highest Win",
                                value = String.format(Locale.US, "%.1f", dailyProgress.highestWin),
                                icon = "⭐",
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Max Combo",
                                value = "x${dailyProgress.maxCombo}",
                                icon = "⚡",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatCard(
                                title = "Free Games",
                                value = "${dailyProgress.freeGamesTriggered}",
                                icon = "🎉",
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Wilds Created",
                                value = "${dailyProgress.wildsCreated}",
                                icon = "🃏",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Daily Missions Section
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Daily Challenge Missions",
                                color = Color(0xFFFFD700),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            val completedCount = missions.count { it.isCompleted }
                            Text(
                                text = "$completedCount/${missions.size} Done",
                                color = Color(0xFFFDE68A),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    items(missions) { mission ->
                        MissionRow(mission = mission, onClaim = { onClaimMission(mission) })
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, icon: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x33FFFFFF)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x22F59E0B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = title, color = Color(0xFF94A3B8), fontSize = 10.sp)
                Text(text = icon, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun MissionRow(mission: DailyMission, onClaim: () -> Unit) {
    val progress = (mission.current.toFloat() / mission.target.toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x22FFFFFF)),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (mission.isCompleted && !mission.isClaimed) Color(0xFFFFD700) else Color(0x15FFFFFF)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mission.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = mission.description,
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = if (mission.isCompleted) Color(0xFF22C55E) else Color(0xFFF59E0B),
                    trackColor = Color(0x33FFFFFF)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${mission.current.coerceAtMost(mission.target)}/${mission.target} (+${mission.rewardPoints} pts, +${mission.rewardXp} XP)",
                    color = Color(0xFFFDE68A),
                    fontSize = 9.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            when {
                mission.isClaimed -> {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x3322C55E))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "CLAIMED",
                            color = Color(0xFF86EFAC),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                mission.isCompleted -> {
                    Button(
                        onClick = onClaim,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.testTag("claim_mission_${mission.id}")
                    ) {
                        Text(
                            text = "CLAIM",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x22FFFFFF))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "IN PROGRESS",
                            color = Color(0xFF94A3B8),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
