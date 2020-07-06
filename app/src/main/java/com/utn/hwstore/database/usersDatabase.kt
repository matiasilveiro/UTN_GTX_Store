package com.utn.hwstore.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.utn.hwstore.entities.User

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class usersDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        var INSTANCE: usersDatabase? = null

        fun getAppDataBase(context: Context): usersDatabase? {
            if (INSTANCE == null) {
                synchronized(usersDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        usersDatabase::class.java,
                        "userDB"
                    ).allowMainThreadQueries().build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }
}