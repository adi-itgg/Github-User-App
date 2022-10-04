package me.phantomx.githubuserapp.activity

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.phantomx.githubuserapp.R
import me.phantomx.githubuserapp.activity.base.BaseActivityScope
import me.phantomx.githubuserapp.adapter.ShortUserAdapter
import me.phantomx.githubuserapp.data.SearchData
import me.phantomx.githubuserapp.databinding.ActivityHomeBinding
import me.phantomx.githubuserapp.extension.settingsDataStore
import me.phantomx.githubuserapp.extension.showDialog
import me.phantomx.githubuserapp.listener.SearchViewOnQueryTextListener
import me.phantomx.githubuserapp.records.AppManager.SETTINGS_DARK_MODE
import me.phantomx.githubuserapp.sealed.ApiResponse
import me.phantomx.githubuserapp.viewmodel.HomeViewModel


@AndroidEntryPoint
class HomeActivity : BaseActivityScope<ActivityHomeBinding>() {

    private val viewModel by viewModels<HomeViewModel>()

    private val shortUserAdapter = ShortUserAdapter(this)

    private var backJob: Job? = null

    override fun initViewBinding() = ActivityHomeBinding.inflate(layoutInflater)

    override fun beforeCreate() {
        installSplashScreen()
    }

    override fun initialize() = with(binding) {

        darkMode.setOnClickListener {
            lifecycleScope.launch {
                settingsDataStore.edit {
                    it[SETTINGS_DARK_MODE] = when (AppCompatDelegate.getDefaultNightMode()) {
                        AppCompatDelegate.MODE_NIGHT_YES -> AppCompatDelegate.MODE_NIGHT_NO
                        else -> AppCompatDelegate.MODE_NIGHT_YES
                    }
                }
            }
        }

        fav.setOnClickListener {
            startActivity(Intent(mThis, FavoriteActivity::class.java))
        }

        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = shortUserAdapter
        }

        with(viewModel) {
            searchLiveData.observe(mThis) {
                if (it == null) return@observe

                recyclerView.visibility = View.VISIBLE
                with(shimmerLayout) {
                    stopShimmer()
                    visibility = View.GONE
                }

                when (val data = it) {
                    is ApiResponse.Message -> showDialog(data.title, data.content)
                    is ApiResponse.Success<*> ->
                        if (data.data is SearchData)
                            shortUserAdapter.update(data.data.shortUsers)
                    is ApiResponse.Error -> showDialog(
                        resources.getString(R.string.error),
                        data.message
                    )
                }
            }

            with(searchView) {
                isIconified = false
                clearFocus()
                setOnQueryTextListener(object : SearchViewOnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        recyclerView.visibility = View.GONE
                        shimmerLayout.visibility = View.VISIBLE
                        shimmerLayout.showShimmer(true)
                        shimmerLayout.startShimmer()
                        searchUser(query ?: "")
                        clearFocus()
                        return true
                    }
                })
            }
        }

    }

    override fun onBackPressed() {
        if (backJob?.isActive == true) {
            super.onBackPressed()
            return
        }
        backJob = lifecycleScope.launch {
            Toast.makeText(
                mThis,
                resources.getString(R.string.back_twice_message),
                Toast.LENGTH_SHORT
            ).show()
            delay(2000)
        }
    }

}