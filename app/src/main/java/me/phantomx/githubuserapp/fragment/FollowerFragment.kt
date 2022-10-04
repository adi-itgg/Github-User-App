package me.phantomx.githubuserapp.fragment

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import me.phantomx.githubuserapp.R
import me.phantomx.githubuserapp.adapter.ShortUserAdapter
import me.phantomx.githubuserapp.data.ShortUser
import me.phantomx.githubuserapp.databinding.FragmentFollowerBinding
import me.phantomx.githubuserapp.extension.showDialog
import me.phantomx.githubuserapp.fragment.base.BaseFragment
import me.phantomx.githubuserapp.sealed.ApiResponse
import me.phantomx.githubuserapp.viewmodel.DetailsViewModel

class FollowerFragment: BaseFragment<FragmentFollowerBinding>() {

    private val viewModel by activityViewModels<DetailsViewModel>()

    override fun initBinding() = FragmentFollowerBinding.inflate(layoutInflater)

    override fun onCreated() = with(binding.root) {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = ShortUserAdapter(requireActivity()).apply {
            viewModel.followersLiveData.observe(viewLifecycleOwner) {
                if (it == null) return@observe
                when(val response = it) {
                    is ApiResponse.Message -> requireContext().showDialog(response.title, response.content)
                    is ApiResponse.Success<*> -> {
                        if (response.data is List<*>)
                            update(response.data.filterIsInstance(ShortUser::class.java))
                    }
                    is ApiResponse.Error -> requireContext().showDialog(
                        resources.getString(R.string.error),
                        response.message
                    ) {
                        setOnDismissListener {
                            requireActivity().onBackPressed()
                        }
                    }
                }
            }
            viewModel.fetchFollowers()
        }
    }

}