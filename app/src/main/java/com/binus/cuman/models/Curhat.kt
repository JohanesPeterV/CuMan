package com.binus.cuman.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp


data class Curhat (
        @DocumentId var id: String = "",
        var topic: String = "",
        var user: String = "",
        var content: String = "",
        var likeCount: Int? = 0,
        var dislikeCount: Int? = 0,
        var viewCount: Int? = 0,
        var commentCount: Int? = 0,
        var isAnonymous: Boolean = true,
        var usersGiveThumbUp: List<String>? = listOf(),
        var usersGiveCool: List<String>? = listOf(),
        var usersGiveLove: List<String>? = listOf(),
        var usersGiveThumbDowns: List<String>? = listOf(),
        var usersGiveAngry: List<String> = listOf(),
        @ServerTimestamp var createdAt:  Timestamp? = null,
        @ServerTimestamp var updatedAt: Timestamp? = null
)