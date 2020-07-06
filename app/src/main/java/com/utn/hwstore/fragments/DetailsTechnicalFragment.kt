package com.utn.hwstore.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide

import com.utn.hwstore.R

class DetailsTechnicalFragment : Fragment() {

    companion object {
        fun newInstance() = DetailsTechnicalFragment()
    }

    private lateinit var viewModel: DetailsTechnicalViewModel
    private lateinit var viewModelDetails: DetailsViewModel

    private lateinit var txtDetails: TextView
    private lateinit var txtPrice: TextView
    private lateinit var imageItem: ImageView
    private lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_details_technical, container, false)

        txtDetails = v.findViewById(R.id.txt_specs)
        txtPrice = v.findViewById(R.id.txt_price)
        imageItem = v.findViewById(R.id.iv_item)

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(DetailsTechnicalViewModel::class.java)
        viewModelDetails = ViewModelProvider(requireActivity()).get(DetailsViewModel::class.java)

        viewModelDetails.item.observe(viewLifecycleOwner, Observer { result ->
            Log.d("DETAILS","Item selected: ${result.brand} ${result.model}")
            Glide.with(v)
                .load(result.imageURL)
                .centerCrop()
                .into(imageItem)

            txtDetails.text = result.description
            val price = "$ ${result.price.toString()}"
            txtPrice.text = price
        })
    }

    override fun onStart() {
        super.onStart()
    }
}
