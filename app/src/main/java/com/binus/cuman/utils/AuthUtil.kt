package com.binus.cuman.utils

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthUtil {
    companion object {
        fun reAuthGoogle(context: Context): Task<Void?>? {
            val currUser = FirebaseAuth.getInstance().currentUser
            val googleAcc = GoogleSignIn.getLastSignedInAccount(context)
            val credential = GoogleAuthProvider.getCredential(googleAcc?.getIdToken(), null)
            return currUser?.reauthenticate(credential)


        }

        fun reAuthEmail(oldPassword: String): Task<Void?>? {
            val currUser = FirebaseAuth.getInstance().currentUser
            val currAcc = EmailAuthProvider.getCredential(currUser?.email.toString(), oldPassword)
            return currUser?.reauthenticate(currAcc)

        }
    }
}