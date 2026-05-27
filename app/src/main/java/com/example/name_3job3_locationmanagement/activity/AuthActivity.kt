package com.example.name_3job3_locationmanagement.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.name_3job3_locationmanagement.databinding.ActivityAuthBinding
import com.example.name_3job3_locationmanagement.repo.UserRepository
import com.example.name_3job3_locationmanagement.viewmodle.AuthViewModel

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

override fun onCreate(savedInstanceState: Bundle?) {  
    super.onCreate(savedInstanceState)  
    binding = ActivityAuthBinding.inflate(layoutInflater)  
    setContentView(binding.root)  



    // app start হওয়ার সাথে সাথে email load হয়  
    val prefs = getSharedPreferences("login_prefs", MODE_PRIVATE)  

    val isRemembered = prefs.getBoolean("remember", false)  

    if (isRemembered) {  
        binding.email.setText(prefs.getString("email", ""))  
        binding.cbRemember.isChecked = true  
    }  

    // Initial animations for titles

//        val fadeInSlide = AnimationUtils.loadAnimation(this, R.anim.fade_in_slide)
//        binding.appTitle.startAnimation(fadeInSlide)
//        binding.subTitle.startAnimation(fadeInSlide)
//        binding.appTitle.setOnClickListener {
//            it.startAnimation(fadeInSlide)
//        }
//
//        // Bounce animation for "Welcome" text on touch
//        val bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce)
//        binding.tvWelcome.setOnClickListener {
//            it.startAnimation(bounceAnim)
//        }

binding.btnLogin.setOnClickListener {  

        val email = binding.email.text.toString().trim()  
        val password = binding.password.text.toString().trim()  

        if (email.isEmpty() || password.isEmpty()) {  
            Toast.makeText(this, "please fill the box", Toast.LENGTH_SHORT).show()  
            return@setOnClickListener  
        }  
        // SAVE EMAIL HERE  
        val prefs = getSharedPreferences("login_prefs", MODE_PRIVATE)  
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
    binding.btnRegister.setOnClickListener {  

        val email = binding.email.text.toString().trim()  
        val password = binding.password.text.toString().trim()  

        if (email.isEmpty() || password.isEmpty()) {  
            Toast.makeText(this, "please fill the box", Toast.LENGTH_SHORT).show()  
            return@setOnClickListener  
        }  

        viewModel.register(email, password)  
    }  
    viewModel.registerResult.observe(this) { (success, message) ->  
        if (success) {  
            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()  
        } else {  
            Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()  
        }  
    }  
    viewModel.loginResult.observe(this) { (success, message) ->  
        if (success) {  
            Toast.makeText(this, "Loggin Successful", Toast.LENGTH_SHORT).show()  
            navigateToFriendList()  
        } else {  
            Toast.makeText(this, "Loggin Failed", Toast.LENGTH_SHORT).show()  
        }  
    }  
}  
private fun navigateToFriendList() {  
    val intent = Intent(this, FriendListActivity::class.java)  
    startActivity(intent)  
    finish()  
}

}
