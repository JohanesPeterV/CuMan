package com.binus.cuman.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class CommentNotification (
    @DocumentId var id: String,
    var curhatPosterUserId: String = "",
    var commentId: String = "",
    @ServerTimestamp var createdAt: Timestamp? = null
)