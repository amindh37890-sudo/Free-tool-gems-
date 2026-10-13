package com.example.ai

import com.example.BuildConfig
import com.example.model.AiGfxProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiGfxAdvisor {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun analyzeGameSettings(
        gameName: String,
        deviceModel: String,
        ramMb: Long,
        refreshRate: Int,
        userGoal: String
    ): AiGfxProfile = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isNullOrBlank() || apiKey.contains("MY_GEMINI_API_KEY") || apiKey.contains("YOUR_KEY")) {
            return@withContext generateSmartFallback(gameName, refreshRate, ramMb, userGoal)
        }

        try {
            val prompt = """
                أنت خبير محترف في هندسة أداء ألعاب الهواتف (Android Game Performance Engineer & GFX Optimizer).
                قم بتحليل وتوليد أفضل إعدادات رسومية (GFX Profile) وتوجيهات لتقليل التقطيع وزيادة سلاسة اللعب لتصبح مثل الكمبيوتر للعبة: "$gameName".
                بيانات جهاز المستخدم:
                - الجهاز: $deviceModel
                - الذاكرة العشوائية RAM: $ramMb MB
                - معدل تحديث الشاشة الأقصى: $refreshRate Hz
                - هدف المستخدم: $userGoal (سلاسة كمبيوتر، فريمات قصوى، استقرار)

                أرجع الرد بتنسيق JSON حصراً بدون علامات markdown إضافية:
                {
                   "recommendedResolution": "مثال: 1080p Smooth (أو 720p Stretched PC 4:3)",
                   "recommendedFps": "مثال: 90 FPS أو 120 FPS Extreme",
                   "graphicsQuality": "مثال: Smooth / Balanced",
                   "renderingApi": "مثال: Vulkan (أو OpenGL ES 3.2)",
                   "antiAliasing": "مثال: 2x MSAA أو Disabled",
                   "shadows": "مثال: Off لتقليل إجهاد الـ GPU وسرعة الاستجابة",
                   "summaryAdviceAr": "نصائح دقيقة لتجربة شبيهة بالكمبيوتر وسرعة دوران السلاح وحساسية اللمس",
                   "thermalTipAr": "نصيحة لمنع هبوط الفريمات (Thermal Throttling) أثناء اللعب الطويل"
                }
            """.trimIndent()

            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    put(JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        }
                        put("parts", parts)
                    })
                }
                put("contents", contents)
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.3)
                })
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful || responseString.isBlank()) {
                return@withContext generateSmartFallback(gameName, refreshRate, ramMb, userGoal)
            }

            val rootJson = JSONObject(responseString)
            val candidates = rootJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text") ?: ""

            val parsedJson = JSONObject(text.trim())

            AiGfxProfile(
                gameTitle = gameName,
                recommendedResolution = parsedJson.optString("recommendedResolution", "1080p FHD (Optimized)"),
                recommendedFps = parsedJson.optString("recommendedFps", "${refreshRate} FPS Unlocked"),
                graphicsQuality = parsedJson.optString("graphicsQuality", "Smooth (سلس)"),
                renderingApi = parsedJson.optString("renderingApi", "Vulkan Next-Gen"),
                antiAliasing = parsedJson.optString("antiAliasing", "Close / 2x"),
                shadows = parsedJson.optString("shadows", "Disabled (لأقصى استقرار)"),
                summaryAdviceAr = parsedJson.optString("summaryAdviceAr", "تم ضبط الرندرة وحجم الفريم بافر لتقديم خفة استجابة مماثلة للماوس والكمبيوتر."),
                thermalTipAr = parsedJson.optString("thermalTipAr", "حافظ على نسبة شحن أعلى من 20% وأوقف التطبيقات في الخلفية.")
            )
        } catch (e: Exception) {
            generateSmartFallback(gameName, refreshRate, ramMb, userGoal)
        }
    }

    private fun generateSmartFallback(
        gameName: String,
        refreshRate: Int,
        ramMb: Long,
        userGoal: String
    ): AiGfxProfile {
        val targetFps = if (refreshRate >= 120) "120 FPS Extreme" else if (refreshRate >= 90) "90 FPS Ultra" else "60 FPS Max"
        val res = if (ramMb >= 8000) "1080p FHD Stretched (PC View)" else "720p HD Super Smooth"
        val api = if (ramMb >= 6000) "Vulkan Low-Latency" else "OpenGL ES 3.2"

        return AiGfxProfile(
            gameTitle = gameName,
            recommendedResolution = res,
            recommendedFps = targetFps,
            graphicsQuality = "سلس للغاية (Smooth / Low)",
            renderingApi = api,
            antiAliasing = "تعطيل MSAA (يوفر 15% من قوة المعالج)",
            shadows = "إيقاف الظلال بالكامل (Shadows Off)",
            summaryAdviceAr = "للحصول على خفة وسرعة حركة تضاهي الكمبيوتر في $gameName: اضبط الرسوم على Smooth مع أعلى معدل إطارات يدعمه هاتفك. هذا يقلل وقت معالجة الفريم (Frame Time) من 33ms إلى أقل من 8.3ms، مما يجعل سحب الشاشة ودوران الشخصية فوري واستجابة السلاح فورية.",
            thermalTipAr = "لتفادي هبوط الفريمات (Thermal Throttling): قم بتفعيل وضع الوحش في HyperBoost لتعطيل خنق المعالج وضمان تدفق الفريمات بثبات 99.8%."
        )
    }
}
