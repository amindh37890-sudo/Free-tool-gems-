package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameBoostMode
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricGreen
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.NeonPurple
import com.example.viewmodel.GameBoosterUiState

@Composable
fun DashboardScreen(
    state: GameBoosterUiState,
    onTriggerBoost: () -> Unit,
    onSelectMode: (GameBoostMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Header Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "HYPERBOOST STUDIO",
                    color = CyberCyan,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${state.telemetry.deviceModel} • ${state.telemetry.androidVersion}",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "STATUS: READY",
                    color = ElectricGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Hero Telemetry Dial
        Box(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outer Glowing Ring
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(if (state.isBoosting) pulseScale else 1f)
                    .clip(CircleShape)
                    .background(
                        Brush.sweepGradient(
                            listOf(
                                CyberCyan,
                                NeonPurple,
                                AlertRed,
                                ElectricGreen,
                                CyberCyan
                            )
                        )
                    )
            )
            // Inner Core
            Box(
                modifier = Modifier
                    .size(184.dp)
                    .clip(CircleShape)
                    .background(DarkBackground)
                    .border(2.dp, DarkSurfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (state.isBoosting) "BOOSTING" else "${state.telemetry.currentFps}",
                        color = Color.White,
                        fontSize = if (state.isBoosting) 20.sp else 46.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = if (state.isBoosting) "OPTIMIZING..." else "FPS (معدل الشاشة)",
                        color = CyberCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "هدف النمط: ${state.selectedMode.targetFps} FPS",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // HyperBoost Trigger Button
        Button(
            onClick = onTriggerBoost,
            enabled = !state.isBoosting,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(vertical = 4.dp)
                .testTag("hyperboost_trigger_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                CyberCyan,
                                NeonPurple,
                                AlertRed
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Boost",
                        tint = DarkBackground,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = if (state.isBoosting) "جارٍ تفريغ الذاكرة ورفع تردد المعالج..." else "تفعيل أقصى تسريع (1-TAP HYPERBOOST)",
                        color = DarkBackground,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Success / Freed memory toast banner
        AnimatedVisibility(
            visible = state.boostSuccessMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            state.boostSuccessMessage?.let { msg ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ElectricGreen.copy(alpha = 0.15f))
                        .border(1.dp, ElectricGreen, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "✓ $msg",
                        color = ElectricGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hardware Telemetry Matrix
        Text(
            text = "مؤشرات عتاد الجهاز اللحظية (Live Telemetry)",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                title = "الذاكرة العشوائية RAM",
                value = "${state.telemetry.usedRamPercent}%",
                subtitle = "${state.telemetry.availableRamMb} MB متاح",
                icon = Icons.Default.Memory,
                accentColor = CyberCyan,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "حرارة البطارية",
                value = "${String.format("%.1f", state.telemetry.batteryTempCelsius)}°C",
                subtitle = state.telemetry.thermalStatus,
                icon = Icons.Default.Thermostat,
                accentColor = if (state.telemetry.batteryTempCelsius > 42) AlertRed else FlameOrange,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                title = "طاقة البطارية",
                value = "${state.telemetry.batteryLevelPercent}%",
                subtitle = if (state.telemetry.isCharging) "جارٍ الشحن ⚡" else "استخدام البطارية",
                icon = Icons.Default.Bolt,
                accentColor = ElectricGreen,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "أنوية المعالج CPU",
                value = "${state.telemetry.cpuCores} Cores",
                subtitle = "بنية متعددة المهام",
                icon = Icons.Default.Speed,
                accentColor = NeonPurple,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Game Boost Mode Selector
        Text(
            text = "أنماط تعزيز الألعاب (Performance Profiles)",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        GameBoostMode.entries.forEach { mode ->
            val isSelected = state.selectedMode == mode
            val modeColor = Color(mode.colorHex)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) modeColor.copy(alpha = 0.15f) else DarkSurface)
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) modeColor else DarkCardBorder,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable { onSelectMode(mode) }
                    .padding(14.dp)
                    .testTag("mode_${mode.name}")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = mode.titleAr,
                            color = if (isSelected) modeColor else Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = mode.descriptionAr,
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(modeColor.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${mode.targetFps} FPS",
                            color = modeColor,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurface)
            .border(1.dp, DarkCardBorder, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = value,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 6.dp)
            )
            Text(
                text = subtitle,
                color = accentColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
