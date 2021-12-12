package com.binus.cuman.models

import com.google.firebase.firestore.DocumentId

data class CurhatTopic (
        @DocumentId var id: String = "",
        var name: String = "")