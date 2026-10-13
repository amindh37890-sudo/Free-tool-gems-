package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricGreen
import com.example.ui.theme.FlameOrange

@Composable
fun CrosshairCustomizer(
    modifier: Modifier = Modifier,
    initialColor: Color = AlertRed,
    onConfigChange: (Color, Float, String) -> Unit = { _, _, _ -> }
) {
    var selectedColor by remember { mutableStateOf(initialColor) }
    var crosshairSize by remember { mutableFloatStateOf(24f) }
    var selectedStyle by remember { mutableStateOf("Cross") } // "Cross", "Dot", "CircleDot"

    val colorList = listOf(AlertRed, CyberCyan, ElectricGreen, FlameOrange, Color.White)
    val styles = listOf("Cross" to "صليب (Cross)", "Dot" to "نقطة (Dot)", "CircleDot" to "دائرة ونقطة")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(1.dp, DarkSurfaceVariant, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "🎯 تخصيص نقطة التصويب للشاشة (Crosshair Studio)",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "تساعدك على ثبات ودقة توجيه السلاح في منتصف الشاشة بسرعة فائقة.",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
        )

        // Preview Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(DarkBackground)
                .border(1.dp, DarkSurfaceVariant, RoundedCornerShape(12.dp))
                .testTag("crosshair_preview_box"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(90.dp)) {
                val center = Offset(size.width / 2, size.height / 2)
                val half = crosshairSize

                when (selectedStyle) {
                    "Dot" -> {
                        drawCircle(
                            color = selectedColor,
                            radius = half / 2.5f,
                            center = center
                        )
                    }
                    "CircleDot" -> {
                        drawCircle(
                            color = selectedColor,
                            radius = half,
                            center = center,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                        )
                        drawCircle(
                            color = selectedColor,
                            radius = 3.5f,
                            center = center
                        )
                    }
                    else -> { // Cross
                        // Horizontal line
                        drawLine(
                            color = selectedColor,
                            start = Offset(center.x - half, center.y),
                            end = Offset(center.x + half, center.y),
                            strokeWidth = 3.5f
                        )
                        // Vertical line
                        drawLine(
                            color = selectedColor,
                            start = Offset(center.x, center.y - half),
                            end = Offset(center.x, center.y + half),
                            strokeWidth = 3.5f
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 2.5f,
                            center = center
                        )
                    }
                }
            }
        }

        // Color selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "اللون:",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            colorList.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (selectedColor == color) 2.5.dp else 1.dp,
                            color = if (selectedColor == color) Color.White else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable {
                            selectedColor = color
                            onConfigChange(color, crosshairSize, selectedStyle)
                        }
                )
            }
        }

        // Style selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            styles.forEach { (key, label) ->
                val isSelected = selectedStyle == key
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) CyberCyan.copy(alpha = 0.2f) else DarkBackground)
                        .border(
                            1.dp,
                            if (isSelected) CyberCyan else DarkSurfaceVariant,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            selectedStyle = key
                            onConfigChange(selectedColor, crosshairSize, selectedStyle)
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) CyberCyan else Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Size slider
        Column(modifier = Modifier.padding(top = 10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "حجم النقطة:", color = Color.White, fontSize = 11.sp)
                Text(text = "${crosshairSize.toInt()} px", color = CyberCyan, fontSize = 11.sp)
            }
            Slider(
                value = crosshairSize,
                onValueChange = {
                    crosshairSize = it
                    onConfigChange(selectedColor, it, selectedStyle)
                },
                valueRange = 12f..48f,
                colors = SliderDefaults.colors(
                    thumbColor = CyberCyan,
                    activeTrackColor = CyberCyan,
                    inactiveTrackColor = DarkSurfaceVariant
                )
            )
        }
    }
}
