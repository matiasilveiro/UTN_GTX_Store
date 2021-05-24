package com.utn.hwstore.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.utn.hwstore.adapters.HwItemCartAdapter
import com.utn.hwstore.databinding.FragmentShoppingCartBinding
import com.utn.hwstore.entities.HwItem
import com.utn.hwstore.viewmodels.ShoppingCartViewModel

class ShoppingCartFragment : Fragment() {

    companion object {
        fun newInstance() = ShoppingCartFragment()
    }

    private var _binding: FragmentShoppingCartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShoppingCartViewModel by activityViewModels()

    private var itemsList: ArrayList<HwItem> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShoppingCartBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = "Carrito de compras"
        viewModel.updateSubtotal()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.subtotal.observe(viewLifecycleOwner, Observer { result ->
            val btnText = "Comprar carrito ($${result})"
            binding.btnCheckout.text = btnText
        })
    }

    override fun onStart() {
        super.onStart()

        itemsList = viewModel.cart
        val itemsListAdapter = HwItemCartAdapter(viewModel.cart){item ->
            onItemClick(item)
        }

        with(binding.rvShoppingCart) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = itemsListAdapter
        }

        binding.btnCheckout.setOnClickListener {
            Snackbar.make(binding.root, "Proximamente...",Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun onItemClick(item: HwItem) {
        Snackbar.make(binding.root, "TODO: Dialog para remover del carrito", Snackbar.LENGTH_SHORT).show()
        val subtotal = viewModel.subtotal.value?.minus(item.price)
        viewModel.subtotal.value = subtotal
    }
}
