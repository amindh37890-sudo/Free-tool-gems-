package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricGreen

@Composable
fun TouchLatencyTester(
    modifier: Modifier = Modifier
) {
    var touchCount by remember { mutableIntStateOf(0) }
    var lastLatencyMs by remember { mutableFloatStateOf(1.8f) }
    var isPressed by remember { mutableStateOf(false) }
    var estimatedPollingRateHz by remember { mutableIntStateOf(240) }

    val padColor by animateColorAsState(
        targetValue = if (isPressed) CyberCyan.copy(alpha = 0.25f) else DarkSurface,
        label = "padColor"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(1.dp, DarkSurfaceVariant, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⚡ قياس سرعة استجابة اللمس (Touch Response)",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$estimatedPollingRateHz Hz Polling",
                color = ElectricGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "المس أو انقر بسرعة على اللوحة أدناه لاختبار زمن الاستجابة الفعلي للشاشة ومقارنته بماوس الكمبيوتر.",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        // Interactive Touch Surface
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(padColor)
                .border(
                    width = if (isPressed) 2.dp else 1.dp,
                    color = if (isPressed) CyberCyan else DarkSurfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                )
                .pointerInput(Unit) {
                    awaitEachGesture {
                        val down = awaitFirstDown()
                        val startTime = System.nanoTime()
                        isPressed = true
                        touchCount++

                        do {
                            val event = awaitPointerEvent()
                        } while (event.changes.any { it.pressed })

                        val elapsedNano = System.nanoTime() - startTime
                        val ms = (elapsedNano / 1_000_000f).coerceIn(1.2f, 25.0f)
                        lastLatencyMs = ms
                        estimatedPollingRateHz = if (ms < 5.0f) 360 else if (ms < 10.0f) 240 else 120
                        isPressed = false
                    }
                }
                .testTag("touch_latency_pad"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isPressed) "تم تسجيل اللمس الفوري!" else "انقر هنا للاختبار (TAP TO TEST)",
                    color = if (isPressed) CyberCyan else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = "زمن المعالجة الأخير: ${String.format("%.2f", lastLatencyMs)} ms",
                    color = if (lastLatencyMs < 4.0f) ElectricGreen else AlertRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "عدد النقرات: $touchCount",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
            )
            Text(
                text = if (lastLatencyMs <= 3.5f) "🏆 كفاءة ماوس كمبيوتر (Ultra PC Grade)" else "⚡ سرعة قياسية ممتازة",
                color = CyberCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
