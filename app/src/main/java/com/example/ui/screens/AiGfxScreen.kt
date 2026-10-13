package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiGfxProfile
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
fun AiGfxScreen(
    state: GameBoosterUiState,
    onRequestAiOptimization: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val popularGames = listOf(
        "PUBG Mobile",
        "Free Fire MAX",
        "Call of Duty: Mobile",
        "Warzone Mobile",
        "Genshin Impact",
        "Arena Breakout"
    )

    var selectedGame by remember { mutableStateOf(popularGames[0]) }
    var customGameInput by remember { mutableStateOf("") }
    var selectedGoal by remember { mutableStateOf("سلاسة كمبيوتر فائقة (PC Smoothness 120 FPS)") }

    val goals = listOf(
        "سلاسة كمبيوتر فائقة (PC Smoothness 120 FPS)",
        "أعلى استقرار فريمات للبطولات (Competitive 90 FPS)",
        "توازن الرسوم وحرارة الجهاز (Balanced Cool)"
    )

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
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = CyberCyan,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Column {
                Text(
                    text = "مهندس الرسوميات الذكي (AI GFX Studio)",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "تحليل قدرات عتاد هاتفك وتوليد أفضل توليفة رسومية لتجربة مماثلة للكمبيوتر",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }
        }

        // Popular games chips
        Text(
            text = "اختر اللعبة المستهدفة:",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            popularGames.take(3).forEach { game ->
                val isSelected = selectedGame == game
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) CyberCyan.copy(alpha = 0.2f) else DarkSurface)
                        .border(
                            1.dp,
                            if (isSelected) CyberCyan else DarkCardBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { selectedGame = game }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = game,
                        color = if (isSelected) CyberCyan else Color(0xFFCBD5E1),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            popularGames.drop(3).forEach { game ->
                val isSelected = selectedGame == game
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) CyberCyan.copy(alpha = 0.2f) else DarkSurface)
                        .border(
                            1.dp,
                            if (isSelected) CyberCyan else DarkCardBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { selectedGame = game }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = game,
                        color = if (isSelected) CyberCyan else Color(0xFFCBD5E1),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Custom game textfield if needed
        OutlinedTextField(
            value = customGameInput,
            onValueChange = {
                customGameInput = it
                if (it.isNotBlank()) selectedGame = it
            },
            placeholder = { Text("أو اكتب اسم لعبة أخرى هنا...", color = Color(0xFF64748B), fontSize = 12.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface,
                focusedBorderColor = CyberCyan,
                unfocusedBorderColor = DarkCardBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Goal selector
        Text(
            text = "الهدف المفضل للأداء:",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        goals.forEach { goal ->
            val isSelected = selectedGoal == goal
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) NeonPurple.copy(alpha = 0.2f) else DarkSurface)
                    .border(
                        1.dp,
                        if (isSelected) NeonPurple else DarkCardBorder,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { selectedGoal = goal }
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.Speed,
                        contentDescription = null,
                        tint = if (isSelected) NeonPurple else Color(0xFF64748B),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = goal,
                        color = if (isSelected) Color.White else Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Analyze Button
        Button(
            onClick = {
                val gameToAnalyze = if (customGameInput.isNotBlank()) customGameInput else selectedGame
                onRequestAiOptimization(gameToAnalyze, selectedGoal)
            },
            enabled = !state.isAiLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("ai_generate_profile_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CyberCyan
            )
        ) {
            if (state.isAiLoading) {
                CircularProgressIndicator(
                    color = DarkBackground,
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.5.dp
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "جارٍ التحليل الهندسي بالذكاء الاصطناعي...",
                    color = DarkBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = DarkBackground,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "توليد ملف الرسوميات المخصص (GENERATE GFX)",
                        color = DarkBackground,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Result Card
        AnimatedVisibility(visible = state.aiProfile != null) {
            state.aiProfile?.let { profile ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurface)
                        .border(1.5.dp, CyberCyan, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ملف الرسوميات الأمثل: ${profile.gameTitle}",
                            color = CyberCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ElectricGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "VERIFIED AI PROFILE",
                                color = ElectricGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    GfxSpecRow("دقة العرض (Resolution)", profile.recommendedResolution, Icons.Default.Tv)
                    GfxSpecRow("معدل الفريمات (Target FPS)", profile.recommendedFps, Icons.Default.Speed)
                    GfxSpecRow("محرك الرندرة (Rendering API)", profile.renderingApi, Icons.Default.AutoAwesome)
                    GfxSpecRow("جودة الرسوم (Graphics Quality)", profile.graphicsQuality, Icons.Default.CheckCircle)
                    GfxSpecRow("ممانعة التشويش (Anti-Aliasing)", profile.antiAliasing, Icons.Default.Tv)
                    GfxSpecRow("الظلال والمؤثرات (Shadows)", profile.shadows, Icons.Default.LocalFireDepartment)

                    Spacer(modifier = Modifier.height(10.dp))

                    // AI Advice Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkBackground)
                            .border(1.dp, DarkSurfaceVariant, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "💡 توجيهات هندسية لسلاسة مماثلة للكمبيوتر:",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = profile.summaryAdviceAr,
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 4.dp),
                                lineHeight = 16.sp
                            )
                        }
                    }

                    // Thermal tip Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(FlameOrange.copy(alpha = 0.1f))
                            .border(1.dp, FlameOrange.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "❄️ الوقاية من خنق المعالج الحراري (Thermal Throttling):",
                                color = FlameOrange,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = profile.thermalTipAr,
                                color = Color(0xFFE2E8F0),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 4.dp),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GfxSpecRow(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.size(6.dp))
            Text(
                text = title,
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
            )
        }
        Text(
            text = value,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
