package com.utn.hwstore.database

import androidx.room.*
import com.utn.hwstore.entities.User

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY username")
    fun loadAllPersons(): MutableList<User?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPerson(user: User?)

    @Update
    fun updatePerson(user: User?)

    @Delete
    fun delete(user: User?)

    @Query("SELECT * FROM users WHERE username = :username")
    fun loadPersonByUsername(username: String): User?
}