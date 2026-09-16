package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SlotCard
import com.example.data.model.SymbolType

@Composable
fun CardTile(
    card: SlotCard,
    modifier: Modifier = Modifier
) {
    // Luxurious, smooth and slower animations
    val pulseScale by animateFloatAsState(
        targetValue = if (card.isWinning) 1.10f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "pulse_scale"
    )

    val flipAngle by animateFloatAsState(
        targetValue = if (card.isWildTransformed) 360f else 0f,
        animationSpec = tween(
            durationMillis = 850,
            easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1.0f) // smooth cubic ease
        ),
        label = "flip_angle"
    )

    val winBorderColor by animateColorAsState(
        targetValue = when {
            card.isWinning -> Color(0xFFFFDF00)
            card.isWild -> Color(0xFFFFD700)
            card.isGolden -> Color(0xFFFFA000)
            card.isScatter -> Color(0xFFFFE082)
            else -> Color(0xFFCBD5E1)
        },
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "border_color"
    )

    val cardShape = RoundedCornerShape(6.dp)

    // Colors
    val cardBackground = when {
        card.isScatter -> Brush.radialGradient(
            colors = listOf(Color(0xFFFFEEA0), Color(0xFFE5A800), Color(0xFFB37B00))
        )
        card.isWild -> Brush.verticalGradient(
            colors = if (card.symbol == SymbolType.WILD_BIG) {
                listOf(Color(0xFFFFF0B3), Color(0xFFFFD54F), Color(0xFFFF8F00))
            } else {
                listOf(Color(0xFFE0F7FA), Color(0xFF80DEEA), Color(0xFF0097A7))
            }
        )
        card.isGolden -> Brush.verticalGradient(
            colors = listOf(Color(0xFFFFF9E6), Color(0xFFFFE082), Color(0xFFFFCA28))
        )
        else -> Brush.verticalGradient(
            colors = listOf(Color(0xFFFFFFFF), Color(0xFFF1F5F9), Color(0xFFE2E8F0))
        )
    }

    val borderColor = when {
        card.isWinning -> Color(0xFFFFDF00)
        card.isWild -> Color(0xFFFFD700)
        card.isGolden -> Color(0xFFFFA000)
        card.isScatter -> Color(0xFFFFE082)
        else -> Color(0xFFCBD5E1)
    }

    val borderWidth = if (card.isWinning || card.isGolden || card.isWild) 2.dp else 1.dp

    Box(
        modifier = modifier
            .padding(1.5.dp)
            .aspectRatio(0.82f)
            .scale(if (card.isWinning) pulseScale else 1f)
            .graphicsLayer {
                rotationY = flipAngle
            }
            .shadow(
                elevation = if (card.isWinning || card.isWild) 6.dp else 2.dp,
                shape = cardShape,
                ambientColor = if (card.isWinning) Color(0xFFFFD700) else Color.Black,
                spotColor = if (card.isWinning) Color(0xFFFFD700) else Color.Black
            )
            .clip(cardShape)
            .background(cardBackground)
            .border(borderWidth, winBorderColor, cardShape)
            .testTag("slot_card_${card.symbol.name}")
    ) {
        when {
            card.isScatter -> ScatterContent()
            card.isWild -> WildContent(isBig = card.symbol == SymbolType.WILD_BIG)
            else -> RegularCardContent(card = card)
        }

        // Golden Card Indicator Ribbon/Badge
        if (card.isGolden && !card.isWild && !card.isScatter) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFFFD700), Color(0xFFFFF9C4), Color(0xFFFFD700))
                        )
                    )
                    .align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun RegularCardContent(card: SlotCard) {
    val isRed = card.symbol == SymbolType.HEART || card.symbol == SymbolType.DIAMOND || card.symbol == SymbolType.QUEEN
    val suitColor = if (isRed) Color(0xFFDC2626) else Color(0xFF0F172A)
    val cornerSymbol = when (card.symbol) {
        SymbolType.ACE -> "A"
        SymbolType.KING -> "K"
        SymbolType.QUEEN -> "Q"
        SymbolType.JACK -> "J"
        SymbolType.SPADE -> "♠"
        SymbolType.HEART -> "♥"
        SymbolType.DIAMOND -> "♦"
        SymbolType.CLUB -> "♣"
        else -> ""
    }

    val suitIcon = when (card.symbol) {
        SymbolType.ACE -> "♠"
        SymbolType.KING -> "♠"
        SymbolType.QUEEN -> "♥"
        SymbolType.JACK -> "♠"
        SymbolType.SPADE -> "♠"
        SymbolType.HEART -> "♥"
        SymbolType.DIAMOND -> "♦"
        SymbolType.CLUB -> "♣"
        else -> ""
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Top Left corner index
        Column(
            modifier = Modifier
                .padding(start = 3.dp, top = 2.dp)
                .align(Alignment.TopStart),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = cornerSymbol,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = suitColor,
                lineHeight = 11.sp
            )
            if (card.symbol in listOf(SymbolType.ACE, SymbolType.KING, SymbolType.QUEEN, SymbolType.JACK)) {
                Text(
                    text = suitIcon,
                    fontSize = 8.sp,
                    color = suitColor,
                    lineHeight = 8.sp
                )
            }
        }

        // Center Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            when (card.symbol) {
                SymbolType.ACE -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "♠",
                            fontSize = 28.sp,
                            color = Color(0xFF0F172A),
                            lineHeight = 28.sp
                        )
                        Text(
                            text = "ACE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = if (card.isGolden) Color(0xFFB45309) else Color(0xFF0F172A),
                            letterSpacing = 1.sp
                        )
                    }
                }
                SymbolType.KING -> {
                    RoyalFaceBadge(name = "K", title = "KING", icon = "👑", color = Color(0xFF1E3A8A))
                }
                SymbolType.QUEEN -> {
                    RoyalFaceBadge(name = "Q", title = "QUEEN", icon = "👸", color = Color(0xFFB91C1C))
                }
                SymbolType.JACK -> {
                    RoyalFaceBadge(name = "J", title = "JACK", icon = "💂", color = Color(0xFF1D4ED8))
                }
                SymbolType.SPADE -> {
                    Text(text = "♠", fontSize = 28.sp, color = Color(0xFF0F172A))
                }
                SymbolType.HEART -> {
                    Text(text = "♥", fontSize = 28.sp, color = Color(0xFFDC2626))
                }
                SymbolType.DIAMOND -> {
                    Text(text = "♦", fontSize = 28.sp, color = Color(0xFFEA580C))
                }
                SymbolType.CLUB -> {
                    Text(text = "♣", fontSize = 28.sp, color = Color(0xFF1E3A8A))
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun RoyalFaceBadge(name: String, title: String, icon: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = icon, fontSize = 20.sp)
        Text(
            text = title,
            fontSize = 8.sp,
            fontWeight = FontWeight.Black,
            color = color,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun WildContent(isBig: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isBig) "👑" else "🃏",
                fontSize = 24.sp
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(
                            if (isBig) listOf(Color(0xFFD97706), Color(0xFFF59E0B), Color(0xFFD97706))
                            else listOf(Color(0xFF0284C7), Color(0xFF0EA5E9), Color(0xFF0284C7))
                        )
                    )
                    .padding(horizontal = 5.dp, vertical = 1.dp)
            ) {
                Text(
                    text = if (isBig) "BIG WILD" else "WILD",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun ScatterContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer Coin Bevel
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFFFFFBEB), Color(0xFFFDE68A), Color(0xFFD97706))
                    )
                )
                .border(2.dp, Color(0xFFB45309), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF78350F),
                    lineHeight = 15.sp
                )
                Text(
                    text = "SCATTER",
                    fontSize = 5.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF92400E),
                    letterSpacing = 0.2.sp
                )
            }
        }
    }
}
