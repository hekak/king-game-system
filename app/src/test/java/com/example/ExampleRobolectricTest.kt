package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.DailyMission
import com.example.data.model.SlotCard
import com.example.data.model.SymbolType
import com.example.engine.SlotEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        val adminAppName = context.getString(R.string.admin_app_name)
        assertEquals("King Game", appName)
        assertEquals("King Game Admin", adminAppName)
    }

    @Test
    fun `admin repository persists and retrieves config`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = com.example.data.repository.GameAdminRepository.getInstance(context)
        val testConfig = com.example.data.model.GameAdminConfig(winRatioPercent = 50)
        repo.updateConfig(testConfig)
        assertEquals(50, repo.getConfig().winRatioPercent)
    }

    @Test
    fun `sound manager initializes safely`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val soundManager = com.example.util.SoundManager.getInstance(context)
        assertNotNull(soundManager)
        assertTrue(soundManager.isEnabled)
    }

    @Test
    fun `generateInitialGrid creates 5 reels with 4 rows`() {
        val grid = SlotEngine.generateInitialGrid()
        assertEquals(5, grid.size)
        for (col in grid) {
            assertEquals(4, col.size)
        }
    }

    @Test
    fun `golden cards appear only on reels 2 3 4`() {
        for (iteration in 1..20) {
            val grid = SlotEngine.generateInitialGrid()
            // Column 0 (Reel 1) and Column 4 (Reel 5) must never have golden cards
            assertTrue(grid[0].none { it.isGolden })
            assertTrue(grid[4].none { it.isGolden })
        }
    }

    @Test
    fun `spin execution returns valid spin result with cascades`() {
        val result = SlotEngine.playSpin(currentBet = 2.0, isFreeGame = false)
        assertNotNull(result)
        assertEquals(5, result.initialGrid.size)
        assertTrue(result.totalWin >= 0.0)
    }
}
