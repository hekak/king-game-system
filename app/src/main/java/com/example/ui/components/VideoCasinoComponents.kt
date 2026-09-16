package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.VideoGameCategory
import com.example.data.model.VideoGameModel
import kotlinx.coroutines.delay

// ----------------------------------------------------
// 1. Top Header Bar (UUOK / Super Ace Style)
// ----------------------------------------------------
@Composable
fun VideoCasinoTopBar(
    balance: Double,
    onOpenDeposit: () -> Unit,
    onRefreshBalance: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0D061E))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Brand Logo (King Game Royal 3D Crown & Ace)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.testTag("casino_top_logo")
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.5.dp, Brush.linearGradient(listOf(Color(0xFFFFDF00), Color(0xFFD97706))), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_king_game_logo),
                    contentDescription = "King Game Logo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "KING",
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "GAME",
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        letterSpacing = 0.5.sp
                    )
                }
                Text(
                    text = "ROYAL CASINO",
                    color = Color(0xFFFBBF24),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
            }
        }

        // Right side: Balance Pill & Golden Deposit Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Balance Pill with Refresh
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1E1038))
                    .border(1.dp, Color(0xFFEAB308).copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                    .clickable { onOpenDeposit() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("casino_balance_pill")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "৳",
                        color = Color(0xFF34D399),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                    val formatted = String.format(java.util.Locale.US, "%,.2f", balance)
                    Text(
                        text = formatted,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = Color(0xFFEAB308),
                        modifier = Modifier
                            .size(14.dp)
                            .clickable { onRefreshBalance() }
                    )
                }
            }

            // Golden Deposit Button
            Button(
                onClick = onOpenDeposit,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEAB308)
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                modifier = Modifier
                    .height(32.dp)
                    .testTag("casino_top_deposit_button")
            ) {
                Text(
                    text = "Deposit",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                )
            }
        }
    }
}

// ----------------------------------------------------
// 2. Promotional Banner Carousel
// ----------------------------------------------------
@Composable
fun VideoPromoBannerCarousel(
    onBannerClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val banners = listOf(
        Pair("ডিপোজিট ৫০% বোনাস পেতে পারেন", "প্রথম ডিপোজিটে পান বিশাল ক্যাশব্যাক বোনাস!"),
        Pair("iPhone 16 Pro Max জিততে দৈনিক ডিপোজিট ড্র", "আজই অংশ নিন এবং প্রতিদিন জিতে নিন মেগা প্রাইজ!"),
        Pair("দৈনিক প্রথম জমা বোনাস পান ৮৮৮৮ বোনাস", "প্রতিদিন নতুন রোমাঞ্চ ও আনলিমিটেড ফ্রি স্পিন!")
    )

    var currentBannerIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            currentBannerIndex = (currentBannerIndex + 1) % banners.size
        }
    }

    val banner = banners[currentBannerIndex]

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .clickable { onBannerClick(currentBannerIndex) }
            .testTag("casino_promo_banner"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF130926)),
        border = BorderStroke(1.dp, Color(0xFFD97706))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFF1E1038),
                            Color(0xFF4C1D95),
                            Color(0xFF1E0A3C)
                        )
                    )
                )
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFEF4444))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "OFFER",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = banner.first,
                            color = Color(0xFFFEF08A),
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = banner.second,
                        color = Color(0xFFCBD5E1),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "🎁", fontSize = 28.sp)
            }
        }
    }
}

// ----------------------------------------------------
// 3. Announcement Marquee Ticker
// ----------------------------------------------------
@Composable
fun VideoAnnouncementMarquee(
    notice: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0F0624))
            .padding(horizontal = 12.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(Color(0xFF10B981)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Campaign,
                contentDescription = "Announcement",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (notice.isNotBlank()) notice else "🎉 দৈনিক প্রথম জমা বোনাস পান ৮৮৮৮ বোনাস! 🚀 একাধিবার জমা করে আজই জিতুন বিশেষ ড্র!",
            color = Color(0xFFE2E8F0),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}

// ----------------------------------------------------
// 4. Category Navigation Bar (Hot, Slots, Fish, Live, Poker, Sports)
// ----------------------------------------------------
@Composable
fun VideoCategoryNavBar(
    selectedCategory: VideoGameCategory,
    onSelectCategory: (VideoGameCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = VideoGameCategory.values()

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0A0418))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items(categories) { cat ->
            val isSelected = cat == selectedCategory
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onSelectCategory(cat) }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("category_tab_${cat.name.lowercase()}")
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) {
                                Brush.linearGradient(
                                    listOf(Color(0xFFF59E0B), Color(0xFFD97706))
                                )
                            } else {
                                Brush.linearGradient(
                                    listOf(Color(0x22FFFFFF), Color(0x11FFFFFF))
                                )
                            }
                        )
                        .border(
                            1.dp,
                            if (isSelected) Color(0xFFFDE047) else Color(0x33FFFFFF),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = cat.icon, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = cat.displayName,
                    color = if (isSelected) Color(0xFFFBBF24) else Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal
                )
            }
        }
    }
}

// ----------------------------------------------------
// 5. Jackpot Winner Live Rolling Counter & Showcase
// ----------------------------------------------------
@Composable
fun VideoJackpotWinnerSection(
    onGameClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var jackpotAmount by remember { mutableLongStateOf(28073500156L) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1200)
            jackpotAmount += (17..89).random()
        }
    }

    val digitsStr = jackpotAmount.toString()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF13092B)),
        border = BorderStroke(1.2.dp, Color(0xFFEAB308))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF1F0D42), Color(0xFF0F0624))
                    )
                )
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with mascot emojis
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "🐰", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "JACKPOT WINNER",
                    color = Color(0xFFFFD700),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "🦁", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Glowing Digital Digits Counter
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF06020E))
                    .border(1.dp, Color(0xFF78350F), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                digitsStr.forEachIndexed { index, char ->
                    Box(
                        modifier = Modifier
                            .size(width = 16.dp, height = 24.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF1E1038)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char.toString(),
                            color = Color(0xFFFDE047),
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                    if ((digitsStr.length - 1 - index) % 3 == 0 && index != digitsStr.length - 1) {
                        Text(
                            text = ",",
                            color = Color(0xFFEAB308),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Mini trending games ticker below jackpot
            val miniGames = listOf(
                Pair("Super Ace", "👑"),
                Pair("Money Coming", "💵"),
                Pair("Mega Wheel", "🎡"),
                Pair("Fortune Tiger", "🐯"),
                Pair("Crazy Time", "🎪"),
                Pair("777 Coins", "🎰"),
                Pair("Lucky Neko", "🐱"),
                Pair("Baccarat", "🎴")
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(miniGames) { (name, icon) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x33FFFFFF))
                            .border(1.dp, Color(0x33F59E0B), RoundedCornerShape(8.dp))
                            .clickable { onGameClick(name) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = icon, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = name,
                                color = Color(0xFFE2E8F0),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 6. 3-Column Game Card (Exact match to video!)
// ----------------------------------------------------
@Composable
fun VideoGameCard3Col(
    game: VideoGameModel,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(4.dp)
            .clickable { onClick() }
            .testTag("game_card_${game.id}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Square Artwork Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.95f),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF180A30)),
            border = BorderStroke(1.2.dp, Color(game.accentColorHex).copy(alpha = 0.7f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(game.primaryColorHex),
                                Color(game.secondaryColorHex),
                                Color(0xFF0D051A)
                            )
                        )
                    )
            ) {
                // Top Badges Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Provider Badge (JL / PG / PP / SPRIBE / BNG)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when (game.providerShort) {
                                    "JL" -> Color(0xFFCA8A04)
                                    "PG" -> Color(0xFF0D9488)
                                    "PP" -> Color(0xFF7C3AED)
                                    "SPRIBE" -> Color(0xFFDC2626)
                                    else -> Color(0xFFD97706)
                                }
                            )
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = game.providerShort,
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // Multiplier or Star
                    if (game.badge.contains("X", ignoreCase = true)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0x99000000))
                                .padding(horizontal = 3.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = game.badge,
                                color = Color(0xFFFDE047),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color(0x66000000))
                                .clickable { onToggleFavorite() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) Color(0xFFFFD700) else Color(0xAAFFFFFF),
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }
                }

                // Center Artwork Emoji / Visual
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = game.iconEmoji,
                            fontSize = 36.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Game Name Label
        Text(
            text = game.title,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

// ----------------------------------------------------
// 7. Game Launch Modal / Bottom Sheet (00:50 - 00:52 in video)
// ----------------------------------------------------
@Composable
fun VideoGameLaunchDialog(
    game: VideoGameModel,
    onPlayReal: () -> Unit,
    onPlayDemo: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF16092E)),
            border = BorderStroke(1.5.dp, Color(game.accentColorHex))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(game.primaryColorHex))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = game.provider,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Big Art Box
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(game.primaryColorHex), Color(game.secondaryColorHex))
                            )
                        )
                        .border(1.dp, Color(game.accentColorHex), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = game.iconEmoji, fontSize = 44.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = game.title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )

                if (game.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = game.description,
                        color = Color(0xFFCBD5E1),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Play Button (Primary)
                Button(
                    onClick = {
                        onDismiss()
                        onPlayReal()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("launch_play_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Play",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Free Trial Button (Outline)
                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onPlayDemo()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .testTag("launch_trial_button"),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFF38BDF8))
                ) {
                    Text(
                        text = "Free Trial",
                        color = Color(0xFF38BDF8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// 8. Video Bottom Navigation Bar (5 tabs with Center Golden Button)
// ----------------------------------------------------
enum class VideoBottomNavTab {
    HOME,
    PROMOTION,
    INVITE,
    DEPOSIT,
    PROFILE
}

@Composable
fun VideoCasinoBottomNav(
    selectedTab: VideoBottomNavTab,
    onSelectTab: (VideoBottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spin_coin")
    val coinRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "coinAngle"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF090315))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 1: Home
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "Home",
                isSelected = selectedTab == VideoBottomNavTab.HOME,
                onClick = { onSelectTab(VideoBottomNavTab.HOME) }
            )

            // Tab 2: Promotion
            BottomNavItem(
                icon = Icons.Default.CardGiftcard,
                label = "Promotion",
                isSelected = selectedTab == VideoBottomNavTab.PROMOTION,
                onClick = { onSelectTab(VideoBottomNavTab.PROMOTION) }
            )

            // Tab 3: Center Invite Floating Golden Coin
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onSelectTab(VideoBottomNavTab.INVITE) }
                    .testTag("bottom_nav_invite_center")
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFFFDF00), Color(0xFFD97706), Color(0xFFB45309))
                            )
                        )
                        .border(1.5.dp, Color(0xFFFEF08A), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "S",
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.rotate(coinRotation * 0.1f)
                    )
                }
                Text(
                    text = "Invite",
                    color = if (selectedTab == VideoBottomNavTab.INVITE) Color(0xFFFBBF24) else Color(0xFF94A3B8),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Tab 4: Deposit
            BottomNavItem(
                icon = Icons.Default.Wallet,
                label = "Deposit",
                isSelected = selectedTab == VideoBottomNavTab.DEPOSIT,
                onClick = { onSelectTab(VideoBottomNavTab.DEPOSIT) }
            )

            // Tab 5: Profile
            BottomNavItem(
                icon = Icons.Default.Person,
                label = "Profile",
                isSelected = selectedTab == VideoBottomNavTab.PROFILE,
                onClick = { onSelectTab(VideoBottomNavTab.PROFILE) }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .testTag("bottom_nav_${label.lowercase()}")
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Color(0xFF22C55E) else Color(0xFF94A3B8),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isSelected) Color(0xFF22C55E) else Color(0xFF94A3B8),
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// ----------------------------------------------------
// 9. Floating Social Community Buttons
// ----------------------------------------------------
@Composable
fun VideoFloatingSocials(
    onWhatsAppClick: () -> Unit,
    onTelegramClick: () -> Unit,
    onFacebookClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(end = 10.dp, bottom = 65.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.End
    ) {
        // WhatsApp
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF25D366))
                .clickable { onWhatsAppClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "💬", fontSize = 18.sp)
        }

        // Telegram
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF0284C7))
                .clickable { onTelegramClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "✈️", fontSize = 16.sp)
        }

        // Facebook
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF1877F2))
                .clickable { onFacebookClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🌐", fontSize = 16.sp)
        }
    }
}
