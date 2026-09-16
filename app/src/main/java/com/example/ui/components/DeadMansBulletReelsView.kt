package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import com.example.data.model.BulletCard
import com.example.data.model.BulletSymbol

@Composable
fun DeadMansBulletReelsView(
    grid: List<List<BulletCard>>,
    isSpinning: Boolean,
    winMultiplier: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2B1305), Color(0xFF190B03), Color(0xFF0F0602))
                )
            )
            .border(2.dp, Brush.horizontalGradient(listOf(Color(0xFFD97706), Color(0xFFF59E0B), Color(0xFFD97706))), RoundedCornerShape(18.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Western Bounty Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF3F1D0B))
                .border(1.dp, Color(0xFFD97706), RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🤠 DEAD MAN'S BULLET",
                    color = Color(0xFFFDE047),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFEA580C))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "SKYWIND 9,990x",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp
                    )
                }
            }

            Text(
                text = if (winMultiplier > 1) "BOUNTY: ${winMultiplier}x" else "MAX CAP: 999x",
                color = if (winMultiplier > 1) Color(0xFF4ADE80) else Color(0xFFFBBF24),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 3x3 Western Grid
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("dead_mans_bullet_grid"),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (colIdx in 0 until 3) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (rowIdx in 0 until 3) {
                        val card = grid.getOrNull(colIdx)?.getOrNull(rowIdx)
                            ?: BulletCard(symbol = BulletSymbol.HORSESHOE)
                        BulletTile(card = card, isSpinning = isSpinning)
                    }
                }
            }
        }
    }
}

@Composable
private fun BulletTile(
    card: BulletCard,
    isSpinning: Boolean,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (card.isWinning) 1.08f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "bullet_scale"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            card.isWinning -> Color(0xFFFDE047)
            card.symbol == BulletSymbol.GOLD_BULLET -> Color(0xFFF59E0B)
            card.symbol == BulletSymbol.SILVER_BULLET -> Color(0xFFCBD5E1)
            card.symbol == BulletSymbol.BOUNTY_COLLECTOR -> Color(0xFFEF4444)
            card.symbol == BulletSymbol.WILD_OUTLAW -> Color(0xFF10B981)
            else -> Color(0xFF5A2E14)
        },
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "bullet_border"
    )

    val cardBg = when (card.symbol) {
        BulletSymbol.GOLD_BULLET -> Brush.verticalGradient(listOf(Color(0xFF78350F), Color(0xFF451A03)))
        BulletSymbol.SILVER_BULLET -> Brush.verticalGradient(listOf(Color(0xFF334155), Color(0xFF1E293B)))
        BulletSymbol.BOUNTY_COLLECTOR -> Brush.verticalGradient(listOf(Color(0xFF7F1D1D), Color(0xFF450A0A)))
        BulletSymbol.WILD_OUTLAW -> Brush.verticalGradient(listOf(Color(0xFF064E3B), Color(0xFF022C22)))
        else -> Brush.verticalGradient(listOf(Color(0xFF231107), Color(0xFF160904)))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(84.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(cardBg)
            .border(if (card.isWinning) 2.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = card.symbol.iconEmoji,
                fontSize = 28.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = when {
                    card.prizeMultiplier > 0 -> "+${card.prizeMultiplier}x"
                    card.symbol == BulletSymbol.WILD_OUTLAW -> "WILD"
                    card.symbol == BulletSymbol.BOUNTY_COLLECTOR -> "COLLECT"
                    else -> card.symbol.displayName.take(8)
                },
                color = when {
                    card.prizeMultiplier > 0 -> Color(0xFFFDE047)
                    card.symbol == BulletSymbol.WILD_OUTLAW -> Color(0xFF34D399)
                    else -> Color(0xFFF5EBE1)
                },
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
            )
        }
    }
}
