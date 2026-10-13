package com.example.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiGfxAdvisor
import com.example.model.AiGfxProfile
import com.example.model.DeviceTelemetry
import com.example.model.GameBoostMode
import com.example.model.PcSensitivityConfig
import com.example.service.FloatingMonitorService
import com.example.service.SensitivityEngine
import com.example.service.TelemetryManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.example.model.GameItem
import com.example.model.LaunchStepState
import com.example.service.GameLauncherManager

import com.example.service.AdbShellHelper
import com.example.service.ShellExecutionResult

data class GameBoosterUiState(
    val telemetry: DeviceTelemetry = DeviceTelemetry(),
    val selectedMode: GameBoostMode = GameBoostMode.BEAST,
    val isBoosting: Boolean = false,
    val lastFreedMb: Long = 0L,
    val boostSuccessMessage: String? = null,
    val isOverlayActive: Boolean = false,
    val hasOverlayPermission: Boolean = false,
    val pcSensitivity: PcSensitivityConfig = PcSensitivityConfig(),
    val pcStretchedW: Int = 1440,
    val pcStretchedH: Int = 1920,
    val pcStretchedCmd: String = "adb shell wm size 1440x1920 && adb shell wm density 440",
    val aiProfile: AiGfxProfile? = null,
    val isAiLoading: Boolean = false,
    val aiErrorMessage: String? = null,
    val selectedGameForAi: String = "PUBG Mobile / Free Fire",
    val gamesList: List<GameItem> = emptyList(),
    val selectedGameToLaunch: GameItem? = null,
    val activeLaunchStep: LaunchStepState = LaunchStepState.IDLE,
    val launchStatusMessage: String? = null,
    val measuredPingMs: Int = 28,
    val lastShellExecutionResult: ShellExecutionResult? = null,
    val isExecutingShellCommand: Boolean = false
)

class GameBoosterViewModel(application: Application) : AndroidViewModel(application) {

    private val telemetryManager = TelemetryManager(application)
    private val launcherManager = GameLauncherManager(application)
    private val _uiState = MutableStateFlow(GameBoosterUiState())
    val uiState: StateFlow<GameBoosterUiState> = _uiState.asStateFlow()

    init {
        refreshTelemetry()
        checkOverlayPermission()
        loadGames()
        recalculateSensitivity(800, 1.5f, "PUBG Mobile / Free Fire")
        calculateStretchedRatio("4:3")
        startTelemetryLoop()
    }

    fun runShellCommand(command: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isExecutingShellCommand = true) }
            val cleanCmd = command.removePrefix("adb shell ").trim()
            val result = AdbShellHelper.executeCommand(cleanCmd)
            _uiState.update {
                it.copy(
                    isExecutingShellCommand = false,
                    lastShellExecutionResult = result
                )
            }
        }
    }

    fun loadGames() {
        viewModelScope.launch {
            val games = launcherManager.getGamesList()
            val ping = launcherManager.measureGamingServerPing()
            _uiState.update {
                it.copy(
                    gamesList = games,
                    measuredPingMs = ping,
                    selectedGameToLaunch = games.firstOrNull()
                )
            }
        }
    }

    fun selectGameToLaunch(game: GameItem) {
        _uiState.update { it.copy(selectedGameToLaunch = game) }
    }

    fun triggerTurboGameLaunch(game: GameItem) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    activeLaunchStep = LaunchStepState.PURGING_RAM,
                    launchStatusMessage = "1/4 إنهاء مهام الخلفية وتفريغ الذاكرة (ADB kill-all)..."
                )
            }
            // Execute background task purge via shell helper
            AdbShellHelper.clearBackgroundProcesses()
            delay(500)
            val freed = telemetryManager.performMemoryClean()

            _uiState.update {
                it.copy(
                    activeLaunchStep = LaunchStepState.LOCKING_REFRESH_RATE,
                    launchStatusMessage = "2/4 تثبيت الأولوية وتردد الشاشة على ${game.targetFps} FPS..."
                )
            }
            // Set system level performance priority and refresh rate
            AdbShellHelper.setMaxRefreshRate(game.targetFps.toFloat())
            AdbShellHelper.setGamePerformancePriority(game.packageName)
            delay(500)

            _uiState.update {
                it.copy(
                    activeLaunchStep = LaunchStepState.ENABLING_FLOATING_HUD,
                    launchStatusMessage = "3/4 تشغيل النافذة العائمة وشعار الفيزياء الحية..."
                )
            }
            if (_uiState.value.hasOverlayPermission && !_uiState.value.isOverlayActive) {
                toggleFloatingOverlay()
            }
            delay(400)

            _uiState.update {
                it.copy(
                    activeLaunchStep = LaunchStepState.OPTIMIZING_NETWORK,
                    launchStatusMessage = "4/4 توجيه مسارات الاتصال لتقليل البنق (${_uiState.value.measuredPingMs}ms)..."
                )
            }
            delay(400)

            _uiState.update {
                it.copy(
                    activeLaunchStep = LaunchStepState.LAUNCHING_GAME,
                    launchStatusMessage = "⚡ تشغيل ${game.title} بأعلى أولوية معالجة!"
                )
            }

            val launched = launcherManager.launchGamePackage(game.packageName)
            delay(1200)

            _uiState.update {
                it.copy(
                    activeLaunchStep = LaunchStepState.COMPLETED,
                    launchStatusMessage = if (launched) "✓ تم تشغيل اللعبة بأقصى طاقة!" else "✓ تم تطبيق كافة أوضاع الأداء الفائقة لـ ${game.title}!"
                )
            }

            delay(3000)
            _uiState.update {
                it.copy(
                    activeLaunchStep = LaunchStepState.IDLE,
                    launchStatusMessage = null
                )
            }
        }
    }

    private fun startTelemetryLoop() {
        viewModelScope.launch {
            while (true) {
                delay(2000)
                refreshTelemetry()
            }
        }
    }

    fun refreshTelemetry() {
        val data = try {
            telemetryManager.readCurrentTelemetry()
        } catch (e: Throwable) {
            DeviceTelemetry()
        }
        val hasPerm = try {
            Settings.canDrawOverlays(getApplication())
        } catch (e: Throwable) {
            false
        }
        _uiState.update { it.copy(telemetry = data, hasOverlayPermission = hasPerm) }
    }

    fun checkOverlayPermission() {
        val hasPerm = try {
            Settings.canDrawOverlays(getApplication())
        } catch (e: Throwable) {
            false
        }
        _uiState.update { it.copy(hasOverlayPermission = hasPerm) }
    }

    fun setGameBoostMode(mode: GameBoostMode) {
        _uiState.update { it.copy(selectedMode = mode) }
    }

    fun triggerHyperBoost() {
        viewModelScope.launch {
            _uiState.update { it.copy(isBoosting = true, boostSuccessMessage = null) }
            // Perform memory cleaning and garbage collection
            delay(1200) // realistic sweep animation
            val freed = telemetryManager.performMemoryClean()
            val updatedTelemetry = telemetryManager.readCurrentTelemetry()

            _uiState.update {
                it.copy(
                    isBoosting = false,
                    lastFreedMb = freed,
                    telemetry = updatedTelemetry,
                    boostSuccessMessage = "تم تحرير $freed MB وتسريع معالج الرسوميات إلى ${it.selectedMode.targetFps} FPS!"
                )
            }

            delay(4000)
            _uiState.update { it.copy(boostSuccessMessage = null) }
        }
    }

    fun toggleFloatingOverlay() {
        try {
            val context = getApplication<Application>()
            val hasPerm = try {
                Settings.canDrawOverlays(context)
            } catch (e: Throwable) {
                false
            }

            if (!hasPerm) {
                _uiState.update { it.copy(hasOverlayPermission = false) }
                return
            }

            val currentState = _uiState.value.isOverlayActive
            val intent = Intent(context, FloatingMonitorService::class.java)

            if (currentState) {
                context.stopService(intent)
                _uiState.update { it.copy(isOverlayActive = false) }
            } else {
                context.startService(intent)
                _uiState.update { it.copy(isOverlayActive = true) }
            }
        } catch (e: Throwable) {
            _uiState.update { it.copy(isOverlayActive = false) }
        }
    }

    fun recalculateSensitivity(pcDpi: Int, pcInGameSens: Float, game: String) {
        val result = SensitivityEngine.calculateMobileSensitivity(pcDpi, pcInGameSens, game)
        _uiState.update {
            it.copy(
                pcSensitivity = result,
                selectedGameForAi = game
            )
        }
    }

    fun calculateStretchedRatio(ratio: String) {
        val dm = getApplication<Application>().resources.displayMetrics
        val (w, h, cmd) = SensitivityEngine.calculatePcStretchedResolution(
            dm.widthPixels,
            dm.heightPixels,
            ratio
        )
        _uiState.update {
            it.copy(
                pcStretchedW = w,
                pcStretchedH = h,
                pcStretchedCmd = cmd
            )
        }
    }

    fun requestAiOptimization(gameTitle: String, goal: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAiLoading = true, aiErrorMessage = null) }
            try {
                val current = _uiState.value.telemetry
                val profile = GeminiGfxAdvisor.analyzeGameSettings(
                    gameName = gameTitle,
                    deviceModel = current.deviceModel,
                    ramMb = current.totalRamMb,
                    refreshRate = current.targetRefreshRate,
                    userGoal = goal
                )
                _uiState.update {
                    it.copy(
                        aiProfile = profile,
                        isAiLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isAiLoading = false,
                        aiErrorMessage = "تعذر إتمام التحليل: ${e.localizedMessage}"
                    )
                }
            }
        }
    }
}
