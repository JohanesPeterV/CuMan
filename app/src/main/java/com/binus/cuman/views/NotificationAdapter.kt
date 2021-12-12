package com.binus.cuman.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.binus.cuman.databinding.NotificationItemBinding
import com.binus.cuman.models.CurhatComment
import com.binus.cuman.models.Notification
import com.binus.cuman.repositories.CurhatCommentRepository
import com.binus.cuman.repositories.UserRepository
import com.binus.cuman.utils.CurhatUtil
import com.binus.cuman.utils.CurhatViewUtil

class NotificationAdapter: ListAdapter<Notification, NotificationAdapter.ViewHolder> (NotificationDiffCallback)  {

    class ViewHolder(var binding: NotificationItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(notif: Notification) {
            notif.commentId?.let {
                binding.notificationDate.text = CurhatViewUtil.formatDate(notif.createdAt,binding.root.context)
                CurhatCommentRepository.getComment(it).addOnSuccessListener {
                    val comment = it.toObject(CurhatComment::class.java)
                    binding.notificationComment.text = "'${comment?.content}'"

                    if (comment != null) {
                        UserRepository.getUserById(comment.user) {user ->
                            binding.notificationUsername.text = user?.name
                            CurhatViewUtil.setCurhatUserImage(false, user!!, binding.notificationUserImage, binding.root)
                        }

                        binding.notifViewBtn.setOnClickListener {
                            CurhatUtil.moveToCurhatDetail(comment.curhatId, it.context)
                        }
                    }
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = NotificationItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

object NotificationDiffCallback : DiffUtil.ItemCallback<Notification>() {
    override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem.id == newItem.id
    }
}

