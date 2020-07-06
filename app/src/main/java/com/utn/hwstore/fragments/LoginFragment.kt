package com.utn.hwstore.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.utn.hwstore.MainActivity

import com.utn.hwstore.R
import com.utn.hwstore.database.UserDao
import com.utn.hwstore.database.usersDatabase
import com.utn.hwstore.entities.User
import com.wajahatkarim3.roomexplorer.RoomExplorer

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {

    private lateinit var btnDebug: Button
    private lateinit var btnLogin: Button
    private lateinit var btnSignup: Button
    private lateinit var edtUsername: EditText
    private lateinit var edtPassword: EditText
    private lateinit var txtError: TextView
    private lateinit var txtRememberPassword: TextView

    private var db: usersDatabase? = null
    private var userDao: UserDao? = null
    private lateinit var usersList: ArrayList<User>

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

        return v
    }

    override fun onStart() {
        super.onStart()

        db = usersDatabase.getAppDataBase(v.context)
        userDao = db?.userDao()

        btnSignup.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToSignupFragment()
            v.findNavController().navigate(action)
        }

        btnLogin.setOnClickListener {
            if(edtUsername.text.isNotBlank() and edtPassword.text.isNotBlank()) {
                val username = edtUsername.text.toString()
                val password = edtPassword.text.toString()
                val user = User(username, password)

                val userInDb = userDao?.loadPersonByUsername(username)
                if(userInDb != null) {
                    if(userInDb.equals(user)) {
                        /*
                        val action = LoginFragmentDirections.actionLoginFragmentToMainNavgraph()
                        v.findNavController().navigate(action)
                         */
                        startActivity(Intent(context, MainActivity::class.java))
                        activity?.finish()
                    } else {
                        txtError.text = getString(R.string.msg_user_input_error)
                    }
                } else {
                    txtError.text = getString(R.string.msg_user_missing)
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
