package com.executor.crudapplication.db

import androidx.lifecycle.LiveData
import com.executor.crudapplication.db.UserDAO
import com.executor.crudapplication.db.UserEntity

class UserRepository(private val userDAO: UserDAO) {

    val getAllUser: LiveData<List<UserEntity>> = userDAO.getAllUser()

    suspend fun insertUser(userEntity: UserEntity) {
        userDAO.insertUser(userEntity)
    }

    suspend fun updateUser(userEntity: UserEntity) {
        userDAO.updateUser(userEntity)
    }

    suspend fun deleteUser(userEntity: UserEntity) {
        userDAO.deleteUser(userEntity)
    }
    suspend fun isEmailExist(email:String):Int{
        return userDAO.isEmailExist(email)
    }
}