// Update PreferencesManager to handle favorite shops
package com.example.navi

import android.content.Context
import android.content.SharedPreferences

//PreferencesManager: classe per gestire impostazioni comuni
class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    var requireIdentification: Boolean
        get() = sharedPreferences.getBoolean("require_identification", false)
        set(value) = sharedPreferences.edit().putBoolean("require_identification", value).apply()

    var currentTheme: String
        get() = sharedPreferences.getString("currentTheme", "Predefinito di sistema") ?: "Predefinito di sistema"
        set(value) = sharedPreferences.edit().putString("currentTheme", value).apply()

    var isDarkTheme: Boolean
        get() = sharedPreferences.getBoolean("isDarkTheme", false)
        set(value) = sharedPreferences.edit().putBoolean("isDarkTheme", value).apply()

    var viewOption: String
        get() = sharedPreferences.getString("viewOption", "List") ?: "List"
        set(value) = sharedPreferences.edit().putString("viewOption", value).apply()

    var sortOption: String
        get() = sharedPreferences.getString("sortOption", "Discendente") ?: "Discendente"
        set(value) = sharedPreferences.edit().putString("sortOption", value).apply()

    var showDettagliMenu: Boolean
        get() = sharedPreferences.getBoolean("showDettagliMenu", false)
        set(value) = sharedPreferences.edit().putBoolean("showDettagliMenu", value).apply()

    var showOrdinaMenu: Boolean
        get() = sharedPreferences.getBoolean("showOrdinaMenu", false)
        set(value) = sharedPreferences.edit().putBoolean("showOrdinaMenu", value).apply()

    var offersPerRow: Int
        get() = sharedPreferences.getInt("offersPerRow", 2)
        set(value) = sharedPreferences.edit().putInt("offersPerRow", value).apply()

    var favoriteShops: Set<String>
        get() = sharedPreferences.getStringSet("favoriteShops", emptySet()) ?: emptySet()
        set(value) = sharedPreferences.edit().putStringSet("favoriteShops", value).apply()
}