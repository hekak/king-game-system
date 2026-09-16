package com.example.data.model

import java.util.UUID

enum class SymbolType(
    val displayName: String,
    val pay3: Double,
    val pay4: Double,
    val pay5: Double
) {
    ACE("Ace", 1.0, 3.0, 5.0),
    KING("King", 0.8, 2.4, 4.0),
    QUEEN("Queen", 0.6, 1.8, 3.0),
    JACK("Jack", 0.4, 1.2, 2.0),
    SPADE("Spade", 0.2, 0.6, 1.0),
    HEART("Heart", 0.2, 0.6, 1.0),
    DIAMOND("Diamond", 0.1, 0.3, 0.5),
    CLUB("Club", 0.1, 0.3, 0.5),
    SCATTER("Scatter", 0.0, 0.0, 0.0),
    WILD_LITTLE("Little Wild", 0.0, 0.0, 0.0),
    WILD_BIG("Big Wild", 0.0, 0.0, 0.0)
}

data class SlotCard(
    val id: String = UUID.randomUUID().toString(),
    val symbol: SymbolType,
    val isGolden: Boolean = false,
    val isWinning: Boolean = false,
    val isEliminating: Boolean = false,
    val isWildTransformed: Boolean = false,
    val isNewlyDropped: Boolean = false
) {
    val isWild: Boolean
        get() = symbol == SymbolType.WILD_LITTLE || symbol == SymbolType.WILD_BIG

    val isScatter: Boolean
        get() = symbol == SymbolType.SCATTER
}

data class WinningWay(
    val symbol: SymbolType,
    val matchLength: Int, // 3, 4, or 5 reels
    val waysCount: Int,   // number of combinations across adjacent reels
    val basePay: Double,  // base pay for this symbol at matchLength * waysCount
    val totalWin: Double, // basePay * betMultiplier * comboMultiplier
    val winningPositions: List<Pair<Int, Int>> // (col, row)
)

data class CascadeStep(
    val stepIndex: Int,
    val grid: List<List<SlotCard>>,
    val winningWays: List<WinningWay>,
    val multiplier: Int,
    val stepWin: Double,
    val transformedWildPositions: List<Pair<Int, Int>> = emptyList()
)

data class SpinResult(
    val initialGrid: List<List<SlotCard>>,
    val cascadeSteps: List<CascadeStep>,
    val totalWin: Double,
    val finalMultiplier: Int,
    val scattersCount: Int,
    val triggeredFreeGames: Boolean,
    val freeSpinsAwarded: Int = 0
)

data class DailyMission(
    val id: Int,
    val title: String,
    val description: String,
    val target: Int,
    val current: Int,
    val rewardPoints: Long,
    val rewardXp: Long,
    val isClaimed: Boolean
) {
    val isCompleted: Boolean
        get() = current >= target
}
