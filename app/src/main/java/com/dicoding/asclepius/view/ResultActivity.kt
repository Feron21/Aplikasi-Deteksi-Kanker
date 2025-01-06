package com.dicoding.asclepius.view

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil URI gambar dari Intent
        val imageUriString = intent.getStringExtra("IMAGE_URI")

        // Periksa apakah imageUriString tidak null
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)

            // Tampilkan gambar yang dipilih
            binding.resultImage.setImageURI(imageUri)

            // Dapatkan hasil klasifikasi dan skor kepercayaan
            classifyImage(imageUri)
        } else {
            // Tampilkan pesan kesalahan jika imageUriString null
            showToast("Image URI is null. Please select an image.")
            finish() // Kembali ke MainActivity
        }
    }

    private fun classifyImage(imageUri: Uri) {
        // Pastikan kita hanya memanggil klasifikasi jika SDK >= P
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val imageClassifier = ImageClassifierHelper(this) // Pass context here
            val result = imageClassifier.classifyStaticImage(imageUri) // Hanya pass imageUri
            displayResult(result.first, result.second)
        } else {
            showToast("Image classification is not supported on this version of Android.")
            finish() // Kembali ke MainActivity
        }
    }

    private fun displayResult(label: String, confidence: Float) {
        binding.resultText.text = "$label - ${"%.2f".format(confidence)}%"
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}