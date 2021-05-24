package com.utn.hwstore.fragments

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.utn.hwstore.R
import com.utn.hwstore.databinding.FragmentSignupBinding
import com.utn.hwstore.entities.MyResult
import com.utn.hwstore.entities.User
import com.utn.hwstore.utils.UsersRepository
import kotlinx.android.synthetic.main.fragment_new_item.*
import kotlinx.coroutines.launch

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private val usersRepository = UsersRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSignupBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        binding.btnCreate.setOnClickListener {
            signUpCallback()
        }
    }

    private fun signUpCallback() {
        val email = binding.edtUsername.text.toString()
        val password = binding.edtPassword.text.toString()
        val passwordCheck = binding.edtPasswordCheck.text.toString()

        if(email.isNotBlank() and password.isNotBlank() and passwordCheck.isNotBlank()) {
            if(password == passwordCheck) {
                lifecycleScope.launch {
                    enableUI(false)

                    val user = User("", true, email.substringBefore('@'), "", email)
                    val result = usersRepository.createNewUser(user, email, password)

                    when(result) {
                        is MyResult.Success -> {
                            showDialog("¡Acción exitosa!","Usuario creado con éxito")
                            findNavController().navigateUp()
                        }
                        is MyResult.Failure -> {
                            Log.w(TAG, "createUserWithEmail:failure", result.exception)
                            //Snackbar.make(binding.root, "Authentication failed.", Snackbar.LENGTH_SHORT).show()
                            showDialog("Oops, ocurrió un error","No se pudo crear el usuario solicitado")
                        }
                    }
                    enableUI(true)
                }
            } else {
                binding.txtErrormsg.text = getString(R.string.msg_mismatch_passwords)
                showDialog("Oops, ocurrió un error",getString(R.string.msg_mismatch_passwords))
            }
        } else {
            binding.txtErrormsg.text = getString(R.string.msg_user_input_incomplete)
            showDialog("Oops, ocurrió un error",getString(R.string.msg_user_input_incomplete))
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

    private fun enableUI(enable: Boolean) {
        if(enable) {
            grayblur.visibility = View.INVISIBLE
            loader.visibility = View.INVISIBLE
        } else {
            grayblur.visibility = View.VISIBLE
            loader.visibility = View.VISIBLE
        }
    }

}
