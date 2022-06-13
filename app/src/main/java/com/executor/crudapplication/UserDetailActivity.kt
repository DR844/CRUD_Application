package com.executor.crudapplication

import android.app.ActionBar
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.executor.crudapplication.db.UserEntity
import com.executor.crudapplication.db.UserViewModel
import kotlinx.android.synthetic.main.activity_user_detail.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class UserDetailActivity : AppCompatActivity() {

    private lateinit var mUserViewModel: UserViewModel

    var imagePath: String? = null;

    companion object {
        private var myAge = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)
//
//        val actionBar: ActionBar? = actionBar
//        actionBar?.setHomeButtonEnabled(true)

        val myCalender = Calendar.getInstance()

        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            myCalender.set(Calendar.YEAR, year)
            myCalender.set(Calendar.MONTH, month)
            myCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updatable(myCalender)
        }


        tvContactCalender.setOnClickListener {
            DatePickerDialog(
                this,
                datePicker,
                myCalender.get(Calendar.YEAR),
                myCalender.get(Calendar.MONTH),
                myCalender.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        btnSave.setOnClickListener {
            insertDataToDatabase()
        }


    }

    private fun updatable(myCalender: Calendar) {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
//        Toast.makeText(this, "$currentYear", Toast.LENGTH_SHORT).show()
        val birthYear = myCalender.get(Calendar.YEAR)
//        Toast.makeText(this, "$birthYear", Toast.LENGTH_SHORT).show()
        myAge = currentYear - birthYear
//        Toast.makeText(this, "$myAge", Toast.LENGTH_SHORT).show()
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        tvContactCalender.text = sdf.format(myCalender.time)
    }


    private fun insertDataToDatabase() {
        //        if (imagePath == null) {
//            Toast.makeText(this, "Please Select Image", Toast.LENGTH_SHORT).show()
//        } else {
        val img = BitmapFactory.decodeFile(imagePath)
//
//            val stream = ByteArrayOutputStream()
//            img.compress(Bitmap.CompressFormat.PNG, 20, stream)
//            val imageByte: ByteArray = stream.toByteArray()

//        Log.d("Image", "insertDataToDatabase: $img")
        val fName = etContactFirstName.text.toString()
        val lName = etContactLastName.text.toString()
        val dob = tvContactCalender.text.toString()
        val number = etContactNumber.text.toString()

        if (!TextUtils.isEmpty(fName) && !TextUtils.isEmpty(lName) && !TextUtils.isEmpty(dob) && !TextUtils.isEmpty(
                number)
        ) {
            val user =
                UserEntity(
                    0,
                    "hi",
                    fName,
                    lName,
                    dob,
                    myAge,
                    number,
                    Date()
                )
            GlobalScope.launch {
                mUserViewModel.insertUser(user)
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            Toast.makeText(this, "Successfully Added", Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(
                this,
                "Please fill out all Fields ",
                Toast.LENGTH_SHORT
            )
                .show()
        }
//            }

    }
}