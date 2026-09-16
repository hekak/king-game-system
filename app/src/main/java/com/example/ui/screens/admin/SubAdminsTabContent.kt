package com.example.ui.screens.admin

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.example.data.db.AdminWalletConfigEntity
import com.example.data.db.DepositRequestEntity
import com.example.data.db.RegisteredAccountEntity
import com.example.data.db.SubAdminEntity
import com.example.data.db.WithdrawRequestEntity
import com.example.ui.viewmodel.AdminViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SubAdminsTabContent(
    allSubAdmins: List<SubAdminEntity>,
    allAccounts: List<RegisteredAccountEntity>,
    allDeposits: List<DepositRequestEntity>,
    allWithdraws: List<WithdrawRequestEntity>,
    walletConfig: AdminWalletConfigEntity?,
    viewModel: AdminViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showAddForm by remember { mutableStateOf(false) }

    // Form states
    var nameInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var codeInput by remember { mutableStateOf("") }
    var commissionInput by remember { mutableDoubleStateOf(5.0) }
    var notesInput by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf<String?>(null) }

    // Computed totals
    val totalReferredAccounts = allAccounts.count { it.referredBySubAdmin.isNotBlank() }
    val approvedDeposits = allDeposits.filter { it.status == "APPROVED" }
    val approvedWithdraws = allWithdraws.filter { it.status == "APPROVED" }

    // Group accounts by referral code
    val accountsByCode = remember(allAccounts) {
        allAccounts.groupBy { it.referredBySubAdmin.trim().uppercase() }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header & Overview KPIs
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1038)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF8B5CF6))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "সাব-এডমিন ও এজেন্ট নেটওয়ার্ক",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "রেফারেল ট্র্যাকিং ও কমিশন সিস্টেম",
                                    color = Color(0xFFC084FC),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Button(
                            onClick = { showAddForm = !showAddForm },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (showAddForm) Color(0xFFDC2626) else Color(0xFF9333EA)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(
                                imageVector = if (showAddForm) Icons.Default.Close else Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (showAddForm) "বাতিল" else "নতুন এজেন্ট",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Metric summary badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricSmallBox(
                            title = "মোট সাব-এডমিন",
                            value = "${allSubAdmins.size} জন",
                            color = Color(0xFF38BDF8),
                            modifier = Modifier.weight(1f)
                        )
                        MetricSmallBox(
                            title = "রেফারেল ইউজার",
                            value = "$totalReferredAccounts জন",
                            color = Color(0xFF4ADE80),
                            modifier = Modifier.weight(1f)
                        )
                        MetricSmallBox(
                            title = "সক্রিয় এজেন্ট",
                            value = "${allSubAdmins.count { it.status == "ACTIVE" }} জন",
                            color = Color(0xFFFBBF24),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 2. Data Export & Statement Download Section (এডমিন ডাউনলোড প্যানেল)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF140D2B)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF3B82F6))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = Color(0xFF60A5FA),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "এডমিন ডাটা ডাউনলোড ও অডিট স্টেটমেন্ট (Export Reports)",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "সকল সাব-এডমিন ও তাদের সকল ইউজারের লেনদেন সহ সম্পূর্ণ ডাটা এক ক্লিকে ডাউনলোড করুন:",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.exportSubAdminsReport(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("export_subadmins_csv_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "📥 সাব-এডমিন রিপোর্ট CSV ডাউনলোড করুন",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { viewModel.exportAllPlayersReport(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("export_players_csv_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "👥 সকল প্লেয়ার ও সাব-এডমিন ডাটা ডাউনলোড করুন (CSV)",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { viewModel.exportMasterFullAudit(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("export_master_audit_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "👑 মাস্টার ক্যাসিনো অডিট রিপোর্ট ডাউনলোড করুন (TXT)",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 3. New Sub-Admin Registration Form
        item {
            AnimatedVisibility(visible = showAddForm) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF241442)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFFFD700))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "নতুন সাব-এডমিন যুক্ত করুন",
                            color = Color(0xFFFFD700),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "সাব-এডমিনদের ইউনিক রেফারেল কোড দিন, যার মাধ্যমে তারা প্লেয়ার যুক্ত করবে।",
                            color = Color(0xFFD1D5DB),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("সাব-এডমিনের নাম") },
                            placeholder = { Text("যেমন: হাসান মাহমুদ") },
                            leadingIcon = { Icon(Icons.Default.Person, null, tint = Color(0xFFC084FC)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = adminTextFieldColors()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it.take(11) },
                            label = { Text("মোবাইল নম্বর") },
                            placeholder = { Text("যেমন: 017XXXXXXXX") },
                            leadingIcon = { Icon(Icons.Default.Phone, null, tint = Color(0xFFC084FC)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = adminTextFieldColors()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = codeInput,
                            onValueChange = { codeInput = it.take(12).uppercase() },
                            label = { Text("রেফারেল কোড (ইউনিক)") },
                            placeholder = { Text("যেমন: AGENT01 বা VIP77") },
                            leadingIcon = { Icon(Icons.Default.CardGiftcard, null, tint = Color(0xFFFBBF24)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = adminTextFieldColors()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Commission Slider
                        Text(
                            text = "কমিশন শতকরা: ${String.format(Locale.US, "%.1f", commissionInput)}%",
                            color = Color(0xFFFDE047),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Slider(
                            value = commissionInput.toFloat(),
                            onValueChange = { commissionInput = it.toDouble() },
                            valueRange = 0f..25f,
                            steps = 49,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFFFD700),
                                activeTrackColor = Color(0xFFFFD700),
                                inactiveTrackColor = Color(0xFF475569)
                            )
                        )

                        OutlinedTextField(
                            value = notesInput,
                            onValueChange = { notesInput = it },
                            label = { Text("নোট বা নোটস (ঐচ্ছিক)") },
                            placeholder = { Text("যেমন: ঢাকা বিভাগীয় প্রধান এজেন্ট") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = adminTextFieldColors()
                        )

                        if (formError != null) {
                            Text(
                                text = formError!!,
                                color = Color(0xFFEF4444),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val name = nameInput.trim()
                                val phone = phoneInput.trim()
                                val code = codeInput.trim().uppercase()
                                if (name.isBlank()) {
                                    formError = "দয়া করে সাব-এডমিনের নাম লিখুন"
                                } else if (phone.length < 11) {
                                    formError = "সঠিক ১১ ডিজিটের মোবাইল নম্বর দিন"
                                } else if (code.length < 3) {
                                    formError = "কমপক্ষে ৩ অক্ষরের রেফারেল কোড দিন"
                                } else if (allSubAdmins.any { it.referralCode.equals(code, ignoreCase = true) }) {
                                    formError = "এই রেফারেল কোডটি ইতিমধ্যে অন্য সাব-এডমিনের আছে!"
                                } else {
                                    formError = null
                                    viewModel.addSubAdmin(
                                        name = name,
                                        phone = phone,
                                        referralCode = code,
                                        commissionPercent = commissionInput,
                                        notes = notesInput
                                    )
                                    // Reset
                                    nameInput = ""
                                    phoneInput = ""
                                    codeInput = ""
                                    notesInput = ""
                                    showAddForm = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "সাব-এডমিন যুক্ত নিশ্চিত করুন",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // 4. Sub-Admin List Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "সাব-এডমিন তালিকা ও পারফরম্যান্স (${allSubAdmins.size})",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "রিয়েল-টাইম হিসাব",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }
        }

        // Empty state
        if (allSubAdmins.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .background(Color(0xFF1A102F), RoundedCornerShape(14.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "কোনো সাব-এডমিন পাওয়া যায়নি",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "উপরের 'নতুন এজেন্ট' বাটনে ক্লিক করে সাব-এডমিন যুক্ত করুন",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Sub-Admins list
        items(allSubAdmins, key = { it.id }) { subAdmin ->
            val cleanCode = subAdmin.referralCode.trim().uppercase()
            val referredAccounts = accountsByCode[cleanCode] ?: emptyList()
            val referredPhones = referredAccounts.map { it.userPhone }.toSet()

            val subAdminDeposits = approvedDeposits.filter { it.userPhone in referredPhones }
            val subAdminWithdraws = approvedWithdraws.filter { it.userPhone in referredPhones }

            val totalDepositAmount = subAdminDeposits.sumOf { it.amount }
            val totalWithdrawAmount = subAdminWithdraws.sumOf { it.amount }
            val netCasinoProfit = totalDepositAmount - totalWithdrawAmount
            val estimatedCommission = if (netCasinoProfit > 0) (netCasinoProfit * subAdmin.commissionPercent / 100.0) else 0.0

            SubAdminCard(
                subAdmin = subAdmin,
                referredAccounts = referredAccounts,
                totalDepositAmount = totalDepositAmount,
                totalWithdrawAmount = totalWithdrawAmount,
                netProfit = netCasinoProfit,
                commission = estimatedCommission,
                onToggleStatus = { viewModel.toggleSubAdminStatus(subAdmin.id, subAdmin.status) },
                onDelete = { viewModel.deleteSubAdmin(subAdmin.id) },
                context = context
            )
        }
    }
}

@Composable
private fun SubAdminCard(
    subAdmin: SubAdminEntity,
    referredAccounts: List<RegisteredAccountEntity>,
    totalDepositAmount: Double,
    totalWithdrawAmount: Double,
    netProfit: Double,
    commission: Double,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit,
    context: Context
) {
    var expanded by remember { mutableStateOf(false) }
    val isActive = subAdmin.status == "ACTIVE"

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B0E33)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (isActive) Color(0xFF8B5CF6) else Color(0xFF64748B))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Name + Referral Code + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = subAdmin.name,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isActive) Color(0xFF166534) else Color(0xFF7F1D1D))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isActive) "সক্রিয়" else "স্থগিত",
                                color = if (isActive) Color(0xFF86EFAC) else Color(0xFFFCA5A5),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = "মোবাইল: ${subAdmin.phone}",
                        color = Color(0xFFCBD5E1),
                        fontSize = 11.sp
                    )
                }

                // Referral Code Pill with Copy
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF311042))
                        .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(8.dp))
                        .clickable {
                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cm.setPrimaryClip(ClipData.newPlainText("Referral Code", subAdmin.referralCode))
                            Toast.makeText(context, "রেফারেল কোড কপি করা হয়েছে: ${subAdmin.referralCode}", Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = subAdmin.referralCode,
                            color = Color(0xFFFFD700),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Performance KPI Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MetricSmallBox(
                    title = "মোট ইউজার",
                    value = "${referredAccounts.size} জন",
                    color = Color(0xFF38BDF8),
                    modifier = Modifier.weight(1f)
                )
                MetricSmallBox(
                    title = "মোট ডিপোজিট",
                    value = "৳ ${String.format(Locale.US, "%,d", totalDepositAmount.toLong())}",
                    color = Color(0xFF4ADE80),
                    modifier = Modifier.weight(1f)
                )
                MetricSmallBox(
                    title = "মোট উইথড্র",
                    value = "৳ ${String.format(Locale.US, "%,d", totalWithdrawAmount.toLong())}",
                    color = Color(0xFFF43F5E),
                    modifier = Modifier.weight(1f)
                )
                MetricSmallBox(
                    title = "কমিশন (${String.format(Locale.US, "%.0f", subAdmin.commissionPercent)}%)",
                    value = "৳ ${String.format(Locale.US, "%,d", commission.toLong())}",
                    color = Color(0xFFFBBF24),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Referral Link Bar with Share and Copy
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F071D))
                    .border(0.5.dp, Color(0xFF4C1D95), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = subAdmin.referralLink.ifBlank { "ref=${subAdmin.referralCode}" },
                    color = Color(0xFFA78BFA),
                    fontSize = 11.sp,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cm.setPrimaryClip(ClipData.newPlainText("Referral Link", subAdmin.referralLink))
                            Toast.makeText(context, "রেফারেল লিংক কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Link",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    android.content.Intent.EXTRA_TEXT,
                                    "রয়েল ক্যাসিনো বাংলাদেশে জয়েন করুন সাব-এডমিন '${subAdmin.name}'-এর সাথে!\nরেফারেল কোড: ${subAdmin.referralCode}\nলিংক: ${subAdmin.referralLink}"
                                )
                            }
                            context.startActivity(android.content.Intent.createChooser(intent, "রেফারেল লিংক শেয়ার করুন"))
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Controls & Toggle
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Expand user list button
                Row(
                    modifier = Modifier
                        .clickable { expanded = !expanded }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (expanded) "ইউজার তালিকা লুকান" else "এই এজেন্টের ইউজার দেখুন (${referredAccounts.size})",
                        color = Color(0xFFC084FC),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color(0xFFC084FC),
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Action buttons: toggle & delete
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = onToggleStatus,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isActive) Color(0xFFB45309) else Color(0xFF15803D)
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(
                            text = if (isActive) "স্থগিত" else "সক্রিয়",
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Expandable Referred Users List
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F071D))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "সাব-এডমিন '${subAdmin.name}'-এর মাধ্যমে নিবন্ধিত প্লেয়ারবৃন্দ:",
                        color = Color(0xFFFDE047),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    if (referredAccounts.isEmpty()) {
                        Text(
                            text = "এই সাব-এডমিনের রেফারেল কোড দিয়ে এখনো কোনো প্লেয়ার যুক্ত হয়নি।",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp
                        )
                    } else {
                        val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
                        referredAccounts.forEachIndexed { idx, acc ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${idx + 1}. ${acc.playerName.ifBlank { "Player" }} (${acc.userPhone})",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "ডিভাইস: ${acc.deviceModel} | ${sdf.format(Date(acc.registeredAt))}",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 9.sp
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF10B981))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "VERIFIED",
                                        color = Color.White,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                            if (idx < referredAccounts.size - 1) {
                                HorizontalDivider(
                                    color = Color(0x22FFFFFF),
                                    thickness = 0.5.dp,
                                    modifier = Modifier.padding(vertical = 2.dp)
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
private fun MetricSmallBox(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF110822))
            .border(0.5.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                color = Color(0xFF94A3B8),
                fontSize = 9.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun adminTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedBorderColor = Color(0xFFFFD700),
    unfocusedBorderColor = Color(0xFF475569),
    focusedLabelColor = Color(0xFFFFD700),
    unfocusedLabelColor = Color(0xFF94A3B8)
)
