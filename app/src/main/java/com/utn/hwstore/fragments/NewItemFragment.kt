package com.utn.hwstore.fragments

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageMetadata
import com.utn.hwstore.databinding.FragmentNewItemBinding
import com.utn.hwstore.entities.HwItem
import com.utn.hwstore.entities.MyResult
import com.utn.hwstore.utils.ShopRepository
import com.utn.hwstore.viewmodels.DetailsViewModel
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.android.synthetic.main.fragment_new_item.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.File

class NewItemFragment : Fragment() {

    private var _binding: FragmentNewItemBinding? = null
    private val binding get() = _binding!!

    private val args: NewItemFragmentArgs by navArgs()
    private var item = HwItem()

    private val shopRepository = ShopRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNewItemBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)

        (activity as AppCompatActivity).supportActionBar?.title = "Nuevo producto"
        args.item?.let {
            fillDataFields(it)
            (activity as AppCompatActivity).supportActionBar?.title = "Editar producto"
            binding.btnSaveProduct.text = "Modificar producto"
        }

        binding.btnSaveProduct.setOnClickListener { onSaveProductClicked() }
        binding.btnChooseImage.setOnClickListener { openImagePicker() }

        return binding.root
    }

    private fun openImagePicker() {
        TedBottomPicker.with(activity as AppCompatActivity)
            .showCameraTile(false)
            .showTitle(false)
            .setCompleteButtonText("Done")
            .setEmptySelectionText("No Select")
            .show { uri ->
                //Snackbar.make(v,"Image selected: $uri",Snackbar.LENGTH_SHORT).show()
                binding.imgNewItem.visibility = View.VISIBLE
                Glide.with(binding.root)
                    .load(uri)
                    .centerCrop()
                    .into(binding.imgNewItem)
                item.imageURL = uri.toString()
            }
    }

    private fun onSaveProductClicked() {
        if(isDataCompleted()) {
            val newItem = HwItem(
                binding.edtBrand.text.toString(),
                binding.edtModel.text.toString(),
                "Notebook",
                binding.edtDescription.text.toString(),
                binding.edtSpecs.text.toString(),
                binding.edtPrice.text.toString().toDouble(),
                item.imageURL,
                item.uid)

            updateOrCreateItemInFirebase(newItem)
        } else {
            //Snackbar.make(binding.root, "Por favor rellene todos los campos", Snackbar.LENGTH_SHORT).show()
            showDialog("¡Atención!","Por favor rellene todos los campos")
        }
    }

    private fun updateOrCreateItemInFirebase(item: HwItem) {
        val parentJob = Job()
        val fbScope = CoroutineScope(Dispatchers.Main + parentJob)    // Main dispatcher para enableUI

        fbScope.launch {
            enableUI(false)

            val result = if (item.uid.isEmpty()) shopRepository.createNewItem(item) else shopRepository.modifyItem(item)
            when(result) {
                is MyResult.Success -> {
                    Snackbar.make(binding.root, "Producto añadido: ${item.brand} ${item.model}", Snackbar.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is MyResult.Failure -> {
                    handleFirebaseException(result.exception)
                }
            }

            enableUI(true)
        }
    }

    private fun handleFirebaseException(e: Exception) {
        when(e) {
            is FirebaseFirestoreException -> {
                Log.d(TAG, "FirestoreException: $e")
                //Snackbar.make(binding.root, "Error añadiendo producto", Snackbar.LENGTH_SHORT).show()
                showDialog("Oops, ocurrió un error","Error añadiendo producto")
            }
            is StorageException -> {
                Log.d(TAG, "StorageException: $e")
                //Snackbar.make(binding.root, "Error subiendo imagen", Snackbar.LENGTH_SHORT).show()
                showDialog("Oops, ocurrió un error","Error subiendo imagen")
            }
        }
    }

    private fun enableUI(enable: Boolean) {
        if(enable) {
            grayblur.visibility = View.INVISIBLE
            loader.visibility = View.INVISIBLE
        } else {
            grayblur.visibility = View.VISIBLE
            loader.visibility = View.VISIBLE
        }
    }
    
    private fun isDataCompleted(): Boolean {
        return(
                binding.edtBrand.text.isNotBlank() and
                binding.edtModel.text.isNotBlank() and
                binding.edtDescription.text.isNotBlank() and
                binding.edtSpecs.text.isNotBlank() and
                binding.edtPrice.text.isNotBlank() and
                item.imageURL.isNotBlank()
                )
    }

    private fun fillDataFields(item: HwItem) {
        binding.edtBrand.setText(item.brand)
        binding.edtModel.setText(item.model)
        binding.edtDescription.setText(item.description)
        binding.edtSpecs.setText(item.details)
        binding.edtPrice.setText(item.price.toString())

        binding.imgNewItem.visibility = View.VISIBLE
        Glide.with(binding.root)
            .load(item.imageURL)
            .centerCrop()
            .into(binding.imgNewItem)

        this.item = item
    }

    private fun showDialog(title: String, message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
