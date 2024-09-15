package com.example.navi

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_preferences), Context.MODE_PRIVATE)

    var requireIdentification: Boolean
        get() = sharedPreferences.getBoolean(context.getString(R.string.require_identification_key), false)
        set(value) = sharedPreferences.edit().putBoolean(context.getString(R.string.require_identification_key), value).apply()

    var currentTheme: String
        get() = sharedPreferences.getString(context.getString(R.string.current_theme_key), context.getString(R.string.default_theme)) ?: context.getString(R.string.default_theme)
        set(value) = sharedPreferences.edit().putString(context.getString(R.string.current_theme_key), value).apply()

    var viewOption: String
        get() = sharedPreferences.getString(context.getString(R.string.view_option_key), context.getString(R.string.default_view_option)) ?: context.getString(R.string.default_view_option)
        set(value) = sharedPreferences.edit().putString(context.getString(R.string.view_option_key), value).apply()

    var sortOption: String
        get() = sharedPreferences.getString(context.getString(R.string.sort_option_key), context.getString(R.string.default_sort_option)) ?: context.getString(R.string.default_sort_option)
        set(value) = sharedPreferences.edit().putString(context.getString(R.string.sort_option_key), value).apply()

    var offersPerRow: Int
        get() = sharedPreferences.getInt(context.getString(R.string.offers_per_row_key), 2)
        set(value) = sharedPreferences.edit().putInt(context.getString(R.string.offers_per_row_key), value).apply()

    var favoriteShops: Set<String>
        get() = sharedPreferences.getStringSet(context.getString(R.string.favorite_shops_key), emptySet()) ?: emptySet()
        set(value) = sharedPreferences.edit().putStringSet(context.getString(R.string.favorite_shops_key), value).apply()
}