package com.utn.hwstore.fragments

import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

import com.utn.hwstore.R
import com.utn.hwstore.entities.User

/**
 * A simple [Fragment] subclass.
 */
class SignupFragment : Fragment() {

    private lateinit var btnCreate: Button
    private lateinit var edtUsername: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtPasswordCheck: EditText
    private lateinit var txtError: TextView

    private lateinit var auth: FirebaseAuth

    private lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_signup, container, false)

        edtUsername = v.findViewById(R.id.edt_username)
        edtPassword = v.findViewById(R.id.edt_password)
        edtPasswordCheck = v.findViewById(R.id.edt_password_check)
        txtError = v.findViewById(R.id.txt_errormsg)

        btnCreate = v.findViewById(R.id.btn_create)

        auth = FirebaseAuth.getInstance()

        return v
    }

    override fun onStart() {
        super.onStart()

        btnCreate.setOnClickListener {
            if(edtUsername.text.isNotBlank() and edtPassword.text.isNotBlank()) {
                val username = edtUsername.text.toString()
                val password = edtPassword.text.toString()
                val passwordCheck = edtPasswordCheck.text.toString()

                if(password == passwordCheck) {
                    auth.createUserWithEmailAndPassword(username, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success")
                                val user = auth.currentUser

                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(user?.email?.substringBefore('@'))
                                    .setPhotoUri(Uri.parse("https://emprendedoresnews.com/wp-content/uploads/2017/09/Larry-Page-Karl-Mondon-BANG-e1565041974268.jpg"))
                                    .build()

                                user?.updateProfile(profileUpdates)
                                    ?.addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Log.d(TAG, "User profile updated.")
                                        }
                                    }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                Snackbar.make(v, "Authentication failed.", Snackbar.LENGTH_SHORT).show()
                            }
                        }

                    v.findNavController().navigateUp()
                } else {
                    txtError.text = getString(R.string.msg_mismatch_passwords)
                }
            } else {
                txtError.text = getString(R.string.msg_user_input_incomplete)
            }
        }
    }

}
