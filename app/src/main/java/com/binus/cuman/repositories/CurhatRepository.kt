package com.binus.cuman.repositories

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import java.util.*
import com.binus.cuman.models.*
import java.time.LocalDateTime
import java.time.OffsetDateTime
import kotlin.collections.HashMap


class CurhatRepository {
    companion object {
        private val COLLECTION_NAME = "curhats"

        fun getNewestCurhat(curhat: Curhat?, callback: (List<Curhat>) -> Unit) {
            val db = FirebaseFirestore.getInstance()

            if (curhat == null) {
                db.collection(COLLECTION_NAME)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .orderBy("viewCount", Query.Direction.DESCENDING)
                    .limit(5).get()
                    .addOnSuccessListener { curhatDocs ->
                        val curhats = mutableListOf<Curhat>()
                        for (curhatDoc in curhatDocs) {
                            val curhat = curhatDoc.toObject(Curhat::class.java)
                            curhat.id = curhatDoc.id
                            curhats.add(curhat)
                        }
                        callback(curhats)
                    }
            } else {
                db.collection(COLLECTION_NAME)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .orderBy("viewCount", Query.Direction.DESCENDING)
                    .startAfter(curhat.createdAt, curhat.viewCount).limit(5).get()
                    .addOnSuccessListener { curhatDocs ->
                        val curhats = mutableListOf<Curhat>()
                        for (curhatDoc in curhatDocs) {
                            val curhat = curhatDoc.toObject(Curhat::class.java)
                            curhat.id = curhatDoc.id
                            curhats.add(curhat)
                        }
                        callback(curhats)
                    }
            }
        }

        fun getHottestCurhat(curhat: Curhat?, callback: (List<Curhat>) -> Unit) {
            val db = FirebaseFirestore.getInstance()


            if (curhat == null) {

                db.collection(COLLECTION_NAME)
                    .orderBy("viewCount", Query.Direction.DESCENDING)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .limit(5).get()
                    .addOnSuccessListener { curhatDocs ->

                        val curhats = mutableListOf<Curhat>()
                        for (curhatDoc in curhatDocs) {
                            val curhat = curhatDoc.toObject(Curhat::class.java)
                            curhat.id = curhatDoc.id
                            curhats.add(curhat)
                        }
                        callback(curhats)
                    }
            } else {
                db.collection(COLLECTION_NAME)
                    .orderBy("viewCount", Query.Direction.DESCENDING)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .startAfter(curhat.viewCount).limit(5)
                    .get()
                    .addOnSuccessListener { curhatDocs ->
                        val curhats = mutableListOf<Curhat>()
                        for (curhatDoc in curhatDocs) {
                            val curhat = curhatDoc.toObject(Curhat::class.java)
                            curhat.id = curhatDoc.id
                            curhats.add(curhat)
                        }
                        callback(curhats)
                    }
            }
        }

        fun addCurhat(content: String, isAnon: Boolean, topicId: String, callback: () -> Unit) {
            val db = FirebaseFirestore.getInstance()

            val curhat =
                Curhat(
                    "",
                    topicId,
                    UserRepository.getCurrentUserId().toString(),
                    content,
                    isAnonymous = isAnon,
                    createdAt = Timestamp.now(),
                    updatedAt = Timestamp.now()
                )
            db.collection(COLLECTION_NAME).add(curhat)
                .addOnSuccessListener { callback() }
        }

        fun getById(curhatId: String, callback: (Curhat) -> Unit) {
            val db = FirebaseFirestore.getInstance()

            db.collection(COLLECTION_NAME).document(curhatId).get()
                .addOnSuccessListener { curhatDoc ->
                    Log.i("CurhatRepo", curhatDoc.toString())
                    val curhat = curhatDoc.toObject(Curhat::class.java)
                    if (curhat != null) {
                        callback(curhat)
                    } else {
                        callback(Curhat())
                    }
                }
        }

        fun getByTopic(topicId: String, callback: (List<Curhat>) -> Unit) {
            val db = FirebaseFirestore.getInstance()

            db.collection(COLLECTION_NAME).whereEqualTo("topic", topicId).get()
                .addOnSuccessListener { curhatDocs ->
                    val curhats = mutableListOf<Curhat>()
                    for (curhatDoc in curhatDocs) {
                        val curhat = curhatDoc.toObject(Curhat::class.java)
                        curhat.id = curhatDoc.id
                        curhats.add(curhat)
                    }
                    callback(curhats.toList())
                }
        }

        fun getCurhatBySearch(searchString: String,mostLike: Boolean): Query {
            val db = FirebaseFirestore.getInstance()
            if(mostLike)return db.collection(COLLECTION_NAME).orderBy("likeCount",Query.Direction.DESCENDING)
            return db.collection(COLLECTION_NAME).orderBy("dislikeCount",Query.Direction.DESCENDING)
        }
      
        @RequiresApi(Build.VERSION_CODES.O)
        fun getCurhatBySearch(searchString: String, timeType: Int): Query {
            val db = FirebaseFirestore.getInstance()

            val nowDate=Timestamp.now()
            var date: Timestamp

            if(timeType==0){
                date = Timestamp(nowDate.seconds-86400,nowDate.nanoseconds)
            }
            else if(timeType==1){
                date = Timestamp(nowDate.seconds-604800,nowDate.nanoseconds)
            }
            else if(timeType==2){

                val now = Timestamp.now()
                val nowInstant = now.toDate().toInstant()
                val zoneId = TimeZone.getDefault().toZoneId()

                val time = LocalDateTime.ofInstant(nowInstant, zoneId)
                    .minusMonths(1)
                    .toInstant(OffsetDateTime.ofInstant(nowInstant, zoneId).offset).let {
                        Timestamp(Date.from(it))
                    }

                date = time
            }else{
                val now = Timestamp.now()
                val nowInstant = now.toDate().toInstant()
                val zoneId = TimeZone.getDefault().toZoneId()

                val time = LocalDateTime.ofInstant(nowInstant, zoneId)
                    .minusYears(1)
                    .toInstant(OffsetDateTime.ofInstant(nowInstant, zoneId).offset).let {
                        Timestamp(Date.from(it))
                    }

                date = time

            }

            return db.collection(COLLECTION_NAME).whereGreaterThan("createdAt",date)

        }

        fun update(curhatId: String, curhat: HashMap<String, Any>, callback: () -> Unit) {
            val db = FirebaseFirestore.getInstance()
            db.collection(COLLECTION_NAME).document(curhatId)
                .update(
                    "content",
                    curhat.get("content"),
                    "topic",
                    curhat.get("topic"),
                    "anonymous",
                    curhat.get("anonymous"),
                    "updatedAt",
                    Timestamp.now()
                )
                .addOnSuccessListener {
                    callback()
                }
        }

        fun deleteById(curhatId: String, callback: () -> Unit) {
            val db = FirebaseFirestore.getInstance()

            db.collection(COLLECTION_NAME).document(curhatId).delete()
                .addOnSuccessListener {
                    CurhatCommentRepository.deleteAllById(curhatId) {
                        callback()
                    }
                }
        }

        fun incrementCommentCount(curhatId: String) {
            val db = FirebaseFirestore.getInstance()
            db.collection(COLLECTION_NAME).document(curhatId)
                .update("commentCount", FieldValue.increment(1))
        }

        fun decrementCommentCount(curhatId: String) {
            val db = FirebaseFirestore.getInstance()
            db.collection(COLLECTION_NAME).document(curhatId)
                .update("commentCount", FieldValue.increment(-1))
        }

        fun incrementViewCount(curhatId: String) {
            val db = FirebaseFirestore.getInstance()
            db.collection(COLLECTION_NAME).document(curhatId)
                .update("viewCount", FieldValue.increment(1))
        }

        fun countUserPost(id: String): Query {
            val db = FirebaseFirestore.getInstance()
            val curhats = db.collection(COLLECTION_NAME).whereEqualTo("user", id)
            return curhats
        }

        fun userProfilePost(id: String): Query {
            val db = FirebaseFirestore.getInstance()
            val curhats = db.collection(COLLECTION_NAME).whereEqualTo("user", id)
                .orderBy("createdAt", Query.Direction.DESCENDING).limit(3)
            return curhats
        }

        fun getLikeDislikeCount(id: String, callback: (Long, Long) -> Unit) {
            val db = FirebaseFirestore.getInstance()
            db.collection(COLLECTION_NAME).document(id).get()
                .addOnSuccessListener {
                    val (likeCount, dislikeCount) = Pair(it.get("likeCount"), it.get("dislikeCount"))
                    callback(likeCount as Long, dislikeCount as Long)
                }
        }

        fun deleteUser(userId: String){
            val db = FirebaseFirestore.getInstance()
            val collection = db.collection(COLLECTION_NAME)
            val curhats = db.collection(COLLECTION_NAME).whereEqualTo("user", userId)

            curhats.get().addOnSuccessListener { it ->
                for (a in it){
                    CurhatCommentRepository.deleteAllById(a.id) {}
                    collection.document(a.id).delete()
                }
                db.collection(COLLECTION_NAME).document(userId).delete()


            }

        }
    }

}

