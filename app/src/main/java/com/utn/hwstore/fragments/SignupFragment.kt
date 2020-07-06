package com.utn.hwstore.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.navigation.findNavController

import com.utn.hwstore.R
import com.utn.hwstore.database.UserDao
import com.utn.hwstore.database.usersDatabase
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

    private var db: usersDatabase? = null
    private var userDao: UserDao? = null
    private lateinit var usersList: ArrayList<User>

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

        return v
    }

    override fun onStart() {
        super.onStart()

        db = usersDatabase.getAppDataBase(v.context)
        userDao = db?.userDao()

        btnCreate.setOnClickListener {
            if(edtUsername.text.isNotBlank() and edtPassword.text.isNotBlank()) {
                val username = edtUsername.text.toString()
                val password = edtPassword.text.toString()
                val passwordCheck = edtPasswordCheck.text.toString()

                if(password == passwordCheck) {
                    val user = User(username, password)
                    userDao?.insertPerson(user)
                    /*
                    if(usersList.contains(user)) {
                        txtError.text = getString(R.string.msg_new_user_duplicated)
                    } else {
                        userDao?.insertPerson(user)
                    }
                     */

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
