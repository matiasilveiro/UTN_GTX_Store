package com.utn.hwstore.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

import com.utn.hwstore.R
import com.utn.hwstore.adapters.HwItemAdapter
import com.utn.hwstore.adapters.HwItemCartAdapter
import com.utn.hwstore.entities.HwItem

class ShoppingCartFragment : Fragment() {

    companion object {
        fun newInstance() = ShoppingCartFragment()
    }

    private lateinit var viewModel: ShoppingCartViewModel

    private lateinit var rvShoppingCart: RecyclerView
    private lateinit var itemsListAdapter: HwItemCartAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var btnCheckout: Button

    private var itemsList: ArrayList<HwItem> = ArrayList()

    private lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_shopping_cart, container, false)

        activity?.title = "Carrito de compras"

        rvShoppingCart = v.findViewById(R.id.rv_shopping_cart)

        btnCheckout = v.findViewById(R.id.btn_checkout)

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(ShoppingCartViewModel::class.java)

        viewModel.subtotal.observe(viewLifecycleOwner, Observer { result ->
            val btnText = "Comprar carrito ($${result.toString()})"
            btnCheckout.text = btnText
        })
    }

    override fun onStart() {
        super.onStart()

        itemsList = viewModel.cart

        rvShoppingCart.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context)
        rvShoppingCart.layoutManager = linearLayoutManager

        itemsListAdapter = HwItemCartAdapter(viewModel.cart){item ->
            onItemClick(item)
        }
        rvShoppingCart.adapter = itemsListAdapter

        btnCheckout.setOnClickListener {
            Snackbar.make(v, "Proximamente...",Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun onItemClick(item: HwItem) {
        Snackbar.make(v, "TODO: Dialog para remover del carrito", Snackbar.LENGTH_SHORT).show()
        val subtotal = viewModel.subtotal.value?.minus(item.price)
        viewModel.subtotal.value = subtotal
    }
}
