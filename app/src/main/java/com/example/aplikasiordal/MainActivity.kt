package com.example.aplikasiordal

import android.app.appsearch.GetByDocumentIdRequest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.aplikasiordal.databinding.ActivityMainBinding
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    //1. bikin binding dari ,main activity
    private lateinit var binding: ActivityMainBinding
    private lateinit var credentialManager: CredentialManager
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 2. inisisasi binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        // 3. set content dari binding
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        credentialManager = CredentialManager.create(this)
        auth = Firebase.auth

        registerEvents()
    }

    fun registerEvents() {
        binding.btnLogin.setOnClickListener {
            lifecycleScope.launch {
                val request = prepareRequest()
                loginByGoogle(request)
            }
        }
    }

    fun prepareRequest(): GetCredentialRequest {
        val serverClientId="930235766139-98lu32nq9d9sndg0aeco0mghjid9ms9v.apps.googleusercontent.com"

        val googleIdOption = GetGoogleIdOption
            .Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .build()

        val request = GetCredentialRequest
            .Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return request
    }
        suspend fun loginByGoogle(request: GetCredentialRequest){
           try {
               val result = credentialManager.getCredential(
                   context = this,
                   request = request
               )

               val credential = result.credential
               val idToken = GoogleIdTokenCredential.createFrom(credential.data)

               firebaseloginCallback(idToken.idToken)

           }catch (exc: NoCredentialException){
               Toast.makeText(this, "login gagal: " + exc.message, Toast.LENGTH_LONG).show()
           }catch (exc: Exception){
               Toast.makeText(this, "Login gagal:" + exc.message, Toast.LENGTH_LONG).show()
           }
        }

    fun firebaseloginCallback(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this){task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login berhasil", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this,"Login gagal", Toast.LENGTH_LONG).show()
                }

            }
    }

}