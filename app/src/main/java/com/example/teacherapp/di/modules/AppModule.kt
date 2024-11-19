package com.example.teacherapp.di.modules

import com.example.teacherapp.data.datasource.remote.firebase.SessionManager
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSessionManager(
        auth: FirebaseAuth
    ): SessionManager {
        return SessionManager(auth)
    }
}