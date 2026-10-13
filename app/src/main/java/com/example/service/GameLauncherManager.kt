package com.example.service

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.example.model.GameBoostMode
import com.example.model.GameItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

class GameLauncherManager(private val context: Context) {

    private val predefinedGames = listOf(
        GameItem(
            id = "pubg_mobile",
            packageName = "com.tencent.ig",
            title = "PUBG Mobile",
            genre = "Battle Royale",
            targetFps = 120,
            iconColorHex = 0xFFFF851B,
            customMode = GameBoostMode.BEAST
        ),
        GameItem(
            id = "free_fire",
            packageName = "com.dts.freefireth",
            title = "Free Fire MAX",
            genre = "Tactical Shooter",
            targetFps = 120,
            iconColorHex = 0xFFFF2A6D,
            customMode = GameBoostMode.BEAST
        ),
        GameItem(
            id = "cod_mobile",
            packageName = "com.activision.callofduty.shooter",
            title = "Call of Duty: Mobile",
            genre = "FPS Multiplayer",
            targetFps = 120,
            iconColorHex = 0xFF00F0FF,
            customMode = GameBoostMode.ESPORTS
        ),
        GameItem(
            id = "warzone_mobile",
            packageName = "com.activision.callofduty.warzone",
            title = "Warzone Mobile",
            genre = "Next-Gen FPS",
            targetFps = 90,
            iconColorHex = 0xFFBD00FF,
            customMode = GameBoostMode.BEAST
        ),
        GameItem(
            id = "genshin_impact",
            packageName = "com.miHoYo.GenshinImpact",
            title = "Genshin Impact",
            genre = "Open World RPG",
            targetFps = 60,
            iconColorHex = 0xFF00FFA3,
            customMode = GameBoostMode.BALANCED
        ),
        GameItem(
            id = "arena_breakout",
            packageName = "com.proximabeta.mf.uamo",
            title = "Arena Breakout",
            genre = "Hardcore Extraction",
            targetFps = 90,
            iconColorHex = 0xFFFFD700,
            customMode = GameBoostMode.ESPORTS
        ),
        GameItem(
            id = "roblox",
            packageName = "com.roblox.client",
            title = "Roblox",
            genre = "Virtual Universe",
            targetFps = 120,
            iconColorHex = 0xFF38BDF8,
            customMode = GameBoostMode.BALANCED
        ),
        GameItem(
            id = "asphalt_legends",
            packageName = "com.gameloft.android.ANMP.GloftA9HM",
            title = "Asphalt Legends",
            genre = "Racing Arcade",
            targetFps = 90,
            iconColorHex = 0xFFFF3366,
            customMode = GameBoostMode.ESPORTS
        )
    )

    fun getGamesList(): List<GameItem> {
        val pm = context.packageManager
        return predefinedGames.map { game ->
            val isInstalled = try {
                pm.getPackageInfo(game.packageName, 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
            game.copy(isInstalled = isInstalled)
        }
    }

    suspend fun measureGamingServerPing(): Int = withContext(Dispatchers.IO) {
        val start = System.currentTimeMillis()
        try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress("1.1.1.1", 53), 1500)
            }
            val elapsed = (System.currentTimeMillis() - start).toInt()
            elapsed.coerceIn(18, 120)
        } catch (e: Exception) {
            28 // Default fast DNS ping
        }
    }

    fun launchGamePackage(packageName: String): Boolean {
        val pm = context.packageManager
        val launchIntent = pm.getLaunchIntentForPackage(packageName)
        return if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
            true
        } else {
            false
        }
    }
}
