package com.utn.hwstore.fragments

import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.github.nikartm.button.FitButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageMetadata
import com.rowland.cartcounter.view.CartCounterActionView

import com.utn.hwstore.R
import com.utn.hwstore.entities.HwItem
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.android.synthetic.main.fragment_new_item.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.File

/**
 * A simple [Fragment] subclass.
 */
class NewItemFragment : Fragment() {

    private lateinit var editBrand: EditText
    private lateinit var editModel: EditText
    private lateinit var editDescription: EditText
    private lateinit var editSpecs: EditText
    private lateinit var editPrice: EditText

    private lateinit var btnSaveProduct: Button
    private lateinit var btnPickImage: Button

    private lateinit var imgItem: ImageView
    private var imgURL: String = ""

    private val args: NewItemFragmentArgs by navArgs()
    private lateinit var viewModelDetails: DetailsViewModel
    private var modifyProduct: Boolean = false
    private var modifyImage: Boolean = false

    private lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_new_item, container, false)

        setHasOptionsMenu(true)

        editBrand = v.findViewById(R.id.edt_brand)
        editModel = v.findViewById(R.id.edt_model)
        editDescription = v.findViewById(R.id.edt_description)
        editSpecs = v.findViewById(R.id.edt_specs)
        editPrice = v.findViewById(R.id.edt_price)

        imgItem = v.findViewById(R.id.img_new_item)
        imgItem.visibility = View.GONE

        btnSaveProduct = v.findViewById(R.id.btn_save_product)
        btnPickImage = v.findViewById(R.id.btn_choose_image)

        if(args.item.brand.isNotBlank()) {
            fillDataFields(args.item)
            modifyProduct = true
            activity?.title = "Editar producto"
            btnSaveProduct.text = "Modificar producto"
        } else {
            activity?.title = "Nuevo producto"
        }

        return v
    }

    override fun onStart() {
        super.onStart()

        val parentJob = Job()
        val fbScope = CoroutineScope(Dispatchers.Main + parentJob)    // Main dispatcher para enableUI


        btnSaveProduct.setOnClickListener {
            if(isDataCompleted()) {
                val newItem = HwItem(editBrand.text.toString(),
                    editModel.text.toString(),
                    "Notebook",
                    editDescription.text.toString(),
                    editSpecs.text.toString(),
                    editPrice.text.toString().toDouble(),
                    imgURL,
                "")

                val db = FirebaseFirestore.getInstance()

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
                Snackbar.make(v, "Por favor rellene todos los campos", Snackbar.LENGTH_SHORT).show()
            }
        }

        btnPickImage.setOnClickListener {
            TedBottomPicker.with(activity as AppCompatActivity)
                .showCameraTile(false)
                .showTitle(false)
                .setCompleteButtonText("Done")
                .setEmptySelectionText("No Select")
                .show { uri->
                    //Snackbar.make(v,"Image selected: $uri",Snackbar.LENGTH_SHORT).show()
                    imgItem.visibility = View.VISIBLE
                    Glide.with(v)
                        .load(uri)
                        .centerCrop()
                        .into(imgItem)
                    imgURL = uri.toString()

                    modifyImage = true
                }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModelDetails = ViewModelProvider(requireActivity()).get(DetailsViewModel::class.java)
    }

    private suspend fun createItemInFirebase(item: HwItem) {
        val db = FirebaseFirestore.getInstance()
        try {
            item.imageURL = saveImage(imgURL, item.model)!!
            Log.d(TAG, "Image URL: ${item.imageURL}")

            val reference = db.collection("Products").add(item).await()

            item.uid = reference.id
            db.collection("Products").document(reference.id).set(item).await()

            Snackbar.make(v, "Producto añadido: ${item.brand} ${item.model}", Snackbar.LENGTH_SHORT).show()
            v.findNavController().navigateUp()

        } catch (e: Exception) {
            when(e) {
                is FirebaseFirestoreException -> {
                    Log.d(TAG, "FirestoreException: $e")
                    Snackbar.make(v, "Error añadiendo producto: ${item.brand} ${item.model}", Snackbar.LENGTH_SHORT).show()
                }
                is StorageException -> {
                    Log.d(TAG, "StorageException: $e")
                    Snackbar.make(v, "Error subiendo imagen: ${item.brand} ${item.model}", Snackbar.LENGTH_SHORT).show()
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

            Snackbar.make(v, "Producto modificado: ${item.brand} ${item.model}", Snackbar.LENGTH_SHORT).show()
            v.findNavController().navigateUp()
            
        } catch (e: Exception) {
            when(e) {
                is FirebaseFirestoreException -> {
                    Log.d(TAG, "FirestoreException: $e")
                    Snackbar.make(v, "Error modificando producto: ${item.brand} ${item.model}", Snackbar.LENGTH_SHORT).show()
                }
                is StorageException -> {
                    Log.d(TAG, "StorageException: $e")
                    Snackbar.make(v, "Error subiendo imagen: ${item.brand} ${item.model}", Snackbar.LENGTH_SHORT).show()
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
                editBrand.text.isNotBlank() and
                editModel.text.isNotBlank() and
                editDescription.text.isNotBlank() and
                editSpecs.text.isNotBlank() and
                editPrice.text.isNotBlank() and
                imgURL.isNotBlank()
                )
    }

    private fun fillDataFields(item: HwItem) {
        editBrand.setText(item.brand)
        editModel.setText(item.model)
        editDescription.setText(item.description)
        editSpecs.setText(item.details)
        editPrice.setText(item.price.toString())
        imgURL = item.imageURL

        imgItem.visibility = View.VISIBLE
        Glide.with(v)
            .load(imgURL)
            .centerCrop()
            .into(imgItem)
    }
}
