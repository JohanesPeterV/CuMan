package com.binus.cuman.views.update_profile

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import android.view.MenuItem
import com.binus.cuman.R
import com.binus.cuman.repositories.UserRepository
import java.util.*

class UpdateProfileActivity : AppCompatActivity() {
    val viewModel= UpdateProfileViewModel()
    lateinit var imageView: ImageView
    var cldr: Calendar = Calendar.getInstance()
    var day: Int = cldr.get(Calendar.DAY_OF_MONTH)
    var month: Int = cldr.get(Calendar.MONTH)
    var year: Int = cldr.get(Calendar.YEAR)


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)
        var picker : DatePickerDialog

        val userEditText: EditText = this.findViewById(R.id.update_user_name)
        val dobButton: TextView = this.findViewById(R.id.update_dob_submit)

        val submitButton: Button = this.findViewById(R.id.update_submit_btn)
        val genderSpinner: Spinner = this.findViewById(R.id.update_profile_spinner_gender)
        val options = arrayOf(getString(R.string.female),getString(R.string.male))



        setGenderSpinner(genderSpinner, options)
        UserRepository.getUserById(FirebaseAuth.getInstance().currentUser?.uid.toString()).get().addOnSuccessListener {
            viewModel.userName=it["name"].toString()
            viewModel.genderString=it["gender"].toString()
            viewModel.dob= it.getTimestamp("dateOfBirth")!!
            dobButton.setText(viewModel.dob.toDate().date.toString() + "/" + (viewModel.dob.toDate().month + 1).toString() + "/" + (viewModel.dob.toDate().year+1900).toString())
            userEditText.setText(viewModel.userName)
            if(viewModel.genderString.equals("Male"))genderSpinner.setSelection(1)
            else genderSpinner.setSelection(0)

        }

        viewModel.dob= Timestamp.now()
        dobButton.setOnClickListener {
            picker = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    dobButton.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                    this.year=year
                    this.month=monthOfYear
                    this.day=dayOfMonth
                },
                year,
                month,
                day
            )

            picker.show()





            viewModel.dob= Timestamp(Date(year-1900,month,day,0,0))


        }



        submitButton.setOnClickListener{
            viewModel.dob= Timestamp(Date(year-1900,month,day,0,0))
            viewModel.userName=userEditText.text.toString()

            if(viewModel.userName.isEmpty()){

                Toast.makeText(this, getString(R.string.err_ucbe) , Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            UserRepository.userUpdateProfile(viewModel.userName,viewModel.genderString,viewModel.dob,this)


            finish()

        }

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


            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(position==1){
                    viewModel.genderString="Male"
                }
                else{
                    viewModel.genderString="Female"
                }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

            }
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}