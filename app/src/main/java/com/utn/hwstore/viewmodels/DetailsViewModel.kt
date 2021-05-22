package com.utn.hwstore.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.utn.hwstore.entities.HwItem

class DetailsViewModel : ViewModel() {
    val item: MutableLiveData<HwItem> = MutableLiveData<HwItem>()
}
