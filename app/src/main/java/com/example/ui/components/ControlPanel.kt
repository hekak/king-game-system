package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.TurboSpeed
import java.util.Locale

@Composable
fun ControlPanel(
    currentBet: Double,
    currentWin: Double,
    balance: Double,
    playerLevel: Int,
    isSpinning: Boolean,
    turboSpeed: TurboSpeed,
    isAutoSpinActive: Boolean,
    autoSpinRemaining: Int,
    isFreeGame: Boolean,
    freeSpinsRemaining: Int,
    onSpinClick: () -> Unit,
    onBetAdjust: (Boolean) -> Unit,
    onBetClick: () -> Unit = {},
    onTurboClick: () -> Unit,
    onAutoSpinToggle: () -> Unit,
    onReloadBalance: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spinTransition = rememberInfiniteTransition(label = "spin_rotation")
    val spinAngle by spinTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (turboSpeed == TurboSpeed.SUPER_TURBO) 300 else 600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_angle"
    )

    val glowPulse by spinTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF140828),
                        Color(0xFF0D051C),
                        Color(0xFF070210)
                    )
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(listOf(Color(0xFFD97706), Color(0xFF451A03))),
                RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        // Status Row: WIN & BALANCE
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // WIN status
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "WIN",
                    color = Color(0xFFFFD700),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = String.format(Locale.US, "%.2f", currentWin),
                    color = if (currentWin > 0) Color(0xFF4ADE80) else Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.testTag("win_display_text")
                )
            }

            // Free Spin Counter if active
            if (isFreeGame) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFB91C1C))
                        .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "FREE SPINS: $freeSpinsRemaining",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // BALANCE & LEVEL
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x33000000))
                    .clickable { onReloadBalance() }
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                // Level Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF16A34A))
                        .padding(horizontal = 4.dp, vertical = 1.5.dp)
                ) {
                    Text(
                        text = "LV.$playerLevel",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Balance",
                        color = Color(0xFF94A3B8),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Medium
                    )
                    val displayBalance = if (balance % 1.0 == 0.0) {
                        String.format(Locale.US, "%,d pts", balance.toLong())
                    } else {
                        String.format(Locale.US, "%,.2f pts", balance)
                    }
                    Text(
                        text = displayBalance,
                        color = Color(0xFFFFE082),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("balance_display_text")
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                // Plus button to open deposit
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Deposit Points",
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Main Controls Row: Bet Adjust | Spin Button | Auto & Turbo Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bet Selector Pill
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x44000000))
                    .border(1.dp, Color(0x33F59E0B), RoundedCornerShape(12.dp))
                    .clickable(enabled = !isSpinning) { onBetClick() }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "BET",
                        color = Color(0xFFFDE68A),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "ছক",
                        color = Color(0xFF34D399),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x44FFFFFF))
                            .clickable(enabled = !isSpinning) { onBetAdjust(false) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease Bet",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    val formattedBet = if (currentBet % 1.0 == 0.0) {
                        String.format(Locale.US, "%,d", currentBet.toLong())
                    } else {
                        String.format(Locale.US, "%.1f", currentBet)
                    }

                    Text(
                        text = formattedBet,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .clickable(enabled = !isSpinning) { onBetClick() }
                            .testTag("bet_amount_text")
                    )

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x44FFFFFF))
                            .clickable(enabled = !isSpinning) { onBetAdjust(true) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase Bet",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Big Golden Spin Button (Centered)
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .scale(if (isSpinning) 0.96f else 1.0f)
                    .shadow(12.dp, shape = CircleShape, ambientColor = Color(0xFFFFD700), spotColor = Color(0xFFFFD700))
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = if (isSpinning) {
                                listOf(Color(0xFFF59E0B), Color(0xFFD97706), Color(0xFF92400E))
                            } else {
                                listOf(Color(0xFFFFFBEB), Color(0xFFFBBF24), Color(0xFFD97706), Color(0xFF78350F))
                            }
                        )
                    )
                    .border(2.5.dp, Color(0xFFFFFBEB), CircleShape)
                    .clickable(enabled = !isSpinning || isAutoSpinActive) {
                        if (isAutoSpinActive) onAutoSpinToggle() else onSpinClick()
                    }
                    .testTag("main_spin_button"),
                contentAlignment = Alignment.Center
            ) {
                if (isSpinning) {
                    Icon(
                        imageVector = Icons.Default.Cached,
                        contentDescription = "Spinning",
                        tint = Color(0xFF78350F),
                        modifier = Modifier
                            .size(36.dp)
                            .rotate(spinAngle)
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Spin",
                            tint = Color(0xFF78350F),
                            modifier = Modifier.size(30.dp)
                        )
                        Text(
                            text = "SPIN",
                            color = Color(0xFF78350F),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            // Auto & Turbo Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Auto Spin Button (Unlimited until stopped or out of balance)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isAutoSpinActive) Color(0xFFDC2626) else Color(0x33FFFFFF)
                        )
                        .border(
                            1.dp,
                            if (isAutoSpinActive) Color(0xFFF87171) else Color(0x33F59E0B),
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onAutoSpinToggle() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("auto_spin_button")
                ) {
                    Icon(
                        imageVector = if (isAutoSpinActive) Icons.Default.Stop else Icons.Default.Cached,
                        contentDescription = "Auto Spin",
                        tint = if (isAutoSpinActive) Color.White else Color(0xFFFDE68A),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (isAutoSpinActive) "STOP ♾️" else "AUTO ♾️",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Turbo Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            when (turboSpeed) {
                                TurboSpeed.SUPER_TURBO -> Color(0xFFDC2626)
                                TurboSpeed.TURBO -> Color(0xFFD97706)
                                TurboSpeed.NORMAL -> Color(0x33FFFFFF)
                            }
                        )
                        .border(1.dp, Color(0x33F59E0B), RoundedCornerShape(10.dp))
                        .clickable { onTurboClick() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("turbo_spin_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Turbo",
                        tint = if (turboSpeed != TurboSpeed.NORMAL) Color.White else Color(0xFFFDE68A),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = turboSpeed.label,
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
