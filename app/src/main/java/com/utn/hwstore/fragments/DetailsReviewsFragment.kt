package com.utn.hwstore.fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

import com.utn.hwstore.R
import com.utn.hwstore.adapters.HwItemAdapter
import com.utn.hwstore.adapters.ReviewListAdapter
import com.utn.hwstore.entities.Review

class DetailsReviewsFragment : Fragment() {

    companion object {
        fun newInstance() = DetailsReviewsFragment()
    }

    private lateinit var viewModel: DetailsReviewsViewModel

    private lateinit var rvReviewsList: RecyclerView
    private lateinit var reviewsListAdapter: ReviewListAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    private var reviewsList: ArrayList<Review> = ArrayList<Review>()

    private lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_details_reviews, container, false)

        rvReviewsList = v.findViewById(R.id.rv_reviews_list)

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DetailsReviewsViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onStart() {
        super.onStart()

        reviewsList.add(Review("Gregory House","Tremenda app!",5.0,
            "https://pbs.twimg.com/profile_images/1227175449681448962/hWMd9_nJ_400x400.jpg"))
        reviewsList.add(Review("Elon Musk","Mas o menos eh, incompleta",4.0,
        "https://i0.wp.com/espacionegocios.com.ar/wp-content/uploads/2020/02/elon-musk.jpg"))
        reviewsList.add(Review("Mark","Tremenda app igual!",4.9,
        "https://tentulogo.com/wp-content/uploads/Mark-Zuckerberg.jpg"))
        reviewsList.add(Review("El niño que vivió","Tremenda app!",5.0,
        "https://aws.revistavanityfair.es/prod/designs/v1/assets/785x589/39710.jpg"))

        rvReviewsList.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context)
        rvReviewsList.layoutManager = linearLayoutManager

        reviewsListAdapter = ReviewListAdapter(reviewsList){item ->
            onItemClick(item)
        }
        rvReviewsList.adapter = reviewsListAdapter
        rvReviewsList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }

    private fun onItemClick(item: Review) {
        Snackbar.make(v, "TODO: hacer algo con esto", Snackbar.LENGTH_SHORT).show()
    }
}
