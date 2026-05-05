package com.example.name_3job3_locationmanagement.viewmodle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.name_3job3_locationmanagement.data.AppUsers
import com.example.name_3job3_locationmanagement.repo.UserRepository

class MyProfileViewModel(private val repo: UserRepository) : ViewModel() {

    private val _user = MutableLiveData<AppUsers?>()
    val user: LiveData<AppUsers?> get() = _user

    fun loadUser(userId: String) {
        repo.getUserById(userId) {
            _user.postValue(it)
        }
    }

    fun updateUsername(userId: String, newName: String, onResult: (Boolean) -> Unit) {
        repo.updateUsername(userId, newName) {
            onResult(it)
        }
    }
}