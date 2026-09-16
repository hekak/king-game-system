package com.example.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.example.data.db.AdminWalletConfigEntity
import com.example.data.db.DepositRequestEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class PaymentMethod(val label: String, val brandColor: Color, val emoji: String) {
    NAGAD("নগদ", Color(0xFFEA580C), "🔴"),
    BKASH("বিকাশ", Color(0xFFBE185D), "🟣"),
    ROCKET("রকেট", Color(0xFF7C3AED), "🔵"),
    UPAY("উপায়", Color(0xFFD97706), "🟡")
}

@Composable
fun DepositDialog(
    userPhone: String,
    walletConfig: AdminWalletConfigEntity,
    depositHistory: List<DepositRequestEntity>,
    onSubmitDeposit: (amount: Double, method: String, adminWallet: String, trxId: String) -> Unit,
    onOpenWithdraw: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var selectedMethod by remember { mutableStateOf(PaymentMethod.NAGAD) }
    var amountInput by remember { mutableStateOf("500") }
    var trxIdInput by remember { mutableStateOf("") }
    var submissionSuccess by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    val activeWalletNumber = when (selectedMethod) {
        PaymentMethod.NAGAD -> walletConfig.nagadWallet.ifBlank { "01303347372" }
        PaymentMethod.BKASH -> walletConfig.bkashWallet.ifBlank { "01303347372" }
        PaymentMethod.ROCKET -> walletConfig.rocketWallet.ifBlank { "01303347372" }
        PaymentMethod.UPAY -> walletConfig.upayWallet.ifBlank { "01303347372" }
    }

    val amountValue = amountInput.toDoubleOrNull() ?: 0.0
    val isAmountValid = amountValue in 100.0..20000.0
    val isTrxValid = trxIdInput.trim().length >= 4

    val minDeposit = walletConfig.minDeposit.coerceAtLeast(100.0)
    val maxDeposit = walletConfig.maxDeposit.coerceAtMost(20000.0)

    val timeFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(decorFitsSystemWindows = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .imePadding()
                .clip(RoundedCornerShape(22.dp))
                .testTag("deposit_dialog"),
            color = Color(0xFF0F0624),
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(
                1.5.dp,
                Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFF59E0B), Color(0xFF78350F)))
            )
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
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEAB308)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = "Deposit",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "পয়েন্ট রিচার্জ ও ডিপোজিট",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "আইডি: ${userPhone.ifBlank { "Not Registered" }}",
                                color = Color(0xFFFDE68A),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = {
                                onDismiss()
                                onOpenWithdraw()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("switch_to_withdraw_button")
                        ) {
                            Text("💸 উইথড্রো", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("deposit_dialog_close")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Navigation Tabs: [ডিপোজিট করুন] | [আবেদন হিস্ট্রি]
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color(0xFF1E1438),
                    contentColor = Color(0xFFFFD700),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = {
                            selectedTabIndex = 0
                            submissionSuccess = false
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ডিপোজিট করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("আবেদন হিস্ট্রি (${depositHistory.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (selectedTabIndex == 0) {
                    // TAB 1: Deposit Form
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (submissionSuccess) {
                            // Submission Success Card
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF14532D))
                                    .border(1.dp, Color(0xFF22C55E), RoundedCornerShape(12.dp))
                                    .padding(14.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Success",
                                        tint = Color(0xFF4ADE80),
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "আবেদন সফলভাবে সাবমিট হয়েছে!",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "এডমিন আপনার পেমেন্ট ট্রানজ্যাকশন যাচাই করে অ্যাকাউন্টে পয়েন্ট যুক্ত করবেন। হিস্ট্রি ট্যাবে স্ট্যাটাস দেখতে পারেন।",
                                        color = Color(0xFFDCFCE7),
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        // Cash-out Instruction Banner Box (Exact Bangla requirement)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF1E1B4B), Color(0xFF311042))
                                    )
                                )
                                .border(1.dp, Color(0xFFD97706), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = Color(0xFFFFD700),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "ক্যাশ আউট নির্দেশিকা:",
                                        color = Color(0xFFFFD700),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "নগদ/বিকাশ/ রকেট/উপায়/ $activeWalletNumber এই নম্বরে টাকা ক্যাশ আউট করুন এবং আপনার সেন্ডমানির স্কিনশট বা ট্যান্জাকশন আইডি দিন।",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 1. Payment Method Select
                        Text(
                            text = "পেমেন্ট মাধ্যম বেছে নিন:",
                            color = Color(0xFFE2E8F0),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            PaymentMethod.values().forEach { method ->
                                val isSelected = selectedMethod == method
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) method.brandColor else Color(0xFF1F1235))
                                        .border(
                                            1.dp,
                                            if (isSelected) Color.White else Color(0xFF3B2859),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedMethod = method }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = method.emoji, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = method.label,
                                            color = Color.White,
                                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Active Wallet Display & Copy Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF26123D))
                                .border(1.dp, Color(0xFF6B21A8), RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${selectedMethod.label} ক্যাশ আউট ওয়ালেট নম্বর:",
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = activeWalletNumber,
                                        color = Color(0xFFFFD700),
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )
                                }

                                Button(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Wallet Number", activeWalletNumber)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "ওয়ালেট নম্বর কপি করা হয়েছে: $activeWalletNumber", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("copy_wallet_number_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "কপি করুন", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 2. Amount Input (100 - 20,000)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "পয়েন্টের পরিমাণ লিখুন:",
                                color = Color(0xFFE2E8F0),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "১০০ - ২০,০০০ পয়েন্ট",
                                color = Color(0xFFFBBF24),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = amountInput,
                            onValueChange = { input ->
                                val digits = input.filter { it.isDigit() }
                                amountInput = digits
                                validationError = null
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("deposit_amount_input"),
                            placeholder = { Text("১০০ থেকে ২০০০০ লিখুন") },
                            trailingIcon = {
                                Text(
                                    text = "Points",
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFFD700),
                                unfocusedBorderColor = Color(0xFF475569)
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Amount Preset Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(100, 500, 1000, 2000, 5000, 10000, 20000).forEach { preset ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (amountInput == preset.toString()) Color(0xFFEAB308) else Color(0xFF1E1438))
                                        .border(
                                            1.dp,
                                            if (amountInput == preset.toString()) Color(0xFFFEF08A) else Color(0xFF3B2859),
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable {
                                            amountInput = preset.toString()
                                            validationError = null
                                        }
                                        .padding(vertical = 5.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$preset",
                                        color = if (amountInput == preset.toString()) Color.Black else Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 3. Transaction ID (TrxID)
                        Text(
                            text = "ট্যান্জাকশন আইডি (TrxID):",
                            color = Color(0xFFE2E8F0),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = trxIdInput,
                            onValueChange = {
                                trxIdInput = it.uppercase().trim()
                                validationError = null
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("deposit_trx_input"),
                            placeholder = { Text("যেমন: 8K9LM4P2X") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFFD700),
                                unfocusedBorderColor = Color(0xFF475569)
                            )
                        )

                        if (validationError != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = validationError ?: "",
                                color = Color(0xFFEF4444),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                if (amountValue < minDeposit || amountValue > maxDeposit) {
                                    validationError = "পয়েন্ট ১০০ থেকে ২০,০০০ এর মধ্যে হতে হবে।"
                                    return@Button
                                }
                                if (trxIdInput.trim().length < 4) {
                                    validationError = "অনুগ্রহ করে সঠিক ট্যান্জাকশন আইডি (TrxID) দিন।"
                                    return@Button
                                }
                                onSubmitDeposit(
                                    amountValue,
                                    selectedMethod.name,
                                    activeWalletNumber,
                                    trxIdInput.trim()
                                )
                                submissionSuccess = true
                                trxIdInput = ""
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("submit_deposit_request_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAmountValid && isTrxValid) Color(0xFF16A34A) else Color(0xFF475569)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            enabled = isAmountValid && isTrxValid
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "আবেদন সাবমিট করুন (${String.format(Locale.US, "%.0f", amountValue)} পয়েন্ট)",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }
                    }
                } else {
                    // TAB 2: Deposit History
                    if (depositHistory.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "আপনার কোন ডিপোজিট আবেদন নেই",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(depositHistory, key = { it.id }) { req ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF170C2A))
                                        .border(1.dp, Color(0xFF332050), RoundedCornerShape(10.dp))
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "${req.method} - ${String.format(Locale.US, "%,.0f", req.amount)} pts",
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "TrxID: ${req.trxId}",
                                                color = Color(0xFFCBD5E1),
                                                fontSize = 11.sp
                                            )
                                            Text(
                                                text = timeFormat.format(Date(req.submittedAt)),
                                                color = Color(0xFF94A3B8),
                                                fontSize = 10.sp
                                            )
                                        }

                                        // Status Chip
                                        val statusColor = when (req.status) {
                                            "APPROVED" -> Color(0xFF16A34A)
                                            "REJECTED" -> Color(0xFFDC2626)
                                            else -> Color(0xFFD97706)
                                        }
                                        val statusLabel = when (req.status) {
                                            "APPROVED" -> "অনুমোদিত"
                                            "REJECTED" -> "বাতিল"
                                            else -> "অপেক্ষমান"
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(statusColor)
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = statusLabel,
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black
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
