package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricGreen
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.NeonPurple

data class AdbTweak(
    val titleAr: String,
    val descriptionAr: String,
    val command: String,
    val category: String,
    val badgeColor: Color
)

@Composable
fun AdbConsoleScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val tweaks = listOf(
        AdbTweak(
            titleAr = "قفل معدل الشاشة على 120Hz إجباري",
            descriptionAr = "يجبر النظام على العمل بتردد 120Hz فائق السلاسة طوال الوقت بدون هبوط إلى 60Hz",
            command = "adb shell settings put system peak_refresh_rate 120.0 && adb shell settings put system min_refresh_rate 120.0",
            category = "FPS UNLOCK",
            badgeColor = CyberCyan
        ),
        AdbTweak(
            titleAr = "إلغاء خنق المعالج الحراري (Thermal Throttling Bypass)",
            descriptionAr = "يمنع النظام من تقليل تردد الأنوية عند ارتفاع الحرارة، للحفاظ على ثبات الفريمات في المباريات الطويلة",
            command = "adb shell cmd thermalservice override-status 0",
            category = "PERFORMANCE",
            badgeColor = FlameOrange
        ),
        AdbTweak(
            titleAr = "تسريع استجابة اللمس والحركات إلى 0.5x",
            descriptionAr = "يضاعف سرعة انتقال الشاشات واستجابة النوافذ لخفة حركة تماثل استجابة الكمبيوتر الفورية",
            command = "adb shell settings put global window_animation_scale 0.5 && adb shell settings put global transition_animation_scale 0.5 && adb shell settings put global animator_duration_scale 0.5",
            category = "TOUCH RESPONSE",
            badgeColor = ElectricGreen
        ),
        AdbTweak(
            titleAr = "ترجمة محرك اللعبة إلى السرعة القصوى (AOT Speed Compile)",
            descriptionAr = "يقوم بترجمة أكواد اللعبة مسبقاً لمنع التقطيع واللاق اللحظي (Micro-stutters) أثناء المواجهات",
            command = "adb shell cmd package compile -m speed -f com.tencent.ig",
            category = "GAME ENGINE",
            badgeColor = NeonPurple
        ),
        AdbTweak(
            titleAr = "منح صلاحيات النظام المتقدمة لـ HyperBoost",
            descriptionAr = "يرفع صلاحيات التطبيق للتحكم بإعدادات النظام الآمنة وقراءة استهلاك المعالج بدقة",
            command = "adb shell pm grant ${context.packageName} android.permission.WRITE_SECURE_SETTINGS",
            category = "SYSTEM ACCESS",
            badgeColor = AlertRed
        )
    )

    fun copyToClipboard(cmd: String, label: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("ADB Command", cmd))
        Toast.makeText(context, "تم نسخ أمر: $label", Toast.LENGTH_SHORT).show()
    }

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
                imageVector = Icons.Default.Terminal,
                contentDescription = null,
                tint = CyberCyan,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Column {
                Text(
                    text = "كونسول أوامر المطور (ADB Optimizer)",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "رفع صلاحيات الهاتف وفتح سقف الفريمات بدون روت عبر تصحيح الأخطاء اللاسلكي",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }
        }

        // Wireless Debugging Guide Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurface)
                .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📶 خطوات الاتصال اللاسلكي (Wireless ADB)",
                    color = CyberCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = {
                        try {
                            context.startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS))
                        } catch (e: Exception) {
                            Toast.makeText(context, "يرجى تفعيل خيارات المطور أولاً بالضغط 7 مرات على رقم الإصدار", Toast.LENGTH_LONG).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(text = "فتح خيارات المطور", color = CyberCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Text(
                text = "1. انتقل لإعدادات هاتفك > خيارات المطور > فعّل 'تصحيح الأخطاء اللاسلكي' (Wireless Debugging).\n" +
                        "2. اضغط على 'إقران الجهاز برمز إقران' لتظهر لك نافذة برمز من 6 أرقام مع رقم المنفذ (Port).\n" +
                        "3. يمكنك تشغيل الأوامر أدناه مباشرة من تطبيق مثل Shizuku أو من جهاز كمبيوتر متصل بنفس شبكة Wi-Fi.",
                color = Color(0xFFCBD5E1),
                fontSize = 11.sp,
                lineHeight = 17.sp,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "أوامر التسريع والتحسين الجاهزة (One-Tap Copy):",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        tweaks.forEach { tweak ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tweak.titleAr,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(tweak.badgeColor.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = tweak.category,
                            color = tweak.badgeColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Text(
                    text = tweak.descriptionAr,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                )

                // Code block with copy button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
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
                            text = tweak.command,
                            color = ElectricGreen,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 2,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = { copyToClipboard(tweak.command, tweak.titleAr) },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(text = "نسخ", color = DarkBackground, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
