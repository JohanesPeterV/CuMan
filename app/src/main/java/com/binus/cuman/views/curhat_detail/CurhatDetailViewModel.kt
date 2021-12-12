package com.binus.cuman.views.curhat_detail

import android.content.Intent
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.binus.cuman.R
import com.binus.cuman.models.Curhat
import com.binus.cuman.models.CurhatComment
import com.binus.cuman.repositories.CurhatCommentRepository
import com.binus.cuman.repositories.CurhatRepository
import com.binus.cuman.repositories.NotificationRepository
import com.binus.cuman.repositories.UserRepository

class CurhatDetailViewModel: ViewModel() {
    private var allCurhats: List<CurhatComment> = listOf()
    private var _comments: MutableLiveData<List<CurhatComment>> = MutableLiveData<List<CurhatComment>>().apply {
        value = listOf()
    }
    val comments: LiveData<List<CurhatComment>> get() = _comments

    private var _isFetchingData: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = true }
    val isFetchingData: LiveData<Boolean> get() = _isFetchingData

    var curhat: Curhat = Curhat()
    private var id = ""

    var lim: Long = 5

    fun getCurhatDetail(intent: Intent?, callback: () -> Unit) {
        _isFetchingData.value = true
        if (intent != null) {
            id = getCurhatId(intent)
        }

        CurhatRepository.getById(id) {
            if (it.id.isEmpty()) callback()
            else {
                curhat = it
                getComments() {
                    _isFetchingData.value = false
                }
            }
        }
    }

    fun getComments(callback: () -> Unit) {
        CurhatCommentRepository.getCommentsByCurhatId(id, lim) { comments ->
            if (comments != null) {
                _comments.value = comments
            } else {
                _comments.value = listOf()
            }
            callback()
        }
    }

    private fun getCurhatId(intent: Intent) : String {
        val bundle = intent.extras
        val id = bundle!!.getString("id")
        return id!!
    }

    fun addComment(content: TextView) {
        val currentUserId = UserRepository.getCurrentUserId()
        CurhatCommentRepository.addComment(id, currentUserId, content.text.toString()) {newCommentId ->
            content.text = ""
            Toast.makeText(content.context, content.context.getString(R.string.toast_comment_s), Toast.LENGTH_SHORT).show()
            getCurhatDetail(null) {}
            if(currentUserId!=curhat.user)NotificationRepository.addNotif(newCommentId, curhat.user) {

            }
        }
    }

    fun showMoreComments() {
        lim = lim + 5
        _isFetchingData.value = true
        getComments {
            _isFetchingData.value = false
        }
    }
}

