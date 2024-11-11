package com.example.teacherapp.di.modules

import com.example.teacherapp.domain.repository.*
import com.example.teacherapp.domain.usecase.assignment.*
import com.example.teacherapp.domain.usecase.attendance.*
import com.example.teacherapp.domain.usecase.auth.*
import com.example.teacherapp.domain.usecase.course.*
import com.example.teacherapp.domain.usecase.student.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    // Auth Use Cases
    @Provides
    @ViewModelScoped
    fun provideLoginUseCase(repository: TeacherRepository): LoginUseCase {
        return LoginUseCase(repository)
    }

    // Course Use Cases
    @Provides
    @ViewModelScoped
    fun provideGetCoursesUseCase(repository: CourseRepository): GetCoursesUseCase {
        return GetCoursesUseCase(repository)
    }

    // Assignment Use Cases
    @Provides
    @ViewModelScoped
    fun provideGetAssignmentsUseCase(repository: AssignmentRepository): GetAssignmentsUseCase {
        return GetAssignmentsUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideCreateAssignmentUseCase(repository: AssignmentRepository): CreateAssignmentUseCase {
        return CreateAssignmentUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideUpdateAssignmentUseCase(repository: AssignmentRepository): UpdateAssignmentUseCase {
        return UpdateAssignmentUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideDeleteAssignmentUseCase(repository: AssignmentRepository): DeleteAssignmentUseCase {
        return DeleteAssignmentUseCase(repository)
    }

    // Attendance Use Cases
    @Provides
    @ViewModelScoped
    fun provideGetAttendanceUseCase(repository: AttendanceRepository): GetAttendanceUseCase {
        return GetAttendanceUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideSaveAttendanceUseCase(repository: AttendanceRepository): SaveAttendanceUseCase {
        return SaveAttendanceUseCase(repository)
    }

    // Student Use Cases
    @Provides
    @ViewModelScoped
    fun provideGetStudentsUseCase(repository: StudentRepository): GetStudentsUseCase {
        return GetStudentsUseCase(repository)
    }
}