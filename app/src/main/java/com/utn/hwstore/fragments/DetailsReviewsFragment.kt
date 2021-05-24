package com.utn.hwstore.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.utn.hwstore.adapters.ReviewListAdapter
import com.utn.hwstore.databinding.FragmentDetailsReviewsBinding
import com.utn.hwstore.entities.Review
import com.utn.hwstore.viewmodels.DetailsReviewsViewModel

class DetailsReviewsFragment : Fragment() {

    companion object {
        fun newInstance() = DetailsReviewsFragment()
    }

    private var _binding: FragmentDetailsReviewsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailsReviewsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsReviewsBinding.inflate(inflater, container, false)

        setupRecyclerView()

        return binding.root
    }

    private fun setupRecyclerView() {
        val reviewsList = arrayListOf<Review>()

        reviewsList.add(Review("Gregory House","Tremenda app!",5.0,
            "https://pbs.twimg.com/profile_images/1227175449681448962/hWMd9_nJ_400x400.jpg"))
        reviewsList.add(Review("Elon Musk","Mas o menos eh, incompleta",4.0,
            "https://i0.wp.com/espacionegocios.com.ar/wp-content/uploads/2020/02/elon-musk.jpg"))
        reviewsList.add(Review("Mark","Tremenda app igual!",4.9,
            "https://tentulogo.com/wp-content/uploads/Mark-Zuckerberg.jpg"))
        reviewsList.add(Review("El niño que vivió","Tremenda app!",5.0,
            "https://aws.revistavanityfair.es/prod/designs/v1/assets/785x589/39710.jpg"))

        val reviewsListAdapter = ReviewListAdapter(reviewsList){item ->
            onItemClick(item)
        }

        with(binding.rvReviewsList) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = reviewsListAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun onItemClick(item: Review) {
        Snackbar.make(binding.root, "TODO: hacer algo con esto", Snackbar.LENGTH_SHORT).show()
    }
}
