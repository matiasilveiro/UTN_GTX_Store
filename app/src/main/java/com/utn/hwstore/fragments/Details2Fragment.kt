package com.utn.hwstore.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.utn.hwstore.R

class Details2Fragment : Fragment() {

    companion object {
        fun newInstance() = Details2Fragment()
    }

    private lateinit var viewModel: Details2ViewModel
    private lateinit var viewModelDetails: DetailsViewModel

    private lateinit var txtSpecs: TextView
    private lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_details2, container, false)

        txtSpecs = v.findViewById(R.id.txt_specs)

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(Details2ViewModel::class.java)
        viewModelDetails = ViewModelProvider(requireActivity()).get(DetailsViewModel::class.java)

        viewModelDetails.item.observe(viewLifecycleOwner, Observer { result ->
            txtSpecs.text = result.details
        })
    }

}
