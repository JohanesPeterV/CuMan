package com.binus.cuman.views.feedback

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.binus.cuman.R
import com.binus.cuman.views.FeedbackAdapter

class FeedbackActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        val viewModel = FeedbackViewModel()
        val adapter = FeedbackAdapter()
        val recycler = findViewById<RecyclerView>(R.id.feedback_recycler)
        recycler.adapter = adapter
        val feedbackEmptyTv: TextView = findViewById(R.id.feedback_empty)

        viewModel.feedbacks.observe(this, Observer {
            adapter.submitList(it)

            if (it.isEmpty()) {
                feedbackEmptyTv.visibility = View.VISIBLE
            } else {
                feedbackEmptyTv.visibility = View.GONE
            }
        })


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}