package com.utn.hwstore.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
class User(username: String, password: String) : Parcelable {
    @PrimaryKey
    @ColumnInfo(name = "username")
    var username: String = username

    @ColumnInfo(name = "password")
    var password: String = password

    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(username)
        writeString(password)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User = User(source)
            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        }
    }

    override fun equals(other: Any?): Boolean {
        val user = other as User
        return (this.username.equals(user.username) and this.password.equals(user.password))
    }
}