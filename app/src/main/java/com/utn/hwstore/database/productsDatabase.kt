package com.utn.hwstore.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.utn.hwstore.entities.HwItem

@Database(entities = [HwItem::class], version = 1, exportSchema = false)
abstract class productsDatabase: RoomDatabase() {
    abstract fun hwItemDao(): HwItemDao

    companion object {
        var INSTANCE: productsDatabase? = null

        fun getAppDataBase(context: Context): productsDatabase? {
            if (INSTANCE == null) {
                synchronized(productsDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        productsDatabase::class.java,
                        "myDB"
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