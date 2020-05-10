package com.example.mvi_scaffolding.ui.auth

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.mvi_scaffolding.R
import com.example.mvi_scaffolding.ui.main.MainActivity
import com.example.mvi_scaffolding.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import dagger.android.support.DaggerFragment
import javax.inject.Inject


class SplashScreenFragment : DaggerFragment() {
    private val TAG = "AppDebug: " + SplashScreenFragment::class.java.simpleName

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_screen_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isUserSignedIn = sharedPreferences.getBoolean(Constants.USER_LOGGED_IN, false)

        if (isUserSignedIn) {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity!!.finish()
        }

        val button = view.findViewById<Button>(R.id.splash_screen_btn)
        button.setOnClickListener {
            findNavController().navigate(R.id.action_splashScreenFragment_to_authFragment)
        }
    }

}
