package com.elvis.cameraapp

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    private val CAMERA_PERMISSION_CODE = 1000
    private val IMAGE_CAPTURED = 2022
    private val GALLERY_PERMISSION_CODE = 3000
    private val GALLERY_IMAGE_CODE = 1996
    private var imageUri: Uri? = null
    private var imgView: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imgView = findViewById(R.id.imgCaptured)
        val btnOpenCamera: Button = findViewById(R.id.btnOpenCamera)
        val btnOpenGallery: Button = findViewById(R.id.btnOpenGallery)

        requestCameraPermission()
        requestGalleryPermission()

        val permissionGranted = requestCameraPermission()
        btnOpenGallery.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, GALLERY_IMAGE_CODE)

        }
        btnOpenCamera.setOnClickListener {
            //opening the camera if the permission has been granted
            //if (permissionGranted) {
            //Open the camera
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "My Image")
            values.put(MediaStore.Images.Media.DESCRIPTION, "Taken from my app")
            imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            //Creating an intent to open the camera

            val intentToOpenCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intentToOpenCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(intentToOpenCamera, IMAGE_CAPTURED)
            //}

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == IMAGE_CAPTURED) {
            imgView?.setImageURI(imageUri)
        } else {
            showAlert("Oops, Something went wrong")
        }
    }


    private fun requestCameraPermission(): Boolean {
        var permissionGranted = false

        // If system os is Marshmallow or Above, we need to request runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val cameraPermissionNotGranted = checkSelfPermission( Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
            if (cameraPermissionNotGranted){
                val permission = arrayOf(Manifest.permission.CAMERA)

                // Display permission dialog
                requestPermissions(permission, CAMERA_PERMISSION_CODE)
            }
            else{
                // Permission already granted
                permissionGranted = true
            }
        }
        else{
            // Android version earlier than M -> no need to request permission
            permissionGranted = true
        }

        return permissionGranted
    }
    private fun requestGalleryPermission(): Boolean {
        var permissionGranted = false

        // If system os is Marshmallow or Above, we need to request runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val cameraPermissionNotGranted = checkSelfPermission( Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
            if (cameraPermissionNotGranted){
                val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

                // Display permission dialog
                requestPermissions(permission, GALLERY_PERMISSION_CODE)
            }
            else{
                // Permission already granted
                permissionGranted = true
            }
        }
        else{
            // Android version earlier than M -> no need to request permission
            permissionGranted = true
        }

        return permissionGranted
    }

    // Handle Allow or Deny response from the permission dialog
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode === CAMERA_PERMISSION_CODE) {
            if (grantResults.size === 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                // openCameraInterface()
            } else {
                // Permission was denied
                showAlert("Camera permission was denied. Unable to take a picture.");
            }
            if (requestCode === GALLERY_PERMISSION_CODE) {
                if (grantResults.size === 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted
                    // openCameraInterface()
                } else {
                    // Permission was denied
                    showAlert("Gallery permission was denied. Unable to pick a picture.");
                }
            }
        }
    }
    private fun showAlert(message : String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
        builder.setPositiveButton("Ok", null)
        builder.create().show()
    }

}