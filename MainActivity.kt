package com.example.swipeapps

import android.app.Activity
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.*

class MainActivity : Activity() {
    private lateinit var apps: List<ResolveInfo>
    private val selected = linkedSetOf<String>()

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        selected.addAll(Favs.get(this))
        val pm = packageManager
        apps = pm.queryIntentActivities(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0)
            .filter { it.activityInfo.packageName != packageName }
            .sortedBy { it.loadLabel(pm).toString().lowercase() }

        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(24, 48, 24, 24) }
        root.addView(TextView(this).apply {
            text = "1) Tick your favorite apps below.\n2) Set this as your default assistant, then swipe/long-press to open it."
            setPadding(0, 0, 0, 16)
        })
        root.addView(Button(this).apply {
            text = "Set as default assistant"
            setOnClickListener {
                try { startActivity(Intent(Settings.ACTION_VOICE_INPUT_SETTINGS)) }
                catch (e: Exception) { startActivity(Intent(Settings.ACTION_SETTINGS)) }
            }
        })
        val list = ListView(this)
        list.adapter = object : BaseAdapter() {
            override fun getCount() = apps.size
            override fun getItem(i: Int) = apps[i]
            override fun getItemId(i: Int) = i.toLong()
            override fun getView(i: Int, v: View?, p: ViewGroup?): View {
                val ri = apps[i]
                val pkg = ri.activityInfo.packageName
                val row = LinearLayout(this@MainActivity).apply {
                    gravity = Gravity.CENTER_VERTICAL; setPadding(8, 12, 8, 12)
                }
                row.addView(ImageView(this@MainActivity).apply { setImageDrawable(ri.loadIcon(packageManager)) },
                    LinearLayout.LayoutParams(96, 96))
                row.addView(TextView(this@MainActivity).apply {
                    text = ri.loadLabel(packageManager); setTextColor(Color.BLACK); textSize = 16f
                    setPadding(24, 0, 0, 0)
                }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
                row.addView(CheckBox(this@MainActivity).apply {
                    isChecked = pkg in selected
                    isClickable = false; isFocusable = false
                })
                return row
            }
        }
        list.setOnItemClickListener { _, view, i, _ ->
            val pkg = apps[i].activityInfo.packageName
            if (!selected.remove(pkg)) selected.add(pkg)
            Favs.set(this, selected.toList())
            ((view as ViewGroup).getChildAt(2) as CheckBox).isChecked = pkg in selected
        }
        root.addView(list, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
    }
}
