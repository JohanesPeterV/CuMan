package com.binus.cuman.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.binus.cuman.utils.NotificationUtil

class MyFirebaseMessagingServices : FirebaseMessagingService() {
    private val TAG = "MessagingServices"
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Message data payload: ${remoteMessage.data}")


        // get message
        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["message"]

        showNotification(title!!, message!!)
    }

    private fun scheduleAlarm(
        title: String?,
        message: String?
    ) {
        val alarmMgr = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent =
            Intent(applicationContext, NotificationBroadcastReceiver::class.java).let { intent ->
                intent.putExtra("NOTIFICATION_TITLE", title)
                intent.putExtra("NOTIFICATION_MESSAGE", message)
                PendingIntent.getBroadcast(applicationContext, 0, intent, 0)
            }

        // Parse Schedule time

        alarmMgr.setRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            System.currentTimeMillis(),
            60 * 1000,
            alarmIntent
        )
    }

    private fun showNotification(title: String, message: String) {
        NotificationUtil(applicationContext).showNotification(title, message)
    }
}