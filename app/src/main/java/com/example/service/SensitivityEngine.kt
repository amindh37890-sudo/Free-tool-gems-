package com.example.service

import com.example.model.PcSensitivityConfig

object SensitivityEngine {

    /**
     * Converts PC mouse DPI + In-game sensitivity to Mobile touch and gyroscope sensitivity
     * to replicate the exact muscle memory, turning speed, and 360-degree rotation of a PC gaming mouse.
     */
    fun calculateMobileSensitivity(
        pcDpi: Int,
        pcInGameSens: Float,
        gamePreset: String
    ): PcSensitivityConfig {
        val eDpi = pcDpi * pcInGameSens

        // Normalized ratio for mobile touch sensors
        // Lower eDpi on PC means higher precision, mobile needs scaled multiplier
        val general = (eDpi * 0.08f).coerceIn(60f, 100f)
        val redDot = (general * 0.85f).coerceIn(45f, 95f)
        val scope2x = (general * 0.72f).coerceIn(35f, 85f)
        val scope4x = (general * 0.55f).coerceIn(25f, 75f)
        val gyro = (general * 3.4f).coerceIn(200f, 400f)

        // Recommended system DPI (Android Smallest Width in Developer Options)
        // Default standard phone is 392-411. PC-like tight elements recommend 440-480 dpi.
        val recommendedDpi = when {
            pcDpi <= 400 -> 480
            pcDpi <= 800 -> 440
            else -> 420
        }

        return PcSensitivityConfig(
            pcDpi = pcDpi,
            pcInGameSens = pcInGameSens,
            targetGame = gamePreset,
            convertedGeneralSens = general,
            convertedRedDotSens = redDot,
            converted2xSens = scope2x,
            converted4xSens = scope4x,
            convertedGyroSens = gyro,
            customDpiRecommendation = recommendedDpi
        )
    }

    /**
     * Generates PC 4:3 or 16:10 stretched view resolution commands for Android
     * Makes character models wider (hitbox visual magnification) just like CS:GO / Valorant / PUBG PC.
     */
    fun calculatePcStretchedResolution(
        screenWidth: Int,
        screenHeight: Int,
        ratioType: String // "4:3", "16:10", "1:1"
    ): Triple<Int, Int, String> {
        val maxDim = maxOf(screenWidth, screenHeight)
        val minDim = minOf(screenWidth, screenHeight)

        val (targetW, targetH) = when (ratioType) {
            "4:3" -> Pair((maxDim * 0.75f).toInt(), maxDim)
            "16:10" -> Pair((maxDim * 0.625f).toInt(), maxDim)
            "1:1" -> Pair(maxDim, maxDim)
            else -> Pair(minDim, maxDim)
        }

        val adbCmd = "adb shell wm size ${targetW}x${targetH} && adb shell wm density 440"
        return Triple(targetW, targetH, adbCmd)
    }
}
