package com.binus.cuman.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Notification (
    @DocumentId val id: String? = "",
    val commentId: String? = "",
    val curhatPosterUserId: String? = "",
    val createdAt: Timestamp? = null
)