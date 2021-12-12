package com.binus.cuman.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class User (
    @DocumentId var id: String="",
    val name: String="Kosong",
    var age: Int?=1,
    val email:String?="",
    var gender:String? = "",
    var password:String?="",
    var dateOfBirth: Timestamp? = null,
    val joinedAt: Timestamp?=null,
    var isAdmin: Boolean?=false,
    val profileImageId: String?=""

)