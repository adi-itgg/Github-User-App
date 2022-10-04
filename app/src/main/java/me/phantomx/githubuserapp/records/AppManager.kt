package me.phantomx.githubuserapp.records

import androidx.datastore.preferences.core.intPreferencesKey
import com.google.gson.Gson
import okhttp3.OkHttpClient

object AppManager {

    const val SETTINGS_DATA_STORE = "settings"
    val SETTINGS_DARK_MODE = intPreferencesKey("dark_mode")

    val gson = Gson()
    val okHttpClient = OkHttpClient()

}