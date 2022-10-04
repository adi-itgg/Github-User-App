package me.phantomx.githubuserapp.activity

import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.phantomx.githubuserapp.activity.base.BaseActivityScope
import me.phantomx.githubuserapp.adapter.UserAdapter
import me.phantomx.githubuserapp.databinding.ActivityFavoriteBinding
import me.phantomx.githubuserapp.repository.DatabaseRepository
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteActivity : BaseActivityScope<ActivityFavoriteBinding>() {

    @Inject
    lateinit var dbRepository: DatabaseRepository

    override fun initViewBinding() = ActivityFavoriteBinding.inflate(layoutInflater)

    override fun initialize() = with(binding) {

        back.setOnClickListener {
            onBackPressed()
        }

        with(recyclerView) {
            layoutManager = LinearLayoutManager(mThis)
            adapter = UserAdapter(mThis).apply {
                lifecycleScope.launch {
                    dbRepository.getAll.collectLatest {
                        update(it)
                    }
                }
            }
        }
    }

}