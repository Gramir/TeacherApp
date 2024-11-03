package com.example.teacherapp.di.modules

import com.example.teacherapp.data.datasource.remote.firebase.FirebaseDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseDataSource(): FirebaseDataSource {
        return FirebaseDataSource()
    }
}