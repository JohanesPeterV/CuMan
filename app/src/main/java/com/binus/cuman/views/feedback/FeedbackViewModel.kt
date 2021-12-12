package com.binus.cuman.views.feedback

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.binus.cuman.models.Feedback
import com.binus.cuman.repositories.FeedbackRepository

class FeedbackViewModel: ViewModel() {
    private var _feedbacks = MutableLiveData<List<Feedback>>().apply {
        value = listOf()
    }
    val feedbacks: LiveData<List<Feedback>> get() = _feedbacks

    init {
        FeedbackRepository.getAll {
            _feedbacks.value = it
        }
    }
}