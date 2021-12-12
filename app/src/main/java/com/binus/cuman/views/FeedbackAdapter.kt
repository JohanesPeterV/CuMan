package com.binus.cuman.views

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.binus.cuman.R
import com.binus.cuman.databinding.FeedbackCardItemBinding
import com.binus.cuman.models.Feedback
import com.binus.cuman.repositories.FeedbackRepository
import com.binus.cuman.repositories.UserRepository
import com.binus.cuman.utils.CurhatViewUtil

class FeedbackAdapter : ListAdapter<Feedback, FeedbackAdapter.ViewHolder>(FeedbackDiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder (val binding: FeedbackCardItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(feedback: Feedback) {
            binding.feedbackContent.text = feedback.content
            binding.feedbackDate.text = CurhatViewUtil.formatDate(feedback.createdAt,binding.root.context)

            UserRepository.getUserById(feedback.userId) {
                if (it != null) {
                    binding.feedbackUserName.text = it.name
                }
            }

            setStatusChip(feedback.status)
            OnMarkSolvedBtnClicked(feedback)
            OnDeleteButtonClicked(feedback)
        }

        private fun OnDeleteButtonClicked(feedback: Feedback) {
            binding.deleteFeedbackBtn.setOnClickListener {
                val builder = AlertDialog.Builder(binding.root.context)
                builder.setMessage(binding.root.context.getString(R.string.sureq))
                    .setCancelable(false)
                    .setPositiveButton(binding.root.context.getString(R.string.yes)) { dialog, id ->
                        FeedbackRepository.deleteById(feedback.id) {
                            reloadActivity()
                            Toast.makeText(binding.root.context, binding.root.context.getString(R.string.toast_fsd), Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton(binding.root.context.getString(R.string.no)) { dialog, id ->
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
            }
        }

        private fun reloadActivity() {
            val activity = binding.root.context as Activity
            activity.finish()
            activity.overridePendingTransition(0 ,0)
            activity.startActivity(activity.intent)
            activity.overridePendingTransition(0 ,0)
        }

        private fun OnMarkSolvedBtnClicked(feedback: Feedback) {
            binding.feedbackMarkSolvedBtn.setOnClickListener {
                val builder = AlertDialog.Builder(binding.root.context)

                builder.setMessage(binding.root.context.getString(R.string.marked_solveq))
                    .setCancelable(false)
                    .setPositiveButton(binding.root.context.getString(R.string.yes)) { dialog, id ->
                        FeedbackRepository.updateStatusSolved(feedback.id) {
                            setStatusChip("SOLVED")
                            Toast.makeText(binding.root.context, binding.root.context.getString(R.string.marked_solved), Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton(binding.root.context.getString(R.string.no)) { dialog, id ->
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
            }
        }

        fun setStatusChip(status: String?) {
            if (status == "SOLVED") {
                binding.feedbackStatusChipPending.visibility = View.GONE
                binding.feedbackStatusChipSolved.visibility = View.VISIBLE
                binding.feedbackMarkSolvedBtn.visibility = View.INVISIBLE
            } else {
                binding.feedbackStatusChipPending.visibility = View.VISIBLE
                binding.feedbackStatusChipSolved.visibility = View.GONE
                binding.feedbackMarkSolvedBtn.visibility = View.VISIBLE
            }
        }


        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FeedbackCardItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}

object FeedbackDiffCallback : DiffUtil.ItemCallback<Feedback>() {
    override fun areItemsTheSame(oldItem: Feedback, newItem: Feedback): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Feedback, newItem: Feedback): Boolean {
        return oldItem.id == newItem.id
    }
}