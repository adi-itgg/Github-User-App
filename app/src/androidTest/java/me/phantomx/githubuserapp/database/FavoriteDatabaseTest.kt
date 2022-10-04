package me.phantomx.githubuserapp.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import me.phantomx.githubuserapp.data.entity.User
import me.phantomx.githubuserapp.repository.DatabaseRepository
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteDatabaseTest : TestCase() {

    private lateinit var db: FavoriteDatabase
    private lateinit var dbRepository: DatabaseRepository
    private val fakeUser = User(
        66577,
        "https://avatars.githubusercontent.com/u/66577?v=4",
        null,
        1000,
        1000,
        "",
        null,
        "JakeWharton",
        "Jake Wharton",
        999)

    @Before
    public override fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, FavoriteDatabase::class.java).build()
        dbRepository = DatabaseRepository(db.getDao())
    }

    @After
    fun releaseResources() {
        db.close()
    }

    @Test
    fun insertAndCheckData() = runBlocking {
        dbRepository.addUser(fakeUser)
        assertThat(dbRepository.isUserInFav(fakeUser)).isTrue()
    }

    @Test
    fun deleteData() = runBlocking {
        assertThat(dbRepository.deleteUser(fakeUser)).isNotEqualTo(1)
    }


}