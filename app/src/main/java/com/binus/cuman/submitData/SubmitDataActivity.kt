
package com.binus.cuman.submitData



import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.binus.cuman.GlideApp
import com.binus.cuman.R
import com.binus.cuman.repositories.UserRepository
import java.io.IOException
import java.util.*


class SubmitDataActivity : AppCompatActivity() {
    val viewModel: SubmitDataActivityViewModel = SubmitDataActivityViewModel()
    lateinit var imageView: ImageView
    var cldr: Calendar = Calendar.getInstance()
    var day: Int = cldr.get(Calendar.DAY_OF_MONTH)
    var month: Int = cldr.get(Calendar.MONTH)
    var year: Int = cldr.get(Calendar.YEAR)
    var hasPicked: Boolean=false
    var hasChosen: Boolean= false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_data)

        var picker : DatePickerDialog
        val dobButton: TextView = this.findViewById(R.id.register_dob_submit)
        val submitButton: Button = this.findViewById(R.id.register_submit_btn)
        val genderSpinner: Spinner = this.findViewById(R.id.register_spinner_gender)

        imageView= this.findViewById(R.id.user_profile_image_view)



        imageView.setOnClickListener{
            launchGallery()
        }
        viewModel.dob=Timestamp.now()
        dobButton.setText(getString(R.string.dob_hint))

        dobButton.setOnClickListener {
            picker = DatePickerDialog(
                this,
                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                    dobButton.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                    this.year=year
                    this.month=monthOfYear
                    this.day=dayOfMonth
                    this.hasPicked=true

                },
                year,
                month,
                day
            )

            picker.show()





            viewModel.dob= Timestamp(Date(year-1900,month,day,0,0))


        }




        val options = arrayOf(getString(R.string.female),getString(R.string.male))



        setGenderSpinner(genderSpinner, options)

        submitButton.setOnClickListener{
            if(!hasPicked){
                Toast.makeText(this,getString(R.string.sd_validate), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.dob= Timestamp(Date(year-1900,month,day,0,0))

            UserRepository.userSubmitData(viewModel.imageUrl,viewModel.genderString,viewModel.dob)

            finish()

        }

    }
    override fun onBackPressed() {

    }
    private fun setGenderSpinner(
        genderSpinner: Spinner,
        options: Array<String>) {
        genderSpinner.adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options)
        genderSpinner.onItemSelectedListener = object : AdapterView.OnItemClickListener,
            AdapterView.OnItemSelectedListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.genderString = getString(R.string.male)
                viewModel.imageUrl =
                    "https://firebasestorage.googleapis.com/v0/b/cuman-app.appspot.com/o/imageUploads%2Fdefault_male.png?alt=media&token=9ef4eebb-ba41-4bb3-9f90-fb33218cffe1"

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

//                try {
//                    val storageReference=FirebaseStorage.getInstance().getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/cuman-app.appspot.com/o/imageUploads%2FYWlGIoLwwnhR1hUDtP2wilrMERu1?alt=media&token=186b600a-8b65-48da-8462-e6e48131a448")
//
//                    GlideApp.with(this).load(storageReference).into(imageView)
//
//                }catch(e: Exception) {
//                }

                if(position==0){
                    viewModel.genderString="Female"
                }else{
                    viewModel.genderString="Male"
                }

                if(hasChosen)return
                if (viewModel.genderString == "Male") {

                    viewModel.imageUrl =
                        "https://firebasestorage.googleapis.com/v0/b/cuman-app.appspot.com/o/imageUploads%2Fdefault_male.png?alt=media&token=9ef4eebb-ba41-4bb3-9f90-fb33218cffe1"

                }
                else{
                    viewModel.imageUrl =
                        "https://firebasestorage.googleapis.com/v0/b/cuman-app.appspot.com/o/imageUploads%2Fdefault_female.png?alt=media&token=3d6d2148-b6ff-4ae6-9a4c-927362739300"

                }
                try {
                    val storageReference=FirebaseStorage.getInstance().getReferenceFromUrl(viewModel.imageUrl)
                    GlideApp.with(this@SubmitDataActivity).load(storageReference).into(imageView)
                }catch(e: Exception) {
                }

            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }
            filePath = data.data
            try {

                val bitmap = MediaStore.Images.Media.getBitmap(this?.contentResolver, filePath)
                imageView.setImageBitmap(bitmap)

            } catch (e: IOException) {
                e.printStackTrace()
            }
            hasChosen=true
            uploadImage()
        }
    }
    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }
    val PICK_IMAGE_REQUEST = 71
    var filePath : Uri?=null
    var firebaseStore: FirebaseStorage? = FirebaseStorage.getInstance()
    var storageReference: StorageReference? = FirebaseStorage.getInstance().reference

    private fun uploadImage(){
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            val progressDialog = ProgressDialog(this)
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
                        viewModel.imageUrl=uri.toString()
                    }
                }
                Toast
                    .makeText(
                        this,
                        getString(R.string.toast_img_up),
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }
                ?.addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast
                        .makeText(
                            this,
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