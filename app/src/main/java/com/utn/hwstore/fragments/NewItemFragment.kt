package com.utn.hwstore.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.github.nikartm.button.FitButton
import com.google.android.material.snackbar.Snackbar
import com.rowland.cartcounter.view.CartCounterActionView

import com.utn.hwstore.R
import com.utn.hwstore.database.HwItemDao
import com.utn.hwstore.database.productsDatabase
import com.utn.hwstore.entities.HwItem
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.android.synthetic.main.fragment_new_item.*

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

    private var db: productsDatabase? = null
    private var itemDao: HwItemDao? = null

    private val args: NewItemFragmentArgs by navArgs()
    private lateinit var viewModelDetails: DetailsViewModel
    private var modifyProduct: Boolean = false

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

        db = productsDatabase.getAppDataBase(v.context)
        itemDao = db?.hwItemDao()

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

                if(modifyProduct) {
                    itemDao?.updateProduct(newItem)
                    Snackbar.make(v, "Producto modificado: ${newItem.brand} ${newItem.model}", Snackbar.LENGTH_SHORT).show()
                } else {
                    itemDao?.insertProduct(newItem)
                    Snackbar.make(v, "Producto añadido: ${newItem.brand} ${newItem.model}", Snackbar.LENGTH_SHORT).show()
                }
                v.findNavController().navigateUp()
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
                }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModelDetails = ViewModelProvider(requireActivity()).get(DetailsViewModel::class.java)
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
