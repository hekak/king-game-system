package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
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
import com.example.data.model.CasinoGame

@Composable
fun GameTopBar(
    currentGame: CasinoGame = CasinoGame.SUPER_ACE,
    claimableMissionsCount: Int,
    soundEnabled: Boolean,
    onBackToHome: () -> Unit = {},
    onOpenLobby: () -> Unit,
    onOpenLeaderboard: () -> Unit,
    onOpenDailyProgress: () -> Unit,
    onOpenPaytable: () -> Unit,
    onOpenBuyBonus: () -> Unit,
    onToggleSound: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0D051C))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Home & Casino Lobby Buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            // Home Screen Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x33FFFFFF))
                    .border(1.dp, Color(0x66F59E0B), RoundedCornerShape(8.dp))
                    .clickable { onBackToHome() }
                    .padding(horizontal = 7.dp, vertical = 5.dp)
                    .testTag("home_return_top_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Return to Home",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "HOME",
                        color = Color(0xFFFFD700),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Casino Lobby Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFD97706), Color(0xFFF59E0B))
                        )
                    )
                    .border(1.dp, Color(0xFFFDE047), RoundedCornerShape(8.dp))
                    .clickable { onOpenLobby() }
                    .padding(horizontal = 7.dp, vertical = 5.dp)
                    .testTag("lobby_open_top_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Casino,
                        contentDescription = "Casino Lobby",
                        tint = Color.Black,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "LOBBY",
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Daily Missions Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x33FFFFFF))
                    .border(1.dp, Color(0x44F59E0B), RoundedCornerShape(8.dp))
                    .clickable { onOpenDailyProgress() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("daily_missions_top_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ListAlt,
                        contentDescription = "Daily Missions",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Daily",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (claimableMissionsCount > 0) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDC2626)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$claimableMissionsCount",
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Tournament Leaderboard Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFB45309), Color(0xFFD97706))
                        )
                    )
                    .clickable { onOpenLeaderboard() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("tournament_leaderboard_top_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Tournament",
                        tint = Color(0xFFFFFBEB),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Leaderboard",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Right Actions: Buy Bonus Ruby, Paytable Info, Sound
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Buy Bonus Ruby Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF991B1B), Color(0xFFDC2626))
                        )
                    )
                    .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(8.dp))
                    .clickable { onOpenBuyBonus() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("buy_bonus_top_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💎", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "BONUS",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Paytable / Rules Info Button
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0x33FFFFFF))
                    .border(1.dp, Color(0x33F59E0B), CircleShape)
                    .clickable { onOpenPaytable() }
                    .testTag("rules_info_top_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Paytable Rules",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(16.dp)
                )
            }

            // Sound Toggle
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0x33FFFFFF))
                    .border(1.dp, Color(0x33F59E0B), CircleShape)
                    .clickable { onToggleSound() }
                    .testTag("sound_toggle_top_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                    contentDescription = "Toggle Sound",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
