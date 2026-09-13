package com.example.lab2.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class BmiDataStore(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveEntry(entry: BmiEntry) {
        val entries = getAllEntries().toMutableList()
        entries.add(entry)
        prefs.edit().putString(KEY_ENTRIES, gson.toJson(entries)).commit()
    }

    fun getAllEntries(): List<BmiEntry> {
        val json = prefs.getString(KEY_ENTRIES, null) ?: return emptyList()
        val type = object : TypeToken<List<BmiEntry>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun clearAll() {
        prefs.edit().remove(KEY_ENTRIES).commit()
    }

    fun getLatestEntry(): BmiEntry? {
        return getAllEntries().lastOrNull()
    }

    companion object {
        private const val PREFS_NAME = "bmi_data"
        private const val KEY_ENTRIES = "entries"
    }
}
