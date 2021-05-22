package com.utn.hwstore.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.utn.hwstore.entities.HwItem

class DetailsViewModel : ViewModel() {
    val item: MutableLiveData<HwItem> = MutableLiveData<HwItem>()
}
