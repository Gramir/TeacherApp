package com.example.teacherapp.domain.model

import android.os.Parcel
import android.os.Parcelable

data class Assignment(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val courseId: String = "",
    val dueDate: String = "",
    val status: String = "pending",
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "pending",
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(courseId)
        parcel.writeString(dueDate)
        parcel.writeString(status)
        parcel.writeLong(createdAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Assignment> {
        override fun createFromParcel(parcel: Parcel): Assignment {
            return Assignment(parcel)
        }

        override fun newArray(size: Int): Array<Assignment?> {
            return arrayOfNulls(size)
        }
    }
}