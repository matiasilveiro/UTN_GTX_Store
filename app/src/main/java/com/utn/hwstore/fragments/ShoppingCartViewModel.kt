package com.utn.hwstore.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.utn.hwstore.entities.HwItem

class ShoppingCartViewModel : ViewModel() {
    val cart: ArrayList<HwItem> = ArrayList<HwItem>()
    var subtotal: MutableLiveData<Double> = MutableLiveData(0.0)
}
