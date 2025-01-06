package com.dicoding.asclepius.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.viewmodel.ImageViewModel

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageViewModel: ImageViewModel
    private val GALLERY_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi ViewModel
        imageViewModel = ViewModelProvider(this).get(ImageViewModel::class.java)

        // Observe changes in imageUri
        imageViewModel.imageUri.observe(this, Observer { uri ->
            showImage(uri)
        })

        // Set listener to open gallery
        binding.galleryButton.setOnClickListener {
            startGallery()
        }

        // Set listener to analyze image
        binding.analyzeButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                analyzeImage()
            }
        }
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun showImage(uri: Uri?) {
        uri?.let {
            binding.previewImageView.setImageURI(it)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            // Simpan URI ke ViewModel
            imageViewModel.setImageUri(uri)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun analyzeImage() {
        imageViewModel.imageUri.value?.let { uri ->
            // Pass context when creating the ImageClassifierHelper
            val imageClassifier = ImageClassifierHelper(this)
            val (label, confidence) = imageClassifier.classifyStaticImage(uri) // Hanya pass imageUri
            showToast("Label: $label, Confidence: $confidence%")
            moveToResult() // Pindah ke ResultActivity
        } ?: showToast("Please select an image first")
    }

    private fun moveToResult() {
        val intent = Intent(this, ResultActivity::class.java)
        // Mengirim URI sebagai String
        intent.putExtra("IMAGE_URI", imageViewModel.imageUri.value.toString())
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}