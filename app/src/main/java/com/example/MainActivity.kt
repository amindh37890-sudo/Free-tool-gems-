package com.example

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AdbConsoleScreen
import com.example.ui.screens.AiGfxScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FloatingHudScreen
import com.example.ui.screens.PcControlsScreen
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.ElectricGreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GameBoosterViewModel

import androidx.compose.material.icons.filled.SportsEsports
import com.example.ui.screens.GameSpaceScreen

enum class AppTab(val titleAr: String, val icon: ImageVector) {
    ARENA("مساحة الألعاب", Icons.Default.SportsEsports),
    DASHBOARD("الرئيسية", Icons.Default.Speed),
    AI_GFX("رسومات AI", Icons.Default.AutoAwesome),
    PC_MODE("تحكم PC", Icons.Default.Mouse),
    FLOATING_HUD("النافذة العائمة", Icons.Default.Layers)
}

class MainActivity : ComponentActivity() {

    private val viewModel: GameBoosterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshTelemetry()
        viewModel.checkOverlayPermission()
        viewModel.loadGames()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: GameBoosterViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentTab by remember { mutableStateOf(AppTab.DASHBOARD) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "HYPERBOOST // AI GAME STUDIO",
                            color = CyberCyan,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface,
                    titleContentColor = CyberCyan
                ),
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(ElectricGreen.copy(alpha = 0.2f))
                            .border(1.dp, ElectricGreen.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${uiState.telemetry.currentFps} FPS",
                            color = ElectricGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                modifier = Modifier
                    .border(width = 1.dp, color = DarkCardBorder)
                    .height(68.dp)
            ) {
                AppTab.entries.forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.titleAr,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.titleAr,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CyberCyan,
                            selectedTextColor = CyberCyan,
                            indicatorColor = CyberCyan.copy(alpha = 0.15f),
                            unselectedIconColor = Color(0xFF64748B),
                            unselectedTextColor = Color(0xFF64748B)
                        ),
                        modifier = Modifier.testTag("tab_${tab.name}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.ARENA -> GameSpaceScreen(
                    state = uiState,
                    onSelectGame = { viewModel.selectGameToLaunch(it) },
                    onTurboLaunch = { viewModel.triggerTurboGameLaunch(it) }
                )
                AppTab.DASHBOARD -> DashboardScreen(
                    state = uiState,
                    onTriggerBoost = { viewModel.triggerHyperBoost() },
                    onSelectMode = { viewModel.setGameBoostMode(it) }
                )
                AppTab.AI_GFX -> AiGfxScreen(
                    state = uiState,
                    onRequestAiOptimization = { game, goal ->
                        viewModel.requestAiOptimization(game, goal)
                    }
                )
                AppTab.PC_MODE -> PcControlsScreen(
                    state = uiState,
                    onRecalculateSens = { dpi, sens, game ->
                        viewModel.recalculateSensitivity(dpi, sens, game)
                    },
                    onCalculateStretched = { ratio ->
                        viewModel.calculateStretchedRatio(ratio)
                    }
                )
                AppTab.FLOATING_HUD -> FloatingHudScreen(
                    state = uiState,
                    onToggleOverlay = { viewModel.toggleFloatingOverlay() },
                    onCheckPermission = { viewModel.checkOverlayPermission() }
                )
            }
        }
    }
}
