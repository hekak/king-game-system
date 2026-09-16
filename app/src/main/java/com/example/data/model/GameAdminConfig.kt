package com.example.data.model

data class GameAdminConfig(
    val winRatioPercent: Int = 30, // Default / Master winning hit rate
    val superAceWinRatio: Int = 30, // Super Ace
    val deadMansBulletWinRatio: Int = 30, // Dead Man's Bullet
    val gatesOfOlympusWinRatio: Int = 30, // Gates of Olympus
    val sweetBonanzaWinRatio: Int = 30, // Sweet Bonanza
    val maxCascadeSteps: Int = 8,
    val goldenWildChance: Float = 0.25f,
    val bigJokerChance: Float = 0.35f,
    val initialBalance: Double = 1000.0,
    val rtpMultiplierBonus: Float = 1.0f,
    val guaranteedHouseProfitMode: Boolean = true,
    val houseProfitMarginPercent: Int = 40,
    val adminGuaranteedProfitPercent: Int = 40,
    val maxPayoutMultiplierCap: Double = 30.0,
    val autoBalanceLossProtection: Boolean = true,
    val allowPlayerProfit: Boolean = false, // If false, users CANNOT make net profit (Admin profit locked)
    val maxPlayerReturnRatio: Double = 0.60 // Max 60% payout return (Admin keeps at least 30-40% profit)
)
