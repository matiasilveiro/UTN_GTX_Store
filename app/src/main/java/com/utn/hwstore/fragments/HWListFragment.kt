package com.utn.hwstore.fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rowland.cartcounter.view.CartCounterActionView
import com.utn.hwstore.LoginActivity
import com.utn.hwstore.MainActivity

import com.utn.hwstore.R
import com.utn.hwstore.adapters.HwItemAdapter
import com.utn.hwstore.entities.HwItem

class HWListFragment : Fragment() {

    companion object {
        fun newInstance() = HWListFragment()
    }

    private lateinit var viewModel: HWListViewModel

    private lateinit var rvItemsList: RecyclerView
    private lateinit var itemsListAdapter: HwItemAdapter
    private lateinit var gridLayoutManager: GridLayoutManager

    private lateinit var btnAddItem: FloatingActionButton
    private lateinit var btnLogOut: FloatingActionButton

    private lateinit var v: View

    private var itemsList: ArrayList<HwItem> = ArrayList()

    private lateinit var viewModelDetails: DetailsViewModel
    private lateinit var viewModelShoppingCart: ShoppingCartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
        itemsList.add(HwItem("HyperX","Alloy FPS","Teclado","Teclado mecánico con pad numérico lateral","Teclado mecánico de alto rendimiento, con marco de aluminio",8800.0,
            "https://d26lpennugtm8s.cloudfront.net/stores/135/412/products/883089-mla30753761388_052019-o-5725d9d9fa57a8489b15602727520573-1024-1024.jpg"))
        */

        val db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_hw_list, container, false)

        setHasOptionsMenu(true)
        activity?.title = "GTX Store - Lista de productos"

        rvItemsList = v.findViewById(R.id.rv_hw_items)

        btnAddItem = v.findViewById(R.id.btn_add_item)
        btnLogOut = v.findViewById(R.id.btn_log_out)

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(HWListViewModel::class.java)
        viewModelDetails = ViewModelProvider(requireActivity()).get(DetailsViewModel::class.java)
        viewModelShoppingCart = ViewModelProvider(requireActivity()).get(ShoppingCartViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()

        rvItemsList.setHasFixedSize(true)

        val orientation = activity?.resources?.configuration?.orientation
        gridLayoutManager = when(orientation) {
            Configuration.ORIENTATION_PORTRAIT -> GridLayoutManager(context, 2)
            else -> GridLayoutManager(context, 4)
        }
        rvItemsList.layoutManager = gridLayoutManager

        val rootRef = FirebaseFirestore.getInstance()
        val query = rootRef.collection("Products")
            .orderBy("brand")

        val options = FirestoreRecyclerOptions.Builder<HwItem>()
            .setQuery(query, HwItem::class.java)
            .build()

        itemsListAdapter = HwItemAdapter(options){item ->
            onItemClick(item)
        }
        itemsListAdapter.startListening()
        rvItemsList.adapter = itemsListAdapter

        btnAddItem.setOnClickListener {
            val action = HWListFragmentDirections.actionHWListFragmentToNewItemFragment(HwItem("","","","","",0.0,"",""))
            v.findNavController().navigate(action)
        }

        btnLogOut.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            auth.signOut()
            Snackbar.make(v, "Sesión cerrada", Snackbar.LENGTH_SHORT).show()

            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    private fun onItemClick(item: HwItem) {
        viewModelDetails.item.value = item
        val action = HWListFragmentDirections.actionHWListFragmentToDetailsFragment(item)
        v.findNavController().navigate(action)
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
                //Snackbar.make(v, "Shopping cart", Snackbar.LENGTH_SHORT).show()
                val action = HWListFragmentDirections.actionHWListFragmentToShoppingCartFragment()
                v.findNavController().navigate(action)
            }
            else -> Snackbar.make(v, "Undefined", Snackbar.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }
}
