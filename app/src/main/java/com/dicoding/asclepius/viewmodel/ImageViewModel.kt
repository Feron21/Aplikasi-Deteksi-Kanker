package com.dicoding.asclepius.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ImageViewModel : ViewModel() {
    // MutableLiveData untuk menyimpan URI gambar
    private val _imageUri: MutableLiveData<Uri?> = MutableLiveData()
    val imageUri: LiveData<Uri?> get() = _imageUri

    // Fungsi untuk mengatur URI gambar
    fun setImageUri(uri: Uri?) {
        _imageUri.value = uri
    }
}