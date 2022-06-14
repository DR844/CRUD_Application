package com.executor.crudapplication

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.executor.crudapplication.db.UserEntity
import kotlinx.android.synthetic.main.row_contact.view.*

class UserAdapter(private val context: Context, private var listener: RowClickListener) :
    RecyclerView.Adapter<UserAdapter.MyViewHolder>() {

    private var myUser = emptyList<UserEntity>()

    @SuppressLint("NotifyDataSetChanged")
    fun setListData(data: List<UserEntity>) {
        this.myUser = data
        notifyDataSetChanged()
    }

    class MyViewHolder(
        itemView: View,
        private val listener: RowClickListener
    ) :
        RecyclerView.ViewHolder(itemView) {
        val image = itemView.civImage
        val fName = itemView.tvfName
        val lName = itemView.tvlName
        val number = itemView.tvNumber
        val email = itemView.tvEmail
        val age = itemView.tvAge
        val deleteUserId = itemView.ibUserDeleteBtn

        fun bind(data: UserEntity) {
            deleteUserId.setOnClickListener {
                listener.onDeleteUserClickListener(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.row_contact, parent, false)
        return MyViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val userInfo = myUser[position]
        holder.fName.text = userInfo.fName
        holder.lName.text = userInfo.lName
        holder.email.text = userInfo.email
        holder.age.text = userInfo.age.toString()
        holder.number.text = userInfo.number
        Glide.with(context).load(userInfo.image).into(holder.image)
        holder.itemView.setOnClickListener {
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClickListener(myUser[position])
            }
        }
        holder.bind(myUser[position])
    }

    override fun getItemCount(): Int {
        return myUser.size
    }

    interface RowClickListener {
        fun onDeleteUserClickListener(userEntity: UserEntity)
        fun onItemClickListener(userEntity: UserEntity)
    }
}
