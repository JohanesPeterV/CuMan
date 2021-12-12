package com.binus.cuman.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.binus.cuman.databinding.CurhatCardItemBinding
import com.binus.cuman.models.Curhat
import com.binus.cuman.repositories.CurhatRepository
import com.binus.cuman.repositories.UserRepository
import com.binus.cuman.utils.CurhatUtil
import com.binus.cuman.utils.CurhatViewUtil

class CurhatAdapter() : ListAdapter<Curhat, CurhatAdapter.ViewHolder>(CurhatDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val curhat = getItem(position)
        holder.bind(curhat)
    }

    class ViewHolder(var binding: CurhatCardItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(curhat: Curhat) {
            binding.curhatCardContent.text = CurhatViewUtil.trim(curhat.content)
            binding.curhatCardDate.text = CurhatViewUtil.formatDate(curhat.createdAt,binding.root.context)
            CurhatRepository.incrementViewCount(curhat.id)
            binding.curhatCardLikeCount.text = curhat.likeCount.toString()
            binding.curhatCardDislikeCount.text = curhat.dislikeCount.toString()
            setOnViewMoreListener(curhat.id)

            if (curhat.updatedAt?.nanoseconds != curhat.createdAt?.nanoseconds) {
                binding.curhatCardEdited.visibility = View.VISIBLE
            } else {
                binding.curhatCardEdited.visibility = View.GONE
            }

            UserRepository.getUserById(curhat.user) { user ->
                binding.curhatCardUsername.text = if (curhat.isAnonymous) "Anonymous" else user?.name
                CurhatViewUtil.setCurhatUserImage(curhat.isAnonymous, user!!, binding.curhatCardUserimage, binding.root)
            }

            CurhatViewUtil.setReactionBtnColor(
                binding.curhatCardThumbUpBtn,
                binding.curhatCardThumbDownBtn,
                curhat, binding.root)
            CurhatViewUtil.setLikePopupMenu(binding.curhatCardThumbUpBtn, binding.curhatCardThumbDownBtn, curhat, binding.root) {
                updateLikeDislikeCount(curhat.id)
            }
            CurhatViewUtil.setDislikePopupMenu(binding.curhatCardThumbUpBtn, binding.curhatCardThumbDownBtn, curhat, binding.root) {
                updateLikeDislikeCount(curhat.id)
            }

            binding.curhatCardInfoBtn.setOnClickListener {
                CurhatViewUtil.showCurhatInfoModal(curhat, binding.root.context)
            }
        }

        private fun updateLikeDislikeCount(curhatId: String) {
            CurhatRepository.getLikeDislikeCount(curhatId) { likeCount: Long, dislikeCount: Long ->

                binding.curhatCardLikeCount.text = likeCount.toString()
                binding.curhatCardDislikeCount.text = dislikeCount.toString()
            }
        }

        private fun setOnViewMoreListener(id: String) {
            binding.curhatCardViewBtn.setOnClickListener {
                CurhatUtil.moveToCurhatDetail(id, binding.root.context)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CurhatCardItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

object CurhatDiffCallback : DiffUtil.ItemCallback<Curhat>() {
    override fun areItemsTheSame(oldItem: Curhat, newItem: Curhat): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Curhat, newItem: Curhat): Boolean {
        return oldItem.id == newItem.id
    }
}

