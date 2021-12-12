package com.binus.cuman.views.register

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.binus.cuman.MainActivity
import com.binus.cuman.R
import com.binus.cuman.models.User
import com.binus.cuman.repositories.UserRepository
import com.binus.cuman.views.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setRegisterListener()
        setLinkListener()
        // Enables Always-on
    }
    fun setRegisterListener(){

        val registerButton: Button =findViewById(R.id.register_button)
        val googleButton: Button =findViewById(R.id.register_google_button)

        googleButton.setOnClickListener{
            initFireBaseUIIntent()
        }
        registerButton.setOnClickListener {
            val userNameText: EditText = findViewById(R.id.register_user_name)
            val emailText: EditText =findViewById(R.id.register_email)
            val passwordText: EditText =findViewById(R.id.register_password)
            val confirmPasswordText: EditText =findViewById(R.id.register_confirm_password)
            val userName=userNameText.text.toString()
            val email=emailText.text.toString()
            val password=passwordText.text.toString()
            val confirmPassword=confirmPasswordText.text.toString()
            if(userName.isEmpty()){
                Toast.makeText(this,getString(R.string.validate_ue), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(email.isEmpty()){
                Toast.makeText(this,getString(R.string.validate_ee), Toast.LENGTH_SHORT).show()


                return@setOnClickListener
            }

            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Toast.makeText(this,getString(R.string.validate_ef), Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            if(password.length<6){
                Toast.makeText(this,getString(R.string.validate_pl), Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }
            if(!confirmPassword.equals(password)){
                Toast.makeText(this,getString(R.string.validate_pcp), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            UserRepository.getUserByEmail(emailText.text.toString()).addOnSuccessListener { it->
                val its=it.toObjects(User::class.java)
                if(its.isEmpty()){
                    //registerLogic
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener(this){task->
                        if(task.isSuccessful){
                            FirebaseAuth.getInstance().currentUser ?.updateProfile(
                                userProfileChangeRequest {
                                    displayName=userName
                                })
                            FirebaseAuth.getInstance().currentUser?.let { it1 ->
                                UserRepository.userRegister(
                                    it1.uid,userName, email, password)
                            }
                            val intent  = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }else{
                            Toast.makeText(this,getString(R.string.regist_error), Toast.LENGTH_SHORT).show()
                        }
                    }
                    return@addOnSuccessListener
                }
                Toast.makeText(this,getString(R.string.validate_une), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun setLinkListener(){
        val linkBtn: TextView=findViewById(R.id.register_link)
        linkBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun initFireBaseUIIntent(){
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
    } override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RegisterActivity.RC_SIGN_IN) {

            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                //                val user = FirebaseAuth.getInstance().currentUser
                // ...



//                UserRepository.setUser(FirebaseAuth.getInstance().currentUser.uid,FirebaseAuth.getInstance().currentUser.email, FirebaseAuth.getInstance().currentUser.displayName)
                val intent  = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...

            }
        }
    }


    companion object {

        private const val RC_SIGN_IN = 123
    }
}