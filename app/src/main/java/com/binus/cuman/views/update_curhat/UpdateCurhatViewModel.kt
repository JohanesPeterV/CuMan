package com.binus.cuman.views.update_curhat

import android.R
import android.content.Intent
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.binus.cuman.models.Curhat
import com.binus.cuman.models.CurhatTopic
import com.binus.cuman.repositories.CurhatRepository
import com.binus.cuman.repositories.CurhatTopicRepository
import com.binus.cuman.views.TopicAutoCompleteAdapter

class UpdateCurhatViewModel : ViewModel() {
    private var _curhat: MutableLiveData<Curhat> =
        MutableLiveData<Curhat>().apply { value = Curhat() }
    val curhat: LiveData<Curhat> get() = _curhat
    private lateinit var curhatId: String

    private lateinit var topics: List<CurhatTopic>
    private val topicsString = mutableListOf<String>()

    var currentTopicName: String = ""

    init {
        CurhatTopicRepository.getAll { topicsFromRepo ->
            topics = topicsFromRepo
            for (topic in topics) {
                topicsString.add(topic.name)
            }
        }
    }

    fun getCurhat(intent: Intent) {
        curhatId = getCurhatIdFromBundle(intent)

        CurhatRepository.getById(curhatId) {
            val retrievedCurhat = it
            CurhatTopicRepository.get(it.topic) {
                currentTopicName = it.name
                _curhat.value = retrievedCurhat
            }
        }
    }

    fun getCurhatIdFromBundle(intent: Intent): String {
        val bundle = intent.extras
        val id = bundle!!.getString("curhatId")
        return id!!
    }

    fun setTopicAutocomplete(topicTv: AutoCompleteTextView) {
        val adapter = TopicAutoCompleteAdapter(topicTv.context, R.layout.simple_list_item_1, topicsString)
        topicTv.threshold = 1
        topicTv.setAdapter(adapter)
    }

    fun onUpdate(content: EditText, topic: String, isAnon: Boolean, callback: () -> Unit) {
        if (content.text.isEmpty()) {
            content.error = "Content must not be empty"
            return
        }

        getTopicId(topic) { newTopicId ->
            val curhat = hashMapOf<String,Any>(
                "topic" to newTopicId,
                "content" to content.text.toString(),
                "anonymous" to isAnon
            )
            CurhatRepository.update(curhatId, curhat) {
                callback()
            }
        }
    }

    fun getTopicId(topicName: String, callback: (String) -> Unit) {
        if (topicName.length <= 0) {
            val index = topicsString.indexOf(currentTopicName)
            callback(topics.get(index).id)
        } else {
            val index = topicsString.indexOf(topicName)
            if (index == -1) {
                CurhatTopicRepository.addTopic(topicName) {topicId ->
                    callback(topicId)
                }
            } else {
                callback(topics.get(index).id)
            }
        }
    }
}