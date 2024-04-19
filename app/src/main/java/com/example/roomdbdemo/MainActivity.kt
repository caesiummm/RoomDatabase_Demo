package com.example.roomdbdemo

import android.os.Bundle
import android.util.Log
import android.view.RoundedCorner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.example.roomdbdemo.adapter.UserListAdapter
import com.example.roomdbdemo.database.UserDatabase
import com.example.roomdbdemo.database.entity.User
import com.example.roomdbdemo.database.repository.UserRepository
import com.example.roomdbdemo.databinding.ActivityMainBinding
import com.example.roomdbdemo.viewmodel.UserViewModel
import com.example.roomdbdemo.viewmodel.UserViewModelFactory

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    private lateinit var userViewModel: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val userDao = UserDatabase.getInstance(application).userDao
        val repo = UserRepository(userDao)
        val factory = UserViewModelFactory(repo)

        userViewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]
        binding.userViewModel = userViewModel
        binding.lifecycleOwner = this

        initRv()

        // Display Toast message according to action observed
        userViewModel.message.observe(this, Observer { it ->
            it.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })

        binding.ivImageView.load(R.drawable.ic_launcher_foreground) {
            crossfade(true)
            crossfade(400)
            placeholder(R.drawable.ic_image_placeholder)
            transformations(CircleCropTransformation())
        }

        // Registers a photo picker activity launcher in single-select mode.
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    }

    private fun displayUsersList() {
        userViewModel.users.observe(this, Observer {
            Log.i(TAG, it.toString())
            binding.rvUserList.adapter = UserListAdapter(it) { selectedItem: User ->
                listItemClicked(
                    selectedItem
                )
            }
        })
    }

    private fun initRv() {
        binding.rvUserList.layoutManager = LinearLayoutManager(this)
        displayUsersList()
    }

    private fun listItemClicked(user: User) {
        userViewModel.initUpdateAndDelete(user)
    }
}