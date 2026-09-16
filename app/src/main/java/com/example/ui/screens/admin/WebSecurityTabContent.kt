package com.example.ui.screens.admin

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.AdminWalletConfigEntity
import com.example.ui.viewmodel.AdminViewModel

@Composable
fun WebSecurityTabContent(
    walletConfig: AdminWalletConfigEntity?,
    viewModel: AdminViewModel,
    onPreviewWebPortal: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var websiteUrl by remember(walletConfig) {
        mutableStateOf(walletConfig?.websitePortalUrl?.ifBlank { "https://royalslots-bd.com" } ?: "https://royalslots-bd.com")
    }
    var isWebAppMode by remember(walletConfig) {
        mutableStateOf(walletConfig?.isWebAppModeEnabled ?: false)
    }
    var isCodeProtection by remember(walletConfig) {
        mutableStateOf(walletConfig?.codeProtectionActive ?: true)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Website Integration Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF190F33)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF38BDF8))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "ওয়েবসাইট পোর্টাল ইন্টিগ্রেশন (Web App Mode)",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "ওয়েবসাইটের মাধ্যমে অ্যাপটি সরাসরি চালনা করুন",
                                color = Color(0xFF93C5FD),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "আপনার ক্যাসিনো ওয়েবসাইটের লিঙ্ক দিন:",
                        color = Color(0xFFE2E8F0),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = websiteUrl,
                        onValueChange = { websiteUrl = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_website_url_input"),
                        leadingIcon = {
                            Icon(Icons.Default.Language, null, tint = Color(0xFF38BDF8))
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                cm.setPrimaryClip(ClipData.newPlainText("Website URL", websiteUrl))
                                Toast.makeText(context, "ওয়েবসাইট লিঙ্ক কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.ContentCopy, "Copy", tint = Color(0xFF94A3B8))
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0xFF475569),
                            focusedLabelColor = Color(0xFF38BDF8),
                            unfocusedLabelColor = Color(0xFF94A3B8)
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Web App Mode Switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F0721))
                            .border(1.dp, if (isWebAppMode) Color(0xFF10B981) else Color(0xFF334155), RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ওয়েব অ্যাপ মোড চালু (Run via Website)",
                                color = if (isWebAppMode) Color(0xFF4ADE80) else Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isWebAppMode)
                                    "সক্রিয়: খেলোয়াড়রা অ্যাপ ওপেন করলে সরাসরি ওয়েবসাইটে প্রবেশ করবে।"
                                else
                                    "নিষ্ক্রিয়: অ্যাপটি নেটিভ ক্যাসিনো গেম মোডে চলবে।",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }
                        Switch(
                            checked = isWebAppMode,
                            onCheckedChange = { isWebAppMode = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF10B981),
                                checkedTrackColor = Color(0xFF064E3B),
                                uncheckedThumbColor = Color(0xFF64748B),
                                uncheckedTrackColor = Color(0xFF1E293B)
                            ),
                            modifier = Modifier.testTag("admin_web_mode_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { onPreviewWebPortal(websiteUrl.trim()) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                    ) {
                        Icon(Icons.Default.OpenInBrowser, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ওয়েবসাইট পোর্টাল লাইভ টেস্ট করুন", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 2. Source Code Protection & Anti-Decompile Shield
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF160B29)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFEC4899))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFFF472B6),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "সোর্স কোড গোপনীয়তা ও রিভার্স-ইঞ্জিনিয়ারিং শিল্ড",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "R8 / ProGuard কোড অবফুসকেশন ও সিকিউরিটি গার্ড",
                                color = Color(0xFFF9A8D4),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Code Protection Switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F0721))
                            .border(1.dp, if (isCodeProtection) Color(0xFFEC4899) else Color(0xFF334155), RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "কোড হাইডিং ও সোর্স কোড সুরক্ষা (Active Protection)",
                                color = if (isCodeProtection) Color(0xFFF472B6) else Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isCodeProtection)
                                    "সক্রিয়: ক্লাসের নাম, ভ্যারিয়েবল ও লজিক সম্পূর্ণ লুকিয়ে রাখা হয়েছে যাতে কেউ দেখতে বা ডিকম্পাইল না করতে পারে।"
                                else
                                    "সতর্কতা: কোড হাইডিং বন্ধ করলে নিরাপত্তা বিঘ্নিত হতে পারে।",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }
                        Switch(
                            checked = isCodeProtection,
                            onCheckedChange = { isCodeProtection = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFEC4899),
                                checkedTrackColor = Color(0xFF831843),
                                uncheckedThumbColor = Color(0xFF64748B),
                                uncheckedTrackColor = Color(0xFF1E293B)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Security Badges
                    Text(
                        text = "সুরক্ষা প্রটোকল বিবরণ (Security Highlights):",
                        color = Color(0xFFFFD700),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    SecurityFeatureRow(
                        icon = Icons.Default.VisibilityOff,
                        title = "১. ক্লাস ও মেথড অবফুসকেশন (Class Renaming)",
                        desc = "অ্যাপের আসল লজিক a, b, c ইত্যাদি দুর্বোধ্য কোডে রূপান্তরিত করা যাতে কোনো ডি-কম্পাইলার পড়তে না পারে।"
                    )
                    SecurityFeatureRow(
                        icon = Icons.Default.Lock,
                        title = "২. লগ ও ডিবাগ তথ্য সম্পূর্ণ মুছে ফেলা (Log Stripping)",
                        desc = "Logcat বা কনসোলে কোনো অভ্যন্তরীণ তথ্য বা সিক্রেট টোকেন ফাঁস হয় না।"
                    )
                    SecurityFeatureRow(
                        icon = Icons.Default.VpnKey,
                        title = "৩. মেমোরি ও স্ট্রিং এনক্রিপশন (String Protection)",
                        desc = "অ্যাপের মেমোরি রিভার্স ইঞ্জিনিয়ারিং এবং অবৈধ মডিফিকেশন থেকে সুরক্ষিত।"
                    )
                }
            }
        }

        // 3. Save Configuration Button
        item {
            Button(
                onClick = {
                    viewModel.updateWebsiteAndSecurityConfig(
                        url = websiteUrl.trim(),
                        isWebMode = isWebAppMode,
                        codeProtect = isCodeProtection
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_web_security_button")
            ) {
                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "সেটিংস সংরক্ষণ করুন (Save Website & Security)",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun SecurityFeatureRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFF472B6),
            modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text = title,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = desc,
                color = Color(0xFF94A3B8),
                fontSize = 10.sp,
                lineHeight = 14.sp
            )
        }
    }
}
