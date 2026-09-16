package com.example.engine

import com.example.data.model.SweetCell
import com.example.data.model.SweetSpinResult
import com.example.data.model.SweetSymbol
import kotlin.random.Random

/**
 * Slot Engine for Sweet Bonanza.
 * 6x5 Tumbling Grid, Sugar Bomb Multipliers (up to 100x), and Candy Pop SFX.
 */
object SweetBonanzaEngine {

    const val NUM_COLS = 6
    const val NUM_ROWS = 5

    private val SYMBOLS = listOf(
        SweetSymbol.HEART to 5,
        SweetSymbol.PURPLE to 8,
        SweetSymbol.GREEN to 12,
        SweetSymbol.BLUE to 16,
        SweetSymbol.APPLE to 22,
        SweetSymbol.PLUM to 26,
        SweetSymbol.WATERMELON to 30,
        SweetSymbol.GRAPES to 34,
        SweetSymbol.BANANA to 38,
        SweetSymbol.LOLLIPOP to 4,
        SweetSymbol.SUGAR_BOMB to 6
    )

    private fun getRandomSymbol(): SweetSymbol {
        val total = SYMBOLS.sumOf { it.second }
        var r = Random.nextInt(total)
        for ((sym, weight) in SYMBOLS) {
            r -= weight
            if (r < 0) return sym
        }
        return SweetSymbol.BANANA
    }

    fun playSpin(
        currentBet: Double,
        winRatioPercent: Int = 30,
        rtpBonus: Float = 1.0f,
        guaranteedHouseProfit: Boolean = true,
        houseProfitMarginPercent: Int = 50,
        maxPayoutMultiplierCap: Double = 30.0
    ): SweetSpinResult {
        val effectiveWinRatio = if (guaranteedHouseProfit) {
            val maxAllowedRatio = (100 - houseProfitMarginPercent).coerceIn(5, 50)
            winRatioPercent.coerceAtMost(maxAllowedRatio)
        } else {
            winRatioPercent
        }

        val roll = Random.nextInt(100)
        val allowWin = roll < effectiveWinRatio

        val grid = mutableListOf<List<SweetCell>>()
        for (c in 0 until NUM_COLS) {
            val colList = mutableListOf<SweetCell>()
            for (r in 0 until NUM_ROWS) {
                colList.add(SweetCell(symbol = getRandomSymbol()))
            }
            grid.add(colList)
        }

        val counts = mutableMapOf<SweetSymbol, Int>()
        for (c in 0 until NUM_COLS) {
            for (r in 0 until NUM_ROWS) {
                val sym = grid[c][r].symbol
                counts[sym] = (counts[sym] ?: 0) + 1
            }
        }

        var baseWin = 0.0
        val winningSymbols = mutableSetOf<SweetSymbol>()

        if (allowWin) {
            for ((sym, count) in counts) {
                if (sym != SweetSymbol.LOLLIPOP && sym != SweetSymbol.SUGAR_BOMB && count >= 8) {
                    val payMult = sym.payBase * (count / 8.0)
                    baseWin += currentBet * payMult * 0.22 * rtpBonus
                    winningSymbols.add(sym)
                }
            }

            if (baseWin == 0.0) {
                val forced = listOf(SweetSymbol.HEART, SweetSymbol.PURPLE, SweetSymbol.APPLE).random()
                winningSymbols.add(forced)
                baseWin = currentBet * forced.payBase * 0.35 * rtpBonus
            }
        }

        // Sugar Bomb Multipliers: 2x, 5x, 10x, 25x, 50x, 100x
        val bombMultiplier = if (baseWin > 0 && Random.nextFloat() < 0.40f) {
            listOf(2, 3, 5, 10, 15, 25, 50, 100).random()
        } else {
            1
        }

        val rawWin = baseWin * bombMultiplier
        val finalWin = if (guaranteedHouseProfit) {
            val maxAllowed = currentBet * maxPayoutMultiplierCap
            rawWin.coerceAtMost(maxAllowed)
        } else {
            rawWin
        }
        val triggeredFreeSpins = allowWin && (counts[SweetSymbol.LOLLIPOP] ?: 0) >= 4

        val markedGrid = grid.map { col ->
            col.map { cell ->
                cell.copy(isWinning = winningSymbols.contains(cell.symbol))
            }
        }

        return SweetSpinResult(
            grid = markedGrid,
            totalWin = finalWin,
            bombMultiplier = bombMultiplier,
            triggeredFreeSpins = triggeredFreeSpins
        )
    }
}
