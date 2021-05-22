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
import com.utn.hwstore.viewmodels.DetailsViewModel
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.android.synthetic.main.fragment_new_item.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.File

class NewItemFragment : Fragment() {

    private var _binding: FragmentNewItemBinding? = null
    private val binding get() = _binding!!
    private var imgURL: String = ""

    private val args: NewItemFragmentArgs by navArgs()
    private val viewModelDetails: DetailsViewModel by activityViewModels()
    private var modifyProduct: Boolean = false
    private var modifyImage: Boolean = false

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
            modifyProduct = true
            (activity as AppCompatActivity).supportActionBar?.title = "Editar producto"
            binding.btnSaveProduct.text = "Modificar producto"
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val parentJob = Job()
        val fbScope = CoroutineScope(Dispatchers.Main + parentJob)    // Main dispatcher para enableUI


        binding.btnSaveProduct.setOnClickListener {
            if(isDataCompleted()) {
                val newItem = HwItem(
                    binding.edtBrand.text.toString(),
                    binding.edtModel.text.toString(),
                    "Notebook",
                    binding.edtDescription.text.toString(),
                    binding.edtSpecs.text.toString(),
                    binding.edtPrice.text.toString().toDouble(),
                    imgURL,
                "")

                if(modifyProduct) {
                    fbScope.launch {
                        enableUI(false)
                        updateItemInFirebase(newItem)
                        enableUI(true)
                    }
                } else {
                    fbScope.launch {
                        enableUI(false)
                        createItemInFirebase(newItem)
                        enableUI(true)
                    }
                }
            } else {
                Snackbar.make(binding.root, "Por favor rellene todos los campos", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.btnChooseImage.setOnClickListener {
            TedBottomPicker.with(activity as AppCompatActivity)
                .showCameraTile(false)
                .showTitle(false)
                .setCompleteButtonText("Done")
                .setEmptySelectionText("No Select")
                .show { uri->
                    //Snackbar.make(v,"Image selected: $uri",Snackbar.LENGTH_SHORT).show()
                    binding.imgNewItem.visibility = View.VISIBLE
                    Glide.with(binding.root)
                        .load(uri)
                        .centerCrop()
                        .into(binding.imgNewItem)
                    imgURL = uri.toString()

                    modifyImage = true
                }
        }
    }

    private suspend fun createItemInFirebase(item: HwItem) {
        val db = FirebaseFirestore.getInstance()
        try {
            item.imageURL = saveImage(imgURL, item.model)!!
            Log.d(TAG, "Image URL: ${item.imageURL}")

            val reference = db.collection("Products").add(item).await()

            item.uid = reference.id
            db.collection("Products").document(reference.id).set(item).await()

            Snackbar.make(binding.root, "Producto añadido: ${item.brand} ${item.model}", Snackbar.LENGTH_SHORT).show()
            findNavController().navigateUp()

        } catch (e: Exception) {
            when(e) {
                is FirebaseFirestoreException -> {
                    Log.d(TAG, "FirestoreException: $e")
                    Snackbar.make(binding.root, "Error añadiendo producto: ${item.brand} ${item.model}", Snackbar.LENGTH_SHORT).show()
                }
                is StorageException -> {
                    Log.d(TAG, "StorageException: $e")
                    Snackbar.make(binding.root, "Error subiendo imagen: ${item.brand} ${item.model}", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun updateItemInFirebase(item: HwItem) {
        val db = FirebaseFirestore.getInstance()
        try {
            if(modifyImage) {
                item.imageURL = saveImage(imgURL, item.model)!!
                Log.d(TAG, "Image URL: ${item.imageURL}")
            }
            db.collection("Products").document(item.uid).set(item).await()

            Snackbar.make(binding.root, "Producto modificado: ${item.brand} ${item.model}", Snackbar.LENGTH_SHORT).show()
            findNavController().navigateUp()
            
        } catch (e: Exception) {
            when(e) {
                is FirebaseFirestoreException -> {
                    Log.d(TAG, "FirestoreException: $e")
                    //Snackbar.make(binding.root, "Error modificando producto: ${item.brand} ${item.model}", Snackbar.LENGTH_SHORT).show()
                    showDialog("Oops, ocurrió un error","Error modificando producto: ${item.brand} ${item.model}")
                }
                is StorageException -> {
                    Log.d(TAG, "StorageException: $e")
                    Snackbar.make(binding.root, "Error subiendo imagen: ${item.brand} ${item.model}", Snackbar.LENGTH_SHORT).show()
                    showDialog("Oops, ocurrió un error","Error subiendo imagen: ${item.brand} ${item.model}")
                }
            }
        }
    }

    private suspend fun saveImage(filePath: String, name: String): String? {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .build()

        val file = Uri.fromFile(File(filePath))
        val imageRef = storageRef.child("images/$name")

        return withContext(Dispatchers.IO) {
            imageRef.putFile(filePath.toUri(), metadata).await().storage.downloadUrl.await().toString()
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
                imgURL.isNotBlank()
                )
    }

    private fun fillDataFields(item: HwItem) {
        binding.edtBrand.setText(item.brand)
        binding.edtModel.setText(item.model)
        binding.edtDescription.setText(item.description)
        binding.edtSpecs.setText(item.details)
        binding.edtPrice.setText(item.price.toString())
        imgURL = item.imageURL

        binding.imgNewItem.visibility = View.VISIBLE
        Glide.with(binding.root)
            .load(imgURL)
            .centerCrop()
            .into(binding.imgNewItem)
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
