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
import com.utn.hwstore.entities.Review

class ReviewListAdapter(private val itemList: ArrayList<Review>, val onItemClick: (Review) -> Unit): RecyclerView.Adapter<ReviewListAdapter.ReviewListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewListViewHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.item_list_review,parent,false)
        return ReviewListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewListViewHolder, position: Int) {
        holder.setUsername(itemList[position].username)
        holder.setReview(itemList[position].review)
        holder.setStars(itemList[position].stars)
        holder.setImage(itemList[position].image)
        holder.getCardLayout().setOnClickListener {
            onItemClick(itemList[position])
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ReviewListViewHolder (v: View) : RecyclerView.ViewHolder(v){
        private var view: View = v

        internal fun setUsername(name: String) {
            val username = view.findViewById(R.id.txt_username) as TextView
            username.text = name
        }

        internal fun setReview(review: String) {
            val txtReview = view.findViewById(R.id.txt_review) as TextView
            txtReview.text = review
        }

        internal fun setStars(price: Double) {

        }

        internal fun setImage(imageURL: String) {
            val imageView = view.findViewById(R.id.img_user) as ImageView
            Glide.with(view)
                .load(imageURL)
                .centerCrop()
                .into(imageView)
        }

        internal fun getCardLayout(): CardView {
            return view.findViewById(R.id.cv_review_item)
        }
    }
}