package me.phantomx.githubuserapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import me.phantomx.githubuserapp.data.SearchData
import me.phantomx.githubuserapp.repository.ApiRepository
import me.phantomx.githubuserapp.sealed.ApiResponse
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiRepository: ApiRepository
) : ViewModel() {

    init {
        apiRepository.coroutineScope = viewModelScope
    }

    private lateinit var data: SearchData

    private val _searchLiveData = MutableLiveData<ApiResponse>(null)
    val searchLiveData: LiveData<ApiResponse>
        get() = _searchLiveData

    fun searchUser(query: String) = apiRepository.searchUser(query) {
        if (it is ApiResponse.Success<*> && it.data is SearchData)
            data = it.data
        _searchLiveData.value = it
    }

}