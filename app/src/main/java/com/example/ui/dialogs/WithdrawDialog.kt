package com.example.ui.dialogs

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.imePadding
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.db.WithdrawRequestEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun WithdrawDialog(
    userPhone: String,
    currentBalance: Double,
    withdrawHistory: List<WithdrawRequestEntity>,
    requiredTurnover: Double = 0.0,
    completedTurnover: Double = 0.0,
    pendingTurnover: Double = 0.0,
    onSubmitWithdraw: (amount: Double, method: String, phone: String, onResult: (Boolean, String) -> Unit) -> Unit,
    onOpenDeposit: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var selectedMethod by remember { mutableStateOf(PaymentMethod.NAGAD) }
    var amountInput by remember { mutableStateOf(if (currentBalance >= 500) "500" else "100") }
    var phoneInput by remember { mutableStateOf(userPhone.ifBlank { "" }) }
    var submissionSuccess by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(decorFitsSystemWindows = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.98f)
                .imePadding()
                .clip(RoundedCornerShape(24.dp))
                .testTag("withdraw_dialog"),
            color = Color(0xFF0F0624),
            border = BorderStroke(
                1.5.dp,
                Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFFEAB308), Color(0xFF6366F1)))
            ),
            shape = RoundedCornerShape(24.dp)
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
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF059669), Color(0xFF10B981))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = "Withdraw",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "পয়েন্ট উইথড্রো",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF10B981))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "CASH OUT",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                            Text(
                                text = "নগদ, বিকাশ, রকেট ও উপায় গেটওয়ে",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("withdraw_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Navigation Tabs
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color(0xFF190D38),
                    contentColor = Color(0xFF10B981),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("উইথড্রো ফরম", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("withdraw_tab_form")
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "হিস্টোরি (${withdrawHistory.size})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        modifier = Modifier.testTag("withdraw_tab_history")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (selectedTabIndex == 0) {
                    // TAB 0: WITHDRAW FORM
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Current Balance Display
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF160B30))
                                .border(1.dp, Color(0xFF10B981).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "আপনার বর্তমান পয়েন্ট ব্যালেন্স",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "🪙 ${String.format(Locale.US, "%,.2f", currentBalance)}",
                                            color = Color(0xFFFBBF24),
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                        Text(
                                            text = " পয়েন্ট",
                                            color = Color(0xFFE2E8F0),
                                            fontSize = 13.sp
                                        )
                                    }
                                }

                                Button(
                                    onClick = {
                                        onDismiss()
                                        onOpenDeposit()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                        horizontal = 10.dp,
                                        vertical = 6.dp
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ডিপোজিট", fontSize = 11.sp, color = Color.White)
                                }
                            }
                        }

                        // Turnover Requirement & Progress Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("withdraw_turnover_card"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (pendingTurnover > 0.0) Color(0xFF221138) else Color(0xFF063528)
                            ),
                            border = BorderStroke(
                                1.5.dp,
                                if (pendingTurnover > 0.0) Color(0xFFF59E0B) else Color(0xFF10B981)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (pendingTurnover > 0.0) "⚠️" else "✅",
                                            fontSize = 15.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "টার্নওভার অগ্রগতি (Turnover Requirement)",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (pendingTurnover > 0.0) Color(0xFFDC2626) else Color(0xFF059669))
                                            .padding(horizontal = 7.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (pendingTurnover > 0.0) "লকড / অপূর্ণ" else "উইথড্র অনুমোদিত",
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                val progressFraction = if (requiredTurnover > 0.0) {
                                    (completedTurnover / requiredTurnover).coerceIn(0.0, 1.0).toFloat()
                                } else if (pendingTurnover == 0.0) 1.0f else 0.0f

                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = if (pendingTurnover > 0.0) "টার্নওভার পূরণ: ${(progressFraction * 100).roundToInt()}%" else "টার্নওভার ১০০% সম্পূর্ণ",
                                            color = if (pendingTurnover > 0.0) Color(0xFFFBBF24) else Color(0xFF34D399),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (pendingTurnover > 0.0) {
                                            Text(
                                                text = "বাকি: ৳${String.format(Locale.US, "%,.2f", pendingTurnover)}",
                                                color = Color(0xFFF87171),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                    }
                                    LinearProgressIndicator(
                                        progress = { progressFraction },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        color = if (pendingTurnover > 0.0) Color(0xFFF59E0B) else Color(0xFF10B981),
                                        trackColor = Color(0xFF334155)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF0F0720))
                                            .padding(vertical = 5.dp, horizontal = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("টার্নওভার প্রয়োজন", color = Color(0xFF94A3B8), fontSize = 9.sp)
                                            Text(
                                                text = "৳${String.format(Locale.US, "%,.0f", requiredTurnover)}",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF0F0720))
                                            .padding(vertical = 5.dp, horizontal = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("গেম খেলা হয়েছে", color = Color(0xFF94A3B8), fontSize = 9.sp)
                                            Text(
                                                text = "৳${String.format(Locale.US, "%,.0f", completedTurnover)}",
                                                color = Color(0xFF34D399),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF0F0720))
                                            .padding(vertical = 5.dp, horizontal = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("টার্নওভার বাকি", color = Color(0xFF94A3B8), fontSize = 9.sp)
                                            Text(
                                                text = "৳${String.format(Locale.US, "%,.0f", pendingTurnover)}",
                                                color = if (pendingTurnover > 0.0) Color(0xFFF87171) else Color(0xFF10B981),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                if (pendingTurnover > 0.0) {
                                    Text(
                                        text = "📌 নিয়ম: ডিপোজিট ও বোনাস গ্রহণ করার পর সেই পরিমাণ পয়েন্ট দিয়ে সম্পূর্ণ গেম না খেলা পর্যন্ত উইথড্র করা যাবে না। আরও ৳${String.format(Locale.US, "%,.2f", pendingTurnover)} পয়েন্ট গেম খেললে উইথড্র সুবিধা সক্রিয় হবে।",
                                        color = Color(0xFFFCD34D),
                                        fontSize = 10.sp,
                                        lineHeight = 14.sp
                                    )
                                } else {
                                    Text(
                                        text = "✨ আপনার ডিপোজিট ও বোনাস পয়েন্টের সম্পূর্ণ টার্নওভার সম্পন্ন হয়েছে। আপনি নিরাপদে পয়েন্ট উইথড্র করতে পারবেন।",
                                        color = Color(0xFFA7F3D0),
                                        fontSize = 10.sp,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }

                        // Gateway Selector
                        Text(
                            text = "১. নির্দিষ্ট গেটওয়ে নির্বাচন করুন:",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PaymentMethod.values().forEach { method ->
                                val isSelected = selectedMethod == method
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (isSelected) method.brandColor.copy(alpha = 0.25f)
                                            else Color(0xFF160933)
                                        )
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) method.brandColor else Color(0xFF334155),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            selectedMethod = method
                                        }
                                        .padding(vertical = 10.dp, horizontal = 4.dp)
                                        .testTag("withdraw_gateway_${method.name.lowercase()}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = method.emoji, fontSize = 18.sp)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = method.label,
                                            color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
                                        )
                                        if (isSelected) {
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(method.brandColor)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Amount Input
                        Text(
                            text = "২. পয়েন্টের পরিমাণ লিখুন:",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = amountInput,
                            onValueChange = {
                                amountInput = it.filter { ch -> ch.isDigit() || ch == '.' }
                                validationError = null
                            },
                            placeholder = { Text("যেমন: 500", color = Color.Gray, fontSize = 13.sp) },
                            prefix = { Text("🪙 ", color = Color(0xFFFBBF24)) },
                            suffix = { Text("পয়েন্ট", color = Color(0xFF94A3B8), fontSize = 12.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("withdraw_amount_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF10B981),
                                unfocusedBorderColor = Color(0xFF4C1D95),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        // Quick Amount Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            listOf(100.0, 500.0, 1000.0, 2000.0, 5000.0).forEach { amt ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF1E1040))
                                        .border(1.dp, Color(0xFF4338CA), RoundedCornerShape(6.dp))
                                        .clickable {
                                            amountInput = amt.toInt().toString()
                                            validationError = null
                                        }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${amt.toInt()}",
                                        color = Color(0xFFE2E8F0),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Max button
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF047857))
                                    .border(1.dp, Color(0xFF10B981), RoundedCornerShape(6.dp))
                                    .clickable {
                                        amountInput = currentBalance.toInt().coerceAtLeast(0).toString()
                                        validationError = null
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "সব (All)",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        // Mobile Number Input
                        Text(
                            text = "৩. আপনার ${selectedMethod.label} মোবাইল নম্বর:",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = {
                                phoneInput = it.filter { ch -> ch.isDigit() }
                                validationError = null
                            },
                            placeholder = { Text("01XXXXXXXXX", color = Color.Gray, fontSize = 13.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("withdraw_phone_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = selectedMethod.brandColor,
                                unfocusedBorderColor = Color(0xFF4C1D95),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        // Validation error message
                        validationError?.let { err ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF7F1D1D))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "⚠️ $err",
                                    color = Color(0xFFFCA5A5),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Success banner
                        if (submissionSuccess) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF064E3B))
                                    .border(1.dp, Color(0xFF10B981), RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color(0xFF34D399),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = successMessage.ifBlank { "উইথড্রো রিকোয়েস্ট সফলভাবে জমা হয়েছে!" },
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Submit Button
                        Button(
                            onClick = {
                                if (pendingTurnover > 0.0) {
                                    validationError = "টার্নওভার অপূর্ণ! আপনি জমা ও বোনাস নেওয়ার পর সম্পূর্ণ পয়েন্ট গেম খেলেননি। উইথড্র করতে আরও ৳${String.format(Locale.US, "%,.2f", pendingTurnover)} পয়েন্ট গেম খেলা বাকি আছে।"
                                    return@Button
                                }
                                val amount = amountInput.toDoubleOrNull()
                                if (amount == null || amount <= 0) {
                                    validationError = "অনুগ্রহ করে একটি সঠিক পয়েন্টের পরিমাণ লিখুন।"
                                    return@Button
                                }
                                if (amount > currentBalance) {
                                    validationError = "আপনার একাউন্টে পর্যাপ্ত পয়েন্ট নেই! বর্তমান ব্যালেন্স: ${String.format("%.2f", currentBalance)}"
                                    return@Button
                                }
                                if (amount < 100) {
                                    validationError = "সর্বনিম্ন উইথড্রো ১০০ পয়েন্ট।"
                                    return@Button
                                }
                                val cleanPhone = phoneInput.trim()
                                if (cleanPhone.length < 11) {
                                    validationError = "অনুগ্রহ করে একটি সঠিক ১১ ডিজিটের মোবাইল নম্বর লিখুন (যেমন: 017xxxxxxxx)।"
                                    return@Button
                                }

                                isSubmitting = true
                                onSubmitWithdraw(amount, selectedMethod.name, cleanPhone) { success, msg ->
                                    isSubmitting = false
                                    if (success) {
                                        submissionSuccess = true
                                        successMessage = msg
                                        validationError = null
                                        Toast.makeText(context, "উইথড্রো রিকোয়েস্ট জমা হয়েছে!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        validationError = msg
                                    }
                                }
                            },
                            enabled = !isSubmitting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("withdraw_submit_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (pendingTurnover > 0.0) Color(0xFFDC2626) else Color(0xFF10B981)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (isSubmitting) {
                                    "প্রক্রিয়াকরণ হচ্ছে..."
                                } else if (pendingTurnover > 0.0) {
                                    "🔒 উইথড্র লকড (টার্নওভার বাকি ৳${String.format(Locale.US, "%,.0f", pendingTurnover)})"
                                } else {
                                    "উইথড্রো রিকোয়েস্ট পাঠান"
                                },
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Instruction note
                        Text(
                            text = "💡 আপনার রিকোয়েস্ট পাঠানোর পর অ্যাডমিন যাচাই করে আপনার ${selectedMethod.label} নম্বরে টাকা পাঠিয়ে দেবে এবং স্ট্যাটাস আপডেট হবে।",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                } else {
                    // TAB 1: WITHDRAW HISTORY
                    if (withdrawHistory.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = Color(0xFF475569),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "কোনো উইথড্রো রিকোয়েস্ট হিস্টোরি পাওয়া যায়নি",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else {
                        val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(380.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(withdrawHistory, key = { it.id }) { req ->
                                val statusColor = when (req.status) {
                                    "APPROVED" -> Color(0xFF10B981)
                                    "REJECTED" -> Color(0xFFEF4444)
                                    else -> Color(0xFFF59E0B)
                                }
                                val statusLabel = when (req.status) {
                                    "APPROVED" -> "অনুমোদিত (PAID)"
                                    "REJECTED" -> "বাতিল (REJECTED)"
                                    else -> "অপেক্ষমান (PENDING)"
                                }

                                Card(
                                    colors = androidx.compose.material3.CardDefaults.cardColors(
                                        containerColor = Color(0xFF170C33)
                                    ),
                                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.4f)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                val methodEmoji = when (req.method) {
                                                    "NAGAD" -> "🔴 নগদ"
                                                    "BKASH" -> "🟣 বিকাশ"
                                                    "ROCKET" -> "🔵 রকেট"
                                                    "UPAY" -> "🟡 উপায়"
                                                    else -> req.method
                                                }
                                                Text(
                                                    text = methodEmoji,
                                                    color = Color.White,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(statusColor.copy(alpha = 0.2f))
                                                    .border(1.dp, statusColor, RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = statusLabel,
                                                    color = statusColor,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "নম্বর: ${req.userPhone}",
                                                color = Color(0xFFCBD5E1),
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = "🪙 ${String.format("%.0f", req.amount)} পয়েন্ট",
                                                color = Color(0xFFFBBF24),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = dateFormat.format(Date(req.submittedAt)),
                                            color = Color(0xFF64748B),
                                            fontSize = 10.sp
                                        )

                                        if (!req.rejectionReason.isNullOrBlank()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "কারণ: ${req.rejectionReason}",
                                                color = Color(0xFFF87171),
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
