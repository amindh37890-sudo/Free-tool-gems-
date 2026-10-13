package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricGreen
import com.example.ui.theme.NeonPurple
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun LivePhysicsBubblePreview(
    modifier: Modifier = Modifier,
    fps: Int = 120,
    onBubbleClick: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val animatedOffset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(DarkBackground.copy(alpha = 0.85f))
            .border(1.dp, DarkSurfaceVariant, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        val maxBoundX = (constraints.maxWidth / 2.5f)
        val maxBoundY = (constraints.maxHeight / 2.5f)

        // Background grid & instructions
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "مختبر الفيزياء الحية (Physics Sandbox)",
                color = CyberCyan,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "المس واسحب شعار اللعبة بأي سرعة واتركه لتشاهد الارتداد الفيزيائي (Spring & Damping)",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Live Physics Draggable Logo
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        animatedOffset.value.x.roundToInt(),
                        animatedOffset.value.y.roundToInt()
                    )
                }
                .rotate(rotationAngle)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            isDragging = true
                        },
                        onDragEnd = {
                            isDragging = false
                            // Spring physics bounce back to nearest safe zone or center
                            coroutineScope.launch {
                                rotationAngle = 0f
                                animatedOffset.animateTo(
                                    targetValue = Offset.Zero,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                            }
                        },
                        onDragCancel = {
                            isDragging = false
                            coroutineScope.launch {
                                animatedOffset.animateTo(
                                    targetValue = Offset.Zero,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                )
                            }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val newX = (animatedOffset.value.x + dragAmount.x).coerceIn(-maxBoundX, maxBoundX)
                            val newY = (animatedOffset.value.y + dragAmount.y).coerceIn(-maxBoundY, maxBoundY)

                            // Dynamic tilt angle based on drag velocity
                            rotationAngle = (dragAmount.x * 1.5f).coerceIn(-25f, 25f)

                            coroutineScope.launch {
                                animatedOffset.snapTo(Offset(newX, newY))
                            }
                        }
                    )
                }
                .testTag("physics_floating_bubble")
        ) {
            // Glowing outer physics ring
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .shadow(
                        elevation = if (isDragging) 24.dp else 12.dp,
                        shape = CircleShape,
                        spotColor = if (isDragging) AlertRed else CyberCyan
                    )
                    .background(
                        brush = Brush.radialGradient(
                            colors = if (isDragging) {
                                listOf(AlertRed.copy(alpha = 0.8f), NeonPurple, DarkBackground)
                            } else {
                                listOf(CyberCyan.copy(alpha = 0.9f), NeonPurple, DarkBackground)
                            }
                        ),
                        shape = CircleShape
                    )
                    .border(
                        width = if (isDragging) 3.dp else 2.dp,
                        color = if (isDragging) AlertRed else CyberCyan,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (isDragging) Icons.Default.Bolt else Icons.Default.Speed,
                        contentDescription = "HyperBoost Logo",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "$fps FPS",
                        color = ElectricGreen,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp
                    )
                    Text(
                        text = if (isDragging) "LIVE" else "BOOST",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
