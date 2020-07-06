package com.utn.hwstore.database

import androidx.room.*
import com.utn.hwstore.entities.HwItem

@Dao
interface HwItemDao {
    @Query("SELECT * FROM products ORDER BY type")
    fun loadAllProducts(): MutableList<HwItem?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduct(item: HwItem?)

    @Update
    fun updateProduct(item: HwItem?)

    @Delete
    fun delete(user: HwItem?)

    @Query("SELECT * FROM products WHERE model = :model")
    fun loadProductByModel(model: String): HwItem?
}