package com.yuliana.bahasabanjar.model

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage

object StorageHelper {
    private val storageRef = FirebaseStorage.getInstance().reference

    fun uploadFile(uri: Uri, fileName: String, onSuccess: (String) -> Unit) {
        val fileRef = storageRef.child("media/$fileName")

        fileRef.putFile(uri)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { url ->
                    onSuccess(url.toString())
                }
            }
            .addOnFailureListener {
                Log.e("Storage", "Gagal upload", it)
            }
    }
}