package com.example.roomdbdemo.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.roomdbdemo.R
import com.example.roomdbdemo.database.entity.User
import com.example.roomdbdemo.databinding.UserInfoItemViewBinding

class UserListAdapter(private val clickListener:(User) -> Unit)
    : RecyclerView.Adapter<UserViewHolder>() {

    private val userList = ArrayList<User>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: UserInfoItemViewBinding = DataBindingUtil.inflate(layoutInflater, R.layout.user_info_item_view, parent, false)

        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(userList[position], clickListener)
        Log.i("UserListAdapter", userList.toString())
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun setList(users: List<User>) {
        userList.clear()
        userList.addAll(users)
    }

}

class UserViewHolder(private val binding: UserInfoItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(user: User, clickListener:(User) -> Unit) {
        val prefix = "+60"
        binding.tvFirstName.text = user.firstName
        binding.tvLastName.text = user.lastName
        binding.tvUserName.text = user.userName
        binding.tvPhoneNum.text = prefix + user.phone
        binding.tvEmail.text = user.email
        binding.ivPortrait.load(user.portrait) {
            Log.i("UserListAdapter", user.portrait.toString())
            crossfade(true)
            crossfade(400)
            placeholder(R.drawable.ic_image_placeholder)
            transformations(CircleCropTransformation())
        }

        binding.clUserItemLayout.setOnClickListener {
            clickListener(user)
        }
    }
}

