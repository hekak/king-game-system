package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SlotCard

@Composable
fun SlotReelsView(
    grid: List<List<SlotCard>>,
    isSpinning: Boolean,
    modifier: Modifier = Modifier
) {
    val frameShape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp)
            .shadow(12.dp, shape = frameShape, ambientColor = Color.Black, spotColor = Color.Black)
            .clip(frameShape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0C2422), // deep teal casino felt
                        Color(0xFF051715),
                        Color(0xFF03100E)
                    )
                )
            )
            .border(
                2.dp,
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFD97706),
                        Color(0xFFFDE68A),
                        Color(0xFFB45309),
                        Color(0xFF78350F)
                    )
                ),
                frameShape
            )
            .padding(4.dp)
            .testTag("slot_reels_grid")
    ) {
        // Felt watermark
        Box(
            modifier = Modifier.matchParentSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "♠ SuperAce ♠",
                    color = Color(0x12FFFFFF),
                    fontWeight = FontWeight.Black,
                    fontSize = 26.sp,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "1024 WAYS",
                    color = Color(0x0CFFFFFF),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        // 5 Columns
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (colIndex in grid.indices) {
                val columnCards = grid[colIndex]
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 1.dp),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    for (rowIndex in columnCards.indices) {
                        val card = columnCards[rowIndex]
                        CardTile(card = card)
                    }
                }
            }
        }
    }
}
