package me.phantomx.githubuserapp.repository

import android.content.Context
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import me.phantomx.githubuserapp.BuildConfig
import me.phantomx.githubuserapp.R
import me.phantomx.githubuserapp.data.SearchData
import me.phantomx.githubuserapp.data.ShortUser
import me.phantomx.githubuserapp.data.UnknownApiResponse
import me.phantomx.githubuserapp.data.entity.User
import me.phantomx.githubuserapp.extension.httpGet
import me.phantomx.githubuserapp.extension.toObj
import me.phantomx.githubuserapp.sealed.ApiResponse
import java.net.URLEncoder
import kotlin.coroutines.CoroutineContext

class ApiRepository(val context: Context): CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = coroutineScope.coroutineContext

    lateinit var coroutineScope: CoroutineScope

    private var searchJob: Job? = null
    private var fetchUserJob: Job? = null
    private var fetchFollowersJob: Job? = null
    private var fetchFollowingJob: Job? = null

    fun searchUser(query: String, block: (ApiResponse) -> Unit) {
        if (searchJob?.isActive == true) searchJob?.cancel()
        searchJob = launch {

            @Suppress("BlockingMethodInNonBlockingContext")
            String.format(BuildConfig.SEARCH_API_URL, withContext(Default) {
                URLEncoder.encode(query, "utf-8")
            }).httpGet(context) { response ->
                block(if (!response.isSuccessfully)
                    ApiResponse.Error(response.body)
                else response.body.toObj<SearchData> {
                    it.shortUsers.size
                    true
                }?.let {
                    if (it.shortUsers.isEmpty()) {
                        block(ApiResponse.Success(it))
                        ApiResponse.Message(
                            context.resources.getString(R.string.info_title),
                            context.resources.getString(R.string.no_user_found_message, query)
                        )
                    } else
                        ApiResponse.Success(it)
                } ?: ApiResponse.Error(response.body))
            }
        }
    }

    fun fetchUser(shortUser: ShortUser, block: (ApiResponse) -> Unit) {
        if (fetchUserJob?.isActive == true) return
        fetchUserJob = launch {

            String.format(BuildConfig.USER_API_URL, shortUser.login).httpGet(context) { response ->
                block(if (!response.isSuccessfully)
                    ApiResponse.Error(response.body)
                else response.body.toObj<User> {
                    it.htmlUrl.isNotEmpty()
                }?.let {
                    ApiResponse.Success(it)
                } ?: response.body.toObj<UnknownApiResponse> {
                    it.message.isNotEmpty()
                }?.let {
                    ApiResponse.Error(it.message)
                } ?: ApiResponse.Error(response.body))
            }
            fetchUserJob = null
        }
    }

    fun fetchFollowers(shortUser: ShortUser, block: (ApiResponse) -> Unit) {
        if (fetchFollowersJob?.isActive == true) return
        fetchFollowersJob = launch {
            fetchUserJob?.join()

            String.format(BuildConfig.FOLLOWERS_API_URL, shortUser.login).httpGet(context) { response ->
                block(if (!response.isSuccessfully)
                    ApiResponse.Error(response.body)
                else
                    response.body.toObj<List<ShortUser>>()?.let {
                        ApiResponse.Success(it)
                    } ?: response.body.toObj<UnknownApiResponse>()?.let {
                        ApiResponse.Error(it.message)
                    } ?: ApiResponse.Error(response.body))
            }
        }
    }

    fun fetchFollowing(shortUser: ShortUser, block: (List<ShortUser>) -> Unit) {
        if (fetchFollowingJob?.isActive == true) return
        fetchFollowingJob = launch {
            fetchUserJob?.join()

            String.format(BuildConfig.FOLLOWING_API_URL, shortUser.login).httpGet(context) {
                block(if (!it.isSuccessfully)
                    listOf()
                else
                    it.body.toObj() ?: listOf())
            }
        }
    }

}