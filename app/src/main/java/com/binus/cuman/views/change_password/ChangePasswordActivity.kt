package com.binus.cuman.views.change_password

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.binus.cuman.R
import com.binus.cuman.repositories.UserRepository
import com.binus.cuman.utils.AuthUtil


class ChangePasswordActivity (): AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_change_password)
        reAuth()
       setButtonListener()
    }
    fun reAuth(){

        val currUser=FirebaseAuth.getInstance().currentUser
        val googleAcc= GoogleSignIn.getLastSignedInAccount(this)
        if(googleAcc!=null){
            val credential = GoogleAuthProvider.getCredential(googleAcc.getIdToken(), null)
            currUser?.reauthenticate(credential)
        }
    }
    fun setButtonListener(){
        val passwordButton: Button = findViewById(R.id.password_button)
        passwordButton.setOnClickListener {

            val password: TextView=findViewById(R.id.cp_password)
            val confirmPassword: TextView=findViewById(R.id.cp_confirm_password)
            if(password.text.toString().length<6){
                Toast.makeText(this, getString(R.string.validate_pl), Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }
            if(!password.text.toString().equals(confirmPassword.text.toString())){
                Toast.makeText(this, getString(R.string.validate_pcp), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }




            FirebaseAuth.getInstance().currentUser?.updatePassword(password.text.toString())?.addOnCompleteListener { it->
                if(it.isSuccessful){
                    UserRepository.userSetPassword(password.text.toString())
                    finish()
                }
                else{

                    try{
                        AuthUtil.reAuthGoogle(this)?.addOnCompleteListener{task ->
                            if(task.isSuccessful){
                                UserRepository.userSetPassword(password.text.toString())
                            }

                            finish()
                        }
                    }catch (exception:Exception){

                        finish()
                    }
                }
            }
        }
    }
}