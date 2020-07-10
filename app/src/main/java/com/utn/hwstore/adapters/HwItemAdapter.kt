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


class HwItemAdapter(options: FirestoreRecyclerOptions<HwItem>, val onItemClick: (HwItem) -> Unit) : FirestoreRecyclerAdapter<HwItem, HwItemAdapter.HwItemViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HwItemViewHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.item_list_hw,parent,false)
        return HwItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: HwItemViewHolder, position: Int, model: HwItem) {
        holder.setBrand(model.brand)
        holder.setModel(model.model)
        holder.setImage(model.imageURL)
        holder.getCardLayout().setOnClickListener {
            onItemClick(model)
        }
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