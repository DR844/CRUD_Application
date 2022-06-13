package com.executor.crudapplication.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    val getAllUser: LiveData<List<UserEntity>>
    private val repository: UserRepository

    init {
        val userDAO = UserDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDAO)
        getAllUser = repository.getAllUser
    }

    suspend fun insertUser(userEntity: UserEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertUser(userEntity)
        }
    }

    suspend fun updateUser(userEntity: UserEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUser(userEntity)
        }
    }

    suspend fun deleteUser(userEntity: UserEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteUser(userEntity)
        }
    }
}