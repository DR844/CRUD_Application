package com.executor.crudapplication.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("select * from user_table")
    fun getAllUser(): LiveData<List<UserEntity>>

    @Query("SELECT count(email) FROM user_table WHERE email = :email")
    fun isEmailExist(email: String): Int
}