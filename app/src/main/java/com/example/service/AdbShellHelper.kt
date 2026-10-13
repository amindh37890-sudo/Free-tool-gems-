package com.example.service

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

data class ShellExecutionResult(
    val success: Boolean,
    val exitCode: Int,
    val output: String,
    val error: String
)

object AdbShellHelper {

    /**
     * Executes a command string in the Android shell environment.
     * Can execute local shell commands, and if elevated via ADB / Shizuku / su,
     * it executes with system-level privileges.
     */
    suspend fun executeCommand(command: String, timeoutSeconds: Long = 5): ShellExecutionResult =
        withContext(Dispatchers.IO) {
            try {
                val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", command))
                val outputBuilder = StringBuilder()
                val errorBuilder = StringBuilder()

                val outputReader = BufferedReader(InputStreamReader(process.inputStream))
                val errorReader = BufferedReader(InputStreamReader(process.errorStream))

                var line: String?
                while (outputReader.readLine().also { line = it } != null) {
                    outputBuilder.appendLine(line)
                }

                while (errorReader.readLine().also { line = it } != null) {
                    errorBuilder.appendLine(line)
                }

                val completed = process.waitFor(timeoutSeconds, TimeUnit.SECONDS)
                if (!completed) {
                    process.destroyForcibly()
                    return@withContext ShellExecutionResult(
                        success = false,
                        exitCode = -1,
                        output = outputBuilder.toString().trim(),
                        error = "Command timed out after $timeoutSeconds seconds"
                    )
                }

                val exitCode = process.exitValue()
                ShellExecutionResult(
                    success = exitCode == 0,
                    exitCode = exitCode,
                    output = outputBuilder.toString().trim(),
                    error = errorBuilder.toString().trim()
                )
            } catch (e: Throwable) {
                ShellExecutionResult(
                    success = false,
                    exitCode = -1,
                    output = "",
                    error = e.localizedMessage ?: "Unknown shell execution error"
                )
            }
        }

    /**
     * Clears background tasks and memory to free up CPU and GPU bandwidth for gaming.
     */
    suspend fun clearBackgroundProcesses(): ShellExecutionResult {
        val command = "am kill-all"
        return executeCommand(command)
    }

    /**
     * Locks peak and minimum refresh rates to 120Hz for buttery-smooth gameplay.
     */
    suspend fun setMaxRefreshRate(fps: Float = 120.0f): ShellExecutionResult {
        val command = "settings put system peak_refresh_rate $fps && settings put system min_refresh_rate $fps"
        return executeCommand(command)
    }

    /**
     * Sets system window and transition animation scales to 0.5x for snappy, PC-like responsiveness.
     */
    suspend fun setFastAnimationScales(): ShellExecutionResult {
        val command = "settings put global window_animation_scale 0.5 && " +
                "settings put global transition_animation_scale 0.5 && " +
                "settings put global animator_duration_scale 0.5"
        return executeCommand(command)
    }

    /**
     * Assigns high-performance game mode priority to the game package (Android 12+ Game Mode API).
     * Mode 2 = PERFORMANCE mode.
     */
    suspend fun setGamePerformancePriority(packageName: String): ShellExecutionResult {
        val command = "cmd game mode performance $packageName"
        return executeCommand(command)
    }

    /**
     * Optimizes game engine bytecode ahead-of-time (AOT) to eliminate micro-stutters.
     */
    suspend fun compileGameForSpeed(packageName: String): ShellExecutionResult {
        val command = "cmd package compile -m speed -f $packageName"
        return executeCommand(command)
    }

    /**
     * Disables thermal throttling limits safely to maintain frame stability during long matches.
     */
    suspend fun overrideThermalStatus(status: Int = 0): ShellExecutionResult {
        val command = "cmd thermalservice override-status $status"
        return executeCommand(command)
    }

    /**
     * Checks whether shell access is functioning on the device.
     */
    suspend fun checkShellAccess(): Boolean {
        val result = executeCommand("echo 'SHELL_ACTIVE'")
        return result.success && result.output.contains("SHELL_ACTIVE")
    }
}
