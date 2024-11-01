package com.example.lab11


import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthManager(context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signInAnonymously(onComplete: (Boolean) -> Unit) {
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FirebaseAuth", "Autenticación anónima exitosa")
                    onComplete(true)
                } else {
                    Log.e("FirebaseAuth", "Error en la autenticación anónima", task.exception)
                    onComplete(false)
                }
            }
    }
}
