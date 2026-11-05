package com.alan.routineos.data.auth

import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

data class AuthenticatedUser(
    val uid: String,
    val name: String,
    val email: String,
    val photoUrl: String? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null
)

@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("routineos_auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val DEFAULT_BACKEND_URL = "https://routineos-backend.onrender.com"
        private const val FIREBASE_API_KEY = "AIzaSyAP2rWe2RwlFJIHuxTbRDwWmyEXAUjhDcc"
        private const val GOOGLE_WEB_CLIENT_ID = "1002673725777-0ocbplj5fm2gep7vr4o473crleeuh2n2.apps.googleusercontent.com"
    }

    fun getBackendUrl(): String =
        prefs.getString("backend_url", DEFAULT_BACKEND_URL)?.trimEnd('/') ?: DEFAULT_BACKEND_URL

    fun setBackendUrl(url: String) {
        prefs.edit().putString("backend_url", url.trim().trimEnd('/')).apply()
    }

    fun isSignedOut(): Boolean =
        prefs.getBoolean("is_signed_out", true) || prefs.getString("user_email", null).isNullOrBlank()

    fun getSavedUser(): AuthenticatedUser? {
        if (isSignedOut()) return null
        val email = prefs.getString("user_email", null) ?: return null
        val name = prefs.getString("user_name", null) ?: email.substringBefore("@")
        val uid = prefs.getString("user_uid", "local") ?: "local"
        val photo = prefs.getString("user_photo", null)
        val accessToken = prefs.getString("access_token", null)
        val refreshToken = prefs.getString("refresh_token", null)
        return AuthenticatedUser(
            uid = uid,
            name = name,
            email = email,
            photoUrl = photo,
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    fun signOut() {
        val refreshToken = prefs.getString("refresh_token", null)
        prefs.edit()
            .putBoolean("is_signed_out", true)
            .remove("access_token")
            .remove("refresh_token")
            .apply()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                CredentialManager.create(context)
                    .clearCredentialState(androidx.credentials.ClearCredentialStateRequest())
            } catch (_: Exception) {
                // Best-effort credential state clear
            }
            if (!refreshToken.isNullOrBlank()) {
                try {
                    val payload = JSONObject().apply {
                        put("refreshToken", refreshToken)
                    }
                    postJson("${getBackendUrl()}/auth/logout", payload)
                } catch (_: Exception) {
                    // Best-effort server logout
                }
            }
        }
    }

    private fun saveSession(user: AuthenticatedUser) {
        prefs.edit()
            .putBoolean("is_signed_out", false)
            .putString("user_uid", user.uid)
            .putString("user_name", user.name)
            .putString("user_email", user.email)
            .putString("user_photo", user.photoUrl)
            .putString("access_token", user.accessToken)
            .putString("refresh_token", user.refreshToken)
            .apply()
    }

    private fun buildDeviceJson(): JSONObject {
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            ?: "android-device"
        return JSONObject().apply {
            put("platform", "ANDROID")
            put("deviceName", "${Build.MANUFACTURER} ${Build.MODEL}".trim())
            put("deviceFingerprint", androidId)
        }
    }

    suspend fun signInWithEmail(email: String, password: String): Result<AuthenticatedUser> = withContext(Dispatchers.IO) {
        try {
            val cleanEmail = email.trim()
            val payload = JSONObject().apply {
                put("email", cleanEmail)
                put("password", password)
                put("timezone", TimeZone.getDefault().id)
                put("device", buildDeviceJson())
            }
            val response = postJson("${getBackendUrl()}/auth/login", payload)
            val accessToken = response.optString("accessToken")
            val refreshToken = response.optString("refreshToken")
            val displayName = cleanEmail.substringBefore("@").replaceFirstChar { it.uppercase() }

            val user = AuthenticatedUser(
                uid = cleanEmail,
                name = displayName,
                email = cleanEmail,
                accessToken = accessToken.takeIf { it.isNotBlank() },
                refreshToken = refreshToken.takeIf { it.isNotBlank() }
            )
            saveSession(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception(mapBackendError(e.message)))
        }
    }

    suspend fun registerWithEmail(name: String, email: String, password: String): Result<AuthenticatedUser> = withContext(Dispatchers.IO) {
        try {
            val cleanEmail = email.trim()
            val cleanName = name.trim().ifBlank { cleanEmail.substringBefore("@") }
            val payload = JSONObject().apply {
                put("email", cleanEmail)
                put("password", password)
                put("name", cleanName)
                put("timezone", TimeZone.getDefault().id)
                put("device", buildDeviceJson())
            }
            val response = postJson("${getBackendUrl()}/auth/register", payload)
            val status = response.optString("status")

            // Attempt immediate login if backend allows, otherwise save pending/verified session
            var accessToken: String? = null
            var refreshToken: String? = null
            try {
                val loginPayload = JSONObject().apply {
                    put("email", cleanEmail)
                    put("password", password)
                    put("timezone", TimeZone.getDefault().id)
                    put("device", buildDeviceJson())
                }
                val loginRes = postJson("${getBackendUrl()}/auth/login", loginPayload)
                accessToken = loginRes.optString("accessToken").takeIf { it.isNotBlank() }
                refreshToken = loginRes.optString("refreshToken").takeIf { it.isNotBlank() }
            } catch (_: Exception) {
                // Email verification may be required before login
            }

            val user = AuthenticatedUser(
                uid = cleanEmail,
                name = cleanName,
                email = cleanEmail,
                accessToken = accessToken,
                refreshToken = refreshToken
            )
            saveSession(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception(mapBackendError(e.message)))
        }
    }

    suspend fun signInWithGoogle(activityContext: Context): Result<AuthenticatedUser> {
        return try {
            val credentialManager = CredentialManager.create(activityContext)
            val googleIdOption = com.google.android.libraries.identity.googleid.GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(GOOGLE_WEB_CLIENT_ID)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = try {
                credentialManager.getCredential(
                    request = request,
                    context = activityContext
                )
            } catch (e: androidx.credentials.exceptions.NoCredentialException) {
                val fallbackOption = GetSignInWithGoogleOption.Builder(GOOGLE_WEB_CLIENT_ID)
                    .build()
                val fallbackRequest = GetCredentialRequest.Builder()
                    .addCredentialOption(fallbackOption)
                    .build()
                credentialManager.getCredential(
                    request = fallbackRequest,
                    context = activityContext
                )
            }

            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleCred = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleCred.idToken

                withContext(Dispatchers.IO) {
                    // 1. Verify Google ID Token
                    val idpPayload = JSONObject().apply {
                        put("postBody", "id_token=$idToken&providerId=google.com")
                        put("requestUri", "http://localhost")
                        put("returnIdpCredential", true)
                        put("returnSecureToken", true)
                    }
                    val idpResponse = postJson(
                        "https://identitytoolkit.googleapis.com/v1/accounts:signInWithIdp?key=$FIREBASE_API_KEY",
                        idpPayload
                    )
                    val uid = idpResponse.optString("localId")
                    val email = idpResponse.optString("email", googleCred.id)
                    val name = idpResponse.optString("displayName").takeIf { it.isNotBlank() }
                        ?: googleCred.displayName
                        ?: email.substringBefore("@")
                    val photoUrl = idpResponse.optString("photoUrl").takeIf { it.isNotBlank() }
                        ?: googleCred.profilePictureUri?.toString()

                    // 2. Simultaneously sync/authenticate with routineos-backend
                    var accessToken: String? = null
                    var refreshToken: String? = null
                    val derivedPassword = "GAuth_${uid.take(16)}!"
                    try {
                        val loginPayload = JSONObject().apply {
                            put("email", email)
                            put("password", derivedPassword)
                            put("timezone", TimeZone.getDefault().id)
                            put("device", buildDeviceJson())
                        }
                        val loginRes = postJson("${getBackendUrl()}/auth/login", loginPayload)
                        accessToken = loginRes.optString("accessToken").takeIf { it.isNotBlank() }
                        refreshToken = loginRes.optString("refreshToken").takeIf { it.isNotBlank() }
                    } catch (_: Exception) {
                        try {
                            val regPayload = JSONObject().apply {
                                put("email", email)
                                put("password", derivedPassword)
                                put("name", name)
                                put("timezone", TimeZone.getDefault().id)
                                put("device", buildDeviceJson())
                            }
                            postJson("${getBackendUrl()}/auth/register", regPayload)
                        } catch (_: Exception) {
                            // Backend sync best-effort if email already exists with different password
                        }
                    }

                    val user = AuthenticatedUser(
                        uid = uid,
                        name = name,
                        email = email,
                        photoUrl = photoUrl,
                        accessToken = accessToken,
                        refreshToken = refreshToken
                    )
                    saveSession(user)
                    Result.success(user)
                }
            } else {
                Result.failure(Exception("Tipo de credencial de Google no compatible"))
            }
        } catch (e: androidx.credentials.exceptions.GetCredentialCancellationException) {
            Result.failure(Exception("Inicio de sesión con Google cancelado"))
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Error al iniciar sesión con Google"))
        }
    }

    private fun postJson(urlString: String, body: JSONObject): JSONObject {
        val url = URL(urlString)
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            doOutput = true
            connectTimeout = 10_000
            readTimeout = 10_000
        }
        OutputStreamWriter(conn.outputStream, "UTF-8").use { writer ->
            writer.write(body.toString())
            writer.flush()
        }
        val status = conn.responseCode
        val stream = if (status in 200..299) conn.inputStream else conn.errorStream
        val responseText = if (stream != null) {
            BufferedReader(InputStreamReader(stream, "UTF-8")).use { it.readText() }
        } else {
            "{}"
        }
        val json = JSONObject(responseText)
        if (status !in 200..299) {
            val errorMsg = json.optString("error").takeIf { it.isNotBlank() }
                ?: json.optString("message").takeIf { it.isNotBlank() }
                ?: json.optJSONObject("error")?.optString("message")
                ?: "HTTP_$status"
            throw Exception(errorMsg)
        }
        return json
    }

    private fun mapBackendError(raw: String?): String {
        val code = raw ?: return "Error de conexión con el servidor"
        return when {
            code.contains("Invalid credentials", ignoreCase = true) ||
                code.contains("INVALID_LOGIN_CREDENTIALS") ||
                code.contains("INVALID_PASSWORD") -> "Correo o contraseña incorrectos"
            code.contains("not found", ignoreCase = true) ||
                code.contains("EMAIL_NOT_FOUND") -> "No existe una cuenta registrada con este correo"
            code.contains("already exists", ignoreCase = true) ||
                code.contains("EMAIL_EXISTS") -> "Este correo ya está registrado. Inicia sesión."
            code.contains("WEAK_PASSWORD") -> "La contraseña debe tener al menos 6 caracteres"
            code.contains("INVALID_EMAIL") -> "El formato del correo electrónico no es válido"
            code.contains("Unable to resolve host", ignoreCase = true) ||
                code.contains("Failed to connect", ignoreCase = true) ||
                code.contains("timeout", ignoreCase = true) ->
                "No se pudo conectar con el servidor de RoutineOS. Verifica tu conexión."
            else -> "Error de autenticación: $code"
        }
    }
}
