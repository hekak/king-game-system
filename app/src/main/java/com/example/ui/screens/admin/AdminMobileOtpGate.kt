package com.example.ui.screens.admin

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AdminMobileOtpGate(
    adminPhone: String,
    onPhoneChange: (String) -> Unit,
    isOtpSent: Boolean,
    onSendOtp: () -> Unit,
    enteredOtp: String,
    onOtpChange: (String) -> Unit,
    generatedOtp: String,
    isTimerRunning: Boolean,
    timerSeconds: Int,
    errorMessage: String?,
    onResendOtp: () -> Unit,
    onChangeNumber: () -> Unit,
    onVerifyOtp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0F0521), Color(0xFF080214), Color(0xFF020008))
                )
            )
            .imePadding()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp)
                .verticalScroll(scrollState)
                .testTag("admin_otp_gate_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF140B29)),
            border = BorderStroke(
                1.5.dp,
                Brush.linearGradient(
                    listOf(Color(0xFFEAB308), Color(0xFF38BDF8), Color(0xFF6366F1))
                )
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Golden Security Badge
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFFFD700), Color(0xFFD97706), Color(0xFF78350F))
                            )
                        )
                        .border(2.dp, Color(0xFFFFFBEB), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin Security Shield",
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "🔐 সুপার এডমিন মোবাইল ওটিপি",
                    color = Color.White,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "অ্যাপের সার্বিক নিরাপত্তা বজায় রাখতে এডমিন প্যানেলে প্রবেশ করার জন্য মোবাইল ওটিপি ভেরিফিকেশন বাধ্যতামূলক।",
                    color = Color(0xFFCBD5E1),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Security Shield Info Chip
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x2638BDF8))
                        .border(1.dp, Color(0xFF38BDF8), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "টু-ফ্যাক্টর অথেনটিকেশন (2FA Guard Active)",
                            color = Color(0xFFBAE6FD),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Authorized Admin Phone Input
                OutlinedTextField(
                    value = adminPhone,
                    onValueChange = onPhoneChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_phone_input"),
                    enabled = !isOtpSent,
                    label = { Text("অথোরাইজড এডমিন মোবাইল নম্বর") },
                    placeholder = { Text("01303347372") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = "Phone",
                            tint = Color(0xFFFFD700)
                        )
                    },
                    trailingIcon = {
                        if (isOtpSent) {
                            Text(
                                text = "ভেরিফাইং 🔒",
                                color = Color(0xFF10B981),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        disabledTextColor = Color(0xFFCBD5E1),
                        focusedBorderColor = Color(0xFFFFD700),
                        unfocusedBorderColor = Color(0xFF475569),
                        disabledBorderColor = Color(0xFF10B981),
                        focusedLabelColor = Color(0xFFFFD700),
                        unfocusedLabelColor = Color(0xFF94A3B8)
                    )
                )

                // When OTP is Sent
                if (isOtpSent) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // SMS Simulation Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E293B))
                            .border(1.dp, Color(0xFF38BDF8), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Sms,
                                        contentDescription = null,
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "📩 সিকিউরিটি এসএমএস গেটওয়ে",
                                        color = Color(0xFF38BDF8),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF0284C7))
                                        .clickable { onOtpChange(generatedOtp) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "অটো-ফিল করুন ⚡",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "আপনার সুপার এডমিন সিকিউরিটি ওটিপি: $generatedOtp",
                                color = Color(0xFFF0FDF4),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 6-digit OTP input
                    OutlinedTextField(
                        value = enteredOtp,
                        onValueChange = onOtpChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_otp_input"),
                        label = { Text("৬ ডিজিটের এডমিন সিক্রেট ওটিপি") },
                        placeholder = { Text("৬ ডিজিট ওটিপি") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "OTP Code",
                                tint = Color(0xFF38BDF8)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isTimerRunning) "পুনরায় পাঠানো যাবে (${timerSeconds}s)" else "ওটিপি মেয়াদ শেষ",
                            color = if (isTimerRunning) Color(0xFF94A3B8) else Color(0xFFEF4444),
                            fontSize = 11.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (!isTimerRunning) {
                                Text(
                                    text = "🔄 পুনরায় পাঠান",
                                    color = Color(0xFFFBBF24),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { onResendOtp() }
                                )
                            }
                            Text(
                                text = "✏️ নম্বর পরিবর্তন",
                                color = Color(0xFF93C5FD),
                                fontSize = 11.sp,
                                modifier = Modifier.clickable { onChangeNumber() }
                            )
                        }
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x3DF43F5E))
                            .border(1.dp, Color(0xFFF43F5E), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFFDA4AF),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = errorMessage,
                                color = Color(0xFFFDA4AF),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Button
                Button(
                    onClick = {
                        if (!isOtpSent) {
                            onSendOtp()
                        } else {
                            onVerifyOtp()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("admin_otp_action_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isOtpSent) Color(0xFF2563EB) else Color(0xFF059669)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = if (!isOtpSent) Icons.Default.Sms else Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (!isOtpSent) "📲 ওটিপি কোড পাঠান (Send Admin OTP)" else "🛡️ ওটিপি যাচাই করে ড্যাশবোর্ডে প্রবেশ করুন",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
