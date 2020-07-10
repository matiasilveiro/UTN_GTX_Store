package com.utn.hwstore.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
class HwItem(brand: String, model: String, type: String, description: String, details: String, price: Double, imageURL: String, uid: String) : Parcelable {

    @ColumnInfo(name = "brand")
    var brand: String = ""

    @PrimaryKey
    @ColumnInfo(name = "model")
    var model: String = ""

    @ColumnInfo(name = "type")
    var type: String = ""

    @ColumnInfo(name = "description")
    var description: String = ""

    @ColumnInfo(name = "details")
    var details: String = ""

    @ColumnInfo(name = "price")
    var price: Double = 0.0

    @ColumnInfo(name = "imageURL")
    var imageURL: String = ""

    var uid: String = ""

    constructor() : this( "","","","","",0.0,"","")

    init {
        this.brand = brand
        this.model = model
        this.type = type
        this.description = description
        this.details = details
        this.price = price
        this.imageURL = imageURL
        this.uid = uid
    }

    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readDouble(),
        source.readString()!!,
        source.readString()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(brand)
        writeString(model)
        writeString(type)
        writeString(description)
        writeString(details)
        writeDouble(price)
        writeString(imageURL)
        writeString(uid)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<HwItem> = object : Parcelable.Creator<HwItem> {
            override fun createFromParcel(source: Parcel): HwItem = HwItem(source)
            override fun newArray(size: Int): Array<HwItem?> = arrayOfNulls(size)
        }
    }
}