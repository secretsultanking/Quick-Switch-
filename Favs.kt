package com.example.swipeapps

import android.content.Context

object Favs {
    private const val KEY = "favs"
    private fun prefs(c: Context) = c.getSharedPreferences("favs", Context.MODE_PRIVATE)
    fun get(c: Context): List<String> =
        prefs(c).getString(KEY, "")!!.split(",").filter { it.isNotBlank() }
    fun set(c: Context, list: List<String>) =
        prefs(c).edit().putString(KEY, list.joinToString(",")).apply()
}
