package com.executor.crudapplication.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
@TypeConverters(Convertors::class)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDAO

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null
        fun getDatabase(context: Context): UserDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "User_Database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}