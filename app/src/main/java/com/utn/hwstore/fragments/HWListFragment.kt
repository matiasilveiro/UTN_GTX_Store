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
import androidx.lifecycle.lifecycleScope
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
import com.utn.hwstore.entities.MyResult
import com.utn.hwstore.utils.ShopRepository
import com.utn.hwstore.viewmodels.DetailsViewModel
import com.utn.hwstore.viewmodels.HWListViewModel
import com.utn.hwstore.viewmodels.ShoppingCartViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HWListFragment : Fragment() {

    companion object {
        fun newInstance() = HWListFragment()
    }

    private var _binding: FragmentHwListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HWListViewModel by activityViewModels()

    private val viewModelDetails: DetailsViewModel by activityViewModels()
    private val viewModelShoppingCart: ShoppingCartViewModel by activityViewModels()

    private val shopRepository = ShopRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHwListBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.title = "GTX Store - Lista de productos"

        binding.swipeRefresh.setOnRefreshListener {
            getProducts()
        }
        binding.btnAddItem.setOnClickListener { goToNewItem() }
        binding.btnLogOut.setOnClickListener { logoutDialog() }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        getProducts()
    }

    private fun getProducts() {
        lifecycleScope.launch {
            //enableUI(false)

            when(val result = shopRepository.getAllItems()) {
                is MyResult.Success -> {
                    if(result.data.isNotEmpty()) {
                        setupRecyclerView(result.data)
                    } else {
                        showDialog("Oops, lista vacía", "Parece que no hay nada por aquí")
                    }
                }
                is MyResult.Failure -> {
                    showDialog("Oops, ocurrió un error", "Error al obtener la lista de productos")
                }
            }
            binding.swipeRefresh.isRefreshing = false

            //enableUI(true)
        }
    }

    private fun setupRecyclerView(list: ArrayList<HwItem>) {
        val gridLayoutManager = when(activity?.resources?.configuration?.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> GridLayoutManager(context, 2)
            else -> GridLayoutManager(context, 4)
        }

        val itemsListAdapter = HwItemAdapter()
        itemsListAdapter.setData(list)
        itemsListAdapter.onClickListener = { onItemClicked(it) }

        with(binding.rvHwItems) {
            this.setHasFixedSize(true)
            this.layoutManager = gridLayoutManager
            this.adapter = itemsListAdapter
        }
    }

    private fun onItemClicked(item: HwItem) {
        val action = HWListFragmentDirections.actionHWListFragmentToDetailsFragment(item)
        findNavController().navigate(action)
    }

    private fun goToNewItem() {
        val action = HWListFragmentDirections.actionHWListFragmentToNewItemFragment(null)
        findNavController().navigate(action)
    }

    private fun showDialog(title: String, message: String) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun logoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Cerrar sesión")
            .setMessage("¿Estás seguro que deseas cerrar sesión?")
            .setPositiveButton("Aceptar") { dialog: DialogInterface, which: Int ->
                doLogout()
            }
            .setNegativeButton("Cancelar") { dialog: DialogInterface, which: Int ->
                dialog.dismiss()
            }
            .show()
    }

    private fun doLogout() {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
        Snackbar.make(binding.root, "Sesión cerrada", Snackbar.LENGTH_SHORT).show()

        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
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
}
