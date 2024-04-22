package com.example.roomdbdemo

import android.os.Bundle
import android.util.Log
import android.view.RoundedCorner
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.request.ImageRequest
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

        //var imageUri: Any? by remember { mutableStateOf(binding.)}

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

        // Actions taken when home portrait changes
        userViewModel.inputPortrait.observe(this) { uri ->
            uri?.let {
                loadPortraitImage(uri)
            }
        }

        // Registers a photo picker activity launcher in single-select mode.
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                userViewModel.inputPortrait.value = uri.toString()

            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
        val imageClickListener = View.OnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.fabPhotoPicker.setOnClickListener(imageClickListener)
        binding.ivImageView.setOnClickListener(imageClickListener)

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

    private fun loadPortraitImage(uri: String?) {
        binding.ivImageView.load(uri) {
            crossfade(true)
            crossfade(400)
            placeholder(R.drawable.ic_image_placeholder)
            transformations(CircleCropTransformation())
        }
    }
}