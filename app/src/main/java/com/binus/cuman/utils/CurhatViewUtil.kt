package com.binus.cuman.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import com.binus.cuman.GlideApp
import com.binus.cuman.R
import com.binus.cuman.databinding.CurhatInfoPopupBinding
import com.binus.cuman.models.Curhat
import com.binus.cuman.models.CurhatReaction
import com.binus.cuman.models.User
import com.binus.cuman.repositories.*
import java.lang.Exception
import java.text.SimpleDateFormat

class CurhatViewUtil {
    companion object {
        fun formatDate(timestamp: Timestamp?, context: Context) : String {



            val sdf = SimpleDateFormat("dd MMM yyyy '"+context.getString(R.string.at)+"' HH:mm")
            var formatted: String = ""

            if (timestamp != null) {
                formatted = sdf.format(timestamp?.toDate())
            }
            return formatted
        }

        @SuppressLint("RestrictedApi")
        fun setDislikePopupMenu(likeBtn: ImageButton, dislikeBtn: ImageButton, curhat: Curhat, view: View, callback: () -> Unit) {
            val userId = UserRepository.getCurrentUserId()
            dislikeBtn.setOnClickListener {
                val popupMenu = PopupMenu(view.context, it)
                popupMenu.inflate(R.menu.dislike_curhat_menu_items)

                popupMenu.setOnMenuItemClickListener {
                    it.isChecked = true
                    when (it.itemId) {
                        R.id.dislike_curhat_thumb_down -> {
                            CurhatReactionRepository.addDislikeReaction(curhat.id, CurhatReaction.THUMB_DOWN) {
                                val resetedCurhat = resetReactionList(curhat)
                                resetedCurhat.usersGiveThumbDowns = addUserIdToReactionList(resetedCurhat.usersGiveThumbDowns!!, userId)
                                setReactionBtnColor(likeBtn, dislikeBtn, curhat, view)
                                Toast.makeText(view.context, view.context.getString(R.string.toast_curhat_dl), Toast.LENGTH_SHORT).show()
                                callback()
                            }
                            true
                        }
                        R.id.dislike_curhat_angry -> {
                            CurhatReactionRepository.addDislikeReaction(curhat.id, CurhatReaction.ANGRY) {
                                val resetedCurhat = resetReactionList(curhat)
                                resetedCurhat.usersGiveAngry = addUserIdToReactionList(resetedCurhat.usersGiveAngry!!, userId)
                                setReactionBtnColor(likeBtn, dislikeBtn, curhat, view)
                                Toast.makeText(view.context, view.context.getString(R.string.toast_curhat_dl), Toast.LENGTH_SHORT).show()
                                callback()
                            }
                            true
                        }
                        else -> false
                    }
                }

                val popupHelper = MenuPopupHelper(view.context, popupMenu.menu as MenuBuilder, it)
                popupHelper.setForceShowIcon(true)
                popupHelper.show()
            }
        }

        @SuppressLint("RestrictedApi", "ResourceAsColor")
        fun setLikePopupMenu(likeBtn: ImageButton, dislikeBtn: ImageButton, curhat: Curhat, view: View, callback: () -> Unit) {
            val userId = UserRepository.getCurrentUserId()
            likeBtn.setOnClickListener {
                val popupMenu = PopupMenu(view.context, it)
                popupMenu.inflate(R.menu.like_curhat_menu_items)

                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.like_curhat_thumb_up -> {
                            CurhatReactionRepository.addLikeReaction(curhat.id, CurhatReaction.THUMB_UP) {
                                val resetedCurhat = resetReactionList(curhat)
                                resetedCurhat.usersGiveThumbUp = addUserIdToReactionList(resetedCurhat.usersGiveThumbUp!!, userId)
                                setReactionBtnColor(likeBtn, dislikeBtn, curhat, view)
                                Toast.makeText(view.context, view.context.getString(R.string.toast_curhat_l), Toast.LENGTH_SHORT).show()
                                callback()
                            }
                            true
                        }
                        R.id.like_curhat_cool -> {
                            val resetedCurhat = resetReactionList(curhat)
                            resetedCurhat.usersGiveCool = addUserIdToReactionList(resetedCurhat.usersGiveCool!!, userId)
                            CurhatReactionRepository.addLikeReaction(curhat.id, CurhatReaction.COOL) {
                                setReactionBtnColor(likeBtn, dislikeBtn, curhat, view)
                                Toast.makeText(view.context, view.context.getString(R.string.toast_curhat_l), Toast.LENGTH_SHORT).show()
                                callback()
                            }
                            true
                        }
                        R.id.like_curhat_love -> {
                            val resetedCurhat = resetReactionList(curhat)
                            resetedCurhat.usersGiveLove = addUserIdToReactionList(resetedCurhat.usersGiveLove!!, userId)
                            CurhatReactionRepository.addLikeReaction(curhat.id, CurhatReaction.LOVE) {
                                setReactionBtnColor(likeBtn, dislikeBtn, curhat, view)
                                Toast.makeText(view.context, view.context.getString(R.string.toast_curhat_l), Toast.LENGTH_SHORT).show()
                                callback()
                            }
                            true
                        }

                        else -> false
                    }
                }

                val popupHelper = MenuPopupHelper(view.context, popupMenu.menu as MenuBuilder, it)
                popupHelper.setForceShowIcon(true)
                popupHelper.show()
            }
        }

        fun setReactionBtnColor(likeBtn: ImageButton, dislikeBtn: ImageButton, curhat: Curhat, view: View) {
            if (curhat == null) return
            val userId = UserRepository.getCurrentUserId()

            resetReactionBtnColor(likeBtn, dislikeBtn)

            if (
                curhat.usersGiveThumbUp?.contains(userId)!! ||
                curhat.usersGiveCool?.contains(userId)!! ||
                curhat.usersGiveLove?.contains(userId)!!
            ) {
                likeBtn.setColorFilter(
                    ContextCompat.getColor(view.context, R.color.green),
                    android.graphics.PorterDuff.Mode.MULTIPLY
                )
            } else if (
                curhat.usersGiveThumbDowns?.contains(userId)!! ||
                curhat.usersGiveAngry?.contains(userId)!!
            ) {
                dislikeBtn.setColorFilter(
                    ContextCompat.getColor(view.context, R.color.dark_red),
                    android.graphics.PorterDuff.Mode.MULTIPLY
                )
            }
        }

        private fun resetReactionBtnColor(likeBtn: ImageButton, dislikeBtn: ImageButton) {
            likeBtn.clearColorFilter()
            dislikeBtn.clearColorFilter()
        }

        private fun addUserIdToReactionList(reactionList: List<String>, userId: String): List<String> {
            val mutableList = reactionList.toMutableList()
            mutableList.add(userId)
            return mutableList.toList()
        }

        private fun resetReactionList(curhat: Curhat): Curhat {
            curhat.usersGiveThumbDowns = listOf()
            curhat.usersGiveAngry = listOf()
            curhat.usersGiveThumbUp = listOf()
            curhat.usersGiveLove = listOf()
            curhat.usersGiveCool = listOf()
            return curhat
        }

        fun setCurhatUserImage(isAnon: Boolean, user: User, imageView: ImageView, view: View) {
            if (!isAnon) {
                setUserImage(imageView, view, user.profileImageId!!)
            } else {
                val isMale = user.gender == "Male"
                setAnonymousImage(imageView, view, isMale)
            }
        }

        fun setUserImage(imageView: ImageView, view: View, profileImageUrl: String){
            try {
                val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(profileImageUrl)
                GlideApp.with(view)
                    .load(storageReference)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.baseline_account_circle_24)
                    .into(imageView)

            }catch(e: Exception) {
            }
        }

        fun setAnonymousImage(imageView: ImageView, view: View, isMale: Boolean) {
            val image = if (isMale) R.drawable.ic_profile_male else R.drawable.ic_profile_female
            GlideApp.with(view).load(image).into(imageView)
        }

        fun showCurhatInfoModal(curhat: Curhat, context: Context) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val dialogBinding = CurhatInfoPopupBinding.inflate(inflater)

            CurhatTopicRepository.get(curhat.topic) {
                dialogBinding.curhatInfoTopic.text = it.name
            }

            CurhatRepository.getById(curhat.id) { curhat ->
                dialogBinding.curhatInfoAngryCount.text = curhat.usersGiveAngry?.size.toString()
                dialogBinding.curhatInfoThumbsDownCount.text = curhat.usersGiveThumbDowns?.size.toString()
                dialogBinding.curhatInfoThumbUpCount.text = curhat.usersGiveThumbUp?.size.toString()
                dialogBinding.curhatInfoCoolCount.text = curhat.usersGiveCool?.size.toString()
                dialogBinding.curhatInfoLoveCount.text = curhat.usersGiveLove?.size.toString()
                dialogBinding.curhatInfoViewCount.text = curhat.viewCount.toString()
                CurhatCommentRepository.getCommentsByCurhatId(curhat.id) { comments ->
                    dialogBinding.curhatInfoCommentCount.text = comments?.size.toString()
                }
            }

            val dialog = AlertDialog.Builder(context)
            dialog.setView(dialogBinding.root)
            dialog.setTitle(context.getString(R.string.aditional_info))
            dialog.show()
        }

        fun trim(content: String): String {
            if (content.length > 80) return "${content.substring(0, 75)}..."
            return content
        }
    }
}