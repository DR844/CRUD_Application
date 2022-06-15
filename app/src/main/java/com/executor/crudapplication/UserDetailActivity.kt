package com.executor.crudapplication

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.executor.crudapplication.db.UserDatabase
import com.executor.crudapplication.db.UserEntity
import com.executor.crudapplication.db.UserViewModel
import kotlinx.android.synthetic.main.activity_user_detail.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class UserDetailActivity : AppCompatActivity() {

    private lateinit var mUserViewModel: UserViewModel

    private val CAMERA_REQUEST = 100
    private val STORAGE_REQUEST = 101

    lateinit var cameraPermission: Array<String>

    private var photoPath: String? = null

    private var myAge = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)

        val actionBar = supportActionBar
//        actionBar!!.title = "UserDetailActivity"
        actionBar?.setDisplayHomeAsUpEnabled(true)


        cameraPermission =
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )


        ivProfile.setOnClickListener {
            if (!checkCameraPermission()) {
                requestCameraPermission()
            } else {
                showDialog()
            }
        }

        val myCalender = Calendar.getInstance()

        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            myCalender.set(Calendar.YEAR, year)
            myCalender.set(Calendar.MONTH, month)
            myCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updatable(myCalender)
        }


        tvCalender.setOnClickListener {
            val dialog = DatePickerDialog(
                this,
                datePicker,
                myCalender.get(Calendar.YEAR),
                myCalender.get(Calendar.MONTH),
                myCalender.get(Calendar.DAY_OF_MONTH)
            )

            dialog.datePicker.maxDate = myCalender.timeInMillis
            dialog.show()
        }

        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        btnSave.setOnClickListener {
            insertDataToDatabase()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle("Select The Option")
//                setMessage("I just wanted to greet you. I hope you are doing great!")
            setPositiveButton("CAMERA") { _, _ ->
                takePicture()
            }
            setNegativeButton("GALLERY") { _, _ ->
                val pickPhoto = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(
                    pickPhoto,
                    1
                ) //one can be replaced with any action code
            }
            setNeutralButton("CANCEL") { _, _ ->

            }
        }.create().show()
    }

    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            var photofile: File? = null
            try {
                photofile = createImageFile()
            } catch (e: IOException) {
            }
            if (photofile != null) {
                val photoUri = FileProvider.getUriForFile(
                    this,
                    "com.executor.crudapplication.fileprovider",
                    photofile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(intent, 0)
            }

        }
    }

    private fun createImageFile(): File? {
        val filename = "MyProfile"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(filename, ".jpg", storageDir)
        photoPath = image.absolutePath
        return image
    }

    private fun requestCameraPermission() {
        requestPermissions(cameraPermission, STORAGE_REQUEST)
    }


    private fun checkCameraPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == (PackageManager.PERMISSION_GRANTED)
        val result2 = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == (PackageManager.PERMISSION_GRANTED)
        return result && result2

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)
        when (requestCode) {
            0 -> if (resultCode == RESULT_OK) {
//                ivProfile.rotation = 90f
                ivProfile.setImageURI(Uri.parse(photoPath))
            }
            1 -> if (resultCode == RESULT_OK) {
                val selectedImage: Uri? = imageReturnedIntent?.data
                ivProfile.setImageURI(selectedImage)
                photoPath = selectedImage.toString()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST -> {
                if (grantResults.isNotEmpty()) {
                    val camera_accepted = grantResults[0] == (PackageManager.PERMISSION_GRANTED)
                    val storage_accepted = grantResults[1] == (PackageManager.PERMISSION_GRANTED)
                    if (camera_accepted && storage_accepted) {
                        showDialog()
                    }
                }
            }
        }
    }


    private fun updatable(myCalender: Calendar) {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val birthYear = myCalender.get(Calendar.YEAR)
        myAge = currentYear - birthYear
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        tvCalender.text = sdf.format(myCalender.time)
    }


    private fun insertDataToDatabase() {

        val fName = etFirstName.text.toString()
        val lName = etLastName.text.toString()
        val email = etEmail.text.toString()
        val dob = tvCalender.text.toString()
        val number = etNumber.text.toString()


        if (!TextUtils.isEmpty(fName) && !TextUtils.isEmpty(lName) && !TextUtils.isEmpty(photoPath) && !TextUtils.isEmpty(
                email
            )
            && !TextUtils.isEmpty(dob) && !TextUtils.isEmpty(number)
        ) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, " Invalid Email Format", Toast.LENGTH_SHORT).show()
            } else {
                if (UserDatabase.getDatabase(this).userDao().isEmailExist(email) == 0) {
                    val user =
                        UserEntity(
                            0,
                            photoPath!!,
                            fName,
                            lName,
                            email,
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
                    Toast.makeText(this, "This id Already Exists", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(
                this,
                "Please fill out all Fields ",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }
}
