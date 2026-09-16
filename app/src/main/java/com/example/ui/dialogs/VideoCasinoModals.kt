package com.example.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.db.UserProfileEntity

// ----------------------------------------------------
// 1. Promotion Dialog (প্রমোশন ও বোনাস অফার)
// ----------------------------------------------------
@Composable
fun VideoPromotionDialog(
    onClaimBonus: (String) -> Unit,
    onOpenDeposit: () -> Unit,
    onDismiss: () -> Unit
) {
    val promotions = listOf(
        PromoItem("ডিপোজিট ৫০% বোনাস", "প্রথমবার নগদ/বিকাশ জমার সাথে সাথে ৫০% অতিরিক্ত ক্যাশব্যাক পান।", "৫০%", Color(0xFFEF4444)),
        PromoItem("iPhone 16 Pro Max ড্র", "প্রতি ৫০০৳ জমার জন্য ১টি লাকি ড্র টিকিট। প্রতি সপ্তাহে মেগা ড্র!", "iPHONE", Color(0xFFD97706)),
        PromoItem("দৈনিক জমা বোনাস ৮৮৮৮৳", "প্রতিদিনের প্রথম ডিপোজিটে বিশেষ স্পিন ও নগদ ড্রপ বোনাস।", "৮,৮৮৮৳", Color(0xFF10B981)),
        PromoItem("ভিআইপি লেভেল আপ ক্যাশব্যাক", "প্রতিটি গেমে বাজির উপর ভিত্তি করে আনলিমিটেড উইকলি রিবেট।", "১০%", Color(0xFF8B5CF6))
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF130926)),
            border = BorderStroke(1.5.dp, Color(0xFFEAB308))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎁", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "প্রমোশন ও অফার",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    items(promotions.size) { idx ->
                        val promo = promotions[idx]
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1F0F3D)),
                            border = BorderStroke(1.dp, promo.color.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = promo.title,
                                        color = Color(0xFFFEF08A),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = promo.desc,
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(promo.color)
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = promo.tag,
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        onDismiss()
                        onOpenDeposit()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEAB308)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "এখনই ডিপোজিট করুন ও বোনাস নিন",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

data class PromoItem(val title: String, val desc: String, val tag: String, val color: Color)

// ----------------------------------------------------
// 2. Invite & Earn Dialog (ইনভাইট ও রেফারেল কমিশন)
// ----------------------------------------------------
@Composable
fun VideoInviteEarnDialog(
    userProfile: UserProfileEntity,
    agentCommissionPercent: Double,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    val myReferralCode = userProfile.userPhone.takeLast(6).ifBlank { "SUPER88" }.uppercase()
    val inviteUrl = "https://superace.casino/ref/$myReferralCode"

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF130926)),
            border = BorderStroke(1.5.dp, Color(0xFFFFD700))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🪙", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ইনভাইট ও আনলিমিটেড আয়",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Commission Rate Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF78350F), Color(0xFFD97706), Color(0xFFB45309))
                            )
                        )
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "প্রতি বন্ধু আমন্ত্রণে পান",
                            color = Color(0xFFFEF08A),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${agentCommissionPercent.toInt()}% আজীবন রিবেট কমিশন!",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "বন্ধু যত খেলবে, আপনি তত কমিশন পাবেন",
                            color = Color(0xFFFEF9C3),
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Referral Code Box
                Text(
                    text = "আপনার রেফারেল কোড",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E1038))
                        .border(1.dp, Color(0xFF6B21A8), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = myReferralCode,
                        color = Color(0xFFFDE047),
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(inviteUrl))
                            copied = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (copied) "কপি হয়েছে!" else "কপি লিংক",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "০ জন", color = Color(0xFFFFD700), fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Text(text = "মোট আমন্ত্রিত", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "৳০.০০", color = Color(0xFF34D399), fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Text(text = "মোট কমিশন", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 3. Player Profile Dialog (প্রোফাইল, ভিআইপি ও সেটিংস)
// ----------------------------------------------------
@Composable
fun VideoPlayerProfileDialog(
    userProfile: UserProfileEntity,
    soundEnabled: Boolean,
    onToggleSound: () -> Unit,
    onOpenDeposit: () -> Unit,
    onOpenWithdraw: () -> Unit,
    onOpenMissions: () -> Unit,
    onOpenLeaderboard: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF130926)),
            border = BorderStroke(1.5.dp, Color(0xFF8B5CF6))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
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
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF7C3AED)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (userProfile.playerName.isNotBlank()) userProfile.playerName else "প্লেয়ার একাউন্ট",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp
                            )
                            Text(
                                text = if (userProfile.userPhone.isNotBlank()) userProfile.userPhone else "নম্বর সংযুক্ত নেই",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Balance & VIP Box
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1038)),
                    border = BorderStroke(1.dp, Color(0xFFEAB308))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "উপলব্ধ ব্যালেন্স", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            val formatted = String.format(java.util.Locale.US, "%,.2f", userProfile.balance)
                            Text(
                                text = "৳$formatted",
                                color = Color(0xFF34D399),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFEAB308))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "VIP ${userProfile.level}",
                                color = Color.Black,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Deposit and Withdraw Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            onDismiss()
                            onOpenDeposit()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "ডিপোজিট", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            onDismiss()
                            onOpenWithdraw()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "উইথড্র", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Menu Actions: Missions, Leaderboard, Sound
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ProfileOptionRow(
                        icon = Icons.Default.ListAlt,
                        label = "দৈনিক মিশন ও রিওয়ার্ড",
                        onClick = {
                            onDismiss()
                            onOpenMissions()
                        }
                    )
                    ProfileOptionRow(
                        icon = Icons.Default.EmojiEvents,
                        label = "লিডারবোর্ড ও চ্যাম্পিয়ন লিস্ট",
                        onClick = {
                            onDismiss()
                            onOpenLeaderboard()
                        }
                    )
                    ProfileOptionRow(
                        icon = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                        label = if (soundEnabled) "গেম সাউন্ড: অন" else "গেম সাউন্ড: অফ",
                        onClick = onToggleSound
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1B0E33))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFFBBF24),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
