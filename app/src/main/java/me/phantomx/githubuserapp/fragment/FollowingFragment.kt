package me.phantomx.githubuserapp.fragment

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import me.phantomx.githubuserapp.adapter.ShortUserAdapter
import me.phantomx.githubuserapp.databinding.FragmentFollowingBinding
import me.phantomx.githubuserapp.fragment.base.BaseFragment
import me.phantomx.githubuserapp.viewmodel.DetailsViewModel

class FollowingFragment: BaseFragment<FragmentFollowingBinding>() {

    private val viewModel by activityViewModels<DetailsViewModel>()

    override fun initBinding() = FragmentFollowingBinding.inflate(layoutInflater)

    override fun onCreated() = with(binding.root) {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = ShortUserAdapter(requireActivity()).apply {
            viewModel.followingLiveData.observe(viewLifecycleOwner) {
                if (it == null) return@observe
                update(it)
            }
            viewModel.fetchFollowing()
        }
    }

}