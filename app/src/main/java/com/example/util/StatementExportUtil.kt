package com.example.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.data.db.DailyProgressEntity
import com.example.data.db.DepositRequestEntity
import com.example.data.db.RegisteredAccountEntity
import com.example.data.db.SubAdminEntity
import com.example.data.db.UserProfileEntity
import com.example.data.db.WithdrawRequestEntity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object StatementExportUtil {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    private val fileDateSuffix = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)

    fun generateAuditStatement(
        profile: UserProfileEntity,
        registeredAccounts: List<RegisteredAccountEntity> = emptyList(),
        deposits: List<DepositRequestEntity>,
        withdraws: List<WithdrawRequestEntity>,
        today: DailyProgressEntity?
    ): String {
        return generateTextStatement(profile, deposits, withdraws, today, registeredAccounts)
    }

    fun generateTextStatement(
        userProfile: UserProfileEntity,
        deposits: List<DepositRequestEntity>,
        withdraws: List<WithdrawRequestEntity>,
        dailyProgress: DailyProgressEntity?,
        registeredAccounts: List<RegisteredAccountEntity> = emptyList()
    ): String {
        val now = dateFormat.format(Date())
        val approvedDeposits = deposits.filter { it.status == "APPROVED" }
        val approvedWithdraws = withdraws.filter { it.status == "APPROVED" }

        val totalDeposited = approvedDeposits.sumOf { it.amount }
        val totalWithdrawn = approvedWithdraws.sumOf { it.amount }
        val netHouseCashflow = totalDeposited - totalWithdrawn

        val totalBets = userProfile.lifetimeBet.coerceAtLeast(dailyProgress?.totalBet ?: 0.0)
        val totalWins = userProfile.lifetimeWon.coerceAtLeast(dailyProgress?.totalWon ?: 0.0)
        val houseEdgeAmount = (totalBets - totalWins).coerceAtLeast(0.0)
        val houseMarginPercent = if (totalBets > 0) (houseEdgeAmount / totalBets * 100) else 0.0

        val sb = StringBuilder()
        sb.appendLine("================================================================================")
        sb.appendLine("                  ROYAL SLOTS & CASINO - OFFICIAL USER STATEMENT                ")
        sb.appendLine("                    Admin Audit, Financial & Gameplay Ledger                    ")
        sb.appendLine("================================================================================")
        sb.appendLine("Generated On   : $now")
        sb.appendLine("Statement Type : Full Account Audit, Device Lock & Balance Ledger")
        sb.appendLine("Admin Control  : Guaranteed House Profit Protected")
        sb.appendLine("--------------------------------------------------------------------------------")
        sb.appendLine()
        sb.appendLine("[1] ACTIVE USER IDENTITY & HARDWARE SECURITY LOCK")
        sb.appendLine("--------------------------------------------------------------------------------")
        sb.appendLine("User ID (Phone)   : ${if (userProfile.userPhone.isNotBlank()) userProfile.userPhone else "UNREGISTERED"}")
        sb.appendLine("Player Name       : ${userProfile.playerName}")
        sb.appendLine("Device ID (UID)   : ${if (userProfile.deviceId.isNotBlank()) userProfile.deviceId else "Hardware Bound"}")
        sb.appendLine("Device Model      : ${if (userProfile.deviceModel.isNotBlank()) userProfile.deviceModel else "Android Device"}")
        sb.appendLine("Registration Time : ${if (userProfile.registeredAt > 0) dateFormat.format(Date(userProfile.registeredAt)) else "System Verified"}")
        sb.appendLine("Device Lock Rule  : 1 Phone + 1 Device Constraint (Enforced)")
        sb.appendLine("VIP Tier Level    : VIP ${userProfile.level} | Score: ${userProfile.totalScore}")
        sb.appendLine()
        sb.appendLine("[2] FINANCIAL BALANCE & CASHFLOW SUMMARY")
        sb.appendLine("--------------------------------------------------------------------------------")
        sb.appendLine("Current Balance    : ${String.format(Locale.US, "%,.2f", userProfile.balance)} BDT / Chips")
        sb.appendLine("Total Approved Dep : ${String.format(Locale.US, "%,.2f", totalDeposited)} BDT (${approvedDeposits.size} Transactions)")
        sb.appendLine("Total Approved W/D : ${String.format(Locale.US, "%,.2f", totalWithdrawn)} BDT (${approvedWithdraws.size} Transactions)")
        sb.appendLine("Pending Withdraws  : ${String.format(Locale.US, "%,.2f", withdraws.filter { it.status == "PENDING" }.sumOf { it.amount })} BDT")
        sb.appendLine("Admin Cashflow Net : ${if (netHouseCashflow >= 0) "+" else ""}${String.format(Locale.US, "%,.2f", netHouseCashflow)} BDT (Admin Surplus)")
        sb.appendLine()
        sb.appendLine("[3] GAMEPLAY TURNOVER & HOUSE PROFIT RETENTION")
        sb.appendLine("--------------------------------------------------------------------------------")
        sb.appendLine("Total Bets Placed  : ${String.format(Locale.US, "%,.2f", totalBets)} BDT")
        sb.appendLine("Total Payouts Won  : ${String.format(Locale.US, "%,.2f", totalWins)} BDT")
        sb.appendLine("House Net Win      : ${String.format(Locale.US, "%,.2f", houseEdgeAmount)} BDT")
        sb.appendLine("House Margin (RTP) : ${String.format(Locale.US, "%.2f", houseMarginPercent)}% Edge (Safe House Margin)")
        sb.appendLine("Total House Profit : ${String.format(Locale.US, "%,.2f", netHouseCashflow.coerceAtLeast(0.0) + houseEdgeAmount)} BDT (Guaranteed Admin Profit)")
        sb.appendLine()
        sb.appendLine("[4] REGISTERED USER ACCOUNTS (1 PHONE = 1 DEVICE AUDIT)")
        sb.appendLine("--------------------------------------------------------------------------------")
        if (registeredAccounts.isEmpty()) {
            sb.appendLine("No external registered accounts found.")
        } else {
            sb.appendLine(String.format(Locale.US, "%-14s | %-16s | %-18s | %-20s | %s", "Phone/User ID", "Player Name", "Device Model", "Registration Date", "Status"))
            sb.appendLine("--------------------------------------------------------------------------------")
            registeredAccounts.forEach { acc ->
                sb.appendLine(
                    String.format(
                        Locale.US,
                        "%-14s | %-16s | %-18s | %-20s | %s",
                        acc.userPhone,
                        acc.playerName.take(16),
                        acc.deviceModel.take(18),
                        dateFormat.format(Date(acc.registeredAt)),
                        acc.status
                    )
                )
            }
        }
        sb.appendLine()
        sb.appendLine("[5] TRANSACTION LEDGER: DEPOSIT RECORDS")
        sb.appendLine("--------------------------------------------------------------------------------")
        if (deposits.isEmpty()) {
            sb.appendLine("No deposit records found.")
        } else {
            sb.appendLine(String.format(Locale.US, "%-19s | %-12s | %-8s | %-14s | %s", "Date/Time", "Amount", "Method", "Status", "TrxID"))
            sb.appendLine("--------------------------------------------------------------------------------")
            deposits.forEach { dep ->
                val dateStr = dateFormat.format(Date(dep.submittedAt))
                sb.appendLine(
                    String.format(
                        Locale.US,
                        "%-19s | %-12s | %-8s | %-14s | %s",
                        dateStr,
                        String.format(Locale.US, "%,.0f BDT", dep.amount),
                        dep.method,
                        dep.status,
                        dep.trxId
                    )
                )
            }
        }
        sb.appendLine()
        sb.appendLine("[6] TRANSACTION LEDGER: WITHDRAWAL RECORDS")
        sb.appendLine("--------------------------------------------------------------------------------")
        if (withdraws.isEmpty()) {
            sb.appendLine("No withdrawal records found.")
        } else {
            sb.appendLine(String.format(Locale.US, "%-19s | %-12s | %-8s | %-14s | %s", "Date/Time", "Amount", "Method", "Status", "Details"))
            sb.appendLine("--------------------------------------------------------------------------------")
            withdraws.forEach { wd ->
                val dateStr = dateFormat.format(Date(wd.submittedAt))
                sb.appendLine(
                    String.format(
                        Locale.US,
                        "%-19s | %-12s | %-8s | %-14s | %s",
                        dateStr,
                        String.format(Locale.US, "%,.0f BDT", wd.amount),
                        wd.method,
                        wd.status,
                        wd.rejectionReason ?: "Normal Request"
                    )
                )
            }
        }
        sb.appendLine()
        sb.appendLine("================================================================================")
        sb.appendLine("       CONFIDENTIAL AUDIT DOCUMENT - FOR AUTHORIZED ADMIN EYES ONLY             ")
        sb.appendLine("================================================================================")

        return sb.toString()
    }

    fun generateCsvStatement(
        userProfile: UserProfileEntity,
        deposits: List<DepositRequestEntity>,
        withdraws: List<WithdrawRequestEntity>,
        registeredAccounts: List<RegisteredAccountEntity> = emptyList()
    ): String {
        val sb = StringBuilder()
        sb.appendLine("RecordType,Identifier,DateTime,Phone,PlayerName,Amount,Method,Status,ExtraInfo")
        registeredAccounts.forEach { acc ->
            sb.appendLine("ACCOUNT,\"${acc.deviceId}\",${dateFormat.format(Date(acc.registeredAt))},\"${acc.userPhone}\",\"${acc.playerName}\",0,\"NONE\",\"${acc.status}\",\"${acc.deviceModel}\"")
        }
        deposits.forEach { d ->
            sb.appendLine("DEPOSIT,${d.id},${dateFormat.format(Date(d.submittedAt))},\"${d.userPhone}\",\"${userProfile.playerName}\",${d.amount},${d.method},${d.status},\"${d.trxId}\"")
        }
        withdraws.forEach { w ->
            sb.appendLine("WITHDRAW,${w.id},${dateFormat.format(Date(w.submittedAt))},\"${w.userPhone}\",\"${userProfile.playerName}\",${w.amount},${w.method},${w.status},\"${w.rejectionReason ?: ""}\"")
        }
        return sb.toString()
    }

    fun exportStatementFile(
        context: Context,
        profile: UserProfileEntity,
        registeredAccounts: List<RegisteredAccountEntity>,
        deposits: List<DepositRequestEntity>,
        withdraws: List<WithdrawRequestEntity>,
        today: DailyProgressEntity?
    ): File {
        val text = generateAuditStatement(profile, registeredAccounts, deposits, withdraws, today)
        val file = saveStatementToFile(context, text, isCsv = false)
        return file ?: throw IllegalStateException("Failed to create statement file")
    }

    fun exportCsvStatementFile(
        context: Context,
        profile: UserProfileEntity,
        registeredAccounts: List<RegisteredAccountEntity>,
        deposits: List<DepositRequestEntity>,
        withdraws: List<WithdrawRequestEntity>
    ): File {
        val csv = generateCsvStatement(profile, deposits, withdraws, registeredAccounts)
        val file = saveStatementToFile(context, csv, isCsv = true)
        return file ?: throw IllegalStateException("Failed to create CSV file")
    }

    fun saveStatementToFile(context: Context, content: String, isCsv: Boolean = false): File? {
        return try {
            val extension = if (isCsv) "csv" else "txt"
            val time = fileDateSuffix.format(Date())
            val fileName = "User_Statement_$time.$extension"

            val exportDir = File(context.filesDir, "statements").apply { mkdirs() }
            val file = File(exportDir, fileName)
            file.writeText(content)
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun exportSubAdminsCsvFile(
        context: Context,
        subAdmins: List<SubAdminEntity>,
        accounts: List<RegisteredAccountEntity>,
        deposits: List<DepositRequestEntity>,
        withdraws: List<WithdrawRequestEntity>
    ): File {
        val sb = StringBuilder()
        sb.appendLine("SubAdminId,Name,Phone,ReferralCode,ReferralLink,CommissionRate,Status,TotalPlayers,TotalApprovedDeposit,TotalApprovedWithdraw,CreatedDate")
        subAdmins.forEach { sa ->
            val myPlayers = accounts.filter { it.referredBySubAdmin.equals(sa.referralCode, ignoreCase = true) }
            val myPlayerPhones = myPlayers.map { it.userPhone }.toSet()
            val totalDep = deposits.filter { myPlayerPhones.contains(it.userPhone) && it.status == "APPROVED" }.sumOf { it.amount }
            val totalWd = withdraws.filter { myPlayerPhones.contains(it.userPhone) && it.status == "APPROVED" }.sumOf { it.amount }
            val dateStr = dateFormat.format(Date(sa.createdAt))

            sb.appendLine("\"${sa.id}\",\"${sa.name}\",\"${sa.phone}\",\"${sa.referralCode}\",\"${sa.referralLink}\",\"${sa.commissionPercent}%\",\"${sa.status}\",${myPlayers.size},${totalDep},${totalWd},\"$dateStr\"")
        }

        val time = fileDateSuffix.format(Date())
        val exportDir = File(context.filesDir, "statements").apply { mkdirs() }
        val file = File(exportDir, "SubAdmins_Report_$time.csv")
        file.writeText(sb.toString())
        return file
    }

    fun exportAllPlayersWithSubAdminCsvFile(
        context: Context,
        accounts: List<RegisteredAccountEntity>,
        subAdmins: List<SubAdminEntity>,
        deposits: List<DepositRequestEntity>,
        withdraws: List<WithdrawRequestEntity>
    ): File {
        val subAdminMap = subAdmins.associateBy { it.referralCode.uppercase() }
        val sb = StringBuilder()
        sb.appendLine("Phone,PlayerName,Balance,SubAdminCode,SubAdminName,DeviceModel,Status,LifetimeDeposit,LifetimeWithdraw,LifetimeBet,RegisteredDate")
        accounts.forEach { acc ->
            val sa = subAdminMap[acc.referredBySubAdmin.uppercase()]
            val saName = sa?.name ?: if (acc.referredBySubAdmin.isNotBlank()) acc.referredBySubAdmin else "Direct/None"
            val regDate = dateFormat.format(Date(acc.registeredAt))
            sb.appendLine("\"${acc.userPhone}\",\"${acc.playerName}\",${acc.balance},\"${acc.referredBySubAdmin}\",\"$saName\",\"${acc.deviceModel}\",\"${acc.status}\",${acc.lifetimeDeposit},${acc.lifetimeWithdraw},${acc.lifetimeBet},\"$regDate\"")
        }

        val time = fileDateSuffix.format(Date())
        val exportDir = File(context.filesDir, "statements").apply { mkdirs() }
        val file = File(exportDir, "All_Players_Report_$time.csv")
        file.writeText(sb.toString())
        return file
    }

    fun exportMasterFullCasinoAuditFile(
        context: Context,
        profile: UserProfileEntity,
        subAdmins: List<SubAdminEntity>,
        accounts: List<RegisteredAccountEntity>,
        deposits: List<DepositRequestEntity>,
        withdraws: List<WithdrawRequestEntity>,
        today: DailyProgressEntity?
    ): File {
        val baseAudit = generateAuditStatement(profile, accounts, deposits, withdraws, today)
        val sb = StringBuilder()
        sb.append(baseAudit)
        sb.appendLine()
        sb.appendLine("[7] SUB-ADMINS & AGENTS REFERRAL AUDIT")
        sb.appendLine("--------------------------------------------------------------------------------")
        if (subAdmins.isEmpty()) {
            sb.appendLine("No Sub-Admins configured in system.")
        } else {
            sb.appendLine(String.format(Locale.US, "%-8s | %-16s | %-12s | %-10s | %-8s | %-12s", "ID", "Name", "Phone", "Ref Code", "Players", "Turnover BDT"))
            sb.appendLine("--------------------------------------------------------------------------------")
            subAdmins.forEach { sa ->
                val myPlayers = accounts.filter { it.referredBySubAdmin.equals(sa.referralCode, ignoreCase = true) }
                val myPlayerPhones = myPlayers.map { it.userPhone }.toSet()
                val totalDep = deposits.filter { myPlayerPhones.contains(it.userPhone) && it.status == "APPROVED" }.sumOf { it.amount }
                sb.appendLine(
                    String.format(
                        Locale.US,
                        "%-8s | %-16s | %-12s | %-10s | %-8d | %-12.0f",
                        sa.id,
                        sa.name.take(16),
                        sa.phone,
                        sa.referralCode,
                        myPlayers.size,
                        totalDep
                    )
                )
            }
        }
        sb.appendLine()
        sb.appendLine("================================================================================")
        sb.appendLine("                    END OF MASTER CASINO AUDIT FILE                             ")
        sb.appendLine("================================================================================")

        val time = fileDateSuffix.format(Date())
        val exportDir = File(context.filesDir, "statements").apply { mkdirs() }
        val file = File(exportDir, "Master_Casino_Audit_$time.txt")
        file.writeText(sb.toString())
        return file
    }

    fun shareStatementFile(context: Context, file: File, title: String = "ক্যাসিনো অডিট ও ডাটা ফাইল ডাউনলোড / শেয়ার করুন") {
        try {
            val authority = "${context.packageName}.fileprovider"
            val uri = FileProvider.getUriForFile(context, authority, file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = if (file.name.endsWith(".csv")) "text/csv" else "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Royal Casino Report - ${file.name}")
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, title))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
