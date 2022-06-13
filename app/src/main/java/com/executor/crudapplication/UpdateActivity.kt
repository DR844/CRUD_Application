package com.executor.crudapplication

import android.app.ActionBar
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.executor.crudapplication.db.UserEntity
import com.executor.crudapplication.db.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_update.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.max

class UpdateActivity : AppCompatActivity() {

    private lateinit var mUserViewModel: UserViewModel

    companion object {
        private var myAge = 0
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @DelicateCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        val myCalender = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->

            myCalender.set(Calendar.YEAR, year)
            myCalender.set(Calendar.MONTH, month)
            myCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updatable(myCalender)
        }

        tvUpdateCalender.setOnClickListener {
            val dialog = DatePickerDialog(
                this,
                datePicker,
                myCalender.get(Calendar.YEAR),
                myCalender.get(Calendar.MONTH),
                myCalender.get(Calendar.DAY_OF_MONTH)
            )
            val date = tvUpdateCalender.text.toString()
            var simpleFormat2 =  DateTimeFormatter.ISO_DATE;
            var output = LocalDate.parse(date, simpleFormat2)

            val datepicker_1 = dialog.datePicker
            datepicker_1.minDate = myCalender.timeInMillis //set the current day as the max date
//            dialog.datePicker.maxDate = myCalender.timeInMillis
            dialog.show()
        }

        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        etUpdateFirstName.setText(intent.getStringExtra("fname"))
        etUpdateLastName.setText(intent.getStringExtra("lname"))
        tvUpdateCalender.text = intent.getStringExtra("dob")
        etUpdateNumber.setText(intent.getStringExtra("number"))

        btnUpdate.setOnClickListener {
            UpdateDataToDatabase()
        }
    }

    @DelicateCoroutinesApi
    private fun UpdateDataToDatabase() {

        val id = intent.getIntExtra("id", 0)
        val fName = etUpdateFirstName.text.toString()
        val lName = etUpdateLastName.text.toString()
        val number = etUpdateNumber.text.toString()
        val dob = tvUpdateCalender.text.toString()

        val date = tvUpdateCalender.text.toString()
        val dateParts: List<String> = date.split("-")
        val year = dateParts[2]
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        myAge = currentYear - year.toInt()


        if (!TextUtils.isEmpty(fName) && !TextUtils.isEmpty(lName) && !TextUtils.isEmpty(dob) && !TextUtils.isEmpty(
                number)
        ) {
            val user =
                UserEntity(
                    id,
                    "hi",
                    fName,
                    lName,
                    dob,
                    myAge,
                    number,
                    Date()
                )

            GlobalScope.launch {
                mUserViewModel.updateUser(user)
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            Toast.makeText(this, "Successfully Updated", Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(
                this,
                "Please fill out all Fields ",
                Toast.LENGTH_SHORT
            )
                .show()
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
        tvUpdateCalender.text = sdf.format(myCalender.time)
    }
}


