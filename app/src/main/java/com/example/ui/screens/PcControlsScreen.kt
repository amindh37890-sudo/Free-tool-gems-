package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.TouchLatencyTester
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
fun PcControlsScreen(
    state: GameBoosterUiState,
    onRecalculateSens: (Int, Float, String) -> Unit,
    onCalculateStretched: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var pcDpi by remember { mutableIntStateOf(800) }
    var inGameSens by remember { mutableFloatStateOf(1.5f) }
    var selectedRatio by remember { mutableStateOf("4:3") }

    val dpiPresets = listOf(400, 800, 1200, 1600)
    val ratios = listOf("4:3" to "شاشة كمبيوتر ممتدة (4:3 Stretched)", "16:10" to "شاشة عريضة (16:10 Wide)", "16:9" to "شاشة قياسية (16:9)")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Mouse,
                contentDescription = null,
                tint = CyberCyan,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Column {
                Text(
                    text = "محاكي سرعة وسلاسة الكمبيوتر (PC Engine)",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ضبط أبعاد الشاشة ودوران الشخصية وسرعة اللمس بمستوى ماوس الكمبيوتر",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }
        }

        // Section 1: PC Sensitivity & Gyroscope Converter
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurface)
                .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎯 محول حساسية ماوس الكمبيوتر للهاتف",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "eDPI: ${(pcDpi * inGameSens).toInt()}",
                    color = CyberCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Text(
                text = "اختر DPI ماوس الكمبيوتر المعتاد وحساسية اللعبة ليتم تحويلها لحساسية دوران 360° دقيقة وسريعة:",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
            )

            // DPI Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                dpiPresets.forEach { dpi ->
                    val isSelected = pcDpi == dpi
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) CyberCyan.copy(alpha = 0.25f) else DarkBackground)
                            .border(
                                1.dp,
                                if (isSelected) CyberCyan else DarkSurfaceVariant,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                pcDpi = dpi
                                onRecalculateSens(dpi, inGameSens, "PUBG / Free Fire")
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$dpi DPI",
                            color = if (isSelected) CyberCyan else Color(0xFFCBD5E1),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // In-game sensitivity slider
            Column(modifier = Modifier.padding(top = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "حساسية ماوس الكمبيوتر في اللعبة:", color = Color.White, fontSize = 11.sp)
                    Text(text = String.format("%.2f", inGameSens), color = ElectricGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = inGameSens,
                    onValueChange = {
                        inGameSens = it
                        onRecalculateSens(pcDpi, it, "PUBG / Free Fire")
                    },
                    valueRange = 0.5f..4.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = ElectricGreen,
                        activeTrackColor = ElectricGreen,
                        inactiveTrackColor = DarkSurfaceVariant
                    )
                )
            }

            // Converted Results Grid
            Text(
                text = "قيم الحساسية المحسوبة لهاتفك (ضعها في إعدادات اللعبة):",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp, bottom = 6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SensValueBox("عام (سحب ونظرة)", "${state.pcSensitivity.convertedGeneralSens.toInt()}%", CyberCyan, Modifier.weight(1f))
                SensValueBox("النقطة الحمراء Red Dot", "${state.pcSensitivity.convertedRedDotSens.toInt()}%", NeonPurple, Modifier.weight(1f))
                SensValueBox("سكوب 2x & 4x", "${state.pcSensitivity.converted2xSens.toInt()}%", AlertRed, Modifier.weight(1f))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SensValueBox("جيروسكوب (دوران 360°)", "${state.pcSensitivity.convertedGyroSens.toInt()}%", ElectricGreen, Modifier.weight(1.5f))
                SensValueBox("أصغر عرض DPI المقترح", "${state.pcSensitivity.customDpiRecommendation} dp", FlameOrange, Modifier.weight(1.5f))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 2: PC Stretched View 4:3
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurface)
                .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Tv,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "عرض الشاشة الممتد مثل الكمبيوتر (PC Stretched 4:3)",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "ميزة تجعل أجساد اللاعبين أعرض (Hitbox Magnification) وسرعة الدوران البصري أسرع كما يلعب محترفو CS:GO والكمبيوتر.",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
            )

            // Ratio choices
            ratios.forEach { (key, label) ->
                val isSelected = selectedRatio == key
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) CyberCyan.copy(alpha = 0.2f) else DarkBackground)
                        .border(
                            1.dp,
                            if (isSelected) CyberCyan else DarkSurfaceVariant,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            selectedRatio = key
                            onCalculateStretched(key)
                        }
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        if (isSelected) {
                            Text(
                                text = "${state.pcStretchedW} x ${state.pcStretchedH}",
                                color = CyberCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            // Command copy card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkBackground)
                    .border(1.dp, DarkSurfaceVariant, RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.pcStretchedCmd,
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("ADB Command", state.pcStretchedCmd))
                            Toast.makeText(context, "تم نسخ أمر الأبعاد بنجاح!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(text = "نسخ", color = DarkBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 3: Interactive Touch Latency Pad
        TouchLatencyTester()
    }
}

@Composable
private fun SensValueBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DarkBackground)
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, color = Color(0xFF94A3B8), fontSize = 9.sp, maxLines = 1)
            Text(text = value, color = color, fontSize = 13.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 2.dp))
        }
    }
}
