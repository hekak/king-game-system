package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.data.model.GameAdminConfig
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun AdminPanelDialog(
    config: GameAdminConfig,
    onSaveConfig: (GameAdminConfig) -> Unit,
    onShareGame: () -> Unit,
    onResetBalance: (Double) -> Unit,
    onOpenAdminApp: () -> Unit = {},
    onDismiss: () -> Unit
) {
    var winRatio by remember(config) { mutableIntStateOf(config.winRatioPercent) }
    var goldenWildChance by remember(config) { mutableFloatStateOf(config.goldenWildChance) }
    var bigJokerChance by remember(config) { mutableFloatStateOf(config.bigJokerChance) }
    var maxCascades by remember(config) { mutableIntStateOf(config.maxCascadeSteps) }
    var rtpMultiplier by remember(config) { mutableFloatStateOf(config.rtpMultiplierBonus) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .clip(RoundedCornerShape(20.dp))
                .testTag("admin_panel_dialog"),
            color = Color(0xFF0F0624),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                Brush.linearGradient(listOf(Color(0xFFEAB308), Color(0xFF9333EA), Color(0xFF3B82F6)))
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF9333EA)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Game Admin Control",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Live Engine Calibration & Share",
                                color = Color(0xFFA855F7),
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("admin_dialog_close")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Launch Dedicated Admin App Banner Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF7C3AED), Color(0xFFC026D3))
                            )
                        )
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "👑 Dedicated Admin App",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Full analytics, economy grant, audio studio & tournament controls in separate app screen",
                                color = Color(0xFFF3E8FF),
                                fontSize = 10.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onOpenAdminApp,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEAB308)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("open_admin_app_button")
                        ) {
                            Text(
                                text = "Open App",
                                color = Color.Black,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Share Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x333B82F6))
                        .border(1.dp, Color(0xFF3B82F6), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color(0xFF60A5FA),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Share Game / Web Link",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Share this game instantly with friends or players via web link.",
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onShareGame,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .testTag("admin_share_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Share Live Game Link",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 1. Win Ratio Control (30% requested)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x221E1B4B))
                        .border(1.dp, Color(0xFF4338CA), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Winning Hit Ratio",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFEAB308))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "$winRatio% WIN",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Text(
                            text = "Current rule: $winRatio% of spins win, ${100 - winRatio}% lose",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Slider(
                            value = winRatio.toFloat(),
                            onValueChange = { winRatio = it.roundToInt() },
                            valueRange = 5f..95f,
                            steps = 17,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFEAB308),
                                activeTrackColor = Color(0xFFEAB308),
                                inactiveTrackColor = Color(0xFF334155)
                            ),
                            modifier = Modifier.testTag("admin_win_ratio_slider")
                        )

                        // Direct quick selection presets
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(5, 15, 30, 50, 70, 85, 95).forEach { preset ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (winRatio == preset) Color(0xFFEAB308) else Color(0xFF1E1B4B))
                                        .border(1.dp, if (winRatio == preset) Color(0xFFFDE047) else Color(0xFF4338CA), RoundedCornerShape(6.dp))
                                        .clickable { winRatio = preset }
                                        .padding(vertical = 5.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$preset%",
                                        color = if (winRatio == preset) Color.Black else Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "জয়ের শতকরা হার নির্ধারণ করুন ($winRatio% স্পিনে খেলোয়াড় জিতবে)",
                            color = Color(0xFFFDE047),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 2. Golden Wild Transform Rate
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x221E1B4B))
                        .border(1.dp, Color(0xFF4338CA), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Golden Card Spawn Rate",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${(goldenWildChance * 100).roundToInt()}%",
                                color = Color(0xFFFBBF24),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        Slider(
                            value = goldenWildChance,
                            onValueChange = { goldenWildChance = it },
                            valueRange = 0.05f..0.60f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFFBBF24),
                                activeTrackColor = Color(0xFFFBBF24),
                                inactiveTrackColor = Color(0xFF334155)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 3. Big Joker Splash Probability
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x221E1B4B))
                        .border(1.dp, Color(0xFF4338CA), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Big Joker Splash Chance",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${(bigJokerChance * 100).roundToInt()}%",
                                color = Color(0xFFA855F7),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        Slider(
                            value = bigJokerChance,
                            onValueChange = { bigJokerChance = it },
                            valueRange = 0.10f..0.80f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFA855F7),
                                activeTrackColor = Color(0xFFA855F7),
                                inactiveTrackColor = Color(0xFF334155)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 4. Max Cascade Combo Steps
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x221E1B4B))
                        .border(1.dp, Color(0xFF4338CA), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Max Cascade Steps",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "$maxCascades Combos",
                                color = Color(0xFF38BDF8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        Slider(
                            value = maxCascades.toFloat(),
                            onValueChange = { maxCascades = it.roundToInt() },
                            valueRange = 3f..15f,
                            steps = 11,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF38BDF8),
                                activeTrackColor = Color(0xFF38BDF8),
                                inactiveTrackColor = Color(0xFF334155)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Balance Reload
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onResetBalance(5000.0) },
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "+5,000 Pts", fontSize = 11.sp, color = Color.White)
                    }

                    Button(
                        onClick = { onResetBalance(20000.0) },
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "+20,000 Pts", fontSize = 11.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons: Apply / Save
                Button(
                    onClick = {
                        val updated = config.copy(
                            winRatioPercent = winRatio,
                            goldenWildChance = goldenWildChance,
                            bigJokerChance = bigJokerChance,
                            maxCascadeSteps = maxCascades,
                            rtpMultiplierBonus = rtpMultiplier
                        )
                        onSaveConfig(updated)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("admin_save_apply_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Apply",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Apply Engine Settings",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
