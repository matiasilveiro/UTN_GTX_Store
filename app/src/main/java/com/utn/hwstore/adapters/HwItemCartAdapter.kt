package com.utn.hwstore.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.utn.hwstore.databinding.ItemCartListBorderlessBinding
import com.utn.hwstore.entities.HwItem

class HwItemCartAdapter(private val itemList: ArrayList<HwItem>, val onItemLongClick: (HwItem) -> Unit): RecyclerView.Adapter<HwItemCartAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCartListBorderlessBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position], null)

        holder.binding.cvCartItem.setOnLongClickListener {
            onItemLongClick(itemList[position])
            itemList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemList.size)
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ViewHolder(val binding: ItemCartListBorderlessBinding) : RecyclerView.ViewHolder(binding.cvCartItem) {

        internal fun bind(value: HwItem, listener: ((HwItem) -> Unit)?) {
            binding.txtBrand.text = value.brand
            binding.txtModel.text = value.model
            binding.txtPrice.text = value.price.toString()
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