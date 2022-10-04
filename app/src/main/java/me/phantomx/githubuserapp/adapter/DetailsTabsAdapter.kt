package me.phantomx.githubuserapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import me.phantomx.githubuserapp.fragment.FollowerFragment
import me.phantomx.githubuserapp.fragment.FollowingFragment

class DetailsTabsAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle):
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment = when(position) {
        1 -> FollowingFragment()
        else -> FollowerFragment()
    }
}