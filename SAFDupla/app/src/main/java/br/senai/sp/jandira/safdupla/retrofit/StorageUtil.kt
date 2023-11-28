package br.senai.sp.jandira.safdupla.retrofit

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import java.util.UUID

class StorageUtil {
    companion object {

        fun uploadToStorage(
            uri: Uri,
            context: Context,
            type: String,
            onSaveComplete: (String) -> Unit
        ) {
            val storage = Firebase.storage
            val storageRef = storage.reference
            val uniqueImageName = UUID.randomUUID().toString()
            val spaceRef: StorageReference = if (type == "image") {
                storageRef.child("imagens/$uniqueImageName.jpg")
            } else {
                storageRef.child("imagens/$uniqueImageName.png")
            }

            val byteArray: ByteArray? = context.contentResolver.openInputStream(uri)?.use {
                it.readBytes()
            }

            byteArray?.let {
                val uploadTask = spaceRef.putBytes(byteArray)
                uploadTask.addOnFailureListener {
                    Toast.makeText(
                        context,
                        "A foto não salvou.",
                        Toast.LENGTH_SHORT
                    ).show()


                }.addOnSuccessListener { taskSnapshot ->


                    spaceRef.downloadUrl.addOnSuccessListener { downloadUri ->

                        val downloadUrl = downloadUri.toString()

                        Toast.makeText(
                            context,
                            "Foto salvou!",
                            Toast.LENGTH_SHORT
                        ).show()

                        onSaveComplete(downloadUrl)
                        Log.i("URL-STORAGE-UTIL", downloadUrl)

                    }.addOnFailureListener {
                        Toast.makeText(
                            context,
                            "Não deu para baixar a foto.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}