package com.utn.hwstore.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.utn.hwstore.databinding.FragmentDetails2Binding
import com.utn.hwstore.viewmodels.Details2ViewModel
import com.utn.hwstore.viewmodels.DetailsViewModel

class Details2Fragment : Fragment() {

    companion object {
        fun newInstance() = Details2Fragment()
    }

    private var _binding: FragmentDetails2Binding? = null
    private val binding get() = _binding!!

    private val viewModel: Details2ViewModel by activityViewModels()
    private val viewModelDetails: DetailsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetails2Binding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModelDetails.item.observe(viewLifecycleOwner, Observer { result ->
            binding.txtSpecs.text = result.details
        })
    }
}
