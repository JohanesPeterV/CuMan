package com.binus.cuman.repositories

import com.google.firebase.firestore.*
import com.binus.cuman.models.CurhatReaction

class CurhatReactionRepository {
    companion object {
        private val COLLECTION_NAME = "curhats"

        fun addLikeReaction(curhatId: String, type: CurhatReaction, callback: () -> Unit) {
            val db = FirebaseFirestore.getInstance()
            val curhatRef = db.collection(COLLECTION_NAME).document(curhatId)
            val userId = UserRepository.getCurrentUserId()

            db.runTransaction { transaction ->
                removeUserFromReactionList(transaction, userId.toString(), curhatRef)
                addUserToLikeList(transaction, curhatRef, userId.toString(), type)
            }.addOnSuccessListener {
                curhatRef.get().addOnSuccessListener {
                    it.reference.update("likeCount", getLikeCount(it))
                    it.reference.update("dislikeCount", getDislikeCount(it))
                    callback()
                }
            }
        }

        fun addDislikeReaction(curhatId: String, type: CurhatReaction, callback: () -> Unit) {
            val db = FirebaseFirestore.getInstance()
            val curhatRef = db.collection(COLLECTION_NAME).document(curhatId)
            val userId = UserRepository.getCurrentUserId()

            db.runTransaction { transaction ->
                removeUserFromReactionList(transaction, userId.toString(), curhatRef)
                addUserToDislikeList(transaction, curhatRef, userId.toString(), type)
            }.addOnSuccessListener {
                curhatRef.get().addOnSuccessListener {
                    it.reference.update("likeCount", getLikeCount(it))
                    it.reference.update("dislikeCount", getDislikeCount(it))
                    callback()
                }
            }
        }

        private fun addUserToLikeList(transaction: Transaction, curhatRef: DocumentReference, userId: String, type: CurhatReaction) {
            if (type == CurhatReaction.THUMB_UP) {
                transaction.update(curhatRef,"usersGiveThumbUp", FieldValue.arrayUnion(userId))
            } else if (type == CurhatReaction.COOL) {
                transaction.update(curhatRef,"usersGiveCool", FieldValue.arrayUnion(userId))
            } else  {
                transaction.update(curhatRef,"usersGiveLove", FieldValue.arrayUnion(userId))
            }
        }

        fun addUserToDislikeList(transaction: Transaction, curhatRef: DocumentReference, userId: String, type: CurhatReaction) {
            if (type == CurhatReaction.THUMB_DOWN) {
                transaction.update(curhatRef,"usersGiveThumbDowns", FieldValue.arrayUnion(userId))
            } else  {
                transaction.update(curhatRef,"usersGiveAngry", FieldValue.arrayUnion(userId))
            }
        }

        private fun removeUserFromReactionList(transaction: Transaction, userId: String, curhatRef: DocumentReference) {
            transaction.update(curhatRef,"usersGiveThumbUp", FieldValue.arrayRemove(userId))
            transaction.update(curhatRef,"usersGiveLove", FieldValue.arrayRemove(userId))
            transaction.update(curhatRef,"usersGiveCool", FieldValue.arrayRemove(userId))
            transaction.update(curhatRef,"usersGiveThumbDowns", FieldValue.arrayRemove(userId))
            transaction.update(curhatRef,"usersGiveAngry", FieldValue.arrayRemove(userId))
        }

        private fun getLikeCount(snap: DocumentSnapshot): Int {
            val thumbCount = (snap.get("usersGiveThumbUp") as List<String>).size
            val coolCount = (snap.get("usersGiveCool") as List<String>).size
            val loveCount = (snap.get("usersGiveLove") as List<String>).size
            return thumbCount + coolCount + loveCount
        }

        private fun getDislikeCount(snap: DocumentSnapshot): Int {
            val thumbDownCount = (snap.get("usersGiveThumbDowns") as List<String>).size
            val angryCount = (snap.get("usersGiveAngry") as List<String>).size
            return thumbDownCount + angryCount
        }
    }
}