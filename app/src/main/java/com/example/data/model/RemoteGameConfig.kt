package com.example.data.model

import com.squareup.moshi.JsonClass
import org.json.JSONArray
import org.json.JSONObject

@JsonClass(generateAdapter = true)
data class RemoteCustomGame(
    val id: String = "",
    val name: String = "",
    val provider: String = "PG Soft",
    val url: String = "",
    val iconUrl: String = "",
    val category: String = "Slots",
    val isActive: Boolean = true
) {
    fun toJsonObject(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("name", name)
            put("provider", provider)
            put("url", url)
            put("iconUrl", iconUrl)
            put("category", category)
            put("isActive", isActive)
        }
    }

    companion object {
        fun fromJsonObject(obj: JSONObject): RemoteCustomGame {
            return RemoteCustomGame(
                id = obj.optString("id", ""),
                name = obj.optString("name", "Game"),
                provider = obj.optString("provider", "PG Soft"),
                url = obj.optString("url", ""),
                iconUrl = obj.optString("iconUrl", ""),
                category = obj.optString("category", "Slots"),
                isActive = obj.optBoolean("isActive", true)
            )
        }
    }
}

@JsonClass(generateAdapter = true)
data class CloudPlayerRecord(
    val phone: String = "",
    val name: String = "",
    val balance: Double = 0.0,
    val level: Int = 1,
    val isBlocked: Boolean = false,
    val totalDeposit: Double = 0.0,
    val totalWithdraw: Double = 0.0,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    fun toJsonObject(): JSONObject = JSONObject().apply {
        put("phone", phone)
        put("name", name)
        put("balance", balance)
        put("level", level)
        put("isBlocked", isBlocked)
        put("totalDeposit", totalDeposit)
        put("totalWithdraw", totalWithdraw)
        put("lastUpdated", lastUpdated)
    }

    companion object {
        fun fromJsonObject(obj: JSONObject): CloudPlayerRecord {
            return CloudPlayerRecord(
                phone = obj.optString("phone", ""),
                name = obj.optString("name", "Player"),
                balance = obj.optDouble("balance", 0.0).coerceAtLeast(0.0),
                level = obj.optInt("level", 1).coerceIn(1, 100),
                isBlocked = obj.optBoolean("isBlocked", false),
                totalDeposit = obj.optDouble("totalDeposit", 0.0),
                totalWithdraw = obj.optDouble("totalWithdraw", 0.0),
                lastUpdated = obj.optLong("lastUpdated", System.currentTimeMillis())
            )
        }
    }
}

@JsonClass(generateAdapter = true)
data class CloudDepositRecord(
    val id: Long = System.currentTimeMillis(),
    val phone: String = "",
    val amount: Double = 0.0,
    val method: String = "bKash",
    val trxId: String = "",
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val submittedAt: Long = System.currentTimeMillis(),
    val adminNote: String = ""
) {
    fun toJsonObject(): JSONObject = JSONObject().apply {
        put("id", id)
        put("phone", phone)
        put("amount", amount)
        put("method", method)
        put("trxId", trxId)
        put("status", status)
        put("submittedAt", submittedAt)
        put("adminNote", adminNote)
    }

    companion object {
        fun fromJsonObject(obj: JSONObject): CloudDepositRecord {
            return CloudDepositRecord(
                id = obj.optLong("id", System.currentTimeMillis()),
                phone = obj.optString("phone", ""),
                amount = obj.optDouble("amount", 0.0),
                method = obj.optString("method", "bKash"),
                trxId = obj.optString("trxId", ""),
                status = obj.optString("status", "PENDING"),
                submittedAt = obj.optLong("submittedAt", System.currentTimeMillis()),
                adminNote = obj.optString("adminNote", "")
            )
        }
    }
}

@JsonClass(generateAdapter = true)
data class CloudWithdrawRecord(
    val id: Long = System.currentTimeMillis(),
    val phone: String = "",
    val amount: Double = 0.0,
    val method: String = "Nagad",
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val submittedAt: Long = System.currentTimeMillis(),
    val adminNote: String = ""
) {
    fun toJsonObject(): JSONObject = JSONObject().apply {
        put("id", id)
        put("phone", phone)
        put("amount", amount)
        put("method", method)
        put("status", status)
        put("submittedAt", submittedAt)
        put("adminNote", adminNote)
    }

    companion object {
        fun fromJsonObject(obj: JSONObject): CloudWithdrawRecord {
            return CloudWithdrawRecord(
                id = obj.optLong("id", System.currentTimeMillis()),
                phone = obj.optString("phone", ""),
                amount = obj.optDouble("amount", 0.0),
                method = obj.optString("method", "Nagad"),
                status = obj.optString("status", "PENDING"),
                submittedAt = obj.optLong("submittedAt", System.currentTimeMillis()),
                adminNote = obj.optString("adminNote", "")
            )
        }
    }
}

@JsonClass(generateAdapter = true)
data class RemoteGameConfig(
    // System & Control
    val version: Int = 1,
    val lastUpdated: Long = System.currentTimeMillis(),
    val appTitle: String = "King Game",
    val appSubtitle: String = "ROYAL CASINO",
    val appLogoUrl: String = "",
    val appLogoPreset: String = "KING_CROWN",
    val profileBannerUrl: String = "",
    val defaultPlayerAvatar: String = "👑",
    val supportContact: String = "01303347372",
    val welcomeBonusAmount: Double = 50.0,
    val clientSyncToken: String = "",
    val maintenanceMode: Boolean = false,
    val maintenanceMessage: String = "সার্ভার রক্ষণাবেক্ষণ কাজের জন্য সাময়িক বন্ধ আছে। কিছুক্ষণ পর আবার চেষ্টা করুন।",
    val announcementNotice: String = "🎉 কিং গেমে স্বাগতম! প্রতিদিনের ডেইলি মিশন ও বোনাস উপভোগ করুন।",
    val minAppVersion: Int = 1,
    val latestApkDownloadUrl: String = "",

    // Slot Game Math & RTP (Percent 0-100)
    val globalWinRatio: Int = 30,
    val superAceWinRatio: Int = 30,
    val deadMansBulletWinRatio: Int = 30,
    val gatesOfOlympusWinRatio: Int = 30,
    val sweetBonanzaWinRatio: Int = 30,
    val maxCascadeSteps: Int = 8,
    val goldenWildChance: Float = 0.25f,
    val bigJokerChance: Float = 0.35f,
    val rtpMultiplierBonus: Float = 1.0f,

    // Financial & Payment Gateways
    val nagadWallet: String = "01303347372",
    val bkashWallet: String = "01303347372",
    val rocketWallet: String = "01303347372",
    val upayWallet: String = "01303347372",
    val minDeposit: Double = 100.0,
    val maxDeposit: Double = 20000.0,
    val minWithdraw: Double = 500.0,
    val maxWithdraw: Double = 25000.0,

    // Referral & Agent System
    val agentCommissionPercent: Double = 5.0,

    // Web Portal & Obfuscation
    val websitePortalUrl: String = "https://royalslots-bd.com",
    val isWebAppModeEnabled: Boolean = false,
    val customGames: List<RemoteCustomGame> = emptyList(),

    // Cloud Data Sync Across Devices
    val cloudPlayers: List<CloudPlayerRecord> = emptyList(),
    val cloudDeposits: List<CloudDepositRecord> = emptyList(),
    val cloudWithdraws: List<CloudWithdrawRecord> = emptyList()
) {
    fun toJsonString(indent: Int = 2): String {
        val root = JSONObject().apply {
            put("version", version)
            put("lastUpdated", lastUpdated)
            put("appTitle", appTitle)
            put("appSubtitle", appSubtitle)
            put("appLogoUrl", appLogoUrl)
            put("appLogoPreset", appLogoPreset)
            put("profileBannerUrl", profileBannerUrl)
            put("defaultPlayerAvatar", defaultPlayerAvatar)
            put("supportContact", supportContact)
            put("welcomeBonusAmount", welcomeBonusAmount)
            put("clientSyncToken", clientSyncToken)
            put("maintenanceMode", maintenanceMode)
            put("maintenanceMessage", maintenanceMessage)
            put("announcementNotice", announcementNotice)
            put("minAppVersion", minAppVersion)
            put("latestApkDownloadUrl", latestApkDownloadUrl)

            put("globalWinRatio", globalWinRatio)
            put("superAceWinRatio", superAceWinRatio)
            put("deadMansBulletWinRatio", deadMansBulletWinRatio)
            put("gatesOfOlympusWinRatio", gatesOfOlympusWinRatio)
            put("sweetBonanzaWinRatio", sweetBonanzaWinRatio)
            put("maxCascadeSteps", maxCascadeSteps)
            put("goldenWildChance", goldenWildChance.toDouble())
            put("bigJokerChance", bigJokerChance.toDouble())
            put("rtpMultiplierBonus", rtpMultiplierBonus.toDouble())

            put("nagadWallet", nagadWallet)
            put("bkashWallet", bkashWallet)
            put("rocketWallet", rocketWallet)
            put("upayWallet", upayWallet)
            put("minDeposit", minDeposit)
            put("maxDeposit", maxDeposit)
            put("minWithdraw", minWithdraw)
            put("maxWithdraw", maxWithdraw)

            put("agentCommissionPercent", agentCommissionPercent)
            put("websitePortalUrl", websitePortalUrl)
            put("isWebAppModeEnabled", isWebAppModeEnabled)

            val gamesArray = JSONArray()
            customGames.forEach { game ->
                gamesArray.put(game.toJsonObject())
            }
            put("customGames", gamesArray)

            val playersArray = JSONArray()
            cloudPlayers.forEach { player ->
                playersArray.put(player.toJsonObject())
            }
            put("cloudPlayers", playersArray)

            val depositsArray = JSONArray()
            cloudDeposits.forEach { deposit ->
                depositsArray.put(deposit.toJsonObject())
            }
            put("cloudDeposits", depositsArray)

            val withdrawsArray = JSONArray()
            cloudWithdraws.forEach { withdraw ->
                withdrawsArray.put(withdraw.toJsonObject())
            }
            put("cloudWithdraws", withdrawsArray)
        }
        return if (indent > 0) root.toString(indent) else root.toString()
    }

    companion object {
        fun fromJsonString(jsonStr: String): RemoteGameConfig {
            val root = JSONObject(jsonStr)
            val gamesList = mutableListOf<RemoteCustomGame>()
            val gamesArray = root.optJSONArray("customGames")
            if (gamesArray != null) {
                for (i in 0 until gamesArray.length()) {
                    val gObj = gamesArray.optJSONObject(i)
                    if (gObj != null) {
                        gamesList.add(RemoteCustomGame.fromJsonObject(gObj))
                    }
                }
            }

            val playersList = mutableListOf<CloudPlayerRecord>()
            val playersArray = root.optJSONArray("cloudPlayers")
            if (playersArray != null) {
                for (i in 0 until playersArray.length()) {
                    val pObj = playersArray.optJSONObject(i)
                    if (pObj != null) {
                        playersList.add(CloudPlayerRecord.fromJsonObject(pObj))
                    }
                }
            }

            val depositsList = mutableListOf<CloudDepositRecord>()
            val depositsArray = root.optJSONArray("cloudDeposits")
            if (depositsArray != null) {
                for (i in 0 until depositsArray.length()) {
                    val dObj = depositsArray.optJSONObject(i)
                    if (dObj != null) {
                        depositsList.add(CloudDepositRecord.fromJsonObject(dObj))
                    }
                }
            }

            val withdrawsList = mutableListOf<CloudWithdrawRecord>()
            val withdrawsArray = root.optJSONArray("cloudWithdraws")
            if (withdrawsArray != null) {
                for (i in 0 until withdrawsArray.length()) {
                    val wObj = withdrawsArray.optJSONObject(i)
                    if (wObj != null) {
                        withdrawsList.add(CloudWithdrawRecord.fromJsonObject(wObj))
                    }
                }
            }

            val rawTitle = root.optString("appTitle", "King Game")
            val sanitizedTitle = com.example.util.SecurityShield.sanitizePlainText(rawTitle, 40).ifBlank { "King Game" }

            val rawSubtitle = root.optString("appSubtitle", "ROYAL CASINO")
            val sanitizedSubtitle = com.example.util.SecurityShield.sanitizePlainText(rawSubtitle, 40).ifBlank { "ROYAL CASINO" }

            val rawMaintMsg = root.optString("maintenanceMessage", "সার্ভার রক্ষণাবেক্ষণ কাজের জন্য সাময়িক বন্ধ আছে। কিছুক্ষণ পর আবার চেষ্টা করুন।")
            val sanitizedMaintMsg = com.example.util.SecurityShield.sanitizePlainText(rawMaintMsg, 250)

            val rawNotice = root.optString("announcementNotice", "🎉 কিং গেমে স্বাগতম! প্রতিদিনের ডেইলি মিশন ও বোনাস উপভোগ করুন।")
            val sanitizedNotice = com.example.util.SecurityShield.sanitizePlainText(rawNotice, 350)

            val rawNagad = com.example.util.SecurityShield.sanitizePhoneNumber(root.optString("nagadWallet", "01303347372"))
            val rawBkash = com.example.util.SecurityShield.sanitizePhoneNumber(root.optString("bkashWallet", "01303347372"))
            val rawRocket = com.example.util.SecurityShield.sanitizePhoneNumber(root.optString("rocketWallet", "01303347372"))
            val rawUpay = com.example.util.SecurityShield.sanitizePhoneNumber(root.optString("upayWallet", "01303347372"))

            val minDep = root.optDouble("minDeposit", 100.0).coerceIn(10.0, 50000.0)
            val maxDep = root.optDouble("maxDeposit", 20000.0).coerceIn(minDep, 200000.0)
            val minWith = root.optDouble("minWithdraw", 500.0).coerceIn(50.0, 50000.0)
            val maxWith = root.optDouble("maxWithdraw", 25000.0).coerceIn(minWith, 200000.0)

            val webUrl = root.optString("websitePortalUrl", "https://royalslots-bd.com").trim()
            val validWebUrl = if (com.example.util.SecurityShield.validateSecureUrl(webUrl)) webUrl else "https://royalslots-bd.com"

            return RemoteGameConfig(
                version = root.optInt("version", 1).coerceAtLeast(1),
                lastUpdated = root.optLong("lastUpdated", System.currentTimeMillis()),
                appTitle = sanitizedTitle,
                appSubtitle = sanitizedSubtitle,
                appLogoUrl = root.optString("appLogoUrl", "").trim(),
                appLogoPreset = root.optString("appLogoPreset", "KING_CROWN").trim(),
                profileBannerUrl = root.optString("profileBannerUrl", "").trim(),
                defaultPlayerAvatar = root.optString("defaultPlayerAvatar", "👑").trim(),
                supportContact = root.optString("supportContact", "01303347372").trim(),
                welcomeBonusAmount = root.optDouble("welcomeBonusAmount", 50.0).coerceIn(0.0, 10000.0),
                clientSyncToken = root.optString("clientSyncToken", "").trim(),
                maintenanceMode = root.optBoolean("maintenanceMode", false),
                maintenanceMessage = sanitizedMaintMsg,
                announcementNotice = sanitizedNotice,
                minAppVersion = root.optInt("minAppVersion", 1),
                latestApkDownloadUrl = root.optString("latestApkDownloadUrl", "").take(300),

                globalWinRatio = root.optInt("globalWinRatio", 30).coerceIn(0, 100),
                superAceWinRatio = root.optInt("superAceWinRatio", 30).coerceIn(0, 100),
                deadMansBulletWinRatio = root.optInt("deadMansBulletWinRatio", 30).coerceIn(0, 100),
                gatesOfOlympusWinRatio = root.optInt("gatesOfOlympusWinRatio", 30).coerceIn(0, 100),
                sweetBonanzaWinRatio = root.optInt("sweetBonanzaWinRatio", 30).coerceIn(0, 100),
                maxCascadeSteps = root.optInt("maxCascadeSteps", 8).coerceIn(1, 20),
                goldenWildChance = root.optDouble("goldenWildChance", 0.25).toFloat().coerceIn(0.0f, 1.0f),
                bigJokerChance = root.optDouble("bigJokerChance", 0.35).toFloat().coerceIn(0.0f, 1.0f),
                rtpMultiplierBonus = root.optDouble("rtpMultiplierBonus", 1.0).toFloat().coerceIn(0.1f, 10.0f),

                nagadWallet = rawNagad.ifBlank { "01303347372" },
                bkashWallet = rawBkash.ifBlank { "01303347372" },
                rocketWallet = rawRocket.ifBlank { "01303347372" },
                upayWallet = rawUpay.ifBlank { "01303347372" },
                minDeposit = minDep,
                maxDeposit = maxDep,
                minWithdraw = minWith,
                maxWithdraw = maxWith,

                agentCommissionPercent = root.optDouble("agentCommissionPercent", 5.0).coerceIn(0.0, 50.0),
                websitePortalUrl = validWebUrl,
                isWebAppModeEnabled = root.optBoolean("isWebAppModeEnabled", false),
                customGames = gamesList,
                cloudPlayers = playersList,
                cloudDeposits = depositsList,
                cloudWithdraws = withdrawsList
            )
        }
    }
}
