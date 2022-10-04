package me.phantomx.githubuserapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import me.phantomx.githubuserapp.data.entity.User

@Dao
interface FavoriteDao {

    @Insert(onConflict = REPLACE)
    suspend fun insert(user: User)

    @Query("SELECT * FROM user")
    fun getAll(): Flow<List<User>>

    @Query("SELECT * FROM user WHERE id =:id")
    suspend fun get(id: Int): User?

    @Query("DELETE FROM user WHERE id =:id")
    suspend fun delete(id: Int): Int

}