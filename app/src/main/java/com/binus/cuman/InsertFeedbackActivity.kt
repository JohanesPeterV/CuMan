package com.binus.cuman

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.binus.cuman.databinding.ActivityInsertFeedbackBinding
import com.binus.cuman.repositories.FeedbackRepository

class InsertFeedbackActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInsertFeedbackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_insert_feedback)

        setInsertFeedbackBtn()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setInsertFeedbackBtn() {
        binding.insertFeedbackBtn.setOnClickListener {
            val content = binding.insertFeedbackContentEditText.text.toString()

            if (binding.insertFeedbackContentEditText.text.isEmpty()) {
                binding.insertFeedbackContentEditText.error = getString(R.string.feedback_mnbe)
            } else {
                FeedbackRepository.addFeedback(content) {
                    showSuccessToast()
                    finish()
                }
            }
        }
    }

    private fun showSuccessToast() {
        Toast.makeText(this, this.getString(R.string.toast_feedback_s), Toast.LENGTH_SHORT).show()
    }

}