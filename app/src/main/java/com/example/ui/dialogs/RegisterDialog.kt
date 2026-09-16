package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.util.DeviceUtil

enum class RegisterTab {
    LOGIN_RECOVER,
    NEW_REGISTER
}

@Composable
fun RegisterDialog(
    initialPhone: String = "",
    canDismiss: Boolean = false,
    onDismiss: () -> Unit = {},
    onRegister: (phone: String, name: String) -> Unit = { _, _ -> },
    onRegisterWithDevice: (phone: String, name: String, deviceId: String, deviceModel: String, onResult: (Boolean, String) -> Unit) -> Unit = { p, n, _, _, cb -> onRegister(p, n); cb(true, "") },
    onRegisterWithDeviceAndReferral: (phone: String, name: String, deviceId: String, deviceModel: String, referralCode: String, onResult: (Boolean, String) -> Unit) -> Unit = { p, n, d, m, _, cb -> onRegisterWithDevice(p, n, d, m, cb) },
    onLoginOrRecover: (phone: String, deviceId: String, deviceModel: String, onResult: (Boolean, String) -> Unit) -> Unit = { _, _, _, cb -> cb(true, "") }
) {
    val context = LocalContext.current
    val deviceId = remember { DeviceUtil.getDeviceId(context) }
    val deviceModel = remember { DeviceUtil.getDeviceModel() }

    var selectedTab by remember { mutableStateOf(RegisterTab.LOGIN_RECOVER) }
    var phoneInput by remember { mutableStateOf(initialPhone) }
    var nameInput by remember { mutableStateOf("Player") }
    var referralCodeInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val isValidPhone = phoneInput.trim().length == 11 && phoneInput.trim().startsWith("01")

    Dialog(
        onDismissRequest = { if (canDismiss) onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = canDismiss,
            dismissOnClickOutside = canDismiss,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .imePadding()
                .clip(RoundedCornerShape(24.dp))
                .testTag("register_dialog"),
            color = Color(0xFF0F0624),
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFD97706), Color(0xFF78350F)))
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Row with Close button if dismissible
                if (canDismiss) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFFFFF)),
                            shape = CircleShape,
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                            modifier = Modifier.size(30.dp)
                        ) {
                            Text("✕", color = Color.White, fontSize = 14.sp)
                        }
                    }
                }

                // Crown Avatar Header
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFFFD700), Color(0xFFD97706), Color(0xFF92400E))
                            )
                        )
                        .border(2.dp, Color(0xFFFFFBEB), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "👑", fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (selectedTab == RegisterTab.LOGIN_RECOVER) "সরাসরি লগইন ও ব্যালেন্স পুনরুদ্ধার" else "নতুন অ্যাকাউন্ট রেজিস্ট্রেশন",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (selectedTab == RegisterTab.LOGIN_RECOVER) {
                        "আপনার পূর্বে নিবন্ধিত মোবাইল নম্বরটি লিখুন এবং সরাসরি লগইন করুন। ব্যালেন্স ও লেভেল সাথে সাথে লোড হবে।"
                    } else {
                        "মোবাইল নম্বর দিয়ে তাৎক্ষণিক নতুন একাউন্ট তৈরি করুন এবং ওয়েলকাম বোনাস নিয়ে খেলা শুরু করুন।"
                    },
                    color = Color(0xFFCBD5E1),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Mode Selector Tabs (Login / Recover vs New Register)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E1038))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Login / Recover Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .then(
                                if (selectedTab == RegisterTab.LOGIN_RECOVER) {
                                    Modifier.background(Brush.horizontalGradient(listOf(Color(0xFFD97706), Color(0xFFF59E0B))))
                                } else {
                                    Modifier.background(Color.Transparent)
                                }
                            )
                            .clickable {
                                selectedTab = RegisterTab.LOGIN_RECOVER
                                errorMessage = null
                                successMessage = null
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🔑 পুনরায় লগইন",
                            color = if (selectedTab == RegisterTab.LOGIN_RECOVER) Color.Black else Color(0xFFCBD5E1),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // New Register Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .then(
                                if (selectedTab == RegisterTab.NEW_REGISTER) {
                                    Modifier.background(Brush.horizontalGradient(listOf(Color(0xFF2563EB), Color(0xFF3B82F6))))
                                } else {
                                    Modifier.background(Color.Transparent)
                                }
                            )
                            .clickable {
                                selectedTab = RegisterTab.NEW_REGISTER
                                errorMessage = null
                                successMessage = null
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📝 নতুন একাউন্ট",
                            color = if (selectedTab == RegisterTab.NEW_REGISTER) Color.White else Color(0xFFCBD5E1),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Device Security Lock Badge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x2E1E1B4B))
                        .border(1.dp, Color(0xFF6366F1), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color(0xFF818CF8),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "তাৎক্ষণিক ডিভাইস সিঙ্ক ও ডাটা সুরক্ষা সক্রিয়",
                                color = Color(0xFFC7D2FE),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "📱 $deviceModel • ID: ${deviceId.take(12)}...",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Mobile Number Input Field
                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { input ->
                        val digits = input.filter { it.isDigit() }.take(11)
                        phoneInput = digits
                        errorMessage = null
                        successMessage = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("register_phone_input"),
                    enabled = !isSubmitting,
                    label = {
                        Text(
                            if (selectedTab == RegisterTab.LOGIN_RECOVER) "রেজিস্ট্রেশন করা মোবাইল নম্বর" else "মোবাইল নম্বর (১১ ডিজিট)"
                        )
                    },
                    placeholder = { Text("01XXXXXXXXX") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = "Phone",
                            tint = Color(0xFFFFD700)
                        )
                    },
                    trailingIcon = {
                        if (isValidPhone) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Valid",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(20.dp)
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

                // Nickname & Referral Inputs (Only in NEW_REGISTER mode)
                if (selectedTab == RegisterTab.NEW_REGISTER) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it.take(15) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_name_input"),
                        enabled = !isSubmitting,
                        label = { Text("প্লেয়ারের নাম (ঐচ্ছিক)") },
                        placeholder = { Text("Player") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Name",
                                tint = Color(0xFF94A3B8)
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFFFD700),
                            unfocusedBorderColor = Color(0xFF475569),
                            focusedLabelColor = Color(0xFFFFD700),
                            unfocusedLabelColor = Color(0xFF94A3B8)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = referralCodeInput,
                        onValueChange = { referralCodeInput = it.take(15).uppercase() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_referral_input"),
                        enabled = !isSubmitting,
                        label = { Text("সাব-এডমিন / রেফারেল কোড (ঐচ্ছিক)") },
                        placeholder = { Text("যেমন: AGENT01 বা VIP77") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CardGiftcard,
                                contentDescription = "Referral Code",
                                tint = Color(0xFFFBBF24)
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFFBBF24),
                            unfocusedBorderColor = Color(0xFF475569),
                            focusedLabelColor = Color(0xFFFBBF24),
                            unfocusedLabelColor = Color(0xFF94A3B8)
                        )
                    )
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x3DF43F5E))
                            .border(1.dp, Color(0xFFF43F5E), RoundedCornerShape(8.dp))
                            .padding(8.dp)
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
                                text = errorMessage ?: "",
                                color = Color(0xFFFDA4AF),
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                if (successMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x3D10B981))
                            .border(1.dp, Color(0xFF10B981), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = successMessage ?: "",
                            color = Color(0xFF6EE7B7),
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Submit Button - Direct 1-Tap Login/Register (No OTP Needed)
                Button(
                    onClick = {
                        val cleaned = phoneInput.trim()
                        if (cleaned.length != 11 || !cleaned.startsWith("01")) {
                            errorMessage = "অনুগ্রহ করে সঠিক ১১ ডিজিটের মোবাইল নম্বর দিন (যেমন: 01XXXXXXXXX)"
                            return@Button
                        }

                        isSubmitting = true
                        errorMessage = null
                        successMessage = null

                        if (selectedTab == RegisterTab.LOGIN_RECOVER) {
                            onLoginOrRecover(cleaned, deviceId, deviceModel) { success, msg ->
                                isSubmitting = false
                                if (success) {
                                    successMessage = msg
                                } else {
                                    errorMessage = msg
                                }
                            }
                        } else {
                            onRegisterWithDeviceAndReferral(
                                cleaned,
                                nameInput.trim(),
                                deviceId,
                                deviceModel,
                                referralCodeInput.trim().uppercase()
                            ) { success, msg ->
                                isSubmitting = false
                                if (success) {
                                    successMessage = msg
                                } else {
                                    errorMessage = msg
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("register_submit_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isValidPhone && !isSubmitting) {
                            if (selectedTab == RegisterTab.LOGIN_RECOVER) {
                                Color(0xFFD97706)
                            } else {
                                Color(0xFF16A34A)
                            }
                        } else Color(0xFF334155)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isValidPhone && !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedTab == RegisterTab.LOGIN_RECOVER) "লগইন হচ্ছে..." else "রেজিস্ট্রেশন হচ্ছে...",
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    } else {
                        Icon(
                            imageVector = if (selectedTab == RegisterTab.LOGIN_RECOVER) Icons.Default.Login else Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = if (selectedTab == RegisterTab.LOGIN_RECOVER) Color.Black else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (selectedTab == RegisterTab.LOGIN_RECOVER) {
                                "🔑 সরাসরি লগইন করুন"
                            } else {
                                "📝 অ্যাকাউন্ট তৈরি ও খেলা শুরু করুন"
                            },
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = if (selectedTab == RegisterTab.LOGIN_RECOVER) Color.Black else Color.White
                        )
                    }
                }
            }
        }
    }
}
