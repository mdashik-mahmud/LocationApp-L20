package com.example.name_3job3_locationmanagement.activity

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.name_3job3_locationmanagement.databinding.ActivityAuthBinding
import com.example.name_3job3_locationmanagement.repo.UserRepository
import com.example.name_3job3_locationmanagement.viewmodle.AuthViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val repo = UserRepository()

    private val viewModel: AuthViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(repo) as T
            }
        }
    }

    private var pendingAuthType: String = "" // "REGISTER" or "LOGIN"

    // Permission request
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                checkLocationSettings()
            } else {
                Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show()
            }
        }

    // GPS enable result
    private val resolutionLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                checkLocationSettings()
            } else {
                Toast.makeText(this, "Please enable location", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // REGISTER BUTTON (FIRST TIME ONLY STRICT LOCATION REQUIRED)
        binding.btnRegister.setOnClickListener {

            pendingAuthType = "REGISTER"
            checkPermissionAndProceed()
        }

        // LOGIN BUTTON (EVERY TIME LOCATION REQUIRED)
        binding.btnLogin.setOnClickListener {

            pendingAuthType = "LOGIN"
            checkPermissionAndProceed()
        }

        // REGISTER RESULT
        viewModel.registerResult.observe(this) { (success, _) ->
            if (success) {
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
            }
        }

        // LOGIN RESULT
        viewModel.loginResult.observe(this) { (success, _) ->
            if (success) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                navigateToFriendList()
            } else {
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // STEP 1: Permission check
    private fun checkPermissionAndProceed() {

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {

                checkLocationSettings()
            }

            else -> {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    // STEP 2: GPS ON check
    private fun checkLocationSettings() {

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000
        ).build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(request)

        val client = LocationServices.getSettingsClient(this)

        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {

                // LOCATION OK → CONTINUE FLOW
                proceedAuth()
            }
            .addOnFailureListener { exception ->

                if (exception is ResolvableApiException) {

                    try {
                        val intentSenderRequest =
                            IntentSenderRequest.Builder(exception.resolution).build()

                        resolutionLauncher.launch(intentSenderRequest)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
    }

    // STEP 3: AUTH FLOW CONTROL
    private fun proceedAuth() {

        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        when (pendingAuthType) {

            "REGISTER" -> {
                viewModel.register(email, password)
            }

            "LOGIN" -> {
                viewModel.login(email, password)
            }
        }
    }

    // NAVIGATION
    private fun navigateToFriendList() {
        startActivity(Intent(this, FriendListActivity::class.java))
        finish()
    }
}
