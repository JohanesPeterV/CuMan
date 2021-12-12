package com.binus.cuman.views.curhat_detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.binus.cuman.R
import com.binus.cuman.views.CurhatCommentAdapter

class CurhatDetailActivity : AppCompatActivity() {
    private lateinit var viewModel: CurhatDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_curhat_detail)

        viewModel = CurhatDetailViewModel()
        val adapter = CurhatCommentAdapter {
            viewModel.showMoreComments()
        }

        val progressIndicator: LinearProgressIndicator = findViewById(R.id.detailCurhatLoadIndicator)
        val recyclerView: RecyclerView = findViewById(R.id.curhat_comment_recycler)
        recyclerView.adapter = adapter

        viewModel.getCurhatDetail(intent) {
            finish()
        }

        val addBtn: Button = findViewById(R.id.add_comment_btn)
        val addCommentTv: TextView = findViewById(R.id.add_comment_edit_text)
        addBtn.setOnClickListener {
            if (addCommentTv.text.isEmpty()) {
                addCommentTv.error = getString(R.string.add_comment_val)
            } else {
                viewModel.addComment(addCommentTv)
            }
        }

        viewModel.comments.observe(this , Observer { comments ->
            adapter.addHeaderAndSubmitList(viewModel.curhat, comments)
        })

        viewModel.isFetchingData.observe(this, Observer { isFetchingData ->
            if (!isFetchingData) {
                progressIndicator.visibility = View.GONE
            } else {
                progressIndicator.visibility = View.VISIBLE
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

    override fun onResume() {
        super.onResume()
        viewModel.getCurhatDetail(intent) {}
    }
}