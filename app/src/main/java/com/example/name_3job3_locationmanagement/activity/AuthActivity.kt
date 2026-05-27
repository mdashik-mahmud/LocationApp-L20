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
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    private val repo = UserRepository()

    private val viewModel: AuthViewModel by viewModels<AuthViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(repo) as T
            }
        }
    }
    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            if (isGranted) {

                checkLocationSettingsAndProceed()

            } else {
               Toast.makeText(
                    this,
                    "Location permission is required",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // remember email
        val prefs = getSharedPreferences("login_prefs", MODE_PRIVATE)

        val isRemembered = prefs.getBoolean("remember", false)

        if (isRemembered) {
            binding.email.setText(prefs.getString("email", ""))
            binding.cbRemember.isChecked = true
        }
        // LOGIN
        binding.btnLogin.setOnClickListener {

            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val editor = prefs.edit()
            if (binding.cbRemember.isChecked) {

                editor.putString("email", email)
                editor.putBoolean("remember", true)

            } else {

                editor.clear()
            }
            editor.apply()

            viewModel.login(email, password)
        }

        // REGISTER
        binding.btnRegister.setOnClickListener {

            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }
            viewModel.register(email, password)
        }

        // REGISTER RESULT
        viewModel.registerResult.observe(this) { (success, message) ->

            if (success) {

                Toast.makeText(
                    this,
                    "Registration Successful",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                Toast.makeText(
                    this,
                    "Registration Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        // LOGIN RESULT
        viewModel.loginResult.observe(this) { (success, message) ->

            if (success) {

                Toast.makeText(
                    this,
                    "Login Successful",
                    Toast.LENGTH_SHORT
                ).show()

                checkLocationPermissionAndProceed()

            } else {

                Toast.makeText(
                    this,
                    "Login Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // CHECK LOCATION PERMISSION
    private fun checkLocationPermissionAndProceed() {

        when {

            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {

                checkLocationSettingsAndProceed()
            }

            else -> {

                locationPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }
    }

    // CHECK GPS / LOCATION SETTINGS
    private fun checkLocationSettingsAndProceed() {

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000
        ).build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(this)

        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {

            navigateToFriendList()
        }

        task.addOnFailureListener { exception ->

            if (exception is ResolvableApiException) {

                try {

                    exception.startResolutionForResult(
                        this,
                        1001
                    )

                } catch (sendEx: IntentSender.SendIntentException) {

                    sendEx.printStackTrace()
                }
            }
        }
    }

    // NAVIGATE
    private fun navigateToFriendList() {

    val intent = Intent(
        this,
        FriendListActivity::class.java
    )

    startActivity(intent)

    finish()
}
}
