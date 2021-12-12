package com.binus.cuman.repositories

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.*
import com.binus.cuman.R
import com.binus.cuman.models.User

class UserRepository {
    companion object{
        val COLLECTION_NAME = "users"



        fun userRegister(id:String,userName:String,email: String,password: String ){
            val db = FirebaseFirestore.getInstance()
            val user=db.collection(UserRepository.COLLECTION_NAME).document(id)
            user.set(
                hashMapOf(
                    "email" to email,
                    "name" to userName,
                    "password" to password,
                    "isAdmin" to false,
                    "joinedAt" to Timestamp.now()
                ), SetOptions.merge()
            )

        }
        fun userSetPassword(password:String){
            val db = FirebaseFirestore.getInstance()
            val currUser=getCurrentUser()
            val user= currUser?.let { db.collection(UserRepository.COLLECTION_NAME).document(it.uid) }

            if (currUser != null) {
                if(currUser.displayName==null){
                    if (user != null) {
                        user.set(
                            hashMapOf(
                                "email" to currUser.email ,
                                "name" to currUser.email?.substringBefore("@"),
                                "password" to password
                            ), SetOptions.merge()
                        )
                    }
                }else{
                    if (user != null) {
                        user.set(
                            hashMapOf(
                                "email" to currUser.email ,
                                "name" to currUser.displayName,
                                "password" to password
                            ), SetOptions.merge()
                        )
                    }
                }
            }

        }
        fun userUpdatePassword(password:String,context: Context){
            val db = FirebaseFirestore.getInstance()
            val currUser=getCurrentUser()
            val user= currUser?.let { db.collection(UserRepository.COLLECTION_NAME).document(it.uid) }

            user?.set(
                hashMapOf(
                    "password" to password
                ), SetOptions.merge()
            )
            Toast.makeText(context, context.getString(R.string.toast_psu),Toast.LENGTH_SHORT)

        }

        fun userSubmitData(url:String, gender: String, dob: Timestamp){
            val currUser= FirebaseAuth.getInstance().currentUser
            FirebaseAuth.getInstance().currentUser?.let { user ->
                user.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setPhotoUri(Uri.parse(url))
                        .build()
                ).addOnSuccessListener {
                    if(currUser?.displayName==null){
                        if (currUser != null) {
                            getUserById(currUser.uid).set(
                                hashMapOf(
                                    "email" to currUser.email ,
                                    "name" to (currUser.email?.substringBefore("@") ?: ""),
                                    "profileImageId" to url,
                                    "gender" to gender,
                                    "dateOfBirth" to dob,
                                    "isAdmin" to false,
                                    "joinedAt" to Timestamp.now()
                                ), SetOptions.merge())
                        }

                    }else{
                        getUserById(currUser.uid).set(
                            hashMapOf(
                                "email" to currUser.email ,
                                "name" to currUser.displayName,
                                "profileImageId" to url,
                                "gender" to gender,
                                "dateOfBirth" to dob,
                                "isAdmin" to false,
                                "joinedAt" to Timestamp.now()
                            ), SetOptions.merge())

                    }
                }
            }
        }

        fun userUpdateProfile(userName:String, gender: String, dob: Timestamp, context:Context){
            val currUser= FirebaseAuth.getInstance().currentUser
            FirebaseAuth.getInstance().currentUser?.let { user ->
                user.updateProfile(
                    userProfileChangeRequest {
                        displayName=userName
                    }


                ).addOnSuccessListener {
                    if (currUser != null) {
                        getUserById(currUser.uid).set(
                            hashMapOf(
                                "name" to userName,
                                "gender" to gender,
                                "dateOfBirth" to dob
                            ), SetOptions.merge()).addOnSuccessListener {
                            Toast.makeText(context, context.getString(R.string.toast_pus), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        fun getUserByEmail(email:String):Task<QuerySnapshot>{
            val db= FirebaseFirestore.getInstance()
            val user = db.collection(COLLECTION_NAME).whereEqualTo("email",email)
            return user.get()
        }

        fun getUserProfileUrl():String{
            return getCurrentUser()?.photoUrl.toString()
        }

        fun getUserByEmail(email: String,callback: (List<User>) -> Unit) {
            val db = FirebaseFirestore.getInstance()
            db.collection(UserRepository.COLLECTION_NAME).whereEqualTo("email",email).get()
                    .addOnSuccessListener {topicDocs ->
                        val users = mutableListOf<User>()
                        for (topicDoc in topicDocs) {
                            val user = topicDoc.toObject(User::class.java)
                            user.id = topicDoc.id
                            users.add(user)
                        }
                        callback(users)
                    }
        }


        fun setUser(id: String, email: String, name:String) {
            val db = FirebaseFirestore.getInstance()
            val user=db.collection(COLLECTION_NAME).document(id)
            if(name==null){
                user.set(
                    hashMapOf(
                        "email" to email,
                        "name" to email.substringBefore("@")
                    ), SetOptions.merge()
                )
            }else {
                user.set(
                    hashMapOf(
                        "email" to email,
                        "name" to name
                    ), SetOptions.merge()
                )
            }
        }

        fun getUser(id: String):Task<DocumentSnapshot> {
            val db = FirebaseFirestore.getInstance()
            val user=db.collection(UserRepository.COLLECTION_NAME).document(id)
            return user.get()
        }

        fun getCurrentUserId(): String {
            return FirebaseAuth.getInstance().currentUser?.uid.toString()
        }


        fun getCurrentUser(callback: (User?) -> Unit) {
            val db = FirebaseFirestore.getInstance()

            val currentUserId = getCurrentUserId()
            if (currentUserId != null) {
                db.collection(COLLECTION_NAME).document(currentUserId).get()
                    .addOnSuccessListener {
                        val user = it.toObject(User::class.java)
                        user?.age = user?.dateOfBirth?.let { dob -> AgeCalculatorUtil.calculateAge(dob.toDate()) }
                        user?.isAdmin = it.getBoolean("isAdmin")
                        callback(user)
                    }
            }
        }

        fun getUserById(id: String):DocumentReference {
            val db = FirebaseFirestore.getInstance()
            val user=db.collection(UserRepository.COLLECTION_NAME).document(id)
            return user
        }

        fun getUserById(userId: String, callback: (User?) -> Unit) {
            val db = FirebaseFirestore.getInstance()

            db.collection(COLLECTION_NAME).document(userId).get()
                .addOnSuccessListener {
                    var user = it.toObject(User::class.java)
                    user?.age = user?.dateOfBirth?.let { dob -> AgeCalculatorUtil.calculateAge(dob.toDate()) }
                    user?.isAdmin = it.getBoolean("isAdmin")

                    callback(user)
                }
        }
        fun updateProfileImage(url: Uri){
            val currUser= FirebaseAuth.getInstance().currentUser
//



//            currUser.updateProfile(userProfileChangeRequest {
//                photoUri= Uri.parse(""+url)
//            })
            FirebaseAuth.getInstance().currentUser?.let { user ->
                user.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setPhotoUri(url)
                        .build()
                ).addOnSuccessListener {
                    if (currUser != null) {
                        getUserById(currUser.uid)
                            .update("profileImageId", url.toString())
                    }
//                        .set(hashMapOf("profileImageId" to url), SetOptions.merge())
                }
            }
        }

        fun getCurrentUser(): FirebaseUser? {
            val user=FirebaseAuth.getInstance().currentUser
            return user
        }


    }
}