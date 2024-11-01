package com.example.lab11



import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

// Extensión para crear DataStore
val Context.userPreferencesDataStore by preferencesDataStore("user_preferences")

class UserPreferences(private val context: Context) {

    companion object {
        val NAME_KEY = stringPreferencesKey("user_name")
        val SURNAME_KEY = stringPreferencesKey("user_surname")
        val EMAIL_KEY = stringPreferencesKey("user_email")
        val BIRTH_DATE_KEY = stringPreferencesKey("user_birth_date")
    }

    // Función para guardar datos de usuario
    suspend fun saveUserPreferences(name: String, surname: String, email: String, birthDate: String) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[NAME_KEY] = name
            preferences[SURNAME_KEY] = surname
            preferences[EMAIL_KEY] = email
            preferences[BIRTH_DATE_KEY] = birthDate
        }
    }

    // Función para leer datos de usuario
    val userPreferencesFlow: Flow<UserProfile> = context.userPreferencesDataStore.data
        .map { preferences ->
            UserProfile(
                name = preferences[NAME_KEY] ?: "",
                surname = preferences[SURNAME_KEY] ?: "",
                email = preferences[EMAIL_KEY] ?: "",
                birthDate = preferences[BIRTH_DATE_KEY] ?: ""
            )
        }
}

data class UserProfile(
    val name: String,
    val surname: String,
    val email: String,
    val birthDate: String
)
private val database = FirebaseDatabase.getInstance().reference

// Función para guardar los datos de usuario en Firebase
suspend fun saveUserProfileToFirebase(userProfile: UserProfile): Boolean {
    return try {
        // Se usa el ID del usuario como clave en la base de datos
        val userId = database.push().key ?: return false
        database.child("users").child(userId).setValue(userProfile).await()
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

