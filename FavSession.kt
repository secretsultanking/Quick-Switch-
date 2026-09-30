package com.example.swipeapps

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.service.voice.VoiceInteractionSession
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*

class FavSession(context: Context) : VoiceInteractionSession(context) {
    private var grid: GridLayout? = null

    private fun dp(v: Int) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, v.toFloat(), context.resources.displayMetrics).toInt()

    override fun onCreateContentView(): View {
        val root = FrameLayout(context)
        root.setBackgroundColor(Color.parseColor("#66000000"))
        root.setOnClickListener { hide() }

        val panel = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(20), dp(16), dp(32))
            background = GradientDrawable().apply {
                setColor(Color.WHITE)
                cornerRadii = floatArrayOf(dp(24).toFloat(), dp(24).toFloat(),
                    dp(24).toFloat(), dp(24).toFloat(), 0f, 0f, 0f, 0f)
            }
            isClickable = true
        }
        val g = GridLayout(context).apply { columnCount = 4 }
        grid = g
        panel.addView(g)
        root.addView(panel, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM))
        return root
    }

    override fun onShow(args: Bundle?, showFlags: Int) {
        super.onShow(args, showFlags)
        val g = grid ?: return
        g.removeAllViews()
        val pm = context.packageManager
        val favs = Favs.get(context).filter { pm.getLaunchIntentForPackage(it) != null }
        if (favs.isEmpty()) {
            g.addView(TextView(context).apply {
                text = "No favorites yet. Open Swipe Favorites and pick some apps."
                setTextColor(Color.DKGRAY)
            })
            return
        }
        val colW = context.resources.displayMetrics.widthPixels.minus(dp(32)) / 4
        for (pkg in favs) {
            val info = try { pm.getApplicationInfo(pkg, 0) } catch (e: Exception) { continue }
            val item = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL
                setPadding(dp(4), dp(8), dp(4), dp(8))
                setOnClickListener { launch(pkg) }
            }
            item.addView(ImageView(context).apply { setImageDrawable(pm.getApplicationIcon(info)) },
                LinearLayout.LayoutParams(dp(56), dp(56)))
            item.addView(TextView(context).apply {
                text = pm.getApplicationLabel(info)
                textSize = 12f
                maxLines = 1
                gravity = Gravity.CENTER
                setTextColor(Color.BLACK)
            })
            g.addView(item, GridLayout.LayoutParams().apply { width = colW })
        }
    }

    private fun launch(pkg: String) {
        context.packageManager.getLaunchIntentForPackage(pkg)?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(it)
        }
        hide()
    }
}
