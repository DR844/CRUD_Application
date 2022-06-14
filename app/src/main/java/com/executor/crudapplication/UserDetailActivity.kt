package com.executor.crudapplication

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.executor.crudapplication.db.UserDatabase
import com.executor.crudapplication.db.UserEntity
import com.executor.crudapplication.db.UserViewModel
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_user_detail.*
import kotlinx.android.synthetic.main.row_contact.*
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
    lateinit var storagePermission: Array<String>

    private var photoPath: String? = null

    companion object {
        private var myAge = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)

        cameraPermission =
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
//        storagePermission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)


        ivProfile.setOnClickListener {
            if (!checkCameraPermission()) {
                requestCameraPermission()
            } else {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.apply {
                    setTitle("Select The Option")
//                setMessage("I just wanted to greet you. I hope you are doing great!")
                    setPositiveButton("CAMERA") { _, _ ->
//                        savePicture()
                        takePicture()
                    }
                    setNegativeButton("GALLERY") { _, _ ->
                        val pickPhoto = Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(pickPhoto,
                            1) //one can be replaced with any action code
                    }
                    setNeutralButton("CANCEL") { _, _ ->

                    }
                }.create().show()
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

    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            var photofile: File? = null
            try {
                photofile = createImageFile()
            } catch (e: IOException) {
            }
            if (photofile != null) {
                val photoUri = FileProvider.getUriForFile(this,
                    "com.executor.crudapplication.fileprovider",
                    photofile)
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

//    private fun savePicture() {
//        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        val directory =
//            File(externalMediaDirs[0].absolutePath)
//        File.separator + resources.getString(
//            R.string.app_name)
//        if (!directory.exists()) {
//            directory.mkdirs()
//        }
//        file = File(directory, "${System.currentTimeMillis()}.jpg")
//        val uri = FileProvider.getUriForFile(this,
//            "$packageName.provider",
//            file)
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
////                        resultLauncher.launch(takePicture, 0)
//        startActivityForResult(takePicture,
//            0) //zero can be replaced with any action code (called requestCode)
//    }

    private fun requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST)
    }

    private fun pickFromGallery() {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(this);
    }

    private fun requestCameraPermission() {
        requestPermissions(cameraPermission, STORAGE_REQUEST)
    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == (PackageManager.PERMISSION_GRANTED)

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

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            val result = CropImage.getActivityResult(data)
//            if (resultCode == RESULT_OK) {
//                val resultUri = result.uri
////                Picasso.get().load(resultUri).into(ivProfile)
//                val pic = Picasso.get()
//                    .load(resultUri)
//                    .resize(1080, 1080)
//                    .centerCrop()
//                    .into(ivProfile)
//
//                imagePath = resultUri.path
//                Log.d("Image path", "onActivityResult: $imagePath")
//            }
//        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)
        when (requestCode) {
            0 -> if (resultCode == RESULT_OK) {
//                ivProfile.rotation = 90f
                ivProfile.setImageURI(Uri.parse(photoPath))
                Toast.makeText(this, photoPath, Toast.LENGTH_LONG).show()

//                val selectedImage: Uri? = imageReturnedIntent?.data
//                Toast.makeText(this, "Image Captured", Toast.LENGTH_SHORT).show();
//                val img = imageReturnedIntent?.data
//                Toast.makeText(this, "$img", Toast.LENGTH_SHORT).show()
//                Log.d("onActivityResult", "onActivityResult: $img")
//                str = arrayOf(file.absolutePath)
//                MediaScannerConnection.scanFile(this, str, null, null)
//                ivProfile.setImageBitmap(imageReturnedIntent?.extras?.get("data") as Bitmap)
//                ivProfile.setImageURI(selectedImage)
            }
            1 -> if (resultCode == RESULT_OK) {
                val selectedImage: Uri? = imageReturnedIntent?.data
                ivProfile.setImageURI(selectedImage)
                photoPath = selectedImage.toString()
                Toast.makeText(this, photoPath, Toast.LENGTH_SHORT).show()
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
//                        pickFromGallery()
                    } else {
                        Toast.makeText(
                            this,
                            "Please enable to the camera and storage permission",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            STORAGE_REQUEST -> {
                if (grantResults.isNotEmpty()) {
                    val storage_accepted = grantResults[0] == (PackageManager.PERMISSION_GRANTED)
                    if (storage_accepted) {
//                        pickFromGallery()
                    } else {
                        Toast.makeText(
                            this,
                            "Please enable to storage permission",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
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
        tvCalender.text = sdf.format(myCalender.time)
    }


    private fun insertDataToDatabase() {
        //        if (imagePath == null) {
//            Toast.makeText(this, "Please Select Image", Toast.LENGTH_SHORT).show()
//        } else {
//        val img = imagePath
        Log.d("insertDataToDatabase", "insertDataToDatabase: $photoPath")
//
//            val stream = ByteArrayOutputStream()
//            img.compress(Bitmap.CompressFormat.PNG, 20, stream)
//            val imageByte: ByteArray = stream.toByteArray()

//        Log.d("Image", "insertDataToDatabase: $img")

        val fName = etFirstName.text.toString()
        val lName = etLastName.text.toString()
        val email = etEmail.text.toString()
        val dob = tvCalender.text.toString()
        val number = etNumber.text.toString()


        if (!TextUtils.isEmpty(fName) && !TextUtils.isEmpty(lName) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(
                dob) && !TextUtils.isEmpty(number)
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
