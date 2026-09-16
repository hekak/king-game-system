package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.SymbolType
import java.util.Locale

@Composable
fun PaytableRulesDialog(
    currentBet: Double,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Paytable", "Wild & Golden", "Multipliers", "Rules")

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
                    Text(
                        text = "Super Ace - Game Rules & Paytable",
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(30.dp)
                            .testTag("close_paytable_dialog")
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
                                    fontSize = 11.5.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Black else FontWeight.Medium,
                                    color = if (selectedTab == index) Color(0xFFFFD700) else Color(0xFF94A3B8)
                                )
                            }
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    when (selectedTab) {
                        0 -> {
                            // Paytable Tab
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0x33F59E0B))
                                        .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "1. Payouts shown for current Bet = ${String.format(Locale.US, "%.1f", currentBet)}\n" +
                                                "2. WILD substitutes for all symbols except SCATTER.\n" +
                                                "3. 1024 ways pay left-to-right on adjacent reels.",
                                        color = Color.White,
                                        fontSize = 11.5.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            val payItems = listOf(
                                Triple(SymbolType.ACE, "♠ ACE", "👑 Highest Card"),
                                Triple(SymbolType.KING, "👑 KING", "Royal Court"),
                                Triple(SymbolType.QUEEN, "👸 QUEEN", "Royal Court"),
                                Triple(SymbolType.JACK, "💂 JACK", "Royal Court"),
                                Triple(SymbolType.SPADE, "♠ SPADE", "Suit"),
                                Triple(SymbolType.HEART, "♥ HEART", "Suit"),
                                Triple(SymbolType.DIAMOND, "♦ DIAMOND", "Suit"),
                                Triple(SymbolType.CLUB, "♣ CLUB", "Suit")
                            )

                            items(payItems) { (sym, name, subtitle) ->
                                val mult = currentBet / 2.0
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0x22FFFFFF)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x15FFFFFF))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = name,
                                                color = Color(0xFFFFD700),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = subtitle,
                                                color = Color(0xFF94A3B8),
                                                fontSize = 9.sp
                                            )
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                            PayRateItem(reels = 5, amount = sym.pay5 * mult)
                                            PayRateItem(reels = 4, amount = sym.pay4 * mult)
                                            PayRateItem(reels = 3, amount = sym.pay3 * mult)
                                        }
                                    }
                                }
                            }
                        }

                        1 -> {
                            // Wild & Golden Cards Tab
                            item {
                                Text(
                                    text = "Golden Card Feature",
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "• Golden Cards only appear on reels 2, 3, and 4.\n" +
                                            "• Golden Symbols substitute for normal symbols in pays.\n" +
                                            "• When a Golden Symbol is eliminated in a winning combination, it flips and transforms into a WILD Joker Symbol!",
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 11.5.sp,
                                    lineHeight = 17.sp
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Joker Card Modes",
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "• Little Joker: Transforms the eliminated golden card into a Wild Joker in place.\n" +
                                            "• Big Joker: Transforms into a Big Joker and randomly splashes 1 to 4 additional symbols on reels 2 to 5 into Big Joker Wilds!\n" +
                                            "• Wild Jokers substitute for all symbols except SCATTER.",
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 11.5.sp,
                                    lineHeight = 17.sp
                                )
                            }
                        }

                        2 -> {
                            // Combo Multipliers Tab
                            item {
                                Text(
                                    text = "Combo Multiplier Ladder",
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "• Normal Game Multipliers: x1 → x2 → x3 → x5\n" +
                                            "• Free Game Multipliers: x2 → x4 → x6 → x10\n" +
                                            "• The multiplier starts at the first level and increases after each successive elimination/cascade in the same spin!\n" +
                                            "• Resets back to base level when no more winning combinations occur.",
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 11.5.sp,
                                    lineHeight = 17.sp
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Free Game Bonus",
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "• Landing 3 or more SCATTER coins awards 10 Free Spins!\n" +
                                            "• In Free Game, Combo Multipliers are doubled: x2, x4, x6, x10!\n" +
                                            "• Landing 3 Scatters inside Free Game awards +5 extra stacked spins!",
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 11.5.sp,
                                    lineHeight = 17.sp
                                )
                            }
                        }

                        3 -> {
                            // Rules Tab
                            item {
                                Text(
                                    text = "Game Rules & Ways",
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "1. Super Ace is a video slot with 5 reels, 4 rows, and 1024 ways.\n" +
                                            "2. All wins pay left-to-right on adjacent reels starting from the leftmost reel.\n" +
                                            "3. Winning symbols disappear and new symbols cascade down to fill empty spaces.\n" +
                                            "4. Maximum payout ratio is 10,000x your bet.\n" +
                                            "5. Turbo and Super Turbo speed modes allow ultra-fast gameplay.",
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 11.5.sp,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PayRateItem(reels: Int, amount: Double) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "${reels}x",
            color = Color(0xFF94A3B8),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = String.format(Locale.US, "%.2f", amount),
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
