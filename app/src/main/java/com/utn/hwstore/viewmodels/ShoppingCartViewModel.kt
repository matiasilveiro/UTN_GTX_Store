package com.utn.hwstore.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.utn.hwstore.entities.HwItem

class ShoppingCartViewModel : ViewModel() {
    val cart: ArrayList<HwItem> = ArrayList<HwItem>()
    var subtotal: MutableLiveData<Double> = MutableLiveData(0.0)

    fun updateSubtotal() {
        var sum = 0.0
        cart.forEach {
            sum += it.price
        }
        subtotal.value = sum
    }
}
