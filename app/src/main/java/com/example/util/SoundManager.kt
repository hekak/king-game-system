package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * High-performance synthesized arcade audio engine for Super Ace Slot.
 * Generates rich, authentic slot machine sound effects using Android AudioTrack PCM.
 * Zero external MP3/WAV dependencies, instant latency, full offline support.
 */
class SoundManager(private val context: Context) {

    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val sampleRate = 22050
    private val trackCache = ConcurrentHashMap<String, AudioTrack>()

    @Volatile
    var isEnabled: Boolean = true

    init {
        // Pre-warm audio tracks asynchronously
        coroutineScope.launch {
            try {
                preloadSounds()
            } catch (_: Exception) {}
        }
    }

    private fun preloadSounds() {
        getOrCreateTrack("reel_stop") { generateReelStopPcm() }
        getOrCreateTrack("combo_1") { generateComboPcm(523.25, 783.99) }
        getOrCreateTrack("combo_2") { generateComboPcm(659.25, 987.77) }
        getOrCreateTrack("combo_3") { generateComboPcm(783.99, 1174.66) }
        getOrCreateTrack("combo_4") { generateComboPcm(1046.50, 1567.98) }
        getOrCreateTrack("wild_transform") { generateWildTransformPcm() }
        getOrCreateTrack("coin_ping") { generateCoinPingPcm() }
        getOrCreateTrack("button_click") { generateButtonClickPcm() }
        getOrCreateTrack("spin_tick") { generateSpinTickPcm() }
        getOrCreateTrack("big_win") { generateBigWinFanfarePcm() }
        getOrCreateTrack("free_spins") { generateFreeSpinsFanfarePcm() }
        getOrCreateTrack("revolver_spin") { generateRevolverSpinPcm() }
        getOrCreateTrack("gun_cock") { generateGunCockPcm() }
        getOrCreateTrack("gunshot") { generateGunshotRicochetPcm() }
        getOrCreateTrack("western_chord") { generateWesternChordPcm() }
        getOrCreateTrack("thunder") { generateThunderStrikePcm() }
        getOrCreateTrack("candy_pop") { generateCandyPopPcm() }
    }

    private fun getOrCreateTrack(key: String, generator: () -> ShortArray): AudioTrack? {
        val cached = trackCache[key]
        if (cached != null) return cached

        return try {
            val pcm = generator()
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(pcm.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()
            track.write(pcm, 0, pcm.size)
            trackCache[key] = track
            track
        } catch (e: Exception) {
            null
        }
    }

    private fun playTrack(key: String, generator: () -> ShortArray) {
        if (!isEnabled) return
        coroutineScope.launch {
            try {
                val track = getOrCreateTrack(key, generator) ?: return@launch
                synchronized(track) {
                    if (track.state == AudioTrack.STATE_INITIALIZED) {
                        try {
                            track.stop()
                            track.reloadStaticData()
                            track.play()
                        } catch (_: Exception) {}
                    }
                }
            } catch (_: Exception) {}
        }
    }

    /**
     * Play spin start / ticking reels
     */
    fun playSpinStart() {
        playTrack("spin_tick") { generateSpinTickPcm() }
    }

    /**
     * Play solid reel stop / landing thud
     */
    fun playReelStop() {
        playTrack("reel_stop") { generateReelStopPcm() }
    }

    /**
     * Play combo cascade chime with ascending pitch
     */
    fun playCombo(comboStep: Int) {
        val key = when (comboStep) {
            1 -> "combo_1"
            2 -> "combo_2"
            3 -> "combo_3"
            else -> "combo_4"
        }
        playTrack(key) {
            when (comboStep) {
                1 -> generateComboPcm(523.25, 783.99)
                2 -> generateComboPcm(659.25, 987.77)
                3 -> generateComboPcm(783.99, 1174.66)
                else -> generateComboPcm(1046.50, 1567.98)
            }
        }
    }

    /**
     * Play Golden Wild card transformation sparkle
     */
    fun playWildTransform() {
        playTrack("wild_transform") { generateWildTransformPcm() }
    }

    /**
     * Play celebratory Big Win fanfare
     */
    fun playBigWin() {
        playTrack("big_win") { generateBigWinFanfarePcm() }
    }

    /**
     * Play Free Spins bonus trigger fanfare
     */
    fun playFreeSpinsTrigger() {
        playTrack("free_spins") { generateFreeSpinsFanfarePcm() }
    }

    /**
     * Play coin collect / chip drop
     */
    fun playCoinDrop(burstCount: Int = 3) {
        if (!isEnabled) return
        coroutineScope.launch {
            repeat(burstCount) {
                playTrack("coin_ping") { generateCoinPingPcm() }
                delay(75)
            }
        }
    }

    /**
     * Play crisp UI button click
     */
    fun playButtonClick() {
        playTrack("button_click") { generateButtonClickPcm() }
    }

    /**
     * Western revolver cylinder spinning rattle (Dead Man's Bullet)
     */
    fun playRevolverSpin() {
        playTrack("revolver_spin") { generateRevolverSpinPcm() }
    }

    /**
     * Heavy revolver hammer cocking mechanical click
     */
    fun playGunCock() {
        playTrack("gun_cock") { generateGunCockPcm() }
    }

    /**
     * Booming gunshot blast with authentic bullet ricochet whine
     */
    fun playGunshotRicochet() {
        playTrack("gunshot") { generateGunshotRicochetPcm() }
    }

    /**
     * Moody spaghetti western outlaw acoustic guitar twang
     */
    fun playWesternChord() {
        playTrack("western_chord") { generateWesternChordPcm() }
    }

    /**
     * Zeus electric lightning strike and deep rolling thunder
     */
    fun playThunderStrike() {
        playTrack("thunder") { generateThunderStrikePcm() }
    }

    /**
     * Sweet Bonanza candy bubble pop with sugary resonance
     */
    fun playCandyPop() {
        playTrack("candy_pop") { generateCandyPopPcm() }
    }

    fun release() {
        coroutineScope.launch {
            trackCache.values.forEach { track ->
                try {
                    track.stop()
                    track.release()
                } catch (_: Exception) {}
            }
            trackCache.clear()
        }
    }

    // --- Synthesizer Waveform Generators (Rich Casino Acoustics) ---

    private fun generateSpinTickPcm(): ShortArray {
        // Crisp, mechanical reel rolling tick with warm wooden click and soft bell chime
        val durationMs = 320
        val totalSamples = (sampleRate * durationMs / 1000)
        val pcm = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            // Pleasant rhythmic mechanical tick with warm resonance
            val pulse = (sin(2 * PI * 28.0 * t) > 0.0)
            val freq = if (pulse) 620.0 else 440.0
            val env = exp(-t * 6.5) * (if (pulse) 0.85 else 0.35)
            // Fundamental + 2nd overtone for warmth
            val wave = 0.7 * sin(2 * PI * freq * t) + 0.3 * sin(2 * PI * (freq * 1.5) * t)
            val sample = (wave * env * 22000).toInt()
            pcm[i] = sample.coerceIn(-32767, 32767).toShort()
        }
        return pcm
    }

    private fun generateReelStopPcm(): ShortArray {
        // Deep, satisfying mechanical reel lock with wooden felt thud and metallic chime
        val durationMs = 180
        val totalSamples = (sampleRate * durationMs / 1000)
        val pcm = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val freq = 340.0 - (t * 1400.0).coerceAtLeast(0.0)
            val envThud = exp(-t * 26.0)
            val envChime = exp(-t * 12.0)
            // Low thud + subtle metallic sparkle
            val thud = sin(2 * PI * freq.coerceAtLeast(60.0) * t) * envThud * 0.7
            val sparkle = sin(2 * PI * 1320.0 * t) * envChime * 0.3
            val sample = ((thud + sparkle) * 26000).toInt()
            pcm[i] = sample.coerceIn(-32767, 32767).toShort()
        }
        return pcm
    }

    private fun generateComboPcm(f1: Double, f2: Double): ShortArray {
        // Melodic celesta / glockenspiel bell chime with shimmering natural decay and overtone
        val durationMs = 460
        val totalSamples = (sampleRate * durationMs / 1000)
        val pcm = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val env = exp(-t * 6.0)
            // Rich multi-harmonic bell chime: fundamental + perfect 5th + octave overtone
            val wave = 0.55 * sin(2 * PI * f1 * t) +
                       0.30 * sin(2 * PI * f2 * t) +
                       0.15 * sin(2 * PI * (f1 * 2.0) * t)
            val sample = (wave * env * 25000).toInt()
            pcm[i] = sample.coerceIn(-32767, 32767).toShort()
        }
        return pcm
    }

    private fun generateWildTransformPcm(): ShortArray {
        // Magical golden fairy sparkle with smooth upward arpeggiated glissando & gentle chorus
        val durationMs = 520
        val totalSamples = (sampleRate * durationMs / 1000)
        val pcm = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val progress = (t / (durationMs / 1000.0)).coerceIn(0.0, 1.0)
            // Upward frequency glissando
            val baseFreq = 587.33 + progress * 1400.0 // D5 to D7
            val vibrato = sin(2 * PI * 24.0 * t) * 60.0
            val env = sin(PI * progress) * exp(-progress * 0.8)
            val wave = 0.7 * sin(2 * PI * (baseFreq + vibrato) * t) +
                       0.3 * sin(2 * PI * ((baseFreq + vibrato) * 1.5) * t)
            val sample = (wave * env * 25000).toInt()
            pcm[i] = sample.coerceIn(-32767, 32767).toShort()
        }
        return pcm
    }

    private fun generateCoinPingPcm(): ShortArray {
        // Crisp, crystalline casino chip / gold coin clink with realistic high ringing resonance
        val durationMs = 160
        val totalSamples = (sampleRate * durationMs / 1000)
        val pcm = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val env = exp(-t * 22.0)
            val wave = 0.55 * sin(2 * PI * 2093.0 * t) + // C7
                       0.35 * sin(2 * PI * 3135.96 * t) + // G7
                       0.10 * sin(2 * PI * 4186.0 * t)   // C8
            val sample = (wave * env * 24000).toInt()
            pcm[i] = sample.coerceIn(-32767, 32767).toShort()
        }
        return pcm
    }

    private fun generateButtonClickPcm(): ShortArray {
        // Subtle, smooth haptic-style button click
        val durationMs = 55
        val totalSamples = (sampleRate * durationMs / 1000)
        val pcm = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val env = exp(-t * 60.0)
            val sample = (sin(2 * PI * 720.0 * t) * env * 18000).toInt()
            pcm[i] = sample.coerceIn(-32767, 32767).toShort()
        }
        return pcm
    }

    private fun generateBigWinFanfarePcm(): ShortArray {
        // Multi-note arpeggio fanfare: C5 (523Hz), E5 (659Hz), G5 (784Hz), C6 (1046Hz), E6 (1318Hz)
        val notes = listOf(
            Pair(523.25, 90),
            Pair(659.25, 90),
            Pair(783.99, 90),
            Pair(1046.50, 140),
            Pair(1318.51, 380)
        )
        val totalDurationMs = notes.sumOf { it.second }
        val totalSamples = (sampleRate * totalDurationMs / 1000)
        val pcm = ShortArray(totalSamples)

        var sampleOffset = 0
        for ((freq, noteDur) in notes) {
            val noteSamples = (sampleRate * noteDur / 1000)
            for (i in 0 until noteSamples) {
                val t = i.toDouble() / sampleRate
                val env = exp(-t * 5.0)
                val wave = 0.75 * sin(2 * PI * freq * t) + 0.25 * sin(2 * PI * freq * 2 * t)
                val sample = (wave * env * 27000).toInt()
                val destIdx = sampleOffset + i
                if (destIdx < totalSamples) {
                    pcm[destIdx] = sample.coerceIn(-32767, 32767).toShort()
                }
            }
            sampleOffset += noteSamples
        }
        return pcm
    }

    private fun generateFreeSpinsFanfarePcm(): ShortArray {
        // Triumphant trumpet fanfare: G4 (392Hz), C5 (523Hz), E5 (659Hz), G5 (784Hz)
        val notes = listOf(
            Pair(392.00, 120),
            Pair(523.25, 120),
            Pair(659.25, 140),
            Pair(783.99, 450)
        )
        val totalDurationMs = notes.sumOf { it.second }
        val totalSamples = (sampleRate * totalDurationMs / 1000)
        val pcm = ShortArray(totalSamples)

        var sampleOffset = 0
        for ((freq, noteDur) in notes) {
            val noteSamples = (sampleRate * noteDur / 1000)
            for (i in 0 until noteSamples) {
                val t = i.toDouble() / sampleRate
                val env = exp(-t * 4.0)
                val wave = 0.7 * sin(2 * PI * freq * t) + 0.3 * sin(2 * PI * freq * 2 * t)
                val sample = (wave * env * 27000).toInt()
                val destIdx = sampleOffset + i
                if (destIdx < totalSamples) {
                    pcm[destIdx] = sample.coerceIn(-32767, 32767).toShort()
                }
            }
            sampleOffset += noteSamples
        }
        return pcm
    }

    private fun generateRevolverSpinPcm(): ShortArray {
        // Fast metallic chamber cylinder clicking and spinning
        val durationMs = 420
        val totalSamples = (sampleRate * durationMs / 1000)
        val pcm = ShortArray(totalSamples)
        val numClicks = 9
        for (click in 0 until numClicks) {
            val clickStart = (click * totalSamples / numClicks)
            val clickDur = (sampleRate * 18 / 1000)
            for (i in 0 until clickDur) {
                val idx = clickStart + i
                if (idx < totalSamples) {
                    val t = i.toDouble() / sampleRate
                    val env = exp(-t * 120.0)
                    val freq = 1400.0 + click * 70.0
                    val sample = (sin(2 * PI * freq * t) * env * 24000).toInt()
                    pcm[idx] = sample.coerceIn(-32767, 32767).toShort()
                }
            }
        }
        return pcm
    }

    private fun generateGunCockPcm(): ShortArray {
        // Double metallic mechanical snap (hammer pulled back)
        val durationMs = 220
        val totalSamples = (sampleRate * durationMs / 1000)
        val pcm = ShortArray(totalSamples)
        // Click 1 at t=0ms, Click 2 at t=80ms
        val offsets = listOf(0, (sampleRate * 80 / 1000))
        for ((idxOffset, freq) in offsets.zip(listOf(880.0, 1320.0))) {
            val dur = (sampleRate * 35 / 1000)
            for (i in 0 until dur) {
                val idx = idxOffset + i
                if (idx < totalSamples) {
                    val t = i.toDouble() / sampleRate
                    val env = exp(-t * 90.0)
                    val sample = ((sin(2 * PI * freq * t) + 0.4 * sin(2 * PI * freq * 2.5 * t)) * env * 26000).toInt()
                    pcm[idx] = sample.coerceIn(-32767, 32767).toShort()
                }
            }
        }
        return pcm
    }

    private fun generateGunshotRicochetPcm(): ShortArray {
        // Boom crack (0-160ms) + high frequency ricochet zing (100-500ms)
        val durationMs = 520
        val totalSamples = (sampleRate * durationMs / 1000)
        val pcm = ShortArray(totalSamples)
        val rng = kotlin.random.Random(12345)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            // Gunshot crack + deep sub bass
            val crackEnv = exp(-t * 22.0)
            val bassFreq = 120.0 - (t * 200.0).coerceAtLeast(0.0)
            val noise = (rng.nextDouble() * 2.0 - 1.0) * 0.4
            val blast = (sin(2 * PI * bassFreq.coerceAtLeast(40.0) * t) + noise) * crackEnv

            // Ricochet whistle: sweeps down from 3200Hz to 1200Hz with vibrato
            var rico = 0.0
            if (t > 0.08) {
                val rt = t - 0.08
                val ricoEnv = exp(-rt * 5.5) * (1.0 - exp(-rt * 30.0))
                val ricoFreq = 3200.0 - rt * 3400.0 + sin(2 * PI * 40.0 * rt) * 120.0
                rico = sin(2 * PI * ricoFreq.coerceAtLeast(600.0) * rt) * ricoEnv * 0.85
            }

            val sample = ((blast * 0.75 + rico * 0.55) * 28000).toInt()
            pcm[i] = sample.coerceIn(-32767, 32767).toShort()
        }
        return pcm
    }

    private fun generateWesternChordPcm(): ShortArray {
        // Outlaw acoustic minor chord (D3: 146.8Hz, A3: 220Hz, D4: 293.7Hz, F4: 349.2Hz)
        val durationMs = 650
        val totalSamples = (sampleRate * durationMs / 1000)
        val pcm = ShortArray(totalSamples)
        val freqs = listOf(146.83, 220.00, 293.66, 349.23)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val env = exp(-t * 3.8)
            var wave = 0.0
            for ((idx, f) in freqs.withIndex()) {
                val tremolo = 1.0 + 0.15 * sin(2 * PI * 6.0 * t)
                wave += sin(2 * PI * f * t) * (0.35 / (idx * 0.3 + 1.0)) * tremolo
            }
            val sample = (wave * env * 25000).toInt()
            pcm[i] = sample.coerceIn(-32767, 32767).toShort()
        }
        return pcm
    }

    private fun generateThunderStrikePcm(): ShortArray {
        // Sharp electric zap + low frequency rumbling thunder
        val durationMs = 580
        val totalSamples = (sampleRate * durationMs / 1000)
        val pcm = ShortArray(totalSamples)
        val rng = kotlin.random.Random(6789)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val zapEnv = exp(-t * 45.0)
            val zap = (rng.nextDouble() * 2.0 - 1.0) * zapEnv
            val thunderEnv = exp(-t * 4.5) * (1.0 - exp(-t * 20.0))
            val rumbleFreq = 58.0 + sin(2 * PI * 14.0 * t) * 18.0
            val rumble = sin(2 * PI * rumbleFreq * t) * thunderEnv
            val sample = ((zap * 0.6 + rumble * 0.8) * 26000).toInt()
            pcm[i] = sample.coerceIn(-32767, 32767).toShort()
        }
        return pcm
    }

    private fun generateCandyPopPcm(): ShortArray {
        // Cheerful sweet bubble pop (rapid pitch glide 700Hz to 1600Hz)
        val durationMs = 190
        val totalSamples = (sampleRate * durationMs / 1000)
        val pcm = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val progress = t / (durationMs / 1000.0)
            val freq = 700.0 + progress * 950.0
            val env = sin(PI * progress.coerceIn(0.0, 1.0))
            val wave = 0.7 * sin(2 * PI * freq * t) + 0.3 * sin(2 * PI * (freq * 2.0) * t)
            val sample = (wave * env * 24000).toInt()
            pcm[i] = sample.coerceIn(-32767, 32767).toShort()
        }
        return pcm
    }

    companion object {
        @Volatile
        private var INSTANCE: SoundManager? = null

        fun getInstance(context: Context): SoundManager {
            return INSTANCE ?: synchronized(this) {
                val instance = SoundManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
