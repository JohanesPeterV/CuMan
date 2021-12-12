package com.binus.cuman.utils

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat

class UserUtil {
    companion object {
        fun formatDate(timestamp: Timestamp?) : String {
            val sdf = SimpleDateFormat("dd MMM yyyy")
            var formatted: String = ""

            if (timestamp != null) {
                formatted = sdf.format(timestamp?.toDate())
            }
            return formatted
        }
    }
}