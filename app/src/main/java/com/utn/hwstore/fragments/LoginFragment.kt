package com.utn.hwstore.fragments

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.utn.hwstore.MainActivity

import com.utn.hwstore.R
import com.utn.hwstore.databinding.FragmentLoginBinding
import com.utn.hwstore.entities.MyResult
import com.utn.hwstore.utils.UsersRepository
import kotlinx.android.synthetic.main.fragment_new_item.*
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private val RC_SIGN_IN = 42

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val usersRepository = UsersRepository()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        // Test user credentials
        binding.edtUsername.setText("demo@app.com")
        binding.edtPassword.setText("demoapp")

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        tryAutoLogin()

        binding.btnLogin.setOnClickListener {
            signInCallback()
        }

        binding.btnSignup.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToSignupFragment()
            findNavController().navigate(action)
        }

        binding.btnGoogleSignin.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build()

            val signInIntent = GoogleSignIn.getClient(requireContext(), gso).signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        binding.txtRememberPassword.setOnClickListener {
            //Snackbar.make(binding.root, "Jodete", Snackbar.LENGTH_LONG).show()
            showDialog("Jodete","No tuve ganas de implementarlo")
        }

    }

    private fun signInCallback() {
        val username = binding.edtUsername.text.toString()
        val password = binding.edtPassword.text.toString()
        if(username.isNotBlank() and password.isNotBlank()) {
            lifecycleScope.launch {
                enableUI(false)
                val result = usersRepository.loginWithEmailAndPassword(username, password)
                enableUI(true)
                when(result) {
                    is MyResult.Success -> {
                        if(result.data) {
                            Log.d(TAG, "signInWithEmail:success - user:${username}")

                            /*
                            val intent = Intent(context, MainActivity::class.java)
                            startActivity(intent)
                            activity?.finish()
                             */

                            val action = LoginFragmentDirections.actionLoginFragmentToMainActivity()
                            findNavController().navigate(action)
                        }
                    }
                    is MyResult.Failure -> {
                        Log.w(TAG, "signInWithEmail:failure", result.exception)
                        //Snackbar.make(binding.root, "Authentication failed.", Snackbar.LENGTH_SHORT).show()
                        showDialog("Oops!",getString(R.string.msg_user_input_error))
                        binding.txtErrormsg.text = getString(R.string.msg_user_input_error)
                    }
                }
            }
        } else {
            showDialog("Oops!",getString(R.string.msg_user_input_incomplete))
            binding.txtErrormsg.text = getString(R.string.msg_user_input_incomplete)
        }
    }

    private fun tryAutoLogin() {
        lifecycleScope.launch {
            enableUI(false)
            val result = usersRepository.getCurrentUser()
            enableUI(true)
            when(result) {
                is MyResult.Success -> {
                    if(result.data != null) {
                        /*
                        val intent = Intent(context, MainActivity::class.java)
                        intent.putExtra("userUid",result.data.email)
                        startActivity(intent)
                        activity?.finish()
                         */
                        val action = LoginFragmentDirections.actionLoginFragmentToMainActivity()
                        findNavController().navigate(action)
                    } else {
                        showDialog("Oops, ocurrió un error", "Parece que este usuario no existe")
                    }
                }
                is MyResult.Failure -> {
                    Log.e("LoginFragment", "Exception retrieving current user: ${result.exception.message}")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                showDialog("Error", "Revisar en en logcat. Si es ApiException 10, error de SHA-1")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser

                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra("userUid",user!!.displayName)
                    startActivity(intent)
                    activity?.finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Snackbar.make(binding.root, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
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
