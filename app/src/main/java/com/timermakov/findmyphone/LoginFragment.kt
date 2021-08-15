package com.timermakov.findmyphone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.timermakov.findmyphone.User
import com.timermakov.findmyphone.retrofit.Api


class LoginFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onResume() {
        super.onResume()
        var buttonLogin = this.requireView().findViewById<Button>(R.id.buttonLogin)
        buttonLogin.setOnClickListener {
            var inputEmail = this.requireView().findViewById<EditText>(R.id.inputEmail)
            var inputPassword = this.requireView().findViewById<EditText>(R.id.inputPassword)
            val api = Api(this::goToMain)
            api.login(inputEmail.text.toString(), inputPassword.text.toString())
        }
    }

    fun goToMain() {
        if (User.is_parent) {
            requireView().findNavController().navigate(R.id.action_loginFragment_to_parentFragment)
        } else {
            requireView().findNavController().navigate(R.id.action_loginFragment_to_childFragment)
        }
    }
}