package com.utn.hwstore.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.rowland.cartcounter.view.CartCounterActionView

import com.utn.hwstore.R
import com.utn.hwstore.adapters.HwItemAdapter
import com.utn.hwstore.database.HwItemDao
import com.utn.hwstore.database.productsDatabase
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

    private lateinit var v: View

    private var itemsList: ArrayList<HwItem> = ArrayList()

    private var db: productsDatabase? = null
    private var itemDao: HwItemDao? = null

    private lateinit var viewModelDetails: DetailsViewModel
    private lateinit var viewModelShoppingCart: ShoppingCartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
        itemsList.add(HwItem("ASUS","X555","Notebook","Notebook de propósitos generales","",32500.0,
            "https://http2.mlstatic.com/asus-x555-i5-7200u-8gb-512gb-ssd-gtx-940mx-a-pedido-D_NQ_NP_377321-MLA20769175451_062016-F.jpg"))

        itemsList.add(HwItem("Dell","X666","Notebook","Notebook de propósitos generales de alto rendimiento","Notebook de propósitos generales",45500.0,
            "https://http2.mlstatic.com/notebook-dell-core-i7-8565u-8va-8gb-1tb-windows-10-156-hd-D_NQ_NP_772006-MLA32942545554_112019-F.jpg"))

        itemsList.add(HwItem("HP","Omen","Notebook","Notebook gamer de la línea HP Omen","Notebook zarpada uacho",64500.0,
            "https://http2.mlstatic.com/notebook-hp-omen-15-dc0030nr-i716gb1tb-256gbssd-D_NQ_NP_611088-MLA31637826216_072019-F.jpg"))

        itemsList.add(HwItem("MSI","X888","Notebook","Notebook gamer","Notebook gamer de alto rendimiento",80500.0,
            "https://asset.msi.com/global/picture/news/2018/nb/gaming-notebook-20180118-1.png"))

        itemsList.add(HwItem("HyperX","RAM x 16GB","Memoria RAM","Memoria RAM de 16GB","Memoria RAM gamer de 16GB con luces RGB",7500.0,
            "https://www.winpy.cl/files/w17524_hx432c16pb3a-8.jpg"))

        itemsList.add(HwItem("Corsair","Vengeance","Memoria RAM","Memoria RAM de 16GB","Memoria RAM gamer de 16GB con luces RGB",7300.0,
            "https://gamerpc.es/wp-content/uploads/2016/11/corsair-vengeance-pro-series-ddr3.jpg"))

        itemsList.add(HwItem("HyperX","Alloy FPS","Teclado","Teclado mecánico con pad numérico lateral","Teclado mecánico de alto rendimiento, con marco de aluminio",8800.0,
            "https://d26lpennugtm8s.cloudfront.net/stores/135/412/products/883089-mla30753761388_052019-o-5725d9d9fa57a8489b15602727520573-1024-1024.jpg"))
        */
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

        db = productsDatabase.getAppDataBase(v.context)
        itemDao = db?.hwItemDao()

        /*
        for(item in itemsList) {
            itemDao?.insertProduct(item)
        }
        */

        itemsList = itemDao?.loadAllProducts() as ArrayList<HwItem>

        rvItemsList.setHasFixedSize(true)

        val orientation = activity?.resources?.configuration?.orientation
        gridLayoutManager = when(orientation) {
            Configuration.ORIENTATION_PORTRAIT -> GridLayoutManager(context, 2)
            else -> GridLayoutManager(context, 4)
        }
        rvItemsList.layoutManager = gridLayoutManager

        itemsListAdapter = HwItemAdapter(itemsList){item ->
            onItemClick(item)
        }
        rvItemsList.adapter = itemsListAdapter

        btnAddItem.setOnClickListener {
            val action = HWListFragmentDirections.actionHWListFragmentToNewItemFragment(HwItem("","","","","",0.0,""))
            v.findNavController().navigate(action)
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
