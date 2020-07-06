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

class HwItemAdapter(private val itemList: ArrayList<HwItem>, val onItemClick: (HwItem) -> Unit): RecyclerView.Adapter<HwItemAdapter.HwItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HwItemViewHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.item_list_hw,parent,false)
        return HwItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: HwItemViewHolder, position: Int) {
        holder.setBrand(itemList[position].brand)
        holder.setModel(itemList[position].model)
        holder.setImage(itemList[position].imageURL)
        holder.getCardLayout().setOnClickListener {
            onItemClick(itemList[position])
        }

        holder.getCardLayout().setOnLongClickListener {
            itemList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemList.size)
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class HwItemViewHolder (v: View) : RecyclerView.ViewHolder(v){
        private var view: View = v

        internal fun setBrand(name: String) {
            val brand = view.findViewById(R.id.txt_brand) as TextView
            brand.text = name
        }

        internal fun setModel(address: String) {
            val model = view.findViewById(R.id.txt_model) as TextView
            model.text = address
        }

        internal fun setImage(imageURL: String) {
            val imageView = view.findViewById(R.id.img_article) as ImageView
            Glide.with(view)
                .load(imageURL)
                .centerCrop()
                .into(imageView)
        }

        internal fun getCardLayout(): CardView {
            return view.findViewById(R.id.cv_cart_item)
        }
    }
}