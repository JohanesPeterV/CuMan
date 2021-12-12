package com.binus.cuman.views.user_profile

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.binus.cuman.GlideApp
import com.binus.cuman.R
import com.binus.cuman.models.User
import com.binus.cuman.repositories.UserRepository
import com.binus.cuman.utils.CurhatUtil
import com.binus.cuman.views.ProfileCurhatPostedAdapter
import com.binus.cuman.views.ProfileRepliedCurhatAdapter
import com.binus.cuman.views.login.LoginActivity
import com.binus.cuman.views.update_password.UpdatePasswordActivity
import com.binus.cuman.views.update_profile.UpdateProfileActivity
import java.io.IOException
import java.lang.Exception
import java.util.*


class UserProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var imageView: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_user_profile, container, false)
        val viewModel = UserProfileViewModel()
        imageView= rootView.findViewById(R.id.user_profile_image_view)

        try {
            val storageReference=FirebaseStorage.getInstance().getReferenceFromUrl(viewModel
                .currUser?.photoUrl.toString())

            GlideApp.with(this)
                .load(storageReference)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView)

        }catch(e: Exception) {

        }

        val curhatAdapter = ProfileCurhatPostedAdapter()
        val curhatRecyclerView: RecyclerView = rootView.findViewById(R.id.recent_post_rview)
        curhatRecyclerView.adapter = curhatAdapter
        curhatRecyclerView.layoutManager = object : LinearLayoutManager(rootView.context) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        val swipeContainer = rootView.findViewById<SwipeRefreshLayout>(R.id.swipeContainer)
        swipeContainer.setOnRefreshListener {
            viewModel.getRecentCurhats()
            viewModel.getRecentReplies()
            swipeContainer.isRefreshing = false
        }

        val replyAdapter = ProfileRepliedCurhatAdapter()
        val replyRecyclerView: RecyclerView = rootView.findViewById(R.id.recent_reply_rview)
        replyRecyclerView.adapter = replyAdapter
        replyRecyclerView.layoutManager = object : LinearLayoutManager(rootView.context) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }



        val nameView : TextView = rootView.findViewById(R.id.user_profile_name)
        val emailView : TextView = rootView.findViewById(R.id.user_profile_email)
        val genderView : TextView = rootView.findViewById(R.id.up_gender)
        val ageView : TextView = rootView.findViewById(R.id.up_age)
        val joinedAtView: TextView = rootView.findViewById(R.id.user_profile_joined_at)
        val noCurhatPosted: TextView = rootView.findViewById(R.id.profile_no_curhat_posted)
        val noReplyPosted: TextView = rootView.findViewById(R.id.profile_no_reply_posted)
        


        UserRepository.getUserById(FirebaseAuth.getInstance().currentUser?.uid.toString()).addSnapshotListener { value, error ->
            try{
                val user=value?.toObject(User::class.java)
                nameView.text=user?.name
                emailView.text=user?.email
                joinedAtView.text=CurhatUtil.formatDate(user?.joinedAt,requireContext())
                if(user?.gender.equals("Male")) genderView.text=getString(R.string.male)
                else genderView.text=getString(R.string.female)
                ageView.text ="("+ AgeCalculatorUtil.calculateAge(user?.dateOfBirth?.toDate()!!).toString()+" "+getString(R.string.year)

            }catch (exception:Exception){


            }

        }

        imageView.setOnClickListener{
            launchGallery()
        }

        val curhatCountView : TextView = rootView.findViewById(R.id.total_post)
        val totalPostObserver = Observer<Int>{totalPostCount->
            curhatCountView.setText(""+ totalPostCount + " "+ getText(R.string.curhats_posted))
        }

        val replyCountView : TextView = rootView.findViewById(R.id.total_reply)

        val totalReplyObserver = Observer<Int>{totalReplyCount->
            replyCountView.setText(""+ totalReplyCount + " "+getText(R.string.replies_posted))
        }


        viewModel.curhatCount.observe(viewLifecycleOwner,totalPostObserver)
        viewModel.replyCount.observe(viewLifecycleOwner,totalReplyObserver)

        val passwordButton: Button = rootView.findViewById(R.id.user_cp_button)

        passwordButton.setOnClickListener {
            val intent = Intent(this.activity, UpdatePasswordActivity::class.java)
            startActivity(intent)
        }

        val updateButton: Button = rootView.findViewById(R.id.user_uprof_btn)

        updateButton.setOnClickListener {
            val intent = Intent(this.activity, UpdateProfileActivity::class.java)
            startActivity(intent)
        }

        val logOutButton: Button = rootView.findViewById(R.id.log_out_button)

        logOutButton.setOnClickListener {
            AuthUI.getInstance()
                    .signOut(this.requireActivity())
                    .addOnCompleteListener {
                        val intent  = Intent(this.activity, LoginActivity::class.java)
                        startActivity(intent)
                        this.activity?.finish()
                    }
        }

        viewModel.recentCurhats.observe(viewLifecycleOwner, Observer {curhats ->
            if (curhats.isEmpty()) {
                noCurhatPosted.visibility = View.VISIBLE
            } else {
                noCurhatPosted.visibility = View.INVISIBLE
            }
            curhatAdapter.submitList(curhats)
        })

        viewModel.recentReplies.observe(viewLifecycleOwner, Observer {replies ->
            if (replies.isEmpty()) {
                noReplyPosted.visibility = View.VISIBLE
            } else {
                noReplyPosted.visibility = View.INVISIBLE
            }

            replyAdapter.submitList(replies)
        })




        return rootView

    }


    val PICK_IMAGE_REQUEST = 71
    var filePath :Uri?=null
    var firebaseStore: FirebaseStorage? = FirebaseStorage.getInstance()
    var storageReference: StorageReference? = FirebaseStorage.getInstance().reference

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }
            filePath = data.data
            try {

                val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, filePath)
                imageView.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            uploadImage()
        }
    }
    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)

    }
    private fun uploadImage(){
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            val progressDialog = ProgressDialog(activity)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            val ref = storageReference?.child(
                    "imageUploads/"
                            + FirebaseAuth.getInstance().currentUser?.uid
                )





            val temp=ref?.putFile(filePath!!)?.addOnSuccessListener { task->
                progressDialog.dismiss()
                task.storage.downloadUrl.addOnSuccessListener {uri->
                    ref.downloadUrl.addOnSuccessListener {uri->
                        UserRepository.updateProfileImage(uri)
                    }
                }
                Toast
                    .makeText(
                        this.activity,
                        getString(R.string.toast_img_up),
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }
                ?.addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast
                        .makeText(
                            this.activity,
                            "Failed " + e.message,
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }?.addOnProgressListener(OnProgressListener<UploadTask.TaskSnapshot?>() {
                    fun onProgress(
                        taskSnapshot: UploadTask.TaskSnapshot
                    ) {
                        val progress = (100.0
                                * taskSnapshot.bytesTransferred
                                / taskSnapshot.totalByteCount)
                        progressDialog.setMessage(
                            "Uploaded "
                                    + progress.toInt() + "%"
                        )

                    }
                })
            }

        }

    }

