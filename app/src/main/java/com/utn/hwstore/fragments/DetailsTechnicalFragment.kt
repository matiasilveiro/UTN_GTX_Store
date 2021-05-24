package com.utn.hwstore.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.utn.hwstore.databinding.FragmentDetailsTechnicalBinding
import com.utn.hwstore.entities.HwItem
import com.utn.hwstore.viewmodels.DetailsTechnicalViewModel
import com.utn.hwstore.viewmodels.DetailsViewModel

class DetailsTechnicalFragment : Fragment() {

    companion object {
        fun newInstance() = DetailsTechnicalFragment()
    }

    private var _binding: FragmentDetailsTechnicalBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailsTechnicalViewModel by activityViewModels()
    private val viewModelDetails: DetailsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsTechnicalBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModelDetails.item.observe(viewLifecycleOwner, Observer { value ->
            updateItemDetails(value)
        })
    }

    private fun updateItemDetails(item: HwItem) {
        Log.d("DETAILS", "Item selected: ${item.brand} ${item.model}")
        Glide.with(binding.root)
            .load(item.imageURL)
            .centerCrop()
            .into(binding.ivItem)

        binding.txtSpecs.text = item.description
        binding.txtPrice.text = "$ ${item.price}"
    }
}
