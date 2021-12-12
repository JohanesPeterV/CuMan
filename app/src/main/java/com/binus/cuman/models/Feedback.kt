package com.binus.cuman.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class Feedback (
    @DocumentId var id: String = "",
    var userId: String = "",
    var content: String = "",
    var status: String? = "PENDING",
    @ServerTimestamp var createdAt: Timestamp? = null,
    @ServerTimestamp var updatedAt: Timestamp? = null
)