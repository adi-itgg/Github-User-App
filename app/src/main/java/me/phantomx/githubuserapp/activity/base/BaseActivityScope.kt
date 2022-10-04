package me.phantomx.githubuserapp.activity.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.phantomx.githubuserapp.extension.settingsDataStore
import me.phantomx.githubuserapp.listener.BaseActivityScopeListener
import me.phantomx.githubuserapp.records.AppManager.SETTINGS_DARK_MODE

abstract class BaseActivityScope<T: ViewBinding>: AppCompatActivity(), BaseActivityScopeListener<T> {

    private lateinit var _binding: T
    val binding: T
    get() = _binding

    val mThis: BaseActivityScope<T>
    get() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        lifecycleScope.launch(Main.immediate) {
            settingsDataStore.data.collectLatest {
                when(it[SETTINGS_DARK_MODE] ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
                    AppCompatDelegate.MODE_NIGHT_YES -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    AppCompatDelegate.MODE_NIGHT_NO -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }
        beforeCreate()
        super.onCreate(savedInstanceState)
        _binding = initViewBinding()
        setContentView(binding.root)
        initialize()
    }

    open fun beforeCreate() {}

}