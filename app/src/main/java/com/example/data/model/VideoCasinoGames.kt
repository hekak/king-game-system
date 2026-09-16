package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class VideoGameCategory(val displayName: String, val icon: String) {
    HOT("Hot", "🔥"),
    SLOTS("Slots", "🎰"),
    FISH("Fish", "🐟"),
    LIVE("Live", "🎲"),
    POKER("Poker", "♠️"),
    SPORTS("Sports", "⚽")
}

data class VideoGameModel(
    val id: String,
    val title: String,
    val provider: String,          // e.g. "JILI", "PG Soft", "Pragmatic Play", "Spribe"
    val providerShort: String,     // e.g. "JL", "PG", "PP", "SPRIBE", "BNG"
    val category: VideoGameCategory,
    val badge: String = "",        // e.g. "10000X", "200X", "15X", "HOT", "NEW"
    val maxWin: String = "10,000x",
    val primaryColorHex: Long = 0xFF8B5CF6,
    val secondaryColorHex: Long = 0xFF4C1D95,
    val accentColorHex: Long = 0xFFF59E0B,
    val iconEmoji: String = "🎰",
    val description: String = "",
    val nativeGameType: CasinoGame? = null,
    val isHot: Boolean = false,
    val isRecent: Boolean = false
)

object VideoGameCatalog {
    val allGames: List<VideoGameModel> = listOf(
        // --- 1. Super Ace (Native Slot + JL) ---
        VideoGameModel(
            id = "super_ace",
            title = "Super Ace",
            provider = "JILI",
            providerShort = "JL",
            category = VideoGameCategory.HOT,
            badge = "JL",
            maxWin = "1,500x",
            primaryColorHex = 0xFF7C3AED,
            secondaryColorHex = 0xFF2E1065,
            accentColorHex = 0xFFFFD700,
            iconEmoji = "👑",
            description = "১০২৪ ওয়েজ গোল্ডেন ওয়াইল্ড ফ্লিপ ও মাল্টিপ্লায়ার কম্বো",
            nativeGameType = CasinoGame.SUPER_ACE,
            isHot = true
        ),

        // --- 2. Money Coming (JL) ---
        VideoGameModel(
            id = "money_coming",
            title = "Money Coming",
            provider = "JILI",
            providerShort = "JL",
            category = VideoGameCategory.HOT,
            badge = "10000X",
            maxWin = "10,000x",
            primaryColorHex = 0xFF059669,
            secondaryColorHex = 0xFF064E3B,
            accentColorHex = 0xFF34D399,
            iconEmoji = "💵",
            description = "বিগ মানি হুইল ও ১০,০০০ গুণ ক্যাশ জ্যাকপট",
            isHot = true
        ),

        // --- 3. Super Elements (JL) ---
        VideoGameModel(
            id = "super_elements",
            title = "Super Elements",
            provider = "JILI",
            providerShort = "JL",
            category = VideoGameCategory.HOT,
            badge = "200X",
            maxWin = "200x",
            primaryColorHex = 0xFFD97706,
            secondaryColorHex = 0xFF78350F,
            accentColorHex = 0xFFFBBF24,
            iconEmoji = "⚡",
            description = "অগ্নি ও বজ্রপাত এলিমেন্টাল বোনাস স্ট্রাইক",
            isHot = true
        ),

        // --- 4. Fortune Gems 2 (JL) ---
        VideoGameModel(
            id = "fortune_gems_2",
            title = "Fortune Gems 2",
            provider = "JILI",
            providerShort = "JL",
            category = VideoGameCategory.HOT,
            badge = "15X",
            maxWin = "15x",
            primaryColorHex = 0xFFB45309,
            secondaryColorHex = 0xFF451A03,
            accentColorHex = 0xFFF59E0B,
            iconEmoji = "💎",
            description = "মায়া গোল্ডেন গেম ও প্রিমিয়াম জেম গুণক",
            isHot = true
        ),

        // --- 5. Boxing King (JL) ---
        VideoGameModel(
            id = "boxing_king",
            title = "Boxing King",
            provider = "JILI",
            providerShort = "JL",
            category = VideoGameCategory.HOT,
            badge = "JL",
            maxWin = "2,000x",
            primaryColorHex = 0xFFDC2626,
            secondaryColorHex = 0xFF7F1D1D,
            accentColorHex = 0xFFF87171,
            iconEmoji = "🥊",
            description = "নকআউট পাঞ্চ, বেল্ট মাল্টিপ্লায়ার ও ফ্রি রাউন্ড",
            isHot = true
        ),

        // --- 6. Aviator (Spribe) ---
        VideoGameModel(
            id = "aviator",
            title = "Aviator",
            provider = "Spribe",
            providerShort = "SPRIBE",
            category = VideoGameCategory.HOT,
            badge = "HOT",
            maxWin = "100x+",
            primaryColorHex = 0xFFE11D48,
            secondaryColorHex = 0xFF881337,
            accentColorHex = 0xFFFB7185,
            iconEmoji = "✈️",
            description = "রেড ক্র্যাশ প্লেন উড়ার আগেই ক্যাশআউট করুন",
            isHot = true
        ),

        // --- 7. High Flyer (Pragmatic Play) ---
        VideoGameModel(
            id = "high_flyer",
            title = "High Flyer",
            provider = "Pragmatic Play",
            providerShort = "PP",
            category = VideoGameCategory.HOT,
            badge = "NEW",
            maxWin = "5,000x",
            primaryColorHex = 0xFF2563EB,
            secondaryColorHex = 0xFF1E3A8A,
            accentColorHex = 0xFF60A5FA,
            iconEmoji = "🚀",
            description = "সুপারসনিক রকেট ফ্লাইট ও ইনস্ট্যান্ট ক্যাশ মাল্টিপ্লায়ার",
            isHot = true
        ),

        // --- 8. Wild Bounty Showdown (PG Soft) ---
        VideoGameModel(
            id = "wild_bounty",
            title = "Wild Bounty",
            provider = "PG Soft",
            providerShort = "PG",
            category = VideoGameCategory.HOT,
            badge = "PG",
            maxWin = "5,000x",
            primaryColorHex = 0xFF92400E,
            secondaryColorHex = 0xFF451A03,
            accentColorHex = 0xFFFBBF24,
            iconEmoji = "🤠",
            description = "কাউগার্ল বাউন্টি রিভলভার ও গোল্ডেন ওয়াইল্ড",
            isHot = true
        ),

        // --- 9. Fortune Gems (JL) ---
        VideoGameModel(
            id = "fortune_gems",
            title = "Fortune Gems",
            provider = "JILI",
            providerShort = "JL",
            category = VideoGameCategory.HOT,
            badge = "JL",
            maxWin = "375x",
            primaryColorHex = 0xFFCA8A04,
            secondaryColorHex = 0xFF713F12,
            accentColorHex = 0xFFFACC15,
            iconEmoji = "🗿",
            description = "ইনকা গোল্ডেন সান ফেস ও ক্রিস্টাল কম্বিনেশন",
            isHot = true
        ),

        // --- 10. Magic Ace (JL) ---
        VideoGameModel(
            id = "magic_ace",
            title = "Magic Ace",
            provider = "JILI",
            providerShort = "JL",
            category = VideoGameCategory.SLOTS,
            badge = "JL",
            maxWin = "1,500x",
            primaryColorHex = 0xFF6D28D9,
            secondaryColorHex = 0xFF3B0764,
            accentColorHex = 0xFFA78BFA,
            iconEmoji = "🃏",
            description = "জাদুকরী তাসের ক্যাসকেড ও এক্সট্রা ওয়াইল্ড কার্ড",
            isHot = false
        ),

        // --- 11. FlyX (Buck Stakes) ---
        VideoGameModel(
            id = "flyx",
            title = "FlyX",
            provider = "Buck Stakes",
            providerShort = "BT",
            category = VideoGameCategory.HOT,
            badge = "HOT",
            maxWin = "10,000x",
            primaryColorHex = 0xFFEA580C,
            secondaryColorHex = 0xFF7C2D12,
            accentColorHex = 0xFFFB923C,
            iconEmoji = "🛰️",
            description = "মহাকাশ রকেট ক্র্যাশ ও ইন্সট্যান্ট প্রফিট জাম্প",
            isHot = true
        ),

        // --- 12. Dead Man's Bullet (Skywind - Native) ---
        VideoGameModel(
            id = "dead_mans_bullet",
            title = "Dead Man's Bullet",
            provider = "Skywind Group",
            providerShort = "SW",
            category = VideoGameCategory.SLOTS,
            badge = "9,990x",
            maxWin = "9,990x",
            primaryColorHex = 0xFF78350F,
            secondaryColorHex = 0xFF451A03,
            accentColorHex = 0xFFF59E0B,
            iconEmoji = "💀",
            description = "আউটল রিভলভার, বুলেট ক্যাশ ও বাউন্টি রেস্পিনস",
            nativeGameType = CasinoGame.DEAD_MANS_BULLET,
            isHot = true
        ),

        // --- 13. Gates of Olympus (Pragmatic Play - Native) ---
        VideoGameModel(
            id = "gates_of_olympus",
            title = "Gates of Olympus",
            provider = "Pragmatic Play",
            providerShort = "PP",
            category = VideoGameCategory.SLOTS,
            badge = "5,000x",
            maxWin = "5,000x",
            primaryColorHex = 0xFF1D4ED8,
            secondaryColorHex = 0xFF1E3A8A,
            accentColorHex = 0xFF60A5FA,
            iconEmoji = "⚡",
            description = "জিউসের বজ্রপাত ও ৫০০ গুণ পর্যন্ত অরবস ড্রপ",
            nativeGameType = CasinoGame.GATES_OF_OLYMPUS,
            isHot = true
        ),

        // --- 14. Sweet Bonanza (Pragmatic Play - Native) ---
        VideoGameModel(
            id = "sweet_bonanza",
            title = "Sweet Bonanza",
            provider = "Pragmatic Play",
            providerShort = "PP",
            category = VideoGameCategory.SLOTS,
            badge = "21,100x",
            maxWin = "21,100x",
            primaryColorHex = 0xFFBE185D,
            secondaryColorHex = 0xFF700A38,
            accentColorHex = 0xFFF472B6,
            iconEmoji = "🍭",
            description = "সুগার ললিপপ ও ১০০x মাল্টিপ্লায়ার বম্ব টাম্বল",
            nativeGameType = CasinoGame.SWEET_BONANZA,
            isHot = true
        ),

        // --- 15. Fortune Tiger (PG Soft) ---
        VideoGameModel(
            id = "fortune_tiger",
            title = "Fortune Tiger",
            provider = "PG Soft",
            providerShort = "PG",
            category = VideoGameCategory.SLOTS,
            badge = "PG",
            maxWin = "2,500x",
            primaryColorHex = 0xFFC2410C,
            secondaryColorHex = 0xFF7C2D12,
            accentColorHex = 0xFFFB923C,
            iconEmoji = "🐯",
            description = "ভাগ্যবান বাঘের ফুল গ্রিড রিলক ও ১০x সুপার গুণক",
            isHot = true
        ),

        // --- 16. Fortune Dragon (PG Soft) ---
        VideoGameModel(
            id = "fortune_dragon",
            title = "Fortune Dragon",
            provider = "PG Soft",
            providerShort = "PG",
            category = VideoGameCategory.SLOTS,
            badge = "PG",
            maxWin = "2,500x",
            primaryColorHex = 0xFF991B1B,
            secondaryColorHex = 0xFF450A0A,
            accentColorHex = 0xFFF87171,
            iconEmoji = "🐉",
            description = "ড্রাগন বল ফায়ার ও স্পেশাল লাকি স্পিনস",
            isHot = false
        ),

        // --- 17. Fortune Rabbit (PG Soft) ---
        VideoGameModel(
            id = "fortune_rabbit",
            title = "Fortune Rabbit",
            provider = "PG Soft",
            providerShort = "PG",
            category = VideoGameCategory.SLOTS,
            badge = "PG",
            maxWin = "5,000x",
            primaryColorHex = 0xFFDB2777,
            secondaryColorHex = 0xFF831843,
            accentColorHex = 0xFFF472B6,
            iconEmoji = "🐰",
            description = "কিউট খরগোশের গোল্ডেন কয়েন ও প্রাইজ ব্যালন",
            isHot = false
        ),

        // --- 18. Lucky Neko (PG Soft) ---
        VideoGameModel(
            id = "lucky_neko",
            title = "Lucky Neko",
            provider = "PG Soft",
            providerShort = "PG",
            category = VideoGameCategory.SLOTS,
            badge = "PG",
            maxWin = "20,000x",
            primaryColorHex = 0xFF9333EA,
            secondaryColorHex = 0xFF581C87,
            accentColorHex = 0xFFC084FC,
            iconEmoji = "🐱",
            description = "জাপানি মানেকি-নেকো বিড়াল ও আনলিমিটেড ওয়াইল্ড মাল্টিপ্লায়ার",
            isHot = false
        ),

        // --- 19. Treasures of Aztec (PG Soft) ---
        VideoGameModel(
            id = "treasures_of_aztec",
            title = "Treasures of Aztec",
            provider = "PG Soft",
            providerShort = "PG",
            category = VideoGameCategory.SLOTS,
            badge = "PG",
            maxWin = "100,000x",
            primaryColorHex = 0xFF854D0E,
            secondaryColorHex = 0xFF422006,
            accentColorHex = 0xFFFACC15,
            iconEmoji = "🏛️",
            description = "মায়া পিরামিড ট্রপ ও এক্সপান্ডিং মাল্টিপ্লায়ার",
            isHot = true
        ),

        // --- 20. 777 Coins (Booongo / BNG) ---
        VideoGameModel(
            id = "777_coins",
            title = "777 Coins",
            provider = "Booongo",
            providerShort = "BNG",
            category = VideoGameCategory.SLOTS,
            badge = "BNG",
            maxWin = "6,000x",
            primaryColorHex = 0xFFEAB308,
            secondaryColorHex = 0xFF713F12,
            accentColorHex = 0xFFFEF08A,
            iconEmoji = "🎰",
            description = "ক্লাসিক ট্রিপল ৭ ও গোল্ডেন কয়েন হোল্ড অ্যান্ড উইন",
            isHot = false
        ),

        // --- 21. Money Pot (JL) ---
        VideoGameModel(
            id = "money_pot",
            title = "Money Pot",
            provider = "JILI",
            providerShort = "JL",
            category = VideoGameCategory.SLOTS,
            badge = "JL",
            maxWin = "2,000x",
            primaryColorHex = 0xFFB91C1C,
            secondaryColorHex = 0xFF7F1D1D,
            accentColorHex = 0xFFFCA5A5,
            iconEmoji = "🏺",
            description = "গোল্ডেন কড়াই ভর্তি ক্যাশ ও জ্যাকপট পট",
            isHot = false
        ),

        // --- 22. Super Ace Joker (JL) ---
        VideoGameModel(
            id = "super_ace_joker",
            title = "Super Ace Joker",
            provider = "JILI",
            providerShort = "JL",
            category = VideoGameCategory.POKER,
            badge = "JL",
            maxWin = "3,000x",
            primaryColorHex = 0xFF4338CA,
            secondaryColorHex = 0xFF1E1B4B,
            accentColorHex = 0xFF818CF8,
            iconEmoji = "🃏",
            description = "জোকার ওয়াইল্ড ফ্লিপ ও ক্লাসিক সুপ্রিম পোকার পেআউট",
            isHot = false
        ),

        // --- 23. Lucky Jaguar (JL) ---
        VideoGameModel(
            id = "lucky_jaguar",
            title = "Lucky Jaguar",
            provider = "JILI",
            providerShort = "JL",
            category = VideoGameCategory.SLOTS,
            badge = "JL",
            maxWin = "1,000x",
            primaryColorHex = 0xFF047857,
            secondaryColorHex = 0xFF064E3B,
            accentColorHex = 0xFF6EE7B7,
            iconEmoji = "🐆",
            description = "জাগুয়ার ওয়ারিয়র শিল্ড ও ওয়াইল্ড এক্সপ্লোশন",
            isHot = false
        ),

        // --- 24. Jackpot Fishing (JL - Fish) ---
        VideoGameModel(
            id = "jackpot_fishing",
            title = "Jackpot Fishing",
            provider = "JILI",
            providerShort = "JL",
            category = VideoGameCategory.FISH,
            badge = "JL",
            maxWin = "888x",
            primaryColorHex = 0xFF0284C7,
            secondaryColorHex = 0xFF082F49,
            accentColorHex = 0xFF38BDF8,
            iconEmoji = "🦈",
            description = "শার্ক হান্টিং ও ডিপ সি টর্পেডো জ্যাকপট গান",
            isHot = true
        ),

        // --- 25. Ocean King (JL - Fish) ---
        VideoGameModel(
            id = "ocean_king",
            title = "Ocean King",
            provider = "JILI",
            providerShort = "JL",
            category = VideoGameCategory.FISH,
            badge = "JL",
            maxWin = "1,000x",
            primaryColorHex = 0xFF0E7490,
            secondaryColorHex = 0xFF164E63,
            accentColorHex = 0xFF22D3EE,
            iconEmoji = "🐙",
            description = "সমুদ্রের গভীরের বস মনস্টার ও মেগা ড্রাগন ক্যানন",
            isHot = false
        ),

        // --- 26. Mega Wheel (Pragmatic Play - Live) ---
        VideoGameModel(
            id = "mega_wheel",
            title = "Mega Wheel",
            provider = "Pragmatic Play",
            providerShort = "PP",
            category = VideoGameCategory.LIVE,
            badge = "PP",
            maxWin = "500x",
            primaryColorHex = 0xFF4F46E5,
            secondaryColorHex = 0xFF312E81,
            accentColorHex = 0xFFA5B4FC,
            iconEmoji = "🎡",
            description = "লাইভ ডিলার ৫৪-সেগমেন্ট হুইল ও মেগা লাকি নম্বর",
            isHot = true
        ),

        // --- 27. Crazy Time (Evolution - Live) ---
        VideoGameModel(
            id = "crazy_time",
            title = "Crazy Time",
            provider = "Evolution",
            providerShort = "EVO",
            category = VideoGameCategory.LIVE,
            badge = "HOT",
            maxWin = "25,000x",
            primaryColorHex = 0xFFD946EF,
            secondaryColorHex = 0xFF701A75,
            accentColorHex = 0xFFF0ABFC,
            iconEmoji = "🎪",
            description = "পাচিনকো, ক্যাশ হান্ট, কয়েন ফ্লিপ ও ক্রেজি টাইম বোনাস",
            isHot = true
        ),

        // --- 28. Baccarat Classic (Sexy Gaming - Live) ---
        VideoGameModel(
            id = "baccarat_classic",
            title = "Baccarat Classic",
            provider = "Sexy Gaming",
            providerShort = "SEXY",
            category = VideoGameCategory.LIVE,
            badge = "LIVE",
            maxWin = "11x",
            primaryColorHex = 0xFF0F766E,
            secondaryColorHex = 0xFF134E4A,
            accentColorHex = 0xFF2DD4BF,
            iconEmoji = "🎴",
            description = "লাইভ ডিলার ব্যাকারাত, প্লেয়ার ও ব্যাংকার বেটিং",
            isHot = false
        )
    )

    fun getByCategory(category: VideoGameCategory): List<VideoGameModel> {
        return if (category == VideoGameCategory.HOT) {
            allGames.filter { it.isHot }
        } else {
            allGames.filter { it.category == category }
        }
    }
}
