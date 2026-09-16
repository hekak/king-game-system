package com.example.engine

import com.example.data.model.BulletCard
import com.example.data.model.BulletSpinResult
import com.example.data.model.BulletSymbol
import kotlin.random.Random

/**
 * Slot Math & Spin Engine for Skywind Group's "Dead Man's Bullet".
 * 3-Reel Western Showdown with Collector Respins, Bullet Cash Prizes, and 9,990x Max Win Potential.
 */
object DeadMansBulletEngine {

    const val NUM_COLS = 3
    const val NUM_ROWS = 3

    private val REGULAR_SYMBOLS = listOf(
        BulletSymbol.SKULL_OUTLAW to 4,
        BulletSymbol.SHERIFF_BADGE to 7,
        BulletSymbol.DUAL_REVOLVERS to 12,
        BulletSymbol.DYNAMITE to 16,
        BulletSymbol.CASH_BAG to 20,
        BulletSymbol.WHISKEY to 24,
        BulletSymbol.HORSESHOE to 28,
        BulletSymbol.SILVER_BULLET to 14,
        BulletSymbol.GOLD_BULLET to 8,
        BulletSymbol.BOUNTY_COLLECTOR to 5,
        BulletSymbol.WILD_OUTLAW to 10
    )

    private fun getRandomSymbol(): BulletSymbol {
        val totalWeight = REGULAR_SYMBOLS.sumOf { it.second }
        var r = Random.nextInt(totalWeight)
        for ((sym, weight) in REGULAR_SYMBOLS) {
            r -= weight
            if (r < 0) return sym
        }
        return BulletSymbol.HORSESHOE
    }

    private fun getRandomPrize(symbol: BulletSymbol): Int {
        return when (symbol) {
            BulletSymbol.SILVER_BULLET -> listOf(1, 2, 3, 5, 10, 20).random()
            BulletSymbol.GOLD_BULLET -> listOf(5, 10, 25, 50, 100, 250).random()
            BulletSymbol.BOUNTY_COLLECTOR -> listOf(10, 25, 50, 100, 500).random()
            else -> 0
        }
    }

    fun generateGrid(): List<List<BulletCard>> {
        val grid = mutableListOf<List<BulletCard>>()
        for (c in 0 until NUM_COLS) {
            val colCards = mutableListOf<BulletCard>()
            for (r in 0 until NUM_ROWS) {
                val sym = getRandomSymbol()
                colCards.add(
                    BulletCard(
                        symbol = sym,
                        prizeMultiplier = getRandomPrize(sym)
                    )
                )
            }
            grid.add(colCards)
        }
        return grid
    }

    private fun generateNonWinningGrid(): List<List<BulletCard>> {
        val pools = listOf(
            listOf(BulletSymbol.HORSESHOE, BulletSymbol.WHISKEY, BulletSymbol.CASH_BAG),
            listOf(BulletSymbol.DYNAMITE, BulletSymbol.DUAL_REVOLVERS, BulletSymbol.SHERIFF_BADGE),
            listOf(BulletSymbol.HORSESHOE, BulletSymbol.WHISKEY, BulletSymbol.CASH_BAG)
        )
        val grid = mutableListOf<List<BulletCard>>()
        for (c in 0 until NUM_COLS) {
            val colCards = mutableListOf<BulletCard>()
            for (r in 0 until NUM_ROWS) {
                val sym = pools[c][r % pools[c].size]
                colCards.add(BulletCard(symbol = sym))
            }
            grid.add(colCards)
        }
        return grid
    }

    fun playSpin(
        currentBet: Double,
        winRatioPercent: Int = 30,
        isRespin: Boolean = false,
        rtpBonus: Float = 1.0f,
        guaranteedHouseProfit: Boolean = true,
        houseProfitMarginPercent: Int = 50,
        maxPayoutMultiplierCap: Double = 30.0
    ): BulletSpinResult {
        val effectiveWinRatio = if (guaranteedHouseProfit) {
            val maxAllowedRatio = (100 - houseProfitMarginPercent).coerceIn(5, 50)
            winRatioPercent.coerceAtMost(maxAllowedRatio)
        } else {
            winRatioPercent
        }

        val roll = Random.nextInt(100)
        val allowWin = (isRespin && !guaranteedHouseProfit) || roll < effectiveWinRatio

        val baseGrid = if (!allowWin) {
            generateNonWinningGrid()
        } else {
            generateGrid()
        }

        // Evaluate paylines across the 3 rows and diagonals
        // Payline 0: Row 0 (Top)
        // Payline 1: Row 1 (Center - Primary Bounty Line)
        // Payline 2: Row 2 (Bottom)
        // Payline 3: Diagonal Top-Left to Bottom-Right
        // Payline 4: Diagonal Bottom-Left to Top-Right
        val paylinePositions = listOf(
            listOf(Pair(0, 0), Pair(1, 0), Pair(2, 0)),
            listOf(Pair(0, 1), Pair(1, 1), Pair(2, 1)),
            listOf(Pair(0, 2), Pair(1, 2), Pair(2, 2)),
            listOf(Pair(0, 0), Pair(1, 1), Pair(2, 2)),
            listOf(Pair(0, 2), Pair(1, 1), Pair(2, 0))
        )

        var totalWin = 0.0
        val winningCoords = mutableSetOf<Pair<Int, Int>>()
        var highestPay = 0.0
        var mainWinSymbol = ""

        if (allowWin) {
            for (line in paylinePositions) {
                val card0 = baseGrid[line[0].first][line[0].second]
                val card1 = baseGrid[line[1].first][line[1].second]
                val card2 = baseGrid[line[2].first][line[2].second]

                // Skip bullet symbols on standard symbol lines
                if (card0.symbol == BulletSymbol.SILVER_BULLET ||
                    card0.symbol == BulletSymbol.GOLD_BULLET ||
                    card0.symbol == BulletSymbol.BOUNTY_COLLECTOR
                ) continue

                // Check 3 matching or Wild Outlaw
                val targetSym = if (card0.symbol == BulletSymbol.WILD_OUTLAW) {
                    if (card1.symbol == BulletSymbol.WILD_OUTLAW) card2.symbol else card1.symbol
                } else card0.symbol

                val m1 = card1.symbol == targetSym || card1.symbol == BulletSymbol.WILD_OUTLAW
                val m2 = card2.symbol == targetSym || card2.symbol == BulletSymbol.WILD_OUTLAW

                if (m1 && m2 && targetSym != BulletSymbol.WILD_OUTLAW) {
                    val linePay = currentBet * (targetSym.payout3 / 10.0) * rtpBonus
                    totalWin += linePay
                    winningCoords.addAll(line)
                    if (targetSym.payout3 > highestPay) {
                        highestPay = targetSym.payout3
                        mainWinSymbol = targetSym.displayName
                    }
                }
            }
        }

        // Count Bullet & Collector Respins feature
        var bulletCount = 0
        var totalBulletMultiplier = 0
        var hasCollector = false

        for (c in 0 until NUM_COLS) {
            for (r in 0 until NUM_ROWS) {
                val card = baseGrid[c][r]
                if (card.symbol == BulletSymbol.SILVER_BULLET || card.symbol == BulletSymbol.GOLD_BULLET) {
                    bulletCount++
                    totalBulletMultiplier += card.prizeMultiplier
                }
                if (card.symbol == BulletSymbol.BOUNTY_COLLECTOR) {
                    hasCollector = true
                    bulletCount++
                    totalBulletMultiplier += card.prizeMultiplier
                }
            }
        }

        val triggeredRespins = allowWin && (hasCollector || bulletCount >= 3)
        var finalMult = 1

        if (triggeredRespins) {
            // Outlaw Collector Bounty shootout
            val bountyWin = currentBet * totalBulletMultiplier.coerceAtMost(999) * rtpBonus
            totalWin += bountyWin
            finalMult = totalBulletMultiplier.coerceAtLeast(2)
        }

        // Mark winning cards
        val markedGrid = baseGrid.mapIndexed { c, col ->
            col.mapIndexed { r, card ->
                val isWin = winningCoords.contains(Pair(c, r)) ||
                        (triggeredRespins && (card.symbol == BulletSymbol.SILVER_BULLET ||
                                card.symbol == BulletSymbol.GOLD_BULLET ||
                                card.symbol == BulletSymbol.BOUNTY_COLLECTOR))
                card.copy(isWinning = isWin, isLocked = triggeredRespins && isWin)
            }
        }

        val rawWin = totalWin * rtpBonus
        val finalWin = if (guaranteedHouseProfit) {
            val maxAllowed = currentBet * maxPayoutMultiplierCap
            rawWin.coerceAtMost(maxAllowed)
        } else {
            rawWin
        }

        val winType = when {
            finalWin >= currentBet * 50 -> "OUTLAW BOUNTY JACKPOT!"
            finalWin >= currentBet * 20 -> "DEAD MAN MEGA WIN!"
            finalWin >= currentBet * 5 -> "BULLET HIT WIN"
            finalWin > 0 -> "SHOWDOWN WIN"
            else -> ""
        }

        return BulletSpinResult(
            grid = markedGrid,
            totalWin = finalWin,
            winType = winType,
            triggeredRespins = triggeredRespins,
            respinsAwarded = if (triggeredRespins) 3 else 0,
            finalMultiplier = finalMult
        )
    }
}
