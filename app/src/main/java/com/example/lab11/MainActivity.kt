package com.example.lab11

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import com.example.lab11.ui.theme.Lab11Theme
import kotlinx.coroutines.launch




class MainActivity : ComponentActivity() {
    private lateinit var userPreferences: UserPreferences
    private lateinit var authManager: FirebaseAuthManager
    private lateinit var postRepository: PostRepository
    private var imageUri by mutableStateOf<Uri?>(null)

    // Lanzador de actividad para seleccionar una imagen
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userPreferences = UserPreferences(this)
        authManager = FirebaseAuthManager(this)
        postRepository = PostRepository()

        authManager.signInAnonymously { success ->
            if (success) {
                Log.d("MainActivity", "Autenticación completada")
            } else {
                Log.e("MainActivity", "Error en autenticación")
            }
        }

        setContent {
            Lab11Theme {
                val coroutineScope = rememberCoroutineScope()
                var postText by remember { mutableStateOf(TextFieldValue("")) }

                Scaffold(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Campo de texto para la publicación
                        OutlinedTextField(
                            value = postText,
                            onValueChange = { postText = it },
                            label = { Text("Escribe tu publicación") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Botón para seleccionar una imagen
                        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                            Text("Seleccionar Imagen")
                        }

                        // Botón para guardar la publicación
                        Button(
                            onClick = {
                                imageUri?.let { uri ->
                                    coroutineScope.launch {
                                        postRepository.savePost(postText.text, uri) { success ->
                                            if (success) {
                                                Log.d("MainActivity", "Publicación guardada exitosamente")

                                                // Paso 3: Verificar que se guardó correctamente
                                                postRepository.fetchAllPosts { posts ->
                                                    if (posts != null) {
                                                        Log.d("MainActivity", "Publicaciones obtenidas: $posts")
                                                    } else {
                                                        Log.e("MainActivity", "Error al obtener publicaciones")
                                                    }
                                                }
                                            } else {
                                                Log.e("MainActivity", "Error al guardar la publicación")
                                            }
                                        }
                                    }
                                }
                            },
                            enabled = imageUri != null
                        ) {
                            Text("Guardar Publicación")
                        }

                    }
                }
            }
        }
    }
}
