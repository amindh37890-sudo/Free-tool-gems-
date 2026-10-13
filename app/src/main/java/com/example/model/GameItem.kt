package com.example.model

data class GameItem(
    val id: String,
    val packageName: String,
    val title: String,
    val genre: String,
    val targetFps: Int = 120,
    val isInstalled: Boolean = false,
    val pingMs: Int = 32,
    val customMode: GameBoostMode = GameBoostMode.BEAST,
    val isVulkanEnabled: Boolean = true,
    val iconColorHex: Long = 0xFF00F0FF
)

enum class LaunchStepState {
    IDLE,
    PURGING_RAM,
    LOCKING_REFRESH_RATE,
    ENABLING_FLOATING_HUD,
    OPTIMIZING_NETWORK,
    LAUNCHING_GAME,
    COMPLETED
}
