package com.binus.cuman.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.binus.cuman.models.Feedback

class FeedbackRepository {
    companion object {
        private  val COLLECTION_NAME = "feedbacks"

        fun addFeedback(content: String, callback: () -> Unit) {
            val db = FirebaseFirestore.getInstance()

            val currentUserId = UserRepository.getCurrentUserId()
            val feedback = Feedback("", currentUserId.toString(), content)
            db.collection(COLLECTION_NAME).add(feedback)
                .addOnSuccessListener { callback() }
        }

        fun getAll(callback: (List<Feedback>) -> Unit) {
            val db = FirebaseFirestore.getInstance()

            db.collection(COLLECTION_NAME).orderBy("status").get()
                .addOnSuccessListener {
                    val feedbacks = mutableListOf<Feedback>()
                    for (doc in it.documents) {
                        feedbacks.add(doc.toObject(Feedback::class.java)!!)
                    }
                    callback(feedbacks.toList())
                }
        }

        fun deleteById(id: String, callback: () -> Unit) {
            val db = FirebaseFirestore.getInstance()

            db.collection(COLLECTION_NAME).document(id).delete()
                .addOnSuccessListener { callback() }
        }

        fun updateStatusSolved(id: String, callback: () -> Unit) {
            val db = FirebaseFirestore.getInstance()

            db.collection(COLLECTION_NAME).document(id).update("status", "SOLVED")
                .addOnSuccessListener { callback() }
        }
        fun deleteUser(id: String) {
            val db = FirebaseFirestore.getInstance()
            val collection=db.collection(COLLECTION_NAME)
            val feedbacks=db.collection(COLLECTION_NAME).whereEqualTo("userId",id)



            feedbacks.get().addOnSuccessListener { it ->
                for (a in it){
                    NotificationRepository.deleteByCommentId(a.id){}
                    collection.document(a.id).delete()
                }
            }

        }

    }
}