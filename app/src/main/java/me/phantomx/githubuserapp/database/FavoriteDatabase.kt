package me.phantomx.githubuserapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import me.phantomx.githubuserapp.BuildConfig
import me.phantomx.githubuserapp.data.entity.User

@Database(entities = [User::class], version = 1)
abstract class FavoriteDatabase: RoomDatabase() {

    abstract fun getDao(): FavoriteDao

    companion object {
        private var instance: FavoriteDatabase? = null
        fun getInstance(context: Context): FavoriteDatabase {
            return instance ?: synchronized(this) {
                val nInstance = Room.databaseBuilder(
                    context,
                    FavoriteDatabase::class.java,
                    BuildConfig.FAVORITE_DATABASE
                ).build()
                instance = nInstance
                nInstance
            }
        }
    }

}