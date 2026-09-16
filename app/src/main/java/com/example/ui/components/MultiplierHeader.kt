package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MultiplierHeader(
    currentMultiplier: Int,
    isFreeGame: Boolean,
    bannerText: String,
    modifier: Modifier = Modifier
) {
    val multipliers = if (isFreeGame) listOf(2, 4, 6, 10) else listOf(1, 2, 3, 5)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1E0E3E),
                        Color(0xFF110726),
                        Color(0xFF0A0318)
                    )
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(listOf(Color(0xFFB45309), Color(0xFF451A03))),
                RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Game Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "♠ ",
                color = Color(0xFFFFD700),
                fontSize = 14.sp
            )
            Text(
                text = "SuperAce",
                color = Color(0xFFFFE082),
                fontWeight = FontWeight.Black,
                fontSize = 17.sp,
                letterSpacing = 1.sp
            )
            Text(
                text = " ♠",
                color = Color(0xFFFFD700),
                fontSize = 14.sp
            )
            if (isFreeGame) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFDC2626))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "FREE GAME",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Multiplier Ladder Bar
        Row(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0x55000000))
                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                .padding(3.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            multipliers.forEach { multiplierValue ->
                val isActive = currentMultiplier == multiplierValue
                val isPastOrActive = currentMultiplier >= multiplierValue

                val targetScale = if (isActive) 1.12f else 1.0f
                val scale by animateFloatAsState(
                    targetValue = targetScale,
                    animationSpec = tween(200, easing = FastOutSlowInEasing),
                    label = "multiplier_scale"
                )

                val bgColor by animateColorAsState(
                    targetValue = when {
                        isActive && isFreeGame -> Color(0xFFDC2626)
                        isActive -> Color(0xFFF59E0B)
                        isPastOrActive -> Color(0x44F59E0B)
                        else -> Color.Transparent
                    },
                    animationSpec = tween(200),
                    label = "multiplier_bg"
                )

                val textColor by animateColorAsState(
                    targetValue = when {
                        isActive -> Color.White
                        isPastOrActive -> Color(0xFFFDE68A)
                        else -> Color(0xFF94A3B8)
                    },
                    animationSpec = tween(200),
                    label = "multiplier_text"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .scale(scale)
                        .padding(horizontal = 2.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(bgColor)
                        .border(
                            if (isActive) 1.5.dp else 0.dp,
                            if (isActive) Color(0xFFFFEDB3) else Color.Transparent,
                            RoundedCornerShape(6.dp)
                        )
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "x$multiplierValue",
                        color = textColor,
                        fontWeight = if (isActive) FontWeight.Black else FontWeight.Bold,
                        fontSize = if (isActive) 16.sp else 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("multiplier_x$multiplierValue")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Dynamic Announcement Ticker
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0x33000000))
                .padding(horizontal = 8.dp, vertical = 2.5.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = bannerText,
                label = "banner_text_transition"
            ) { targetText ->
                Text(
                    text = targetText,
                    color = if (isFreeGame) Color(0xFFFED7AA) else Color(0xFFE2E8F0),
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
