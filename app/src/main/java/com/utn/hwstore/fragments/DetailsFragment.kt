package com.utn.hwstore.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.rowland.cartcounter.view.CartCounterActionView
import com.utn.hwstore.R
import com.utn.hwstore.databinding.FragmentDetailsBinding
import com.utn.hwstore.entities.HwItem
import com.utn.hwstore.viewmodels.DetailsViewModel
import com.utn.hwstore.viewmodels.ShoppingCartViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class DetailsFragment : Fragment() {

    companion object {
        fun newInstance() = DetailsFragment()
    }

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailsViewModel by activityViewModels()
    private val viewModelShoppingCart: ShoppingCartViewModel by activityViewModels()

    private lateinit var actionView: CartCounterActionView

    private val args: DetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)

        val item = args.item
        activity?.title = "Detalles: ${item.brand} ${item.model}"

        return binding.root
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
                findNavController().navigate(action)
            }
            R.id.edit_item -> {
                Snackbar.make(binding.root, "Editar item", Snackbar.LENGTH_SHORT).show()
                val action = DetailsFragmentDirections.actionDetailsFragmentToNewItemFragment(viewModel.item.value!!)
                findNavController().navigate(action)
            }
            R.id.remove_item -> {
                val parentJob = Job()
                val fbScope = CoroutineScope(Dispatchers.Default + parentJob)
                fbScope.launch {
                    deleteItemFromFirebase(args.item)
                    findNavController().navigateUp()
                }
            }
            else -> Log.d(TAG, "DetailsFragment: MenuItem not found")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()

        binding.viewPager.adapter = createCardAdapter()
        // viewPager.isUserInputEnabled = false
        TabLayoutMediator(binding.tabLayout, binding.viewPager, TabLayoutMediator.TabConfigurationStrategy { tab, position ->
            when (position) {
                0 -> tab.text = "Resumen"
                1 -> tab.text = "Detalles"
                2 -> tab.text = "Opiniones"
                else -> tab.text = "undefined"
            }
        }).attach()

        binding.btnAddToCart.setOnClickListener {
            val product = viewModel.item.value!!
            viewModelShoppingCart.cart.add(product)
            viewModelShoppingCart.subtotal.value = viewModelShoppingCart.subtotal.value?.plus(product.price)
            actionView.count = viewModelShoppingCart.cart.size

            Snackbar.make(binding.root, "Agregado al carrito", Snackbar.LENGTH_SHORT)
                .setAction("Deshacer") {
                    viewModelShoppingCart.cart.removeAt(viewModelShoppingCart.cart.size-1)
                    viewModelShoppingCart.subtotal.value = viewModelShoppingCart.subtotal.value?.minus(product.price)

                    actionView.count = viewModelShoppingCart.cart.size
                    Snackbar.make(binding.root, "Eliminado del carrito", Snackbar.LENGTH_SHORT).show()
                }.show()
        }
    }

    private suspend fun deleteItemFromFirebase(item: HwItem) {
        try {
            val storageRef = FirebaseStorage.getInstance().reference
            storageRef.child("images/${item.model}").delete().await()

            val db = FirebaseFirestore.getInstance()
            db.collection("Products").document(item.uid).delete().await()

            Snackbar.make(binding.root, "Producto eliminado: ${item.brand} ${item.model}", Snackbar.LENGTH_SHORT).show()
        } catch (e: Exception) {
            when(e) {
                is FirebaseFirestoreException -> {
                    Log.d(TAG, "FirestoreException: $e")
                    Snackbar.make(binding.root, "Error eliminando producto: ${item.brand} ${item.model}", Snackbar.LENGTH_SHORT).show()
                }
                is StorageException -> {
                    Log.d(TAG, "StorageException: $e")
                    Snackbar.make(binding.root, "Error eliminando imagen: ${item.brand} ${item.model}", Snackbar.LENGTH_SHORT).show()
                }
            }
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
