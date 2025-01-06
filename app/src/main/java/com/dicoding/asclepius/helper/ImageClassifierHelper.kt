package com.dicoding.asclepius.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.dicoding.asclepius.ml.CancerClassification
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp

class ImageClassifierHelper(private val context: Context) { // Menambahkan context sebagai parameter konstruktor

    private lateinit var model: CancerClassification // Model klasifikasi

    init {
        setupImageClassifier() // Menginisialisasi model saat kelas dibuat
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun classifyStaticImage(imageUri: Uri): Pair<String, Float> {
        // Convert URI to Bitmap
        val source = ImageDecoder.createSource(context.contentResolver, imageUri)
        val bitmap = ImageDecoder.decodeBitmap(source)

        // Ensure the bitmap is in ARGB_8888 format
        val argbBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        // Prepare the image for classification
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .build()

        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(argbBitmap))

        // Classify the image
        val results = model.process(tensorImage) // Menggunakan model yang sudah diinisialisasi

        // Get the label and confidence
        val label: String
        val confidence: Float

        if (results.probabilityAsCategoryList.isNotEmpty()) {
            val topResult = results.probabilityAsCategoryList[1] // Ambil kategori dengan probabilitas tertinggi
            confidence = topResult.score * 100

            // Menentukan label berdasarkan confidence
            label = if (confidence > 60) {
                "Cancer"
            } else {
                "Not Cancer"
            }
        } else {
            label = "Unknown"
            confidence = 0f
        }

        return Pair(label, confidence)
    }

    private fun setupImageClassifier() {
        model = CancerClassification.newInstance(context) // Menginisialisasi model klasifikasi
    }
}