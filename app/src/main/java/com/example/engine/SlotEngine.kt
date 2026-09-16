package com.example.engine

import com.example.data.model.CascadeStep
import com.example.data.model.SlotCard
import com.example.data.model.SpinResult
import com.example.data.model.SymbolType
import com.example.data.model.WinningWay
import java.util.UUID
import kotlin.random.Random

object SlotEngine {

    const val NUM_COLUMNS = 5
    const val NUM_ROWS = 4

    private val REGULAR_SYMBOLS = listOf(
        SymbolType.ACE,
        SymbolType.KING,
        SymbolType.QUEEN,
        SymbolType.JACK,
        SymbolType.SPADE,
        SymbolType.HEART,
        SymbolType.DIAMOND,
        SymbolType.CLUB
    )

    // Weighted distribution for realistic and fun hits
    private val SYMBOL_WEIGHTS = mapOf(
        SymbolType.ACE to 10,
        SymbolType.KING to 12,
        SymbolType.QUEEN to 14,
        SymbolType.JACK to 16,
        SymbolType.SPADE to 18,
        SymbolType.HEART to 20,
        SymbolType.DIAMOND to 22,
        SymbolType.CLUB to 24,
        SymbolType.SCATTER to 4
    )

    private fun getRandomSymbol(allowScatter: Boolean = true): SymbolType {
        val filtered = if (allowScatter) SYMBOL_WEIGHTS else SYMBOL_WEIGHTS.filterKeys { it != SymbolType.SCATTER }
        val totalWeight = filtered.values.sum()
        var r = Random.nextInt(totalWeight)
        for ((sym, weight) in filtered) {
            r -= weight
            if (r < 0) return sym
        }
        return SymbolType.CLUB
    }

    fun generateInitialGrid(goldenChance: Float = 0.22f): List<List<SlotCard>> {
        val grid = mutableListOf<List<SlotCard>>()
        for (col in 0 until NUM_COLUMNS) {
            val columnCards = mutableListOf<SlotCard>()
            for (row in 0 until NUM_ROWS) {
                val symbol = getRandomSymbol(allowScatter = true)
                // Golden cards only appear on reels 2, 3, 4 (cols 1, 2, 3) and cannot be Scatter
                val canBeGolden = col in 1..3 && symbol != SymbolType.SCATTER
                val isGolden = canBeGolden && Random.nextFloat() < goldenChance

                columnCards.add(
                    SlotCard(
                        symbol = symbol,
                        isGolden = isGolden
                    )
                )
            }
            grid.add(columnCards)
        }
        return grid
    }

    private fun generateNonWinningGrid(): List<List<SlotCard>> {
        // Ensure no 3 matching symbols on reels 0, 1, 2
        val pool0 = listOf(SymbolType.ACE, SymbolType.KING)
        val pool1 = listOf(SymbolType.QUEEN, SymbolType.JACK)
        val pool2 = listOf(SymbolType.SPADE, SymbolType.HEART)
        val pool3 = listOf(SymbolType.DIAMOND, SymbolType.CLUB)
        val pool4 = listOf(SymbolType.ACE, SymbolType.KING)

        val pools = listOf(pool0, pool1, pool2, pool3, pool4)
        val grid = mutableListOf<List<SlotCard>>()

        for (col in 0 until NUM_COLUMNS) {
            val columnCards = mutableListOf<SlotCard>()
            for (row in 0 until NUM_ROWS) {
                val sym = pools[col][row % pools[col].size]
                columnCards.add(SlotCard(symbol = sym, isGolden = false))
            }
            grid.add(columnCards)
        }
        return grid
    }

    fun playSpin(
        currentBet: Double,
        isFreeGame: Boolean,
        forcedScatters: Int = 0,
        winRatioPercent: Int = 30,
        maxSteps: Int = 10,
        goldenChance: Float = 0.22f,
        bigJokerChance: Float = 0.35f,
        rtpMultiplierBonus: Float = 1.0f,
        guaranteedHouseProfit: Boolean = true,
        houseProfitMarginPercent: Int = 50,
        maxPayoutMultiplierCap: Double = 30.0
    ): SpinResult {
        // Admin controlled win ratio & guaranteed house profit retention (Admin always secures 50% profit)
        val effectiveWinRatio = if (guaranteedHouseProfit) {
            val maxAllowedRatio = (100 - houseProfitMarginPercent).coerceIn(5, 50)
            winRatioPercent.coerceAtMost(maxAllowedRatio)
        } else {
            winRatioPercent
        }

        val roll = Random.nextInt(100)
        val allowWin = (isFreeGame && !guaranteedHouseProfit) || forcedScatters >= 3 || roll < effectiveWinRatio

        var grid = if (!allowWin) {
            generateNonWinningGrid()
        } else {
            generateInitialGrid(goldenChance)
        }

        // Handle bonus buy or forced scatters if requested
        if (forcedScatters >= 3) {
            val scatterCols = listOf(0, 2, 4).shuffled()
            val newGrid = grid.map { it.toMutableList() }.toMutableList()
            for (i in 0 until 3) {
                val c = scatterCols[i]
                val r = Random.nextInt(NUM_ROWS)
                newGrid[c][r] = SlotCard(symbol = SymbolType.SCATTER, isGolden = false)
            }
            grid = newGrid
        }

        val cascadeSteps = mutableListOf<CascadeStep>()
        var totalWin = 0.0
        var comboIndex = 0
        var stepCount = 0
        val maxAllowedSteps = maxSteps.coerceIn(3, 15) // user configured max steps limit

        val normalMultipliers = listOf(1, 2, 3, 5)
        val freeMultipliers = listOf(2, 4, 6, 10)
        val multiplierList = if (isFreeGame) freeMultipliers else normalMultipliers

        // Count initial scatters
        var initialScatters = 0
        for (c in 0 until NUM_COLUMNS) {
            for (r in 0 until NUM_ROWS) {
                if (grid[c][r].symbol == SymbolType.SCATTER) {
                    initialScatters++
                }
            }
        }

        var currentGrid = grid
        var currentMultiplier = multiplierList[comboIndex]

        while (stepCount < maxAllowedSteps) {
            val winningWays = evaluateWinningWays(currentGrid, currentBet, currentMultiplier)
            if (winningWays.isEmpty()) {
                break
            }

            var stepWin = 0.0
            for (way in winningWays) {
                stepWin += way.totalWin
            }
            totalWin += stepWin

            // Collect all winning positions
            val winningPositions = mutableSetOf<Pair<Int, Int>>()
            for (way in winningWays) {
                winningPositions.addAll(way.winningPositions)
            }

            // Mark winning cards for display
            val markedGrid = currentGrid.mapIndexed { c, col ->
                col.mapIndexed { r, card ->
                    if (Pair(c, r) in winningPositions) card.copy(isWinning = true) else card
                }
            }

            // Identify which golden cards transform to wilds
            val transformedWildPositions = mutableListOf<Pair<Int, Int>>()
            var bigJokerTriggered = false

            for (pos in winningPositions) {
                val (c, r) = pos
                val card = currentGrid[c][r]
                if (card.isGolden) {
                    transformedWildPositions.add(pos)
                    // Configurable chance to become Big Joker Wild
                    if (Random.nextFloat() < bigJokerChance) {
                        bigJokerTriggered = true
                    }
                }
            }

            // Big Joker splash: randomly turn 1 to 4 other symbols on reels 2 to 5 into wilds
            val bigJokerExtraPositions = mutableListOf<Pair<Int, Int>>()
            if (bigJokerTriggered) {
                val candidatePositions = mutableListOf<Pair<Int, Int>>()
                for (c in 1 until NUM_COLUMNS) {
                    for (r in 0 until NUM_ROWS) {
                        val card = currentGrid[c][r]
                        if (Pair(c, r) !in winningPositions && !card.isScatter && !card.isWild) {
                            candidatePositions.add(Pair(c, r))
                        }
                    }
                }
                candidatePositions.shuffle()
                val extraCount = Random.nextInt(1, 4).coerceAtMost(candidatePositions.size)
                for (i in 0 until extraCount) {
                    bigJokerExtraPositions.add(candidatePositions[i])
                }
            }

            cascadeSteps.add(
                CascadeStep(
                    stepIndex = stepCount,
                    grid = markedGrid,
                    winningWays = winningWays,
                    multiplier = currentMultiplier,
                    stepWin = stepWin,
                    transformedWildPositions = transformedWildPositions + bigJokerExtraPositions
                )
            )

            // Perform cascade elimination & symbol drops
            currentGrid = performCascade(
                currentGrid = currentGrid,
                winningPositions = winningPositions,
                transformedWildPositions = transformedWildPositions,
                bigJokerExtraPositions = bigJokerExtraPositions
            )

            // Advance combo multiplier
            comboIndex = (comboIndex + 1).coerceAtMost(multiplierList.size - 1)
            currentMultiplier = multiplierList[comboIndex]
            stepCount++
        }

        val triggeredFreeGames = if (isFreeGame) {
            initialScatters >= 3
        } else {
            initialScatters >= 3
        }
        val freeSpinsAwarded = if (triggeredFreeGames) (if (isFreeGame) 5 else 10) else 0

        val rawWin = totalWin * rtpMultiplierBonus
        val finalWin = if (guaranteedHouseProfit) {
            val maxAllowed = currentBet * maxPayoutMultiplierCap
            rawWin.coerceAtMost(maxAllowed)
        } else {
            rawWin
        }

        return SpinResult(
            initialGrid = grid,
            cascadeSteps = cascadeSteps,
            totalWin = finalWin,
            finalMultiplier = currentMultiplier,
            scattersCount = initialScatters,
            triggeredFreeGames = triggeredFreeGames,
            freeSpinsAwarded = freeSpinsAwarded
        )
    }

    private fun evaluateWinningWays(
        grid: List<List<SlotCard>>,
        bet: Double,
        comboMultiplier: Int
    ): List<WinningWay> {
        val winningWays = mutableListOf<WinningWay>()

        for (sym in REGULAR_SYMBOLS) {
            // Check consecutive matches from reel 0 (leftmost)
            var consecutiveLength = 0
            val matchingPositions = mutableListOf<Pair<Int, Int>>()
            val countsPerReel = mutableListOf<Int>()

            for (col in 0 until NUM_COLUMNS) {
                val matchesInCol = mutableListOf<Pair<Int, Int>>()
                for (row in 0 until NUM_ROWS) {
                    val card = grid[col][row]
                    if (card.symbol == sym || card.isWild) {
                        matchesInCol.add(Pair(col, row))
                    }
                }

                if (matchesInCol.isNotEmpty()) {
                    consecutiveLength++
                    matchingPositions.addAll(matchesInCol)
                    countsPerReel.add(matchesInCol.size)
                } else {
                    break
                }
            }

            if (consecutiveLength >= 3) {
                // Calculate pay
                val basePayPerUnit = when (consecutiveLength) {
                    3 -> sym.pay3
                    4 -> sym.pay4
                    5 -> sym.pay5
                    else -> 0.0
                }

                // 1024 ways combination count = product of matches in matching reels
                var waysCount = 1
                for (count in countsPerReel) {
                    waysCount *= count
                }

                // Standard pay formula: basePayPerUnit * waysCount * (bet / 2.0)
                val baseWin = basePayPerUnit * waysCount * (bet / 2.0)
                val totalWin = baseWin * comboMultiplier

                // Filter matchingPositions to only include reels up to consecutiveLength
                val validPositions = matchingPositions.filter { it.first < consecutiveLength }

                winningWays.add(
                    WinningWay(
                        symbol = sym,
                        matchLength = consecutiveLength,
                        waysCount = waysCount,
                        basePay = baseWin,
                        totalWin = totalWin,
                        winningPositions = validPositions
                    )
                )
            }
        }

        return winningWays
    }

    private fun performCascade(
        currentGrid: List<List<SlotCard>>,
        winningPositions: Set<Pair<Int, Int>>,
        transformedWildPositions: List<Pair<Int, Int>>,
        bigJokerExtraPositions: List<Pair<Int, Int>>
    ): List<List<SlotCard>> {
        val newGrid = mutableListOf<List<SlotCard>>()

        for (col in 0 until NUM_COLUMNS) {
            val remainingInCol = mutableListOf<SlotCard>()

            // Evaluate from bottom row (3) up to top row (0)
            for (row in NUM_ROWS - 1 downTo 0) {
                val pos = Pair(col, row)
                val currentCard = currentGrid[col][row]

                if (pos in transformedWildPositions) {
                    // Golden card turns into Wild Joker
                    val isBig = Random.nextFloat() < 0.35f
                    val wildSymbol = if (isBig) SymbolType.WILD_BIG else SymbolType.WILD_LITTLE
                    remainingInCol.add(
                        0,
                        SlotCard(
                            id = UUID.randomUUID().toString(),
                            symbol = wildSymbol,
                            isGolden = false,
                            isWildTransformed = true
                        )
                    )
                } else if (pos in bigJokerExtraPositions) {
                    // Big Joker extra splash into Wild
                    remainingInCol.add(
                        0,
                        SlotCard(
                            id = UUID.randomUUID().toString(),
                            symbol = SymbolType.WILD_BIG,
                            isGolden = false,
                            isWildTransformed = true
                        )
                    )
                } else if (pos !in winningPositions) {
                    // Card survived elimination
                    remainingInCol.add(0, currentCard.copy(isWinning = false, isEliminating = false))
                }
                // Winning non-golden cards are eliminated and not added
            }

            // Drop in new random cards at the top if needed to fill the 4 rows
            val needed = NUM_ROWS - remainingInCol.size
            val newCards = mutableListOf<SlotCard>()
            for (i in 0 until needed) {
                val sym = getRandomSymbol(allowScatter = false)
                val canBeGolden = col in 1..3
                val isGolden = canBeGolden && Random.nextFloat() < 0.20f
                newCards.add(
                    SlotCard(
                        id = UUID.randomUUID().toString(),
                        symbol = sym,
                        isGolden = isGolden,
                        isNewlyDropped = true
                    )
                )
            }

            newGrid.add(newCards + remainingInCol)
        }

        return newGrid
    }
}
