package com.utn.hwstore.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.Patterns
import android.webkit.URLUtil
import androidx.core.net.toUri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.utn.hwstore.entities.HwItem
import com.utn.hwstore.entities.MyResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

class ShopRepository {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    companion object {
        private const val TAG = "ShopRepository"
    }

    suspend fun createNewItem(item: HwItem): MyResult<String> {
        try {
            val reference = db.collection("Products").add(item).await()
            item.uid = reference.id

            if(item.imageURL.isNotEmpty()) {
                val result = uploadSingleImage(item.imageURL, "Products/${item.uid}")
                if(result is MyResult.Success) {
                    item.imageURL = result.data!!
                }
            }

            db.collection("Products").document(reference.id).set(item).await()

            Log.d(TAG, "Item created with uid ${reference.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Exception thrown: ${e.message}")
            return MyResult.Failure(e)
        }
        return MyResult.Success(item.uid)
    }

    suspend fun modifyItem(item: HwItem): MyResult<Boolean> {
        return try {
            if(item.imageURL.isNotEmpty()) {
                val result = uploadSingleImage(item.imageURL, "Products/${item.uid}")
                if(result is MyResult.Success) {
                    item.imageURL = result.data!!
                }
            }

            db.collection("Products").document(item.uid).set(item).await()
            Log.d(TAG, "Item modified with uid ${item.uid}")
            MyResult.Success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Exception thrown: ${e.message}")
            MyResult.Failure(e)
        }
    }

    suspend fun deleteItem(item: HwItem): MyResult<Boolean> {
        return try {
            db.collection("Promotions").document(item.uid).delete().await()

            Log.d(TAG, "Item deleted with uid ${item.uid}")
            MyResult.Success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Exception thrown: ${e.message}")
            MyResult.Failure(e)
        }
    }

    suspend fun getAllItems(): MyResult<ArrayList<HwItem>> {
        return try {
            val documents = db.collection("Products").orderBy("brand").get().await()
            val list = mutableListOf<HwItem>()
            if(!documents.isEmpty) {
                documents.forEach { document ->
                    list.add(document.toObject<HwItem>())
                }
            }
            MyResult.Success(ArrayList(list))
        } catch (e: Exception) {
            Log.e(TAG, "Exception thrown: ${e.message}")
            MyResult.Failure(e)
        }
    }

    suspend fun getItemByUid(uid: String): MyResult<HwItem?> {
        return try {
            val document = db.collection("Products").document(uid).get().await()
            if(document.exists()) {
                Log.d(TAG, "Item retrieved with uid ${document.id}")
                val item = document.toObject<HwItem>()
                MyResult.Success(item)
            } else {
                MyResult.Success(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception thrown: ${e.message}")
            MyResult.Failure(e)
        }
    }

    suspend fun getItemsByBrand(brand: String): MyResult<ArrayList<HwItem>> {
        return try {
            val documents = db.collection("Products")
                .whereEqualTo("brand", brand)
                .get().await()
            val list = mutableListOf<HwItem>()
            if(!documents.isEmpty) {
                documents.forEach { document ->
                    list.add(document.toObject<HwItem>())
                }
            }
            MyResult.Success(ArrayList(list))
        } catch (e: Exception) {
            Log.e(TAG, "Exception thrown: ${e.message}")
            MyResult.Failure(e)
        }
    }

    private suspend fun uploadSingleImage(image: String, path: String): MyResult<String?> {
        return try{
            if(URLUtil.isValidUrl(image) and Patterns.WEB_URL.matcher(image).matches()) {
                MyResult.Success(image)
            } else {
                val uid = UUID.randomUUID().toString().replace("-", "")
                uploadImageCompressed(image, "$path/$uid")
            }
        } catch (e: java.lang.Exception) {
            MyResult.Failure(e)
        }
    }

    private suspend fun uploadImage(path: String, name: String): MyResult<String?> {
        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .build()

        val imageRef = storage.reference.child("images/$name")

        return try {
            withContext(Dispatchers.IO) {
                val url = imageRef.putFile(path.toUri(), metadata).await().storage.downloadUrl.await().toString()
                MyResult.Success(url)
            }
        } catch (e: Exception) {
            Log.e("FirebaseShopSource", "Exception thrown: ${e.message}")
            MyResult.Failure(e)
        }
    }

    private suspend fun uploadImageCompressed(path: String, name: String): MyResult<String?> {
        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .build()

        val bmp: Bitmap = BitmapFactory.decodeFile(path.toUri().path)
        val baos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos)
        val data: ByteArray = baos.toByteArray()

        val imageRef = storage.reference.child("images/$name")

        return try {
            withContext(Dispatchers.IO) {
                val url = imageRef.putBytes(data).await().storage.downloadUrl.await().toString()
                MyResult.Success(url)
            }
        } catch (e: Exception) {
            Log.e("FirebaseShopSource", "Exception thrown: ${e.message}")
            MyResult.Failure(e)
        }
    }

    private suspend fun deleteImage(path: String): MyResult<Boolean> {
        return try {
            val imageRef = storage.reference.child("images/$path")
            imageRef.delete().await()
            MyResult.Success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Exception thrown: ${e.message}")
            MyResult.Failure(e)
        }
    }

    private suspend fun uploadFile(path: String, name: String): MyResult<String?> {
        val ref = storage.reference.child("files/$name")

        return try {
            withContext(Dispatchers.IO) {
                val url = ref.putFile(path.toUri()).await().storage.downloadUrl.await().toString()
                MyResult.Success(url)
            }
        } catch (e: Exception) {
            Log.e("FirebaseShopSource", "Exception thrown: ${e.message}")
            MyResult.Failure(e)
        }
    }
}