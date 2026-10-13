package com.example.model

data class DeviceTelemetry(
    val currentFps: Int = 60,
    val targetRefreshRate: Int = 120,
    val totalRamMb: Long = 8192,
    val availableRamMb: Long = 4096,
    val usedRamPercent: Int = 50,
    val batteryTempCelsius: Float = 36.5f,
    val batteryLevelPercent: Int = 85,
    val isCharging: Boolean = false,
    val thermalStatus: String = "عادي (Normal)",
    val cpuCores: Int = 8,
    val deviceModel: String = "Android Device",
    val androidVersion: String = "Android 14"
)

enum class GameBoostMode(
    val titleAr: String,
    val titleEn: String,
    val descriptionAr: String,
    val targetFps: Int,
    val colorHex: Long
) {
    BEAST(
        titleAr = "وضع الوحش (Beast Mode)",
        titleEn = "Beast Mode",
        descriptionAr = "أقصى أداء مع فتح سقف الفريمات 120 FPS وتنظيف كامل للذاكرة",
        targetFps = 120,
        colorHex = 0xFFFF2A6D
    ),
    ESPORTS(
        titleAr = "وضع البطولات (Esports Pro)",
        titleEn = "Esports Pro",
        descriptionAr = "استقرار فريمات عالي 90 FPS بدون تقطيع وتقليل زمن استجابة اللمس",
        targetFps = 90,
        colorHex = 0xFF00F0FF
    ),
    BALANCED(
        titleAr = "الوضع المتوازن (Balanced)",
        titleEn = "Balanced Mode",
        descriptionAr = "توازن مثالي بين السلاسة 60 FPS ودرجة حرارة الجهاز",
        targetFps = 60,
        colorHex = 0xFF00FFA3
    ),
    BATTERY_SAVER(
        titleAr = "توفير البطارية (Battery Saver)",
        titleEn = "Battery Saver",
        descriptionAr = "سلاسة 45/60 FPS مع تقليل استهلاك الطاقة والحرارة لأطول جلسة لعب",
        targetFps = 45,
        colorHex = 0xFFFF851B
    )
}

data class PcSensitivityConfig(
    val pcDpi: Int = 800,
    val pcInGameSens: Float = 1.5f,
    val targetGame: String = "PUBG Mobile / Free Fire",
    val convertedGeneralSens: Float = 85f,
    val convertedRedDotSens: Float = 72f,
    val converted2xSens: Float = 60f,
    val converted4xSens: Float = 48f,
    val convertedGyroSens: Float = 280f,
    val customDpiRecommendation: Int = 440
)

data class AiGfxProfile(
    val gameTitle: String,
    val recommendedResolution: String,
    val recommendedFps: String,
    val graphicsQuality: String,
    val renderingApi: String,
    val antiAliasing: String,
    val shadows: String,
    val summaryAdviceAr: String,
    val thermalTipAr: String
)
