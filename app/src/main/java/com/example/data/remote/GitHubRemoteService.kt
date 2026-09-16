package com.example.data.remote

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import com.example.data.db.AdminWalletConfigEntity
import com.example.data.model.GameAdminConfig
import com.example.data.model.RemoteGameConfig
import com.example.data.repository.GameAdminRepository
import com.example.data.repository.GameRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

data class GitHubCredentials(
    val owner: String = "superace-admin",
    val repo: String = "game-config",
    val branch: String = "main",
    val filePath: String = "super_ace_config.json",
    val token: String = "",
    val customRawUrl: String = ""
) {
    val effectiveRawUrl: String
        get() = if (customRawUrl.isNotBlank()) {
            customRawUrl.trim()
        } else {
            "https://raw.githubusercontent.com/${owner.trim()}/${repo.trim()}/${branch.trim()}/${filePath.trim()}"
        }
}

object GitHubRemoteService {
    private const val TAG = "GitHubRemoteService"
    private const val PREFS_NAME = "super_ace_github_prefs"
    private const val KEY_OWNER = "github_owner"
    private const val KEY_REPO = "github_repo"
    private const val KEY_BRANCH = "github_branch"
    private const val KEY_FILE_PATH = "github_file_path"
    private const val KEY_TOKEN = "github_token"
    private const val KEY_CUSTOM_RAW_URL = "github_custom_raw_url"
    private const val KEY_CACHED_CONFIG_JSON = "cached_config_json"
    private const val KEY_LAST_SYNC_TIME = "last_sync_time"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun loadCredentials(context: Context): GitHubCredentials {
        val prefs = getPrefs(context)
        return GitHubCredentials(
            owner = prefs.getString(KEY_OWNER, "hekak") ?: "hekak",
            repo = prefs.getString(KEY_REPO, "king-game-system") ?: "king-game-system",
            branch = prefs.getString(KEY_BRANCH, "main") ?: "main",
            filePath = prefs.getString(KEY_FILE_PATH, "super_ace_config.json") ?: "super_ace_config.json",
            token = prefs.getString(KEY_TOKEN, "") ?: "",
            customRawUrl = prefs.getString(KEY_CUSTOM_RAW_URL, "") ?: ""
        )
    }

    fun saveCredentials(context: Context, creds: GitHubCredentials) {
        getPrefs(context).edit().apply {
            putString(KEY_OWNER, creds.owner.trim())
            putString(KEY_REPO, creds.repo.trim())
            putString(KEY_BRANCH, creds.branch.trim())
            putString(KEY_FILE_PATH, creds.filePath.trim())
            putString(KEY_TOKEN, creds.token.trim())
            putString(KEY_CUSTOM_RAW_URL, creds.customRawUrl.trim())
            apply()
        }
    }

    fun getCachedConfig(context: Context): RemoteGameConfig {
        val json = getPrefs(context).getString(KEY_CACHED_CONFIG_JSON, null)
        if (!json.isNullOrBlank()) {
            try {
                return RemoteGameConfig.fromJsonString(json)
            } catch (e: Exception) {
                Log.w(TAG, "Cached config parse error: ${e.message}")
            }
        }
        // Fallback to bundled asset configuration so any new device starts with King Game setup
        try {
            context.assets.open("default_remote_config.json").bufferedReader().use { reader ->
                val assetJson = reader.readText()
                if (assetJson.isNotBlank()) {
                    val assetCfg = RemoteGameConfig.fromJsonString(assetJson)
                    return assetCfg
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Asset default_remote_config.json read error: ${e.message}")
        }
        return RemoteGameConfig()
    }

    fun getLastSyncTime(context: Context): Long {
        return getPrefs(context).getLong(KEY_LAST_SYNC_TIME, 0L)
    }

    private fun cacheConfig(context: Context, config: RemoteGameConfig) {
        getPrefs(context).edit().apply {
            putString(KEY_CACHED_CONFIG_JSON, config.toJsonString(indent = 0))
            putLong(KEY_LAST_SYNC_TIME, System.currentTimeMillis())
            apply()
        }
    }

    suspend fun fetchRemoteConfig(context: Context, rawUrlOverride: String? = null): Result<RemoteGameConfig> {
        return withContext(Dispatchers.IO) {
            val creds = loadCredentials(context)
            val primaryUrl = (rawUrlOverride?.ifBlank { null } ?: creds.effectiveRawUrl).trim()
            val standardAutoUrl = "https://raw.githubusercontent.com/${creds.owner.trim()}/${creds.repo.trim()}/${creds.branch.trim()}/${creds.filePath.trim()}"
            val gitHubApiUrl = "https://api.github.com/repos/${creds.owner.trim()}/${creds.repo.trim()}/contents/${creds.filePath.trim()}?ref=${creds.branch.trim()}"

            fun tryFetchUrl(targetUrl: String, isApiRaw: Boolean = false): Pair<Boolean, Pair<RemoteGameConfig?, String?>> {
                try {
                    val builder = Request.Builder()
                        .url(targetUrl)
                        .header("Cache-Control", "no-cache")
                        .header("User-Agent", "SuperAce-Android-Client")

                    if (isApiRaw) {
                        builder.header("Accept", "application/vnd.github.raw+json")
                    }
                    if (creds.token.isNotBlank()) {
                        builder.header("Authorization", "Bearer ${creds.token.trim()}")
                    }

                    httpClient.newCall(builder.build()).execute().use { response ->
                        if (!response.isSuccessful) {
                            return Pair(false, Pair(null, "HTTP ${response.code}: ${response.message} ($targetUrl)"))
                        }
                        val bodyString = response.body?.string()
                            ?: return Pair(false, Pair(null, "Empty response from $targetUrl"))

                        val config = RemoteGameConfig.fromJsonString(bodyString)
                        return Pair(true, Pair(config, null))
                    }
                } catch (e: Exception) {
                    return Pair(false, Pair(null, "${e.message} ($targetUrl)"))
                }
            }

            // 1. Try with primary URL
            val firstTry = tryFetchUrl(primaryUrl)
            if (firstTry.first && firstTry.second.first != null) {
                val cfg = firstTry.second.first!!
                cacheConfig(context, cfg)
                return@withContext Result.success(cfg)
            }

            // 2. If primary was custom and failed (e.g. 404 from old custom URL), try standard auto URL
            if (primaryUrl != standardAutoUrl) {
                Log.w(TAG, "Custom URL failed: ${firstTry.second.second}, falling back to standard repo URL: $standardAutoUrl")
                val fallbackTry = tryFetchUrl(standardAutoUrl)
                if (fallbackTry.first && fallbackTry.second.first != null) {
                    val cfg = fallbackTry.second.first!!
                    cacheConfig(context, cfg)
                    saveCredentials(context, creds.copy(customRawUrl = ""))
                    return@withContext Result.success(cfg)
                }
            }

            // 3. Try GitHub Contents API with Bearer token & Accept: application/vnd.github.raw+json
            val apiTry = tryFetchUrl(gitHubApiUrl, isApiRaw = true)
            if (apiTry.first && apiTry.second.first != null) {
                val cfg = apiTry.second.first!!
                cacheConfig(context, cfg)
                return@withContext Result.success(cfg)
            }

            // If GitHub fetch failed, analyze cause for clear user-facing feedback
            val rawError = firstTry.second.second ?: "Failed to connect to GitHub"
            val userFriendlyMsg = if (rawError.contains("404")) {
                "গিটহাবে ফাইলটি পাওয়া যায়নি (HTTP 404)।\n" +
                        "কারণ: github.com/${creds.owner.trim()}/${creds.repo.trim()} রিপোজিটরিটি এখনও তৈরি করা হয়নি অথবা এটি Private অবস্থায় আছে। প্লেয়ারদের জন্য রিপোজিটরি অবশ্যই **Public** হতে হবে।"
            } else if (rawError.contains("401")) {
                "গিটহাব টোকেন এরর (HTTP 401 Bad credentials)। আপনার PAT টোকেনটি সঠিক নয় বা মেয়াদোত্তীর্ণ।"
            } else {
                rawError
            }

            Log.w(TAG, "GitHub fetch failed: $userFriendlyMsg. Falling back to cached config.")
            Result.failure(IOException(userFriendlyMsg))
        }
    }

    suspend fun publishConfigToGitHub(
        context: Context,
        creds: GitHubCredentials,
        config: RemoteGameConfig
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            if (creds.token.isBlank()) {
                return@withContext Result.failure(
                    IllegalArgumentException("GitHub Personal Access Token (PAT) প্রয়োজন। দয়া করে টোকেন প্রদান করুন।")
                )
            }
            if (creds.owner.isBlank() || creds.repo.isBlank() || creds.filePath.isBlank()) {
                return@withContext Result.failure(
                    IllegalArgumentException("GitHub Owner, Repo ও File Path পূরণ করুন।")
                )
            }

            val apiUrl = "https://api.github.com/repos/${creds.owner.trim()}/${creds.repo.trim()}/contents/${creds.filePath.trim()}"

            try {
                // Step 1: Check if file already exists to get its sha
                var currentSha: String? = null
                val getRequest = Request.Builder()
                    .url(apiUrl + "?ref=" + creds.branch.trim())
                    .header("Accept", "application/vnd.github+json")
                    .header("Authorization", "Bearer ${creds.token.trim()}")
                    .header("User-Agent", "SuperAceAdminApp")
                    .build()

                httpClient.newCall(getRequest).execute().use { getResp ->
                    if (getResp.isSuccessful) {
                        val body = getResp.body?.string()
                        if (!body.isNullOrBlank()) {
                            val json = JSONObject(body)
                            currentSha = json.optString("sha", null)
                        }
                    }
                }

                // Step 2: Prepare updated config JSON & Base64 encode
                val updatedConfig = config.copy(
                    lastUpdated = System.currentTimeMillis()
                )
                val jsonString = updatedConfig.toJsonString(indent = 2)
                val base64Content = Base64.encodeToString(jsonString.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)

                val putPayload = JSONObject().apply {
                    put("message", "Update Super Ace game config from Admin Console [${System.currentTimeMillis()}]")
                    put("content", base64Content)
                    put("branch", creds.branch.trim())
                    if (!currentSha.isNullOrBlank()) {
                        put("sha", currentSha)
                    }
                }

                val putBody = putPayload.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val putRequest = Request.Builder()
                    .url(apiUrl)
                    .header("Accept", "application/vnd.github+json")
                    .header("Authorization", "Bearer ${creds.token.trim()}")
                    .header("User-Agent", "SuperAceAdminApp")
                    .put(putBody)
                    .build()

                httpClient.newCall(putRequest).execute().use { putResp ->
                    if (!putResp.isSuccessful) {
                        val errBody = putResp.body?.string() ?: ""
                        val customErr = when (putResp.code) {
                            401 -> "❌ টোকেন এরর (HTTP 401 Bad credentials)।\nআপনার দেওয়া Personal Access Token (PAT) ভুল বা মেয়াদ শেষ। দয়া করে GitHub Settings > Developer settings > Personal access tokens (classic) থেকে 'repo' অপশনে টিক দিয়ে নতুন টোকেন তৈরি করুন।"
                            404 -> "❌ রিপোজিটরি পাওয়া যায়নি (HTTP 404)।\nগিটহাবে '${creds.owner.trim()}/${creds.repo.trim()}' নামে কোনো রিপোজিটরি নেই। দয়া করে https://github.com/new তে গিয়ে '${creds.repo.trim()}' নামে একটি **Public** রিপোজিটরি তৈরি করুন ('Add a README file' এ টিক দিন)।"
                            409 -> "❌ কনফ্লিক্ট এরর (HTTP 409)। গিটহাবে ফাইলটি ইতোমধ্যে পরিবর্তিত হয়েছে। একবার 'গিটহাব সিঙ্ক' বাটনে চাপ দিয়ে আবার চেষ্টা করুন।"
                            else -> "GitHub API ত্রুটি (${putResp.code}): $errBody"
                        }
                        return@withContext Result.failure(IOException(customErr))
                    }

                    // Save credentials & local cache
                    saveCredentials(context, creds)
                    cacheConfig(context, updatedConfig)

                    Result.success("সফলভাবে গিটহাবে লাইভ পাবলিশ করা হয়েছে! সকল খেলোয়াড়দের অ্যাপে ডাটা কার্যকর হবে।")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error publishing to GitHub", e)
                Result.failure(e)
            }
        }
    }

    data class GitHubDiagnosticResult(
        val isSuccess: Boolean,
        val summaryTitle: String,
        val userFound: Boolean,
        val repoFound: Boolean,
        val repoIsPrivate: Boolean,
        val tokenValid: Boolean,
        val fileFound: Boolean,
        val details: List<String>,
        val actionSteps: List<String>
    )

    suspend fun diagnoseGitHub(context: Context, creds: GitHubCredentials): GitHubDiagnosticResult {
        return withContext(Dispatchers.IO) {
            val details = mutableListOf<String>()
            val steps = mutableListOf<String>()
            var userFound = false
            var repoFound = false
            var repoIsPrivate = false
            var tokenValid = false
            var fileFound = false

            // 1. Check Owner/User
            try {
                val userReq = Request.Builder()
                    .url("https://api.github.com/users/${creds.owner.trim()}")
                    .header("User-Agent", "SuperAceAdminApp")
                    .build()
                httpClient.newCall(userReq).execute().use { resp ->
                    if (resp.isSuccessful) {
                        userFound = true
                        details.add("✅ GitHub ইউজার '${creds.owner.trim()}' বিদ্যমান।")
                    } else {
                        details.add("❌ GitHub ইউজার '${creds.owner.trim()}' পাওয়া যায়নি (HTTP ${resp.code})।")
                        steps.add("GitHub এ আপনার সঠিক ইউজারনেম নিশ্চিত করুন।")
                    }
                }
            } catch (e: Exception) {
                details.add("⚠️ ইউজার চেক করার সময় নেটওয়ার্ক ত্রুটি: ${e.message}")
            }

            // 2. Check Token if provided
            if (creds.token.isNotBlank()) {
                try {
                    val authReq = Request.Builder()
                        .url("https://api.github.com/user")
                        .header("Authorization", "Bearer ${creds.token.trim()}")
                        .header("User-Agent", "SuperAceAdminApp")
                        .build()
                    httpClient.newCall(authReq).execute().use { resp ->
                        if (resp.isSuccessful) {
                            tokenValid = true
                            details.add("✅ Personal Access Token (PAT) বৈধ ও সক্রিয়।")
                        } else {
                            details.add("❌ PAT টোকেন ভুল বা মেয়াদোত্তীর্ণ (HTTP ${resp.code} Bad Credentials)!")
                            steps.add("GitHub > Settings > Developer settings > Personal access tokens (classic) থেকে 'repo' পারমিশন দিয়ে নতুন টোকেন নিন।")
                        }
                    }
                } catch (e: Exception) {
                    details.add("⚠️ টোকেন চেক ব্যর্থ: ${e.message}")
                }
            } else {
                details.add("ℹ️ কোনো PAT টোকেন দেওয়া হয়নি (শুধু পাবলিক রিড মোড)।")
            }

            // 3. Check Repo
            try {
                val repoBuilder = Request.Builder()
                    .url("https://api.github.com/repos/${creds.owner.trim()}/${creds.repo.trim()}")
                    .header("User-Agent", "SuperAceAdminApp")
                if (creds.token.isNotBlank() && tokenValid) {
                    repoBuilder.header("Authorization", "Bearer ${creds.token.trim()}")
                }
                httpClient.newCall(repoBuilder.build()).execute().use { resp ->
                    if (resp.isSuccessful) {
                        repoFound = true
                        val body = resp.body?.string() ?: ""
                        if (body.contains("\"private\":true") || body.contains("\"private\": true")) {
                            repoIsPrivate = true
                            details.add("⚠️ রিপোজিটরিটি Private! অন্যান্য ডিভাইসের প্লেয়াররা ফাইল পড়তে পারবে না!")
                            steps.add("গিটহাবে গিয়ে Settings > General থেকে রিপোজিটরিটি **Public** করুন যাতে অন্য ডিভাইসে কাজ করে।")
                        } else {
                            details.add("✅ রিপোজিটরি '${creds.repo.trim()}' পাওয়া গেছে এবং এটি **Public**।")
                        }
                    } else {
                        details.add("❌ রিপোজিটরি '${creds.repo.trim()}' গিটহাবে পাওয়া যায়নি (HTTP ${resp.code})।")
                        steps.add("https://github.com/new তে গিয়ে '${creds.repo.trim()}' নামে একটি Public রিপোজিটরি তৈরি করুন ('Add a README file' এ টিক দিন)।")
                    }
                }
            } catch (e: Exception) {
                details.add("⚠️ রিপোজিটরি চেক ব্যর্থ: ${e.message}")
            }

            // 4. Check File
            val rawUrl = creds.effectiveRawUrl
            try {
                val fileReq = Request.Builder()
                    .url(rawUrl)
                    .header("Cache-Control", "no-cache")
                    .header("User-Agent", "SuperAceAdminApp")
                    .build()
                httpClient.newCall(fileReq).execute().use { resp ->
                    if (resp.isSuccessful) {
                        fileFound = true
                        details.add("✅ কনফিগারেশন ফাইল (${creds.filePath.trim()}) গিটহাবে লাইভ রয়েছে!")
                    } else {
                        details.add("❌ ফাইলটি গিটহাবে নেই (HTTP ${resp.code} Not Found)।")
                        steps.add("অ্যাডমিন প্যানেলে সঠিক টোকেন দিয়ে 'গিটহাবে পাঠান' বাটনে ক্লিক করে ফাইলটি আপলোড করুন।")
                    }
                }
            } catch (e: Exception) {
                details.add("⚠️ ফাইল চেক ব্যর্থ: ${e.message}")
            }

            val allOk = userFound && repoFound && !repoIsPrivate && fileFound
            GitHubDiagnosticResult(
                isSuccess = allOk,
                summaryTitle = if (allOk) "🎉 গিটহাব কনফিগারেশন সম্পূর্ণ সচল ও অন্য ডিভাইসের সাথে যুক্ত!" else "⚠️ কিছু সমস্যা চিহ্নিত হয়েছে (নিচের সমাধান দেখুন)",
                userFound = userFound,
                repoFound = repoFound,
                repoIsPrivate = repoIsPrivate,
                tokenValid = tokenValid,
                fileFound = fileFound,
                details = details,
                actionSteps = steps
            )
        }
    }

    suspend fun syncRemoteConfigToPlayerState(
        context: Context,
        config: RemoteGameConfig,
        repository: GameRepository,
        adminRepository: GameAdminRepository
    ) {
        withContext(Dispatchers.IO) {
            try {
                // 1. Sync Game Math & Win Rates to GameAdminRepository
                val currentMath = adminRepository.getConfig()
                val updatedMath = currentMath.copy(
                    winRatioPercent = config.globalWinRatio,
                    superAceWinRatio = config.superAceWinRatio,
                    deadMansBulletWinRatio = config.deadMansBulletWinRatio,
                    gatesOfOlympusWinRatio = config.gatesOfOlympusWinRatio,
                    sweetBonanzaWinRatio = config.sweetBonanzaWinRatio,
                    maxCascadeSteps = config.maxCascadeSteps,
                    goldenWildChance = config.goldenWildChance,
                    bigJokerChance = config.bigJokerChance,
                    rtpMultiplierBonus = config.rtpMultiplierBonus
                )
                adminRepository.updateConfig(updatedMath)

                // 2. Sync Payment Gateways & Web Settings to Room Database
                val currentWallet = repository.getWalletConfig() ?: AdminWalletConfigEntity()
                val updatedWallet = currentWallet.copy(
                    nagadWallet = config.nagadWallet,
                    bkashWallet = config.bkashWallet,
                    rocketWallet = config.rocketWallet,
                    upayWallet = config.upayWallet,
                    minDeposit = config.minDeposit,
                    maxDeposit = config.maxDeposit,
                    websitePortalUrl = config.websitePortalUrl,
                    isWebAppModeEnabled = config.isWebAppModeEnabled,
                    updatedAt = System.currentTimeMillis()
                )
                repository.updateWalletConfig(updatedWallet)
                cacheConfig(context, config)

                // 3. Sync Current Player Profile & Balance from Cloud
                val currentProfile = repository.getUserProfile()
                if (currentProfile != null && currentProfile.userPhone.isNotBlank()) {
                    val sanitizedPhone = currentProfile.userPhone.trim()
                    val cloudPlayer = config.cloudPlayers.firstOrNull { it.phone.trim() == sanitizedPhone }
                    if (cloudPlayer != null) {
                        if (Math.abs(cloudPlayer.balance - currentProfile.balance) > 0.01) {
                            repository.setExactBalance(cloudPlayer.balance)
                            Log.i(TAG, "Synced player balance from cloud: ${cloudPlayer.balance}")
                        }
                        if (cloudPlayer.level != currentProfile.level) {
                            repository.setPlayerLevel(cloudPlayer.level, 0L)
                        }
                        repository.syncRegisteredAccountSnapshot()
                    }
                }

                // 4. Sync Deposit Requests Status from Cloud
                val localDeposits = repository.getAllDepositRequestsList()
                localDeposits.filter { it.status == "PENDING" }.forEach { localDep ->
                    val cloudMatch = config.cloudDeposits.firstOrNull {
                        it.id == localDep.id || (it.phone.trim() == localDep.userPhone.trim() && it.trxId.isNotBlank() && it.trxId.trim() == localDep.trxId.trim())
                    }
                    if (cloudMatch != null) {
                        if (cloudMatch.status == "APPROVED") {
                            repository.approveDepositRequest(localDep.id)
                            Log.i(TAG, "Deposit #${localDep.id} approved from cloud. Balance credited.")
                        } else if (cloudMatch.status == "REJECTED") {
                            repository.rejectDepositRequest(localDep.id, cloudMatch.adminNote.ifBlank { "এডমিন কর্তৃক বাতিল" })
                            Log.i(TAG, "Deposit #${localDep.id} rejected from cloud.")
                        }
                    }
                }

                // 5. Sync Withdraw Requests Status from Cloud
                val localWithdraws = repository.getAllWithdrawRequestsList()
                localWithdraws.filter { it.status == "PENDING" }.forEach { localWith ->
                    val cloudMatch = config.cloudWithdraws.firstOrNull {
                        it.id == localWith.id || (it.phone.trim() == localWith.userPhone.trim() && Math.abs(it.amount - localWith.amount) < 0.01)
                    }
                    if (cloudMatch != null) {
                        if (cloudMatch.status == "APPROVED") {
                            repository.approveWithdrawRequest(localWith.id)
                            Log.i(TAG, "Withdraw #${localWith.id} approved from cloud.")
                        } else if (cloudMatch.status == "REJECTED") {
                            repository.rejectWithdrawRequest(localWith.id, cloudMatch.adminNote.ifBlank { "এডমিন কর্তৃক বাতিল" }, refundToUser = true)
                            Log.i(TAG, "Withdraw #${localWith.id} rejected from cloud and refunded.")
                        }
                    }
                }

                Log.i(TAG, "Successfully synced remote config & player data from GitHub")
            } catch (e: Exception) {
                Log.e(TAG, "Error applying remote config to player state", e)
            }
        }
    }

    /**
     * Pushes a player's registration or updated balance to GitHub cloud configuration
     */
    suspend fun pushPlayerRegistrationToCloud(
        context: Context,
        phone: String,
        name: String,
        balance: Double,
        level: Int = 1
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val creds = loadCredentials(context)
            val cachedConfig = getCachedConfig(context)
            val effectiveToken = if (creds.token.isNotBlank()) creds.token.trim() else cachedConfig.clientSyncToken.trim()
            if (effectiveToken.isBlank()) {
                return@withContext Result.success("লোকাল ডাটাবেজে সংরক্ষিত (গিটহাব সরাসরি পুশ টোকেন অনুপস্থিত)")
            }

            val sanitizedPhone = phone.trim()
            val existingPlayers = cachedConfig.cloudPlayers.filterNot { it.phone.trim() == sanitizedPhone }.toMutableList()
            existingPlayers.add(
                com.example.data.model.CloudPlayerRecord(
                    phone = sanitizedPhone,
                    name = name.trim().ifBlank { "Player" },
                    balance = balance.coerceAtLeast(0.0),
                    level = level.coerceIn(1, 100),
                    lastUpdated = System.currentTimeMillis()
                )
            )

            val updatedConfig = cachedConfig.copy(
                cloudPlayers = existingPlayers,
                lastUpdated = System.currentTimeMillis()
            )

            val effectiveCreds = creds.copy(token = effectiveToken)
            publishConfigToGitHub(context, effectiveCreds, updatedConfig)
        } catch (e: Exception) {
            Log.w(TAG, "pushPlayerRegistrationToCloud failed: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Pushes a new deposit request to GitHub cloud configuration
     */
    suspend fun pushDepositRequestToCloud(
        context: Context,
        depositId: Long,
        phone: String,
        amount: Double,
        method: String,
        trxId: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val creds = loadCredentials(context)
            val cachedConfig = getCachedConfig(context)
            val effectiveToken = if (creds.token.isNotBlank()) creds.token.trim() else cachedConfig.clientSyncToken.trim()
            if (effectiveToken.isBlank()) {
                return@withContext Result.success("লোকালে সংরক্ষিত হয়েছে")
            }

            val existingDeposits = cachedConfig.cloudDeposits.toMutableList()
            existingDeposits.add(
                0,
                com.example.data.model.CloudDepositRecord(
                    id = depositId,
                    phone = phone.trim(),
                    amount = amount,
                    method = method,
                    trxId = trxId.trim(),
                    status = "PENDING",
                    submittedAt = System.currentTimeMillis()
                )
            )

            val updatedConfig = cachedConfig.copy(
                cloudDeposits = existingDeposits,
                lastUpdated = System.currentTimeMillis()
            )

            val effectiveCreds = creds.copy(token = effectiveToken)
            publishConfigToGitHub(context, effectiveCreds, updatedConfig)
        } catch (e: Exception) {
            Log.w(TAG, "pushDepositRequestToCloud failed: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Pushes a new withdrawal request to GitHub cloud configuration
     */
    suspend fun pushWithdrawRequestToCloud(
        context: Context,
        withdrawId: Long,
        phone: String,
        amount: Double,
        method: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val creds = loadCredentials(context)
            val cachedConfig = getCachedConfig(context)
            val effectiveToken = if (creds.token.isNotBlank()) creds.token.trim() else cachedConfig.clientSyncToken.trim()
            if (effectiveToken.isBlank()) {
                return@withContext Result.success("লোকালে সংরক্ষিত হয়েছে")
            }

            val existingWithdraws = cachedConfig.cloudWithdraws.toMutableList()
            existingWithdraws.add(
                0,
                com.example.data.model.CloudWithdrawRecord(
                    id = withdrawId,
                    phone = phone.trim(),
                    amount = amount,
                    method = method,
                    status = "PENDING",
                    submittedAt = System.currentTimeMillis()
                )
            )

            val updatedConfig = cachedConfig.copy(
                cloudWithdraws = existingWithdraws,
                lastUpdated = System.currentTimeMillis()
            )

            val effectiveCreds = creds.copy(token = effectiveToken)
            publishConfigToGitHub(context, effectiveCreds, updatedConfig)
        } catch (e: Exception) {
            Log.w(TAG, "pushWithdrawRequestToCloud failed: ${e.message}")
            Result.failure(e)
        }
    }
}
