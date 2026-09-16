package com.example.data.model

import java.util.UUID

enum class CasinoGame(
    val id: String,
    val title: String,
    val provider: String,
    val subtitle: String,
    val badge: String,
    val maxWin: String,
    val themeColorHex: Long,
    val rtp: String
) {
    SUPER_ACE(
        id = "super_ace",
        title = "Super Ace",
        provider = "JILI",
        subtitle = "১০২৪ ওয়েজ গোল্ডেন ওয়াইল্ড ক্যাসকেড",
        badge = "🔥 HOT",
        maxWin = "1,500x",
        themeColorHex = 0xFF8B5CF6,
        rtp = "97.0%"
    ),
    DEAD_MANS_BULLET(
        id = "dead_mans_bullet",
        title = "Dead Man's Bullet",
        provider = "Skywind Group",
        subtitle = "আউটল রিভলভার ও বুলেট রেস্পিনস (৯,৯৯০x)",
        badge = "🤠 NEW 9,990x",
        maxWin = "9,990x",
        themeColorHex = 0xFFEA580C,
        rtp = "96.5%"
    ),
    GATES_OF_OLYMPUS(
        id = "gates_of_olympus",
        title = "Gates of Olympus",
        provider = "Pragmatic Play",
        subtitle = "জিউস থান্ডার ও ৫০০x মাল্টিপ্লায়ার",
        badge = "⚡ TOP",
        maxWin = "5,000x",
        themeColorHex = 0xFFEAB308,
        rtp = "96.5%"
    ),
    SWEET_BONANZA(
        id = "sweet_bonanza",
        title = "Sweet Bonanza",
        provider = "Pragmatic Play",
        subtitle = "ক্যান্ডি পপ ও ১০০x সুগার বম্ব",
        badge = "🍭 CLASSIC",
        maxWin = "21,100x",
        themeColorHex = 0xFFEC4899,
        rtp = "96.48%"
    )
}

// --- Dead Man's Bullet (Skywind) Models ---

enum class BulletSymbol(
    val displayName: String,
    val iconEmoji: String,
    val payout3: Double
) {
    SKULL_OUTLAW("Wanted Skull", "💀", 50.0),
    SHERIFF_BADGE("Sheriff Star", "⭐", 25.0),
    DUAL_REVOLVERS("Colt 45 Revolver", "🔫", 15.0),
    DYNAMITE("TNT Dynamite", "🧨", 10.0),
    CASH_BAG("Bounty Bag", "💰", 6.0),
    WHISKEY("Saloon Whiskey", "🍾", 3.0),
    HORSESHOE("Lucky Horseshoe", "🧲", 2.0),
    SILVER_BULLET("Silver Bullet", "🔘", 0.0), // Respin prize
    GOLD_BULLET("Golden Bullet", "🟡", 0.0),   // Multiplier bullet
    BOUNTY_COLLECTOR("Dead Man Bounty", "🎯", 0.0),
    WILD_OUTLAW("Wild Badge", "🤠", 0.0)
}

data class BulletCard(
    val id: String = UUID.randomUUID().toString(),
    val symbol: BulletSymbol,
    val prizeMultiplier: Int = 0, // for silver/gold bullet
    val isWinning: Boolean = false,
    val isLocked: Boolean = false
)

data class BulletSpinResult(
    val grid: List<List<BulletCard>>,
    val totalWin: Double,
    val winType: String,
    val triggeredRespins: Boolean,
    val respinsAwarded: Int,
    val finalMultiplier: Int
)

// --- Gates of Olympus Models ---

enum class OlympusSymbol(
    val displayName: String,
    val iconEmoji: String,
    val payBase: Double
) {
    CROWN("Golden Crown", "👑", 20.0),
    HOURGLASS("Zeus Hourglass", "⏳", 12.0),
    RING("Thunder Ring", "💍", 8.0),
    CHALICE("Golden Chalice", "🏆", 5.0),
    RED_GEM("Ruby Gem", "💎", 4.0),
    PURPLE_GEM("Amethyst Gem", "🔮", 3.0),
    YELLOW_GEM("Topaz Gem", "✨", 2.0),
    GREEN_GEM("Emerald Gem", "💚", 1.5),
    BLUE_GEM("Sapphire Gem", "🔷", 1.0),
    ZEUS("Zeus Scatter", "⚡", 0.0)
}

data class OlympusCell(
    val id: String = UUID.randomUUID().toString(),
    val symbol: OlympusSymbol,
    val isWinning: Boolean = false,
    val multiplier: Int = 1
)

data class OlympusSpinResult(
    val grid: List<List<OlympusCell>>,
    val totalWin: Double,
    val zeusMultiplier: Int,
    val triggeredFreeSpins: Boolean
)

// --- Sweet Bonanza Models ---

enum class SweetSymbol(
    val displayName: String,
    val iconEmoji: String,
    val payBase: Double
) {
    HEART("Red Heart Candy", "💖", 25.0),
    PURPLE("Purple Candy", "💜", 15.0),
    GREEN("Green Candy", "💚", 10.0),
    BLUE("Blue Candy", "💙", 6.0),
    APPLE("Sweet Apple", "🍎", 4.0),
    PLUM("Juicy Plum", "🫐", 3.0),
    WATERMELON("Watermelon", "🍉", 2.5),
    GRAPES("Purple Grapes", "🍇", 2.0),
    BANANA("Banana", "🍌", 1.0),
    LOLLIPOP("Swirl Lollipop", "🍭", 0.0),
    SUGAR_BOMB("Sugar Bomb", "💣", 0.0)
}

data class SweetCell(
    val id: String = UUID.randomUUID().toString(),
    val symbol: SweetSymbol,
    val bombMultiplier: Int = 1,
    val isWinning: Boolean = false
)

data class SweetSpinResult(
    val grid: List<List<SweetCell>>,
    val totalWin: Double,
    val bombMultiplier: Int,
    val triggeredFreeSpins: Boolean
)
