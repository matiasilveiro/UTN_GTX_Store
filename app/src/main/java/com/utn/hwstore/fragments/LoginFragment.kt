package com.utn.hwstore.fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.utn.hwstore.MainActivity

import com.utn.hwstore.R
import com.utn.hwstore.entities.User
import com.wajahatkarim3.roomexplorer.RoomExplorer

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {

    private lateinit var btnDebug: Button
    private lateinit var btnLogin: Button
    private lateinit var btnSignup: TextView
    private lateinit var edtUsername: EditText
    private lateinit var edtPassword: EditText
    private lateinit var txtError: TextView
    private lateinit var txtRememberPassword: TextView

    private lateinit var auth: FirebaseAuth

    private lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_login, container, false)

        edtUsername = v.findViewById(R.id.edt_username)
        edtPassword = v.findViewById(R.id.edt_password)
        txtError = v.findViewById(R.id.txt_errormsg)
        txtRememberPassword = v.findViewById(R.id.txt_remember_password)

        btnSignup = v.findViewById(R.id.btn_signup)
        btnLogin = v.findViewById(R.id.btn_login)
        //btnDebug = v.findViewById(R.id.btn_debug)

        auth = FirebaseAuth.getInstance()

        return v
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser

        if(currentUser != null) {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("userUid",currentUser.email)
            startActivity(intent)
            activity?.finish()
        }

        btnSignup.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToSignupFragment()
            v.findNavController().navigate(action)
        }

        btnLogin.setOnClickListener {
            if(edtUsername.text.isNotBlank() and edtPassword.text.isNotBlank()) {
                val username = edtUsername.text.toString()
                val password = edtPassword.text.toString()

                auth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            Log.d(TAG, "signInWithEmail:success - user:${user?.email}")

                            val intent = Intent(context, MainActivity::class.java)
                            intent.putExtra("userUid","matias")
                            startActivity(intent)
                            activity?.finish()
                            /*
                            val action = LoginFragmentDirections.actionLoginFragmentToMainNavgraph()
                            v.findNavController().navigate(action)
                             */
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            Snackbar.make(v, "Authentication failed.", Snackbar.LENGTH_SHORT).show()
                            txtError.text = getString(R.string.msg_user_input_error)
                        }
                    }
            } else {
                txtError.text = getString(R.string.msg_user_input_incomplete)
            }
        }

        /*
        btnDebug.setOnClickListener {
            RoomExplorer.show(context, usersDatabase::class.java, "myDB")
        }
         */

        txtRememberPassword.setOnClickListener {
            Snackbar.make(v, "Jodete", Snackbar.LENGTH_LONG).show()
        }

    }

}
