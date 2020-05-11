package com.example.mvi_scaffolding.ui.auth

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.mvi_scaffolding.R
import com.example.mvi_scaffolding.firebase.FirebaseHandler
import com.example.mvi_scaffolding.ui.main.MainActivity
import com.example.mvi_scaffolding.utils.Constants
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.auth_frag_middle.*
import kotlinx.android.synthetic.main.fragment_auth.*
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class AuthFragment : DaggerFragment() {
    private val TAG = "AppDebug: " + AuthFragment::class.java.simpleName

    @Inject
    lateinit var editor: SharedPreferences.Editor
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private var countDownTimer: CountDownTimer? = null

    var _verificationId: String? = null

    private val callback by lazy {
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//                Log.d(TAG, "onVerificationCompleted:${credential.smsCode}")

                signInWithPhoneCredentials(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                } else if (e is FirebaseNetworkException) {
                    Toast.makeText(activity, "Turn on the internet connection and try again", Toast.LENGTH_SHORT)
                        .show()
                    return
                }

                Toast.makeText(activity, "Verification failed, try again", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
//                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                _verificationId = verificationId
//                resendToken = token

                //  disabling btn and starting chronometer
                setCountDownTimer()
                auth_frag_verify_btn.isEnabled = false
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
            startPhoneNumberVerification()
        }

        confirm_code_btn.setOnClickListener {
            val code = code_verify_et.text.toString()
            if (code.isNotEmpty()) {
                val credentials = PhoneAuthProvider.getCredential(_verificationId!!, code)
                signInWithPhoneCredentials(credentials)
            }
        }
    }

    fun signInWithPhoneCredentials(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(activity!!) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")

                    countDownTimer?.cancel()

                    val user = task.result?.user
                    saveUid(user!!.uid)

                    //  updating the database
                    updatingTheDatabase(user.uid)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(activity!!, "Invalid code", Toast.LENGTH_SHORT).show()
                    }
                    Toast.makeText(activity!!, "Sign in failed, try again", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updatingTheDatabase(uid: String) {
        val username = getUsername()
        val phoneNumber = getPhoneNumber()
        username?.let {name ->
            phoneNumber?.let {number ->
                FirebaseHandler(activity!!).updateUserBasicInfo(uid, name, number, false)
                    .addOnSuccessListener {
                        //  putting value in shared prefs
                        editor.putBoolean(Constants.USER_LOGGED_IN, true)
                        editor.commit()

                        val intent = Intent(activity, MainActivity::class.java)
                        startActivity(intent)
                        activity!!.finish()
                    }.addOnFailureListener {
                        Log.e(TAG, "signInWithPhoneCredentials: ", it)
                    }
            }
        }
    }

    private fun saveUid(uid: String) {
        editor.putString(Constants.UID, uid)
        editor.commit()
    }

    private fun startPhoneNumberVerification() {
        var phoneNumber = view!!.findViewById<TextView>(R.id.phone_number_et).text.toString()
        val username = name_et.text.toString()
        if (username.isNullOrEmpty()) {
            username_text_layout.error = "Enter a valid username"
            return
        }
        if (phoneNumber.isNullOrEmpty()) {
            phone_text_input.error = "Enter a valid number"
            return
        }

        saveUsername(username)

        phoneNumber = "+91$phoneNumber"
        savePhoneNumber(phoneNumber)

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            activity!!, // Activity (for callback binding)
            callback
        )
    }

    private fun setCountDownTimer() {
        countDownTimer = object : CountDownTimer(1000 * 60, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                count_down_timer_tv.text = java.lang.String.format(
                    Locale.getDefault(),
                    "%d",
                    millisUntilFinished / 1000L
                )
            }

            override fun onFinish() {
                auth_frag_verify_btn.isEnabled = true
            }
        }.start()
    }

    private fun saveUsername(username: String) {
        editor.putString(Constants.USERNAME, username)
        editor.commit()
    }

    private fun getUsername(): String? {
        return sharedPreferences.getString(Constants.USERNAME, null)
    }

    private fun savePhoneNumber(phoneNumber: String) {
        editor.putString(Constants.PHONE_NUMBER, phoneNumber)
        editor.commit()
    }

    private fun getPhoneNumber(): String? {
        return sharedPreferences.getString(Constants.PHONE_NUMBER, null)
    }
}
