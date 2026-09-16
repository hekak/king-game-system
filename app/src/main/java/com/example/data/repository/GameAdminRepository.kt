package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.GameAdminConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameAdminRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("super_ace_admin_prefs", Context.MODE_PRIVATE)

    private val _configFlow = MutableStateFlow(loadConfig())
    val configFlow: StateFlow<GameAdminConfig> = _configFlow.asStateFlow()

    fun getConfig(): GameAdminConfig = _configFlow.value

    fun updateConfig(config: GameAdminConfig) {
        prefs.edit().apply {
            putInt("win_ratio", config.winRatioPercent)
            putInt("super_ace_win_ratio", config.superAceWinRatio)
            putInt("dead_mans_bullet_win_ratio", config.deadMansBulletWinRatio)
            putInt("gates_of_olympus_win_ratio", config.gatesOfOlympusWinRatio)
            putInt("sweet_bonanza_win_ratio", config.sweetBonanzaWinRatio)
            putInt("max_cascades", config.maxCascadeSteps)
            putFloat("golden_wild_chance", config.goldenWildChance)
            putFloat("big_joker_chance", config.bigJokerChance)
            putFloat("rtp_multiplier", config.rtpMultiplierBonus)
            putBoolean("guaranteed_house_profit", config.guaranteedHouseProfitMode)
            putInt("house_profit_margin", config.houseProfitMarginPercent)
            putBoolean("allow_player_profit", config.allowPlayerProfit)
            putFloat("max_player_return_ratio", config.maxPlayerReturnRatio.toFloat())
            apply()
        }
        _configFlow.value = config
    }

    fun setAllowPlayerProfit(allowed: Boolean) {
        val current = _configFlow.value
        updateConfig(current.copy(allowPlayerProfit = allowed))
    }

    fun setSuperAceWinRatio(percent: Int) {
        val current = _configFlow.value
        updateConfig(current.copy(superAceWinRatio = percent))
    }

    fun setDeadMansBulletWinRatio(percent: Int) {
        val current = _configFlow.value
        updateConfig(current.copy(deadMansBulletWinRatio = percent))
    }

    fun setGatesOfOlympusWinRatio(percent: Int) {
        val current = _configFlow.value
        updateConfig(current.copy(gatesOfOlympusWinRatio = percent))
    }

    fun setSweetBonanzaWinRatio(percent: Int) {
        val current = _configFlow.value
        updateConfig(current.copy(sweetBonanzaWinRatio = percent))
    }

    fun setAllGamesWinRatio(percent: Int) {
        val current = _configFlow.value
        updateConfig(
            current.copy(
                winRatioPercent = percent,
                superAceWinRatio = percent,
                deadMansBulletWinRatio = percent,
                gatesOfOlympusWinRatio = percent,
                sweetBonanzaWinRatio = percent
            )
        )
    }

    fun resetToDefaults(): GameAdminConfig {
        val defaultConfig = GameAdminConfig(
            winRatioPercent = 30,
            superAceWinRatio = 30,
            deadMansBulletWinRatio = 30,
            gatesOfOlympusWinRatio = 30,
            sweetBonanzaWinRatio = 30,
            maxCascadeSteps = 8,
            goldenWildChance = 0.25f,
            bigJokerChance = 0.35f,
            initialBalance = 1000.0,
            rtpMultiplierBonus = 1.0f,
            guaranteedHouseProfitMode = true,
            houseProfitMarginPercent = 40,
            allowPlayerProfit = false,
            maxPlayerReturnRatio = 0.60
        )
        updateConfig(defaultConfig)
        return defaultConfig
    }

    private fun loadConfig(): GameAdminConfig {
        val globalWinRatio = prefs.getInt("win_ratio", 30)
        return GameAdminConfig(
            winRatioPercent = globalWinRatio,
            superAceWinRatio = prefs.getInt("super_ace_win_ratio", globalWinRatio),
            deadMansBulletWinRatio = prefs.getInt("dead_mans_bullet_win_ratio", globalWinRatio),
            gatesOfOlympusWinRatio = prefs.getInt("gates_of_olympus_win_ratio", globalWinRatio),
            sweetBonanzaWinRatio = prefs.getInt("sweet_bonanza_win_ratio", globalWinRatio),
            maxCascadeSteps = prefs.getInt("max_cascades", 8),
            goldenWildChance = prefs.getFloat("golden_wild_chance", 0.25f),
            bigJokerChance = prefs.getFloat("big_joker_chance", 0.35f),
            initialBalance = 1000.0,
            rtpMultiplierBonus = prefs.getFloat("rtp_multiplier", 1.0f),
            guaranteedHouseProfitMode = prefs.getBoolean("guaranteed_house_profit", true),
            houseProfitMarginPercent = prefs.getInt("house_profit_margin", 40),
            allowPlayerProfit = prefs.getBoolean("allow_player_profit", false),
            maxPlayerReturnRatio = prefs.getFloat("max_player_return_ratio", 0.60f).toDouble()
        )
    }

    companion object {
        @Volatile
        private var INSTANCE: GameAdminRepository? = null

        fun getInstance(context: Context): GameAdminRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = GameAdminRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
