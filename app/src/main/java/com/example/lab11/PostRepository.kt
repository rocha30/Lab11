package com.example.lab11


import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*
class PostRepository {
    private val db = Firebase.firestore
    private val postsCollection = db.collection("posts")

    // Función para guardar un post
    fun savePost(text: String, imageUri: Uri, callback: (Boolean) -> Unit) {
        val post = hashMapOf(
            "text" to text,
            "imageUri" to imageUri.toString(),
            "timestamp" to System.currentTimeMillis()
        )

        postsCollection.add(post)
            .addOnSuccessListener {
                Log.d("PostRepository", "Publicación guardada con ID: ${it.id}")
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e("PostRepository", "Error al guardar la publicación", e)
                callback(false)
            }
    }

    // Función para obtener todos los posts (paso 3)
    fun fetchAllPosts(callback: (List<Map<String, Any>>?) -> Unit) {
        postsCollection.get()
            .addOnSuccessListener { result ->
                val posts = result.map { document -> document.data }
                callback(posts)
            }
            .addOnFailureListener { e ->
                Log.e("PostRepository", "Error al obtener publicaciones", e)
                callback(null)
            }
    }
}

