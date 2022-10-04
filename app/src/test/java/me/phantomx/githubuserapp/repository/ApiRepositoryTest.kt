package me.phantomx.githubuserapp.repository

import android.content.Context
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import me.phantomx.githubuserapp.data.SearchData
import me.phantomx.githubuserapp.data.ShortUser
import me.phantomx.githubuserapp.data.entity.User
import me.phantomx.githubuserapp.sealed.ApiResponse
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ApiRepositoryTest {

    private lateinit var apiRepository: ApiRepository

    private val fakeShortUser = ShortUser("https://avatars.githubusercontent.com/u/66577?v=4", 66577, "JakeWharton", 1.0)

    @Before
    fun initializeRepository() {
        val context = mock(Context::class.java)
        apiRepository = ApiRepository(context)
        apiRepository.coroutineScope = CoroutineScope(Dispatchers.Default)
    }

    @Test
    fun searchUser() = runBlocking {
        val it = suspendCoroutine { conti ->
            apiRepository.searchUser("Jake") {
                conti.resume(it)
            }
        }
        when (it) {
            is ApiResponse.Error -> assertThat(it.message).isNotEmpty()
            is ApiResponse.Success<*> -> {
                assertThat(it.data).isInstanceOf(SearchData::class.java)
                assertThat((it.data as SearchData).shortUsers).isNotEmpty()
            }
        }
    }

    @Test
    fun fetchUser() = runBlocking {
        val it = suspendCoroutine { conti ->
            apiRepository.fetchUser(fakeShortUser) {
                conti.resume(it)
            }
        }
        when (it) {
            is ApiResponse.Error -> assertThat(it.message).isNotEmpty()
            is ApiResponse.Success<*> -> {
                assertThat(it.data).isInstanceOf(User::class.java)
                assertThat((it.data as User).id).isGreaterThan(0)
            }
        }
    }

    @Test
    fun fetchFollowers() = runBlocking {
        val it = suspendCoroutine { conti ->
            apiRepository.fetchFollowers(fakeShortUser) {
                conti.resume(it)
            }
        }
        when(it) {
            is ApiResponse.Error -> assertThat(it.message).isNotEmpty()
            is ApiResponse.Success<*> -> {
                assertThat(it.data).isInstanceOf(List::class.java)
                assertThat((it.data as List<*>).filterIsInstance(ShortUser::class.java)).isNotEmpty()
            }
        }
    }

    @Test
    fun fetchFollowing() = runBlocking {
        val it = suspendCoroutine { conti ->
            apiRepository.fetchFollowing(fakeShortUser) {
                conti.resume(it)
            }
        }
        assertThat(it).isNotEmpty()
    }
}