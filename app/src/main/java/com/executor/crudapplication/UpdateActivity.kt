package com.executor.crudapplication

import android.annotation.SuppressLint
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
import kotlinx.android.synthetic.main.activity_update.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class UpdateActivity : AppCompatActivity() {

    private lateinit var mUserViewModel: UserViewModel

    private val CAMERA_REQUEST = 100
    private val STORAGE_REQUEST = 101

    lateinit var cameraPermission: Array<String>


    private var photoPath: String? = null

    companion object {
        private var myAge = 0
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        cameraPermission =
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

        ivUpdateProfile.setOnClickListener {
            if (!checkCameraPermission()) {
                requestCameraPermission()
            } else {
                showDialog()
            }
        }
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val date = sdf.parse(intent.getStringExtra("dob")!!)
        val myCalender = Calendar.getInstance()
        myCalender.time = date

//        val myCalender = Calendar.getInstance()

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
            dialog.datePicker.maxDate = myCalender.timeInMillis
            dialog.show()
        }

        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        etUpdateFirstName.setText(intent.getStringExtra("fname"))
        etUpdateLastName.setText(intent.getStringExtra("lname"))
        etUpdateEmail.text = intent.getStringExtra("email")
        tvUpdateCalender.text = intent.getStringExtra("dob")
        etUpdateNumber.setText(intent.getStringExtra("number"))
        ivUpdateProfile.setImageURI(Uri.parse(intent.getStringExtra("img")))
        photoPath = intent.getStringExtra("img")

        btnUpdate.setOnClickListener {
            UpdateDataToDatabase()
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
                ivUpdateProfile.setImageURI(Uri.parse(photoPath))
            }
            1 -> if (resultCode == RESULT_OK) {
                val selectedImage: Uri? = imageReturnedIntent?.data
                ivUpdateProfile.setImageURI(selectedImage)
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


    @DelicateCoroutinesApi
    private fun UpdateDataToDatabase() {

        val id = intent.getIntExtra("id", 0)
        val fName = etUpdateFirstName.text.toString()
        val lName = etUpdateLastName.text.toString()
        val email = etUpdateEmail.text.toString()
        val number = etUpdateNumber.text.toString()
        val dob = tvUpdateCalender.text.toString()


        val date = tvUpdateCalender.text.toString()
        val dateParts: List<String> = date.split("-")
        val year = dateParts[2]
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        myAge = currentYear - year.toInt()




        if (!TextUtils.isEmpty(fName) && !TextUtils.isEmpty(lName) && !TextUtils.isEmpty(dob) && !TextUtils.isEmpty(
                number
            )
        ) {
           /* if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, " Invalid Email Format", Toast.LENGTH_SHORT).show()
            } else {*/
//                if (UserDatabase.getDatabase(this).userDao().isEmailExist(email) == 0) {
                val user =
                    UserEntity(
                        id,
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
                    mUserViewModel.updateUser(user)
                }

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

                Toast.makeText(this, "Successfully Updated", Toast.LENGTH_SHORT).show()

//                } else {
//
//                    Toast.makeText(this, "This id Already Exists", Toast.LENGTH_SHORT).show()
//                }
//            }
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
        val birthYear = myCalender.get(Calendar.YEAR)
        myAge = currentYear - birthYear
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        tvUpdateCalender.text = sdf.format(myCalender.time)
    }
}
