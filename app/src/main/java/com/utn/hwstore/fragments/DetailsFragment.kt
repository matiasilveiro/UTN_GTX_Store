package com.utn.hwstore.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.rowland.cartcounter.view.CartCounterActionView

import com.utn.hwstore.R
import com.utn.hwstore.database.HwItemDao
import com.utn.hwstore.database.productsDatabase
import com.utn.hwstore.entities.HwItem

class DetailsFragment : Fragment() {

    companion object {
        fun newInstance() = DetailsFragment()
    }

    private lateinit var viewModel: DetailsViewModel
    private lateinit var viewModelShoppingCart: ShoppingCartViewModel

    private lateinit var v: View
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var btnAddToCart: FloatingActionButton

    private lateinit var actionView: CartCounterActionView

    private var db: productsDatabase? = null
    private var itemDao: HwItemDao? = null

    private val args: DetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_details, container, false)

        setHasOptionsMenu(true)

        val item = args.item
        activity?.title = "Detalles: ${item.brand} ${item.model}"

        tabLayout = v.findViewById(R.id.tab_layout)
        viewPager = v.findViewById(R.id.view_pager)
        btnAddToCart = v.findViewById(R.id.btn_add_to_cart)

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(DetailsViewModel::class.java)
        viewModelShoppingCart = ViewModelProvider(requireActivity()).get(ShoppingCartViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.details_toolbar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val itemData = menu.findItem(R.id.shopping_cart)
        actionView = itemData.actionView as CartCounterActionView
        actionView.setItemData(menu, itemData)
        actionView.count = viewModelShoppingCart.cart.size
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.shopping_cart -> {
                val action = DetailsFragmentDirections.actionDetailsFragmentToShoppingCartFragment()
                v.findNavController().navigate(action)
            }
            R.id.edit_item -> {
                Snackbar.make(v, "Editar item", Snackbar.LENGTH_SHORT).show()
                val action = DetailsFragmentDirections.actionDetailsFragmentToNewItemFragment(viewModel.item.value!!)
                v.findNavController().navigate(action)
            }
            R.id.remove_item -> {
                itemDao?.delete(viewModel.item.value)
                Snackbar.make(v, "Producto eliminado", Snackbar.LENGTH_SHORT).show()
                v.findNavController().navigateUp()
            }
            else -> ""
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()

        db = productsDatabase.getAppDataBase(v.context)
        itemDao = db?.hwItemDao()

        viewPager.adapter = createCardAdapter()
        // viewPager.isUserInputEnabled = false
        TabLayoutMediator(tabLayout, viewPager, TabLayoutMediator.TabConfigurationStrategy { tab, position ->
            when (position) {
                0 -> tab.text = "Resumen"
                1 -> tab.text = "Detalles"
                2 -> tab.text = "Opiniones"
                else -> tab.text = "undefined"
            }
        }).attach()

        btnAddToCart.setOnClickListener {
            val product = viewModel.item.value!!
            viewModelShoppingCart.cart.add(product)
            viewModelShoppingCart.subtotal.value = viewModelShoppingCart.subtotal.value?.plus(product.price)
            actionView.count = viewModelShoppingCart.cart.size

            Snackbar.make(v, "Agregado al carrito", Snackbar.LENGTH_SHORT)
                .setAction("Deshacer") {
                    viewModelShoppingCart.cart.removeAt(viewModelShoppingCart.cart.size-1)
                    viewModelShoppingCart.subtotal.value = viewModelShoppingCart.subtotal.value?.minus(product.price)

                    actionView.count = viewModelShoppingCart.cart.size
                    Snackbar.make(v, "Eliminado del carrito", Snackbar.LENGTH_SHORT).show()
                }.show()
        }
    }

    private fun createCardAdapter(): ViewPagerAdapter? {
        return ViewPagerAdapter(requireActivity())
    }

    class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun createFragment(position: Int): Fragment {

            return when(position){
                0 -> DetailsTechnicalFragment()
                1 -> Details2Fragment()
                2 -> DetailsReviewsFragment()

                else -> HWListFragment()
            }
        }

        override fun getItemCount(): Int {
            return TAB_COUNT
        }

        companion object {
            private const val TAB_COUNT = 3
        }
    }
}
