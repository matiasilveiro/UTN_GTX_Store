package com.utn.hwstore.fragments

import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rowland.cartcounter.view.CartCounterActionView
import com.utn.hwstore.LoginActivity
import com.utn.hwstore.R
import com.utn.hwstore.adapters.HwItemAdapter
import com.utn.hwstore.databinding.FragmentHwListBinding
import com.utn.hwstore.entities.HwItem
import com.utn.hwstore.viewmodels.DetailsViewModel
import com.utn.hwstore.viewmodels.HWListViewModel
import com.utn.hwstore.viewmodels.ShoppingCartViewModel

class HWListFragment : Fragment() {

    companion object {
        fun newInstance() = HWListFragment()
    }

    private var _binding: FragmentHwListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HWListViewModel by activityViewModels()

    private val viewModelDetails: DetailsViewModel by activityViewModels()
    private val viewModelShoppingCart: ShoppingCartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHwListBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.title = "GTX Store - Lista de productos"

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val gridLayoutManager = when(activity?.resources?.configuration?.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> GridLayoutManager(context, 2)
            else -> GridLayoutManager(context, 4)
        }

        val rootRef = FirebaseFirestore.getInstance()
        val query = rootRef.collection("Products")
            .orderBy("brand")

        val options = FirestoreRecyclerOptions.Builder<HwItem>()
            .setQuery(query, HwItem::class.java)
            .build()

        val itemsListAdapter = HwItemAdapter(options){item ->
            onItemClick(item)
        }
        itemsListAdapter.startListening()

        with(binding.rvHwItems) {
            setHasFixedSize(true)
            layoutManager = gridLayoutManager
            adapter = itemsListAdapter
        }

        binding.btnAddItem.setOnClickListener {
            val action = HWListFragmentDirections.actionHWListFragmentToNewItemFragment(null)
            findNavController().navigate(action)
        }

        binding.btnLogOut.setOnClickListener {
            logoutDialog()
        }
    }

    private fun onItemClick(item: HwItem) {
        viewModelDetails.item.value = item
        val action = HWListFragmentDirections.actionHWListFragmentToDetailsFragment(item)
        findNavController().navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_toolbar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val itemData = menu.findItem(R.id.shopping_cart)
        val actionView = itemData.actionView as CartCounterActionView
        actionView.setItemData(menu, itemData)
        actionView.count = viewModelShoppingCart.cart.size
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.shopping_cart -> {
                val action = HWListFragmentDirections.actionHWListFragmentToShoppingCartFragment()
                findNavController().navigate(action)
            }
            //else -> Snackbar.make(binding.root, "Undefined", Snackbar.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Cerrar sesión")
            .setMessage("¿Estás seguro que deseas cerrar sesión?")
            .setPositiveButton("Aceptar") { dialog: DialogInterface, which: Int ->
                val auth = FirebaseAuth.getInstance()
                auth.signOut()
                Snackbar.make(binding.root, "Sesión cerrada", Snackbar.LENGTH_SHORT).show()

                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
            .setNegativeButton("Cancelar") { dialog: DialogInterface, which: Int ->
                dialog.dismiss()
            }
            .show()
    }
}
