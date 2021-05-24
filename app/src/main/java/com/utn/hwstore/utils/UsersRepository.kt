package com.utn.hwstore.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.util.Patterns
import android.webkit.URLUtil
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.ktx.storage
import com.utn.hwstore.entities.MyResult
import com.utn.hwstore.entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.*

class UsersRepository {

    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private val auth = FirebaseAuth.getInstance()

    suspend fun createNewUser(user: User, email: String, password: String): MyResult<Boolean> {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val profileUpdater = UserProfileChangeRequest.Builder()
                .setDisplayName(user.name)
                .setPhotoUri(Uri.parse(user.profileImage))
                .build()
            result.user?.updateProfile(profileUpdater)?.await()

            user.uid = result.user!!.uid
            db.collection("Users").document(user.uid).set(user).await()

            Log.d("FirebaseUserSource", "Shop created with uid ${user.uid}")
        } catch (e: Exception) {
            Log.e("FirebaseUserSource", "Exception thrown: ${e.message}")
            return MyResult.Failure(e)
        }
        return MyResult.Success(true)
    }

    suspend fun getUserByUid(uid: String): MyResult<User?> {
        return try {
            val document = db.collection("Users").document(uid).get().await()
            if(document.exists()) {
                Log.d("FirebaseUserSource", "User retrieved with uid ${document.id}")
                val user = document.toObject<User>()
                MyResult.Success(user)
            } else {
                MyResult.Success(null)
            }
        } catch (e: Exception) {
            Log.e("FirebaseUserSource", "Exception thrown: ${e.message}")
            MyResult.Failure(e)
        }
    }

    suspend fun userExists(uid: String): MyResult<Boolean> {
        return try {
            val document = db.collection("Users").document(uid).get().await()
            if(document.exists()) {
                MyResult.Success(true)
            } else {
                MyResult.Success(false)
            }
        } catch (e: Exception) {
            Log.e("FirebaseUserSource", "Exception thrown: ${e.message}")
            MyResult.Failure(e)
        }
    }

    suspend fun getCurrentUser(): MyResult<User?> {
        return try {
            val currentUser = auth.currentUser
            val document = db.collection("Users").document(currentUser!!.uid).get().await()
            if(document.exists()) {
                Log.d("FirebaseUserSource", "User retrieved with uid ${document.id}")
                val user = document.toObject<User>()
                MyResult.Success(user)
            } else {
                MyResult.Success(null)
            }
        } catch (e: Exception) {
            Log.e("FirebaseUserSource", "Exception thrown: ${e.message}")
            MyResult.Failure(e)
        }
    }

    suspend fun modifyUser(user: User): MyResult<Boolean> {
        return try {
            if(user.profileImage.isNotEmpty()) {
                val result = uploadProfileImage(user.profileImage, "Users/${user.uid}")
                if(result is MyResult.Success) {
                    user.profileImage = result.data!!
                }
            }
            db.collection("Users").document(user.uid).set(user).await()

            Log.d("FirebaseUserSource", "User modified with uid ${user.uid}")
            MyResult.Success(true)
        } catch (e: Exception) {
            Log.e("FirebaseUserSource", "Exception thrown: ${e.message}")
            MyResult.Failure(e)
        }
    }

    suspend fun loginWithEmailAndPassword(email: String, password: String): MyResult<Boolean> {
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let {
                return userExists(it.uid)
            }
        } catch (e: Exception) {
            Log.w("FirebaseUserSource", e)
            return MyResult.Failure(e)
        }
        return MyResult.Success(false)
    }

    suspend fun loginAnonymously(): MyResult<Boolean> {
        try {
            val result = auth.signInAnonymously().await()
            result.user?.let {
                return MyResult.Success(true)
            }
        } catch (e: Exception) {
            Log.w("FirebaseUserSource", e)
            return MyResult.Failure(e)
        }
        return MyResult.Success(false)
    }

    suspend fun updateEmail(email: String): MyResult<Boolean> {
        return try {
            Log.d("FirebaseUserSource", "Updating email in Firebase Authentication...")
            val user = auth.currentUser!!
            user.updateEmail(email).await()
            user.sendEmailVerification().await()

            Log.d("FirebaseUserSource", "Updating email in Firebase Firestore...")
            val doc = db.collection("Users").document(auth.currentUser!!.uid).get().await()
            val userInDb = doc.toObject<User>()
            userInDb!!.email = email
            db.collection("Users").document(auth.currentUser!!.uid).set(userInDb).await()

            MyResult.Success(true)
        } catch (e: Exception) {
            Log.e("FirebaseUserSource", "Exception thrown: ${e.message}")
            MyResult.Failure(e)
        }
    }

    suspend fun sendPasswordResetEmail(email: String): MyResult<Boolean> {
        try {
            val result = auth.sendPasswordResetEmail(email).await()
            result?.let {
                return MyResult.Success(true)
            }
            return MyResult.Success(false)
        } catch (e: Exception) {
            Log.e("FirebaseUserSource", "Exception thrown: ${e.message}")
            return MyResult.Failure(e)
        }
    }

    fun logout(): MyResult<Boolean> {
        return try {
            auth.signOut()
            MyResult.Success(true)
        } catch (e: Exception) {
            Log.e("FirebaseUserSource", "Exception thrown: ${e.message}")
            MyResult.Failure(e)
        }
    }

    private suspend fun uploadProfileImage(image: String, path: String): MyResult<String?> {
        return try{
            if(URLUtil.isValidUrl(image) and Patterns.WEB_URL.matcher(image).matches()) {
                MyResult.Success(image)
            } else {
                uploadImageCompressed(image, "$path/profileImage")
            }
        } catch (e: java.lang.Exception) {
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

}