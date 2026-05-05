package com.example.name_3job3_locationmanagement.viewmodle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.name_3job3_locationmanagement.data.AppUsers
import com.example.name_3job3_locationmanagement.repo.UserRepository

class FriendListViewModel(private val repo: UserRepository)
    : ViewModel() {
    private val _userList = MutableLiveData<List<AppUsers>>()
    val userList: LiveData<List<AppUsers>> get() = _userList

    fun fetchUsers(){
        repo.getAllUsers { list ->
            _userList.postValue(list)
        }
    }
    //logout
    fun logOut(){
        repo.logOut()
    }
}