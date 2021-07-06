package com.example.ocrtextrecognizer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextRecognizer

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private val permissionCode = 100
    private val requestTakePicture = 200
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermissions()
        findViewById<ImageButton>(R.id.btnCamera).setOnClickListener {
            openCamera()
        }
    }

    private fun checkPermissions() {
        val permissionGrantedCamera = ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (!permissionGrantedCamera) {
            requestPermission()
        }
    }


    private fun requestPermission() {
        val showRequestRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.CAMERA
        )
        if (showRequestRationale) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    permissionCode
            )
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    permissionCode
            )
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            permissionCode -> {
                if (grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(
                            this,
                            getString(R.string.message_permission_denied),
                            Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @Suppress("DEPRECATION")
    @SuppressLint("QueryPermissionsNeeded")
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, requestTakePicture)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            requestTakePicture -> {
                if (resultCode == Activity.RESULT_OK) {
                    val extras = data?.extras
                    val bitmap = extras?.get("data") as Bitmap
                    extractText(bitmap)
                }
            }
            else -> {
                Toast.makeText(this, getString(R.string.txt_picture_canceled), Toast.LENGTH_SHORT)
                        .show()
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun extractText(bitmap: Bitmap) {
        val textRecognizer = TextRecognizer.Builder(applicationContext).build()
        val imageFrame = Frame.Builder().setBitmap(bitmap).build()
        var imageText = ""
        val textBlocks = textRecognizer.detect(imageFrame)
        for (i in 0 until textBlocks.size()) {
            val textBlock = textBlocks[textBlocks.keyAt(i)]
            imageText = textBlock.value
        }
        findViewById<TextView>(R.id.tvResult).text = imageText
    }
}