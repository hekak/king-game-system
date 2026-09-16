package com.example.engine

import com.example.data.model.OlympusCell
import com.example.data.model.OlympusSpinResult
import com.example.data.model.OlympusSymbol
import kotlin.random.Random

/**
 * Slot Engine for Gates of Olympus.
 * 6x5 Tumble Grid, Scatter Pays, Zeus Lightning Multipliers (up to 500x), and Thunder SFX.
 */
object GatesOfOlympusEngine {

    const val NUM_COLS = 6
    const val NUM_ROWS = 5

    private val SYMBOLS = listOf(
        OlympusSymbol.CROWN to 4,
        OlympusSymbol.HOURGLASS to 7,
        OlympusSymbol.RING to 11,
        OlympusSymbol.CHALICE to 15,
        OlympusSymbol.RED_GEM to 20,
        OlympusSymbol.PURPLE_GEM to 25,
        OlympusSymbol.YELLOW_GEM to 30,
        OlympusSymbol.GREEN_GEM to 35,
        OlympusSymbol.BLUE_GEM to 40,
        OlympusSymbol.ZEUS to 3
    )

    private fun getRandomSymbol(): OlympusSymbol {
        val total = SYMBOLS.sumOf { it.second }
        var r = Random.nextInt(total)
        for ((sym, weight) in SYMBOLS) {
            r -= weight
            if (r < 0) return sym
        }
        return OlympusSymbol.BLUE_GEM
    }

    fun playSpin(
        currentBet: Double,
        winRatioPercent: Int = 30,
        rtpBonus: Float = 1.0f,
        guaranteedHouseProfit: Boolean = true,
        houseProfitMarginPercent: Int = 50,
        maxPayoutMultiplierCap: Double = 30.0
    ): OlympusSpinResult {
        val effectiveWinRatio = if (guaranteedHouseProfit) {
            val maxAllowedRatio = (100 - houseProfitMarginPercent).coerceIn(5, 50)
            winRatioPercent.coerceAtMost(maxAllowedRatio)
        } else {
            winRatioPercent
        }

        val roll = Random.nextInt(100)
        val allowWin = roll < effectiveWinRatio

        val grid = mutableListOf<List<OlympusCell>>()
        for (c in 0 until NUM_COLS) {
            val colList = mutableListOf<OlympusCell>()
            for (r in 0 until NUM_ROWS) {
                colList.add(OlympusCell(symbol = getRandomSymbol()))
            }
            grid.add(colList)
        }

        // Count symbols across entire 6x5 grid (Scatter pay: 8+ symbols win)
        val counts = mutableMapOf<OlympusSymbol, Int>()
        for (c in 0 until NUM_COLS) {
            for (r in 0 until NUM_ROWS) {
                val sym = grid[c][r].symbol
                counts[sym] = (counts[sym] ?: 0) + 1
            }
        }

        var baseWin = 0.0
        val winningSymbols = mutableSetOf<OlympusSymbol>()

        if (allowWin) {
            for ((sym, count) in counts) {
                if (sym != OlympusSymbol.ZEUS && count >= 8) {
                    val payMult = sym.payBase * (count / 8.0)
                    baseWin += currentBet * payMult * 0.25 * rtpBonus
                    winningSymbols.add(sym)
                }
            }

            // Guarantee win if allowWin was true but counts didn't naturally hit 8
            if (baseWin == 0.0) {
                val forcedSym = listOf(OlympusSymbol.RED_GEM, OlympusSymbol.PURPLE_GEM, OlympusSymbol.CROWN).random()
                winningSymbols.add(forcedSym)
                baseWin = currentBet * forcedSym.payBase * 0.4 * rtpBonus
            }
        }

        // Zeus Lightning Multipliers: 2x, 3x, 5x, 10x, 25x, 50x, 100x, 500x
        val zeusMultiplier = if (baseWin > 0 && Random.nextFloat() < 0.45f) {
            listOf(2, 3, 5, 8, 10, 15, 25, 50, 100, 500).random()
        } else {
            1
        }

        val rawWin = baseWin * zeusMultiplier
        val finalWin = if (guaranteedHouseProfit) {
            val maxAllowed = currentBet * maxPayoutMultiplierCap
            rawWin.coerceAtMost(maxAllowed)
        } else {
            rawWin
        }
        val triggeredFreeSpins = allowWin && (counts[OlympusSymbol.ZEUS] ?: 0) >= 4

        val markedGrid = grid.map { col ->
            col.map { cell ->
                cell.copy(isWinning = winningSymbols.contains(cell.symbol))
            }
        }

        return OlympusSpinResult(
            grid = markedGrid,
            totalWin = finalWin,
            zeusMultiplier = zeusMultiplier,
            triggeredFreeSpins = triggeredFreeSpins
        )
    }
}
