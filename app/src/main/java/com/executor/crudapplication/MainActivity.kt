package com.executor.crudapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.executor.crudapplication.db.UserEntity
import com.executor.crudapplication.db.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), UserAdapter.RowClickListener {

    private lateinit var mUserViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fabAddBtn.setOnClickListener {
            val intent = Intent(this, UserDetailActivity::class.java)
            startActivity(intent)
        }

        val adapter = UserAdapter(this)
        rvUser.adapter = adapter
        rvUser.layoutManager = LinearLayoutManager(this)

        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        mUserViewModel.getAllUser.observe(this) {
            adapter.setListData(it)
        }

    }

    override fun onDeleteUserClickListener(userEntity: UserEntity) {
        GlobalScope.launch {
            mUserViewModel.deleteUser(userEntity)
        }

    }

    override fun onItemClickListener(userEntity: UserEntity) {
        val intent = Intent(this, UpdateActivity::class.java)
        intent.putExtra("id", userEntity.id)
        intent.putExtra("fname", userEntity.fName)
        intent.putExtra("lname", userEntity.lName)
        intent.putExtra("dob", userEntity.dob)
        intent.putExtra("number", userEntity.number)
        startActivity(intent)
    }
}