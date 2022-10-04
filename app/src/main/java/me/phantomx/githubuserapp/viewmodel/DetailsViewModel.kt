package me.phantomx.githubuserapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import me.phantomx.githubuserapp.data.ShortUser
import me.phantomx.githubuserapp.data.entity.User
import me.phantomx.githubuserapp.extension.showDialog
import me.phantomx.githubuserapp.repository.ApiRepository
import me.phantomx.githubuserapp.sealed.ApiResponse
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val apiRepository: ApiRepository
) : ViewModel() {

    init {
        apiRepository.coroutineScope = viewModelScope
    }

    lateinit var shortUser: ShortUser
    val isUserInitialized
        get() = this::shortUser.isInitialized
    lateinit var user: User

    private lateinit var followerData: List<ShortUser>
    private lateinit var followingData: List<ShortUser>

    private val _userLiveData = MutableLiveData<ApiResponse>()
    val userLiveData: LiveData<ApiResponse> = _userLiveData

    private val _followersLiveData = MutableLiveData<ApiResponse>()
    val followersLiveData: LiveData<ApiResponse> = _followersLiveData

    private val _followingLiveData = MutableLiveData<List<ShortUser>>()
    val followingLiveData: LiveData<List<ShortUser>> = _followingLiveData

    var isFromFavorite = false

    fun fetchUser() {
        if (this::user.isInitialized) {
            _userLiveData.value = ApiResponse.Success(user)
            return
        }
        apiRepository.fetchUser(shortUser) {
            if (it is ApiResponse.Success<*> && it.data is User)
                user = it.data
            _userLiveData.value = it
        }
    }

    fun fetchFollowers() {
        if (!this::shortUser.isInitialized) return
        if (this::followerData.isInitialized) {
            _followersLiveData.value = ApiResponse.Success(followerData)
            return
        }

        apiRepository.fetchFollowers(shortUser) {
            when(val response = it) {
                is ApiResponse.Message -> apiRepository.context.showDialog(response.title, response.content)
                is ApiResponse.Success<*> -> {
                    if (response.data is List<*>)
                        followerData = response.data.filterIsInstance(ShortUser::class.java)
                    _followersLiveData.value = it
                }
                is ApiResponse.Error -> {
                    if (isFromFavorite)
                        _followersLiveData.value = it
                    else
                        _followersLiveData.value = ApiResponse.Success(listOf<List<ShortUser>>())
                }
            }
        }
    }

    fun fetchFollowing() {
        if (!this::shortUser.isInitialized) return
        if (this::followingData.isInitialized) {
            _followingLiveData.value = followingData
            return
        }

        apiRepository.fetchFollowing(shortUser) {
            followingData = it
            _followingLiveData.value = it
        }
    }

}