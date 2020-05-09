package com.example.mvi_scaffolding.ui.auth

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.mvi_scaffolding.R
import com.example.mvi_scaffolding.ui.main.MainActivity
import com.example.mvi_scaffolding.utils.Constants
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_auth.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class AuthFragment : DaggerFragment() {
    private val TAG = "AppDebug: " + AuthFragment::class.java.simpleName

    @Inject
    lateinit var editor: SharedPreferences.Editor

    val callback by lazy {
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:${credential.smsCode}")

                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                activity!!.finish()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
//                storedVerificationId = verificationId
//                resendToken = token

                //  disabling btn and starting chronometer
                chronometer.base = 1000 * 60
               chronometer.start()
                auth_frag_verify_btn.isEnabled = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth_frag_verify_btn.setOnClickListener {
            var phoneNumber = view.findViewById<TextView>(R.id.phone_number_et).text.toString()
            val username = name_et.text.toString()
            if (username.isNullOrEmpty()) {
                name_et.error = "Enter a valid username"
                return@setOnClickListener
            }
            if (phoneNumber.isNullOrEmpty()) {
                phone_number_et.error = "Enter a valid number"
                return@setOnClickListener
            }

            saveUsername(username)

            phoneNumber = "+91$phoneNumber"

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                activity!!, // Activity (for callback binding)
                callback
            )
        }

        chronometer.setOnChronometerTickListener {

        }
    }

    private fun saveUsername(username: String) {
        editor.putString(Constants.USERNAME, username)
        editor.commit()
    }
}
