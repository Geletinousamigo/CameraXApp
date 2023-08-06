package com.nikhil.cameraxapp

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.nikhil.cameraxapp.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var uri: Uri
    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUriString = data?.getStringExtra("imageUri")
            val imageUri = imageUriString?.toUri()
            if (imageUri != null) {
                Log.d(TAG, "This is the value of SavedUri: $imageUri")
                viewBinding.imageResult.setImageURI(imageUri)
                viewBinding.locationOfImage.text = imageUriString
            }
            // Use the captured image Uri here as needed
        } else {
            // Handle the case when the user cancels or there is an error
        }
    }

    private val phoneCameraResultLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture(),

    ) {
        result->
        if (result) {
            Log.d(TAG, "Succesfully clicked using Phone Camera: $result")
            viewBinding.imageResult.setImageURI(uri)
            viewBinding.locationOfImage.text = uri.toString()
        } else {
            Log.e(TAG, "Unsuccessful: $result", )
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        viewBinding.takePhoto.setOnClickListener {
            cameraActivityResultLauncher.launch(
                Intent(this, CameraActivity::class.java)
            )
        }

        viewBinding.takePhotoFromPhoneCamera.setOnClickListener{
            uri = createImageOutputUri()
            phoneCameraResultLauncher.launch(uri)
        }

    }

    private fun createImageOutputUri(): Uri {
        val name = SimpleDateFormat(CameraActivity.FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }
        return contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )!!
    }

}