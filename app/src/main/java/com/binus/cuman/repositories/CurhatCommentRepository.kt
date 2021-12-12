package com.binus.cuman.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.binus.cuman.models.CurhatComment

class CurhatCommentRepository {
    companion object {
        private val COLLECTION_NAME = "comments"

        fun addComment(curhatId: String, userId: String, content: String, callback: (String) -> Unit) {
            val db = FirebaseFirestore.getInstance()

            val comment =
                CurhatComment("", curhatId, userId, content, Timestamp.now(), Timestamp.now())
            db.collection(COLLECTION_NAME).add(comment)
                .addOnSuccessListener {
                    CurhatRepository.incrementCommentCount(curhatId)
                    callback(it.id)
                }
        }

        fun updateComment(commentId: String, content: String, callback: (String) -> Unit) {
            val db = FirebaseFirestore.getInstance()

            db.collection(COLLECTION_NAME).document(commentId)
                .update("content", content)
                .addOnSuccessListener {
                    callback(content)
                }
        }

        fun getCommentsByCurhatId(curhatId: String, lim: Long? = null, callback: (List<CurhatComment>?) -> Unit) {
            val db = FirebaseFirestore.getInstance()
            if (lim == null) {
                db.collection(COLLECTION_NAME).whereEqualTo("curhatId", curhatId)
                    .orderBy("createdAt").get()
                    .addOnSuccessListener {
                        val comments = mutableListOf<CurhatComment>()
                        for (doc in it.documents) {
                            val comment = doc.toObject(CurhatComment::class.java)
                            comments.add(comment!!)
                        }
                        callback(comments)
                    }
            } else {
                db.collection(COLLECTION_NAME).whereEqualTo("curhatId", curhatId).limit(lim)
                    .orderBy("createdAt").get()
                    .addOnSuccessListener {
                        val comments = mutableListOf<CurhatComment>()
                        for (doc in it.documents) {
                            val comment = doc.toObject(CurhatComment::class.java)
                            comments.add(comment!!)
                        }
                        callback(comments)
                    }
            }
        }
        fun getComment(commentId: String): Task<DocumentSnapshot> {
            val db=FirebaseFirestore.getInstance()
            return db.collection(COLLECTION_NAME).document(commentId).get()
        }
        fun deleteById(curhatId: String, commentId: String, callback: () -> Unit)  {
            val db = FirebaseFirestore.getInstance()

            db.collection(COLLECTION_NAME).document(commentId).delete()
                .addOnSuccessListener {
                    CurhatRepository.decrementCommentCount(curhatId)
                    NotificationRepository.deleteByCommentId(commentId) {}
                    callback()
                }
        }

        fun countUserComment(id: String): Query {
            val db = FirebaseFirestore.getInstance()
            val curhatReplies = db.collection(COLLECTION_NAME).whereEqualTo("user", id)
            return curhatReplies
        }

        fun userProfilePost(id: String): Query {
            val db = FirebaseFirestore.getInstance()
            val curhats = db.collection(COLLECTION_NAME).whereEqualTo("user",  id)
                .orderBy("createdAt", Query.Direction.DESCENDING).limit(3)
            return curhats
        }

        fun getCommentCount(curhatId: String, callback: (Int) -> Unit) {
            val db = FirebaseFirestore.getInstance()
            db.collection(COLLECTION_NAME).whereEqualTo("curhatId", curhatId).get()
                .addOnSuccessListener {
                    callback(it.documents.size)
                }
        }

        fun deleteAllById(curhatId: String, callback: () -> Unit) {
            val db = FirebaseFirestore.getInstance()
            db.collection(COLLECTION_NAME).whereEqualTo("curhatId", curhatId).get()
                .addOnSuccessListener {
                    for (doc in it.documents) {
                        doc.reference.delete()
                        NotificationRepository.deleteByCommentId(doc.id) {}
                    }
                    callback()
                }
        }
        fun deleteUser(userId: String){
            val db = FirebaseFirestore.getInstance()
            val collection = db.collection(COLLECTION_NAME)
            val curhats = db.collection(COLLECTION_NAME).whereEqualTo("user", userId)


            curhats.get().addOnSuccessListener { it ->
                for (a in it){
                    NotificationRepository.deleteByCommentId(a.id){}
                    collection.document(a.id).delete()

                }
            }

        }
    }
}