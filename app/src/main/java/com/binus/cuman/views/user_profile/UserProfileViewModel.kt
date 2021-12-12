package com.binus.cuman.views.user_profile

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.binus.cuman.models.*
import com.binus.cuman.repositories.CurhatCommentRepository
import com.binus.cuman.repositories.CurhatRepository

class UserProfileViewModel {
    var initCurhats: List<Curhat>? = listOf()
    var initReplies: List<CurhatComment>? = listOf()

    val curhatCount: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    val replyCount: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    var recentReplies: MutableLiveData<List<CurhatComment>> =
        MutableLiveData<List<CurhatComment>>().apply {
            postValue(initReplies)
        }

    var recentCurhats: MutableLiveData<List<Curhat>> = MutableLiveData<List<Curhat>>().apply {
        postValue(initCurhats)
    }

    val currUser = FirebaseAuth.getInstance().currentUser

    constructor() {
        CurhatRepository.countUserPost(currUser?.uid.toString()).addSnapshotListener { value, e ->
            if (e != null) {
                curhatCount.value = 0
                return@addSnapshotListener
            }
            if (value != null) {
                curhatCount.value = value?.size()
            } else {
                curhatCount.value = 0
            }
        }

        CurhatCommentRepository.countUserComment(currUser?.uid.toString()).addSnapshotListener { value, e ->
            if (e != null) {
                replyCount.value = 0

                return@addSnapshotListener
            }
            if (value != null) {
                replyCount.value = value.size()
            } else {
                replyCount.value = 0
            }
        }

        getRecentCurhats()
        getRecentReplies()
    }

    fun getRecentCurhats() {
        CurhatRepository.userProfilePost(currUser?.uid.toString()).addSnapshotListener { value, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            recentCurhats.value = value?.toObjects(Curhat::class.java)
        }
    }

    fun getRecentReplies() {
        CurhatCommentRepository.userProfilePost(currUser?.uid.toString()).addSnapshotListener { value, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            recentReplies.value = value?.toObjects(CurhatComment::class.java)
        }
    }

    fun uploadImage() {

    }
}