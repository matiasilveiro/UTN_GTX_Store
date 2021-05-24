package com.utn.hwstore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.utn.hwstore.R
import com.utn.hwstore.entities.HwItem
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.utn.hwstore.databinding.ItemListHwBinding
import kotlin.properties.Delegates

class HwItemAdapter(): RecyclerView.Adapter<HwItemAdapter.ViewHolder>() {

    var items: List<HwItem> by Delegates.observable(emptyList()) { _, _, _ -> notifyDataSetChanged() }
    var onClickListener : ( (HwItem) -> Unit )? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListHwBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], onClickListener)
    }

    fun setData (data:MutableList<HwItem>){
        this.items = data
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(private val binding: ItemListHwBinding) : RecyclerView.ViewHolder(binding.cvCartItem) {

        internal fun bind(value: HwItem, listener: ((HwItem) -> Unit)?) {
            binding.txtBrand.text = value.brand
            binding.txtModel.text = value.model
            Glide.with(binding.root)
                .load(value.imageURL)
                .centerCrop()
                .into(binding.imgArticle)

            binding.cvCartItem.setOnClickListener {
                listener?.invoke(value)
            }
        }
    }
}