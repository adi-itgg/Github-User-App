package me.phantomx.githubuserapp.activity

import android.view.View
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.phantomx.githubuserapp.R
import me.phantomx.githubuserapp.activity.base.BaseActivityScope
import me.phantomx.githubuserapp.adapter.DetailsTabsAdapter
import me.phantomx.githubuserapp.data.ShortUser
import me.phantomx.githubuserapp.data.entity.User
import me.phantomx.githubuserapp.databinding.ActivityDetailsBinding
import me.phantomx.githubuserapp.extension.ifEmptyOrNullDo
import me.phantomx.githubuserapp.extension.showDialog
import me.phantomx.githubuserapp.repository.DatabaseRepository
import me.phantomx.githubuserapp.sealed.ApiResponse
import me.phantomx.githubuserapp.viewmodel.DetailsViewModel
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class DetailsActivity : BaseActivityScope<ActivityDetailsBinding>() {

    @Inject
    lateinit var dbRepository: DatabaseRepository

    private val viewModel by viewModels<DetailsViewModel>()

    override fun initViewBinding() = ActivityDetailsBinding.inflate(layoutInflater)

    override fun initialize() = with(binding) {

        shimmer.startShimmer()

        back.setOnClickListener {
            onBackPressed()
        }

        val btnBackMarginSize = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
        val paddingLeftSize = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._62sdp)

        val appBarLeftPadding = paddingLeftSize.toDouble() / 100.0
        val maxBtnTopMargin = btnBackMarginSize.toDouble() / 100.0

        var appBarJob: Job? = null
        var totalScroll = -1

        with(binding.mainLayout) {

            appBar.addOnOffsetChangedListener { _, verticalOffset ->
                if (appBarJob?.isActive == true) appBarJob?.cancel()
                appBarJob = lifecycleScope.launch {
                    delay(1)
                    val voffset = abs(verticalOffset)
                    if (totalScroll == -1)
                        totalScroll = appBar.totalScrollRange
                    if (voffset - totalScroll == 0) {
                        toolbar.setPadding(paddingLeftSize, 0, 0, 0)

                        back.updateUI(btnFav) {
                            topMargin = 0
                            leftMargin = 0
                            rightMargin = 0
                        }
                    } else if (verticalOffset == 0) {
                        toolbar.setPadding(0, 0, 0, 0)

                        back.updateUI(btnFav) {
                            topMargin = btnBackMarginSize
                            leftMargin = btnBackMarginSize
                            rightMargin = btnBackMarginSize
                        }
                    } else {
                        val percent =
                            ((voffset.toDouble() / totalScroll.toDouble()) * 100.0).toInt()
                        val reversePercentage = 100 - percent

                        toolbar.setPadding((appBarLeftPadding * percent).toInt(), 0, 0, 0)

                        val topm = maxBtnTopMargin * reversePercentage
                        val m = topm.toInt()

                        back.updateUI(btnFav) {
                            topMargin = m
                            leftMargin = m
                            rightMargin = m
                        }

                    }
                }
            }

            with(viewModel) {
                if (!isUserInitialized)
                    intent.getParcelableExtra<User>(User::class.simpleName)?.run {
                        user = this
                        shortUser = ShortUser(avatarUrl, id, login, 1.0)
                        isFromFavorite = true
                    }
                if (!isUserInitialized)
                    shortUser = intent.getParcelableExtra(ShortUser::class.simpleName) ?: run {
                        onBackPressed()
                        return
                    }

                toolbar.title = shortUser.login

                image.load(shortUser.avatarUrl) {
                    placeholder(R.drawable.ic_account_circle)
                }

                btnFav.setOnClickListener {
                    lifecycleScope.launch {
                        if (dbRepository.isUserInFav(user))
                            dbRepository.deleteUser(user)
                        else
                            dbRepository.addUser(user)
                    }
                }

                binding.shimmerLayout.simage.load(shortUser.avatarUrl)

                viewPager.offscreenPageLimit = 2
                viewPager.adapter = DetailsTabsAdapter(supportFragmentManager, lifecycle)

                TabLayoutMediator(tabs, viewPager) { tab, position ->
                    tab.text = resources.getString(
                        when (position) {
                            1 -> R.string.following
                            else -> R.string.follower
                        }
                    )
                }.attach()


                userLiveData.observe(mThis) {

                    when (val data = it) {
                        is ApiResponse.Message -> showDialog(data.title, data.content)
                        is ApiResponse.Success<*> -> if (data.data is User) {

                            image.load(user.avatarUrl)

                            toolbar.title = user.name
                            name.text = user.name
                            username.text = user.login
                            followers.text = user.followers.toString()
                            following.text = user.following.toString()


                            company.text = user.company.ifEmptyOrNullDo {
                                iconCompany.visibility = View.GONE
                                company.visibility = View.GONE
                            }
                            location.text = user.location.ifEmptyOrNullDo {
                                iconLocation.visibility = View.GONE
                                location.visibility = View.GONE
                            }
                            repository.text = user.publicRepos.toString().apply {
                                if (this != 0.toString()) return@apply
                                iconRepository.visibility = View.GONE
                                repository.visibility = View.GONE
                            }

                            lifecycleScope.launch {
                                dbRepository.subscribe(user) { isExist ->
                                    btnFav.setImageResource(
                                        if (isExist)
                                            R.drawable.ic_round_favorite_24
                                        else
                                            R.drawable.ic_round_favorite_border_24
                                    )
                                }
                            }
                        }
                        is ApiResponse.Error -> showDialog(
                            resources.getString(R.string.error),
                            data.message
                        ) {
                            setOnDismissListener {
                                onBackPressed()
                            }
                        }
                    }

                    root.visibility = View.VISIBLE
                    with(binding) {
                        shimmer.stopShimmer()
                        shimmer.hideShimmer()
                        shimmerLayout.root.visibility = View.GONE
                    }
                }

                fetchUser()
            }
        }
    }

    private fun ImageButton.updateUI(
        vararg views: ImageButton = arrayOf(),
        block: CoordinatorLayout.LayoutParams.() -> Unit
    ) {
        updateLayoutParams(block)
        views.forEach {
            it.updateUI(block = block)
        }
    }

}