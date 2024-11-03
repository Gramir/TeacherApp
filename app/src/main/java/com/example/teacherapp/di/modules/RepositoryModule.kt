package com.example.teacherapp.di.modules

import com.example.teacherapp.data.repository.*
import com.example.teacherapp.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTeacherRepository(
        impl: TeacherRepositoryImpl
    ): TeacherRepository

    @Binds
    @Singleton
    abstract fun bindCourseRepository(
        impl: CourseRepositoryImpl
    ): CourseRepository

    @Binds
    @Singleton
    abstract fun bindStudentRepository(
        impl: StudentRepositoryImpl
    ): StudentRepository

    @Binds
    @Singleton
    abstract fun bindAssignmentRepository(
        impl: AssignmentRepositoryImpl
    ): AssignmentRepository

    @Binds
    @Singleton
    abstract fun bindAttendanceRepository(
        impl: AttendanceRepositoryImpl
    ): AttendanceRepository
}
