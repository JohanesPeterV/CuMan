package com.binus.cuman.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.firebase.Timestamp
import com.binus.cuman.views.curhat_detail.CurhatDetailActivity
import java.text.SimpleDateFormat

class CurhatUtil {
    companion object {
        fun moveToCurhatDetail(id: String, context: Context) {
            val intent = Intent(context, CurhatDetailActivity::class.java)
            val b = Bundle()
            b.putString("id", id);
            intent.putExtras(b)
            context.startActivity(intent)
        }

        fun formatDate(timestamp: Timestamp?, context: Context) : String {
            val sdf = SimpleDateFormat("dd MMM yyyy")
            var formatted = ""

            if (timestamp != null) {
                formatted = sdf.format(timestamp?.toDate())
            }
            return formatted
        }
    }
}