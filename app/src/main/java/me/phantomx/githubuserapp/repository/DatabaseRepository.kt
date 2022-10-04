package me.phantomx.githubuserapp.repository

import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import me.phantomx.githubuserapp.data.entity.User
import me.phantomx.githubuserapp.database.FavoriteDao

class DatabaseRepository(private val favoriteDao: FavoriteDao) {

    val getAll: Flow<List<User>>
    get() = favoriteDao.getAll()

    suspend fun isUserInFav(user: User): Boolean = withContext(IO) {
        favoriteDao.get(user.id) != null
    }

    suspend fun subscribe(user: User, block: (Boolean) -> Unit) = withContext(Default)  {
        favoriteDao.getAll().collectLatest {
            var isExist = false
            for (u in it) {
                if (u.id != user.id) continue
                isExist = true
                break
            }
            withContext(Main) {
                block(isExist)
            }
        }
    }

    suspend fun addUser(user: User) = withContext(IO) {
        favoriteDao.insert(user)
    }

    suspend fun deleteUser(user: User) = withContext(IO) {
        favoriteDao.delete(user.id)
    }

}