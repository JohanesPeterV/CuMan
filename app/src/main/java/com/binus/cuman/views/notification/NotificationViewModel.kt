package com.binus.cuman.views.notification

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.binus.cuman.models.Notification
import com.binus.cuman.repositories.NotificationRepository
import com.binus.cuman.repositories.UserRepository


class NotificationViewModel () {

    val nList: MutableLiveData<List<Notification>> = MutableLiveData<List<Notification>>().apply {
        value = mutableListOf()
    }

    var limit: Long = 10
    var listSize: Int = 0;
    var currSize:Int=0

    init {
        NotificationRepository.getNotifCount {
            listSize = it
            Log.i("NotifViewModel", listSize.toString())
            getData()
        }
    }

    fun getData(){
        val currentUserId = UserRepository.getCurrentUserId()
        NotificationRepository.getNotif(currentUserId, limit) {
            nList.value = it
            currSize = it.size
        }
    }
}