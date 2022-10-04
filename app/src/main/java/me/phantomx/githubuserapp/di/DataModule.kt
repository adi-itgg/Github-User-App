package me.phantomx.githubuserapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.components.SingletonComponent
import me.phantomx.githubuserapp.database.FavoriteDatabase
import me.phantomx.githubuserapp.repository.ApiRepository
import me.phantomx.githubuserapp.repository.DatabaseRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DBRepositoryModule {

    @Provides
    @Singleton
    fun provideRepositoryDatabase(@ApplicationContext context: Context): DatabaseRepository =
        DatabaseRepository(FavoriteDatabase.getInstance(context).getDao())

}


@Module
@InstallIn(ActivityRetainedComponent::class)
class RepositoryModule {

    @Provides
    @ActivityRetainedScoped
    fun provideApiRepository(@ApplicationContext context: Context) = ApiRepository(context)

}