package com.example.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import kotlin.math.abs

class FloatingMonitorService : Service() {

    private var windowManager: WindowManager? = null
    private var floatingView: FrameLayout? = null
    private var expandedCard: LinearLayout? = null
    private var bubbleContainer: FrameLayout? = null
    private var fpsBadgeText: TextView? = null
    private var ramStatusText: TextView? = null
    private var tempStatusText: TextView? = null
    private var crosshairView: View? = null

    private var layoutParams: WindowManager.LayoutParams? = null
    private val telemetryManager by lazy { TelemetryManager(this) }
    private val handler = Handler(Looper.getMainLooper())
    private var isExpanded = false
    private var isCrosshairEnabled = false

    private val updateRunnable = object : Runnable {
        override fun run() {
            updateTelemetryDisplay()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundServiceNotification()

        if (Settings.canDrawOverlays(this)) {
            setupFloatingView()
            handler.post(updateRunnable)
        } else {
            stopSelf()
        }
    }

    private fun startForegroundServiceNotification() {
        val channelId = "hyperboost_floating_channel"
        val channelName = "HyperBoost Floating HUD"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Live game telemetry and performance overlay"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("HyperBoost Floating HUD نشط")
            .setContentText("مراقبة حية للفريمات والذاكرة وحرارة الهاتف أثناء اللعب")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1001, notification)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupFloatingView() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 40
            y = 200
        }

        // Root container
        floatingView = FrameLayout(this)

        // 1. Draggable Bubble
        bubbleContainer = FrameLayout(this).apply {
            setBackgroundColor(Color.TRANSPARENT)
            setPadding(12, 12, 12, 12)
        }

        val iconBadge = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 18, 24, 18)
            // Cyber dark background with cyan border
            val shape = android.graphics.drawable.GradientDrawable().apply {
                setColor(0xEE0A0E17.toInt())
                cornerRadius = 32f
                setStroke(4, 0xFF00F0FF.toInt())
            }
            background = shape
        }

        fpsBadgeText = TextView(this).apply {
            text = "⚡ 120 FPS"
            setTextColor(0xFF00F0FF.toInt())
            textSize = 12f
            setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD)
        }

        val miniSubtitle = TextView(this).apply {
            text = "HYPERBOOST"
            setTextColor(0xFF94A3B8.toInt())
            textSize = 8f
            setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
        }

        iconBadge.addView(fpsBadgeText)
        iconBadge.addView(miniSubtitle)
        bubbleContainer?.addView(iconBadge)

        // 2. Expandable HUD Menu
        expandedCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 28, 32, 28)
            visibility = View.GONE
            val cardBg = android.graphics.drawable.GradientDrawable().apply {
                setColor(0xF50F1523.toInt())
                cornerRadius = 24f
                setStroke(3, 0xFFFF2A6D.toInt())
            }
            background = cardBg
        }

        val headerRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val titleText = TextView(this).apply {
            text = "HYPERBOOST GAMING HUD"
            setTextColor(0xFFF8FAFC.toInt())
            textSize = 13f
            setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val closeBtn = Button(this).apply {
            text = "✕"
            setTextColor(0xFFFF2A6D.toInt())
            setBackgroundColor(Color.TRANSPARENT)
            textSize = 14f
            setOnClickListener {
                toggleMenuExpansion()
            }
        }
        headerRow.addView(titleText)
        headerRow.addView(closeBtn)
        expandedCard?.addView(headerRow)

        // Telemetry details inside menu
        ramStatusText = TextView(this).apply {
            text = "الذاكرة RAM: 4.2 / 8.0 GB (52%)"
            setTextColor(0xFF00FFA3.toInt())
            textSize = 12f
            setPadding(0, 8, 0, 4)
        }
        tempStatusText = TextView(this).apply {
            text = "الحرارة: 37.2°C • ممتاز"
            setTextColor(0xFFFF851B.toInt())
            textSize = 12f
            setPadding(0, 4, 0, 12)
        }
        expandedCard?.addView(ramStatusText)
        expandedCard?.addView(tempStatusText)

        // Quick Boost Button
        val boostButton = Button(this).apply {
            text = "⚡ تنظيف وتسريع فوري (Quick Boost)"
            setTextColor(0xFF0A0E17.toInt())
            textSize = 12f
            val btnDrawable = android.graphics.drawable.GradientDrawable().apply {
                setColor(0xFF00F0FF.toInt())
                cornerRadius = 16f
            }
            background = btnDrawable
            setOnClickListener {
                val freed = telemetryManager.performMemoryClean()
                text = "✓ تم تنظيف $freed MB وتسريع الرندرة!"
                handler.postDelayed({
                    text = "⚡ تنظيف وتسريع فوري (Quick Boost)"
                }, 2000)
            }
        }
        expandedCard?.addView(boostButton)

        // Crosshair toggle button
        val crosshairBtn = Button(this).apply {
            text = "🎯 نقطة تصويب مركزية (Crosshair: إيقاف)"
            setTextColor(0xFFF8FAFC.toInt())
            textSize = 11f
            val btnDrawable = android.graphics.drawable.GradientDrawable().apply {
                setColor(0xFF1E293B.toInt())
                cornerRadius = 16f
                setStroke(2, 0xFF00FFA3.toInt())
            }
            background = btnDrawable
            setOnClickListener {
                isCrosshairEnabled = !isCrosshairEnabled
                text = if (isCrosshairEnabled) "🎯 نقطة تصويب مركزية (مفعل)" else "🎯 نقطة تصويب مركزية (إيقاف)"
                toggleCenterCrosshair(isCrosshairEnabled)
            }
        }
        expandedCard?.addView(crosshairBtn)

        val mainStack = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(bubbleContainer)
            addView(expandedCard)
        }

        floatingView?.addView(mainStack)

        // Draggable Physics Logic with Touch Listener
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f
        var isDragging = false

        bubbleContainer?.setOnTouchListener { _, event ->
            val lp = layoutParams ?: return@setOnTouchListener false
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = lp.x
                    initialY = lp.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    isDragging = false
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = (event.rawX - initialTouchX).toInt()
                    val deltaY = (event.rawY - initialTouchY).toInt()
                    if (abs(deltaX) > 10 || abs(deltaY) > 10) {
                        isDragging = true
                    }
                    lp.x = initialX + deltaX
                    lp.y = initialY + deltaY
                    windowManager?.updateViewLayout(floatingView, lp)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (!isDragging) {
                        // Click event! Toggle expansion
                        toggleMenuExpansion()
                    } else {
                        // Spring bounce to screen edge
                        snapToEdgeWithPhysics(lp)
                    }
                    true
                }
                else -> false
            }
        }

        windowManager?.addView(floatingView, layoutParams)
    }

    private fun snapToEdgeWithPhysics(lp: WindowManager.LayoutParams) {
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val targetX = if (lp.x < screenWidth / 2) 20 else screenWidth - 180

        val startX = lp.x
        var currentStep = 0
        val totalSteps = 10
        val stepRunnable = object : Runnable {
            override fun run() {
                if (currentStep <= totalSteps) {
                    val progress = currentStep.toFloat() / totalSteps
                    // Ease out bounce / damping
                    val eased = 1f - (1f - progress) * (1f - progress)
                    lp.x = (startX + (targetX - startX) * eased).toInt()
                    windowManager?.updateViewLayout(floatingView, lp)
                    currentStep++
                    handler.postDelayed(this, 16)
                }
            }
        }
        handler.post(stepRunnable)
    }

    private fun toggleMenuExpansion() {
        isExpanded = !isExpanded
        expandedCard?.visibility = if (isExpanded) View.VISIBLE else View.GONE
    }

    private fun toggleCenterCrosshair(enable: Boolean) {
        if (enable) {
            if (crosshairView == null) {
                crosshairView = View(this).apply {
                    val d = android.graphics.drawable.GradientDrawable().apply {
                        shape = android.graphics.drawable.GradientDrawable.OVAL
                        setColor(0xFFFF2A6D.toInt())
                        setSize(16, 16)
                    }
                    background = d
                }
                val crosshairParams = WindowManager.LayoutParams(
                    16,
                    16,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    else
                        @Suppress("DEPRECATION")
                        WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT
                ).apply {
                    gravity = Gravity.CENTER
                }
                windowManager?.addView(crosshairView, crosshairParams)
            }
        } else {
            crosshairView?.let {
                windowManager?.removeView(it)
                crosshairView = null
            }
        }
    }

    private fun updateTelemetryDisplay() {
        val telemetry = telemetryManager.readCurrentTelemetry()
        fpsBadgeText?.text = "⚡ ${telemetry.currentFps} FPS"
        ramStatusText?.text = "الذاكرة RAM: ${telemetry.availableRamMb}MB متبقية (${telemetry.usedRamPercent}%)"
        tempStatusText?.text = "الحرارة: ${String.format("%.1f", telemetry.batteryTempCelsius)}°C • ${telemetry.thermalStatus}"
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable)
        floatingView?.let {
            windowManager?.removeView(it)
            floatingView = null
        }
        crosshairView?.let {
            windowManager?.removeView(it)
            crosshairView = null
        }
    }
}
