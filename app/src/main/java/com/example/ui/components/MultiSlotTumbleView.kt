package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OlympusCell
import com.example.data.model.SweetCell

@Composable
fun GatesOfOlympusReelsView(
    grid: List<List<OlympusCell>>,
    isSpinning: Boolean,
    zeusMultiplier: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2E1065), Color(0xFF1E0740), Color(0xFF0F0324))
                )
            )
            .border(2.dp, Color(0xFFEAB308), RoundedCornerShape(18.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Olympus Header with Zeus Multiplier
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF3B1378))
                .border(1.dp, Color(0xFFFDE047), RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "⚡ GATES OF OLYMPUS",
                    color = Color(0xFFFFDF00),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF7C3AED))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "PRAGMATIC 5,000x",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp
                    )
                }
            }

            Text(
                text = if (zeusMultiplier > 1) "⚡ MULTIPLIER: ${zeusMultiplier}x" else "ZEUS WRATH: ACTIVE",
                color = if (zeusMultiplier > 1) Color(0xFFFDE047) else Color(0xFFC084FC),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 6 cols x 5 rows grid
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("olympus_grid"),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (colIdx in 0 until 6) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (rowIdx in 0 until 5) {
                        val cell = grid.getOrNull(colIdx)?.getOrNull(rowIdx)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (cell?.isWinning == true) Color(0xFF581C87) else Color(0xFF1E0E38)
                                )
                                .border(
                                    width = if (cell?.isWinning == true) 1.5.dp else 0.5.dp,
                                    color = if (cell?.isWinning == true) Color(0xFFFDE047) else Color(0xFF4C1D95),
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cell?.symbol?.iconEmoji ?: "💎",
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SweetBonanzaReelsView(
    grid: List<List<SweetCell>>,
    isSpinning: Boolean,
    bombMultiplier: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF831843), Color(0xFF4C0519), Color(0xFF28020D))
                )
            )
            .border(2.dp, Color(0xFFF472B6), RoundedCornerShape(18.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Sweet Bonanza Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF9D174D))
                .border(1.dp, Color(0xFFF472B6), RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🍭 SWEET BONANZA",
                    color = Color(0xFFFFB6C1),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFDB2777))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "21,100x MAX",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp
                    )
                }
            }

            Text(
                text = if (bombMultiplier > 1) "💣 BOMB: ${bombMultiplier}x" else "SUGAR BOMB: READY",
                color = if (bombMultiplier > 1) Color(0xFFFDE047) else Color(0xFFF9A8D4),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 6 cols x 5 rows grid
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("sweet_bonanza_grid"),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (colIdx in 0 until 6) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (rowIdx in 0 until 5) {
                        val cell = grid.getOrNull(colIdx)?.getOrNull(rowIdx)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (cell?.isWinning == true) Color(0xFFBE185D) else Color(0xFF3B0718)
                                )
                                .border(
                                    width = if (cell?.isWinning == true) 1.5.dp else 0.5.dp,
                                    color = if (cell?.isWinning == true) Color(0xFFFDE047) else Color(0xFF831843),
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cell?.symbol?.iconEmoji ?: "🍬",
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
