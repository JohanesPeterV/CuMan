package com.binus.cuman.views.curhat_by_topic

import android.app.Application
import android.widget.AutoCompleteTextView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.binus.cuman.models.Curhat
import com.binus.cuman.models.CurhatTopic
import com.binus.cuman.repositories.CurhatRepository
import com.binus.cuman.repositories.CurhatTopicRepository
import com.binus.cuman.views.TopicAutoCompleteAdapter


class CurhatByTopicViewModel
    (application: Application) : AndroidViewModel(application) {
    private var _shuffledTopics: MutableLiveData<List<CurhatTopic>> =
        MutableLiveData<List<CurhatTopic>>().apply {
            value = listOf()
        }
    val topics: LiveData<List<CurhatTopic>> get() = _shuffledTopics

    private val topicsString = mutableListOf<String>()
    private var _topics = listOf<CurhatTopic>()

    private var _filteredCurhats: MutableLiveData<List<Curhat>> =
        MutableLiveData<List<Curhat>>().apply {  value = listOf() }
    val filteredCurhats: LiveData<List<Curhat>> get() = _filteredCurhats

    private var _isSizeZero = MutableLiveData<Boolean>().apply { value = true }
    val isSizeZero: LiveData<Boolean> get() = _isSizeZero

    init {
        CurhatTopicRepository.getAll { topics ->
            _shuffledTopics.value = topics.shuffled().take(3)
            _topics = topics
            for (topic in topics) {
                topicsString.add(topic.name)
            }
        }
    }

    fun setTopicAutocomplete(view: AutoCompleteTextView) {
        val adapter = TopicAutoCompleteAdapter(view.context, android.R.layout.simple_list_item_1, topicsString)
        view.threshold = 1
        view.setAdapter(adapter)

        view.setOnItemClickListener { parent, _, position, id ->
            val selectedTopic = parent.adapter.getItem(position) as String
            view.setText(selectedTopic)

        }
    }

    fun updateTopicList(view: AutoCompleteTextView) {
            CurhatTopicRepository.getAll {
                _topics = it
                topicsString.clear()
                for (topic in it) {
                    topicsString.add(topic.name)
                }
                view.setAdapter(null)
                val adapter = TopicAutoCompleteAdapter(view.context, android.R.layout.simple_list_item_1, topicsString)
                view.setAdapter(adapter)
            }
    }

    fun OnFilter(topicName: String) {
        val index = topicsString.indexOf(topicName)
        _isSizeZero.value = false
        if (index != -1) {
            CurhatRepository.getByTopic(_topics.get(index).id) { curhats ->
                _isSizeZero.value = curhats.isEmpty()
                _filteredCurhats.value = curhats
                for (curhat in _filteredCurhats.value!!) {

                }

            }
        }
    }
}