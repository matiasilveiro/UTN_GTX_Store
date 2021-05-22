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
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.utn.hwstore.R
import com.utn.hwstore.databinding.FragmentSignupBinding

class SignupFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignupBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        binding.btnCreate.setOnClickListener {
            if(binding.edtUsername.text.isNotBlank() and binding.edtPassword.text.isNotBlank()) {
                val username = binding.edtUsername.text.toString()
                val password = binding.edtPassword.text.toString()
                val passwordCheck = binding.edtPasswordCheck.text.toString()

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
                                Snackbar.make(binding.root, "Authentication failed.", Snackbar.LENGTH_SHORT).show()
                            }
                        }

                    findNavController().navigateUp()
                } else {
                    binding.txtErrormsg.text = getString(R.string.msg_mismatch_passwords)
                }
            } else {
                binding.txtErrormsg.text = getString(R.string.msg_user_input_incomplete)
            }
        }
    }

}
