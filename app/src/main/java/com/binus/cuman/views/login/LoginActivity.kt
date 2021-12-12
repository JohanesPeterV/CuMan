package com.binus.cuman.views.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

import com.binus.cuman.MainActivity

import com.binus.cuman.R
import com.binus.cuman.models.User
import com.binus.cuman.repositories.UserRepository
import com.binus.cuman.views.register.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)


        setTheme(R.style.Theme_Cuman_SplashScreen)

        setContentView(R.layout.activity_login)
//        if(GLOBALS.CHECK_USER){
//            Toast.makeText(this, "check user true" , Toast.LENGTH_SHORT).show()
//        }else{
//            Toast.makeText(this, "check user false" , Toast.LENGTH_SHORT).show()
//
//        }


        if (FirebaseAuth.getInstance().currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {

            setLoginListener()
            setLinkListener()
        }

    }

    fun setLinkListener() {
        val linkBtn: TextView = findViewById(R.id.login_link)
        linkBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun setLoginListener() {

        val loginButton: Button = findViewById(R.id.login_button)
        val googleButton: Button = findViewById(R.id.login_google_button)

        googleButton.setOnClickListener {
            initFireBaseUIIntent()
        }
        loginButton.setOnClickListener {


            val emailText: EditText = findViewById(R.id.login_email)
            val passwordText: EditText = findViewById(R.id.login_password)
            var passwordString = passwordText.text.toString()
            if (emailText.text.toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.validate_une), Toast.LENGTH_SHORT).show()


                return@setOnClickListener
            }


            UserRepository.getUserByEmail(emailText.text.toString()).addOnSuccessListener { it ->
                val its = it.toObjects(User::class.java)
                if (its.isEmpty()) {

                    Toast.makeText(this, getString(R.string.validate_uu), Toast.LENGTH_SHORT).show()

                    return@addOnSuccessListener
                }
                val itUser = its[0]
                if (passwordText.text.toString() != itUser.password) {
                    Toast.makeText(this, getString(R.string.login_wrongP), Toast.LENGTH_SHORT)
                        .show()

                    return@addOnSuccessListener
                }
                if (passwordString == null) passwordString = ""

                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(emailText.text.toString(), passwordString)
                    .addOnCompleteListener(this) { it ->
                        if (it.isSuccessful) {

                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                getString(R.string.login_failed),
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }


            }

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser



                UserRepository.setUser(
                    user?.uid.toString(),
                    user?.email.toString(),
                    user?.displayName.toString()
                )
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Toast.makeText(this, this.getString(R.string.toast_gsgf), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initFireBaseUIIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.Theme_Cuman) // Set theme
                .setLogo(R.drawable.cuman)
//            .setTosAndPrivacyPolicyUrls(
//                "https://example.com/terms.html",
//                "https://example.com/privacy.html")
                .build(), RC_SIGN_IN
        )

    }


    companion object {

        private const val RC_SIGN_IN = 123
    }
}

