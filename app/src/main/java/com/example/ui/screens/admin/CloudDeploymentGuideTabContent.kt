package com.example.ui.screens.admin

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

@Composable
fun CloudDeploymentGuideTabContent(
    onNavigateToGitHubControl: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedGuideSubTab by remember { mutableIntStateOf(0) }
    var copiedItem by remember { mutableStateOf<String?>(null) }

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        copiedItem = label
        Toast.makeText(context, "$label কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("cloud_deployment_guide_tab"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Header Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF13092B)),
                border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(Color(0xFF38BDF8), Color(0xFF818CF8), Color(0xFFEAB308))))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0284C7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "ক্লাউড পাবলিশ ও রিমোট কন্ট্রোল গাইড",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Firebase ও GitHub দিয়ে ফুল অটোমেশন ও সিকিউরিটি",
                                    color = Color(0xFF38BDF8),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Button(
                            onClick = onNavigateToGitHubControl,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("লাইভ কন্ট্রোল ⚡", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "এই গাইডের মাধ্যমে আপনি খুব সহজে প্লেয়ার অ্যাপ ও এডমিন অ্যাপ Firebase এবং GitHub-এ হোস্ট করে সরাসরি অনলাইন কন্ট্রোল চালু করতে পারবেন। এডমিন অ্যাপের একটি ক্লিকেই সকল প্লেয়ারের অ্যাপের গেম সেটিংস ও ব্যালেন্স সাথে সাথে বদলে যাবে!",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Sub Tabs for Guide
        item {
            TabRow(
                selectedTabIndex = selectedGuideSubTab,
                containerColor = Color(0xFF1E1038),
                contentColor = Color(0xFFFFD700),
                modifier = Modifier.clip(RoundedCornerShape(10.dp))
            ) {
                Tab(
                    selected = selectedGuideSubTab == 0,
                    onClick = { selectedGuideSubTab = 0 },
                    text = { Text("📡 ক্লাউড আর্কিটেকচার", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedGuideSubTab == 1,
                    onClick = { selectedGuideSubTab = 1 },
                    text = { Text("🚀 ধাপে ধাপে গাইড", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedGuideSubTab == 2,
                    onClick = { selectedGuideSubTab = 2 },
                    text = { Text("📋 কনফিগ ও কোড", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        if (selectedGuideSubTab == 0) {
            // Visual Diagram 1: Cloud Architecture Flow
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF180E2B)),
                    border = BorderStroke(1.dp, Color(0xFF38BDF8))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Sensors, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("১. ক্লাউড রিয়েল-টাইম ডাটা সিঙ্ক ডায়াগ্রাম", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        // High Quality Image 1
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0F061F))
                                .border(1.dp, Color(0xFF38BDF8), RoundedCornerShape(12.dp))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_firebase_cloud_sync_guide),
                                contentDescription = "Firebase Cloud Sync Guide Architecture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "উপরের ছবিতে দেখুন: এডমিন অ্যাপ সরাসরি Firebase Realtime Database ও GitHub API তে কানেক্ট করে। প্লেয়ার অ্যাপ চালু হওয়ার সাথে সাথে ক্লাউড থেকে লেটেস্ট উইন-রেট (৫০% এডমিন প্রফিট), লাইভ নোটিশ ও নতুন গেমের ডাটা এনক্রিপ্টেড চ্যানেলের মাধ্যমে ডাউনলোড করে নেয়।",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Visual Diagram 2: Remote Control Flow
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF180E2B)),
                    border = BorderStroke(1.dp, Color(0xFFEAB308))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = Color(0xFFEAB308), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("২. এডমিন লাইভ মোবাইল কন্ট্রোল ড্যাশবোর্ড", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        // High Quality Image 2
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0F061F))
                                .border(1.dp, Color(0xFFEAB308), RoundedCornerShape(12.dp))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_admin_control_flow),
                                contentDescription = "Admin Real-Time Remote Control Flow",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "এডমিন যেকোনো স্থান থেকে মোবাইল ফোনের এডমিন প্যানেল দিয়ে মুহূর্তেই: \n" +
                                    "• এডমিন লাভ মার্জিন ৫০% সেট করতে পারবেন।\n" +
                                    "• জরুরী রক্ষণাবেক্ষণ নোটিশ (Emergency Maintenance) পুশ করতে পারবেন।\n" +
                                    "• ফ্রড বা সন্দেহজনক প্লেয়ার আইডি রিমোটলি ব্লক বা ডিভাইস লক করতে পারবেন।",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        if (selectedGuideSubTab == 1) {
            // Step-by-Step Walkthrough
            item {
                StepCard(
                    stepNumber = "১",
                    title = "GitHub এ গেম কনফিগ ও রিলিজ রিপো তৈরি",
                    color = Color(0xFF38BDF8),
                    description = "১. github.com এ গিয়ে একটি নতুন রিপোজিটরি তৈরি করুন (নাম দিন: king-game-system)।\n" +
                            "২. Settings > Developer settings > Personal access tokens (Tokens classic) এ যান এবং 'repo' পারমিশন দিয়ে একটি টোকেন জেনারেট করুন।\n" +
                            "৩. এই এডমিন অ্যাপের 'গিটহাব ক্লাউড সিঙ্ক' ট্যাবে এসে Owner, Repo Name ও Token বসিয়ে 'সেটিংস সেভ করুন' চাপুন।\n" +
                            "৪. এরপর 'গিটহাবে রিমোট কনফিগ পুশ করুন' চাপলেই আপনার ফাইল অনলাইনে লাইভ হয়ে যাবে!"
                )
            }

            item {
                StepCard(
                    stepNumber = "২",
                    title = "Firebase Realtime Database কানেক্ট করা",
                    color = Color(0xFFF59E0B),
                    description = "১. console.firebase.google.com এ গিয়ে 'Add Project' চাপুন।\n" +
                            "২. Build > Realtime Database এ ক্লিক করে 'Create Database' সিলেক্ট করুন।\n" +
                            "৩. রুলস ট্যাবে গিয়ে Read: true এবং Write: auth != null বা সিক্রেট কি দিয়ে সেট করুন (নিচের ৩য় ট্যাবে রুলস দেওয়া আছে)।\n" +
                            "৪. ডাটাবেজ URL (যেমন: https://your-app.firebaseio.com) পেয়ে যাবেন। এই URL সরাসরি এডমিন ও প্লেয়ার অ্যাপ সিঙ্ক করে।"
                )
            }

            item {
                StepCard(
                    stepNumber = "৩",
                    title = "প্লেয়ার অ্যাপ APK অনলাইনে সহজে পাবলিশ করা",
                    color = Color(0xFF10B981),
                    description = "১. GitHub Repo-এর 'Releases' সেকশনে গিয়ে 'Draft a new release' দিন।\n" +
                            "২. প্লেয়ার অ্যাপের APK ফাইলটি সেখানে আপলোড করে রিলিজ দিন। প্লেয়াররা সেখান থেকে সরাসরি ডাউনলোড করতে পারবে।\n" +
                            "৩. অথবা Firebase App Distribution বা Firebase Hosting ব্যবহার করে ১ ক্লিকেই সুন্দর ডাউনলোড ল্যান্ডিং পেজ তৈরি করতে পারেন।"
                )
            }

            item {
                StepCard(
                    stepNumber = "৪",
                    title = "এডমিন ৫০% লাভ গ্যারান্টি ও নিরাপত্তা বজায় রাখা",
                    color = Color(0xFFA855F7),
                    description = "১. অ্যাপের সমস্ত ইঞ্জিন (Slot, Dead Man's Bullet, Gates of Olympus, Sweet Bonanza) স্বয়ংক্রিয়ভাবে মোট ডিপোজিটের ৫০% এডমিনের জন্য আলাদা সুরক্ষিত রাখে।\n" +
                            "২. কোনো ইউজার জ্যাকপট জিতলে তা অন্য পরাজিত প্লেয়ারদের লসের টাকা থেকে সমন্বয় হয়, এডমিনের ৫০% লাভ অক্ষুণ্ণ থাকে।\n" +
                            "৩. SecurityShield ও R8 Obfuscation এর কারণে APK থেকে কেউ আপনার GitHub Token, Firebase URL বা ভেতরের কোড ডিকম্পাইল করে দেখতে পারবে না।"
                )
            }
        }

        if (selectedGuideSubTab == 2) {
            // Ready-to-copy Templates
            item {
                CodeSnippetCard(
                    title = "১. remote_game_config.json (GitHub কনফিগ ফাইল)",
                    label = "GitHub JSON Template",
                    code = """{
  "version": 1,
  "appStatus": "ONLINE",
  "minAppVersion": 1,
  "houseProfitMarginPercent": 50.0,
  "adminGuaranteedProfitPercent": 50.0,
  "overallWinRatio": 0.40,
  "emergencyMaintenance": false,
  "maintenanceNotice": "নিয়মিত সার্ভার আপডেট চলছে। কিছুক্ষণ পর আবার চেষ্টা করুন।",
  "systemNotice": "কিং গেমিং ক্লাবে স্বাগতম! ১০০% সুরক্ষিত ও স্বচ্ছ গেমিং প্ল্যাটফর্ম।",
  "blockedUserPhones": [],
  "blockedDeviceIds": []
}""",
                    onCopy = { copyToClipboard("GitHub Config JSON", it) }
                )
            }

            item {
                CodeSnippetCard(
                    title = "২. Firebase Realtime Database Security Rules",
                    label = "Firebase Database Rules",
                    code = """{
  "rules": {
    "game_config": {
      ".read": true,
      ".write": "auth != null"
    },
    "deposit_requests": {
      ".read": true,
      ".write": true
    },
    "withdraw_requests": {
      ".read": true,
      ".write": true
    }
  }
}""",
                    onCopy = { copyToClipboard("Firebase Rules", it) }
                )
            }

            item {
                CodeSnippetCard(
                    title = "৩. GitHub Actions Auto-Build APK (.github/workflows/build.yml)",
                    label = "GitHub Action Workflow",
                    code = """name: Build & Release Game APKs

on:
  push:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v4
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
    - name: Build with Gradle
      run: gradle assembleRelease
    - name: Upload APKs
      uses: actions/upload-artifact@v4
      with:
        name: KingGame-Release-APKs
        path: app/build/outputs/apk/release/*.apk""",
                    onCopy = { copyToClipboard("GitHub Workflow", it) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun StepCard(
    stepNumber: String,
    title: String,
    color: Color,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF160B2E)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(color),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stepNumber, color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Black)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = description, color = Color(0xFFCBD5E1), fontSize = 12.sp, lineHeight = 18.sp)
        }
    }
}

@Composable
private fun CodeSnippetCard(
    title: String,
    label: String,
    code: String,
    onCopy: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, color = Color(0xFF38BDF8), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Button(
                    onClick = { onCopy(code) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("কপি 📋", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF020617))
                    .padding(10.dp)
            ) {
                Text(
                    text = code,
                    color = Color(0xFF4ADE80),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
