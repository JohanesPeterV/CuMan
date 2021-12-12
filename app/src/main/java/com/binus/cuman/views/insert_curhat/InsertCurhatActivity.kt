package com.binus.cuman.views.insert_curhat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.binus.cuman.R
import com.binus.cuman.models.CurhatTopic
import com.binus.cuman.repositories.CurhatRepository
import com.binus.cuman.repositories.CurhatTopicRepository
import com.binus.cuman.views.TopicAutoCompleteAdapter

class InsertCurhatActivity : AppCompatActivity() {
    private lateinit var topicAutoCompleteView: AutoCompleteTextView
    private lateinit var topics: List<CurhatTopic>
    private val topicsString = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_curhat)

        setTopicAutocomplete()
        onAddCurhat()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    init {
        CurhatTopicRepository.getAll { topicsFromRepo ->
            topics = topicsFromRepo
            for (topic in topics) {
                topicsString.add(topic.name)
            }
        }
    }

    private fun setTopicAutocomplete() {
        topicAutoCompleteView = findViewById(R.id.update_topic_auto_complete)
        val adapter = TopicAutoCompleteAdapter(this, android.R.layout.simple_list_item_1, topicsString)
        topicAutoCompleteView.threshold = 1
        topicAutoCompleteView.setAdapter(adapter)
    }


    private fun onAddCurhat() {
        val addBtn: Button = findViewById(R.id.insert_feedback_btn)
        val content: TextView = findViewById(R.id.insert_feedback_content_edit_text)
        val anonymousCheckbox: CheckBox = findViewById(R.id.update_set_anonymous_checkbox)

        addBtn.setOnClickListener {
            if (content.text.isEmpty()) {
                content.error =getString(R.string.curhat_content_validation)
            } else if (topicAutoCompleteView.text.isEmpty()) {
                topicAutoCompleteView.error = getString(R.string.curhat_topic_validation)
            } else {
                addCurhat(content, anonymousCheckbox, it)
            }
        }
    }

    private fun addCurhat(
        content: TextView,
        anonymousCheckbox: CheckBox,
        it: View
    ) {
        val topicIndex = topicsString.indexOf(topicAutoCompleteView.text.toString())
        val isAnonymous = anonymousCheckbox.isChecked
        if (topicIndex == -1) {
            val newTopic = topicAutoCompleteView.text.toString()
            CurhatTopicRepository.addTopic(newTopic, callback = {newTopicId ->
                CurhatRepository.addCurhat(content.text.toString(), isAnonymous, newTopicId) {
                    moveToMainActivity()
                }
            })
        } else {
            CurhatRepository.addCurhat(content.text.toString(), isAnonymous, topics.get(topicIndex).id) {
                moveToMainActivity()
            }
        }
        Toast.makeText(it.context, getString(R.string.add_curhat_success), Toast.LENGTH_SHORT).show()

    }

    private fun moveToMainActivity() {
//        val intent = Intent(this, MainActivity::class.java)
//        startActivity(intent)
        finish()
    }
}