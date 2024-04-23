package com.example.roomdbdemo

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import coil.imageLoader
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.example.roomdbdemo.adapter.UserListAdapter
import com.example.roomdbdemo.database.UserDatabase
import com.example.roomdbdemo.database.entity.User
import com.example.roomdbdemo.database.repository.UserRepository
import com.example.roomdbdemo.databinding.ActivityMainBinding
import com.example.roomdbdemo.viewmodel.UserViewModel
import com.example.roomdbdemo.viewmodel.UserViewModelFactory
import java.io.File
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var adapter: UserListAdapter
//    private val CAMERA_REQUEST_CODE = 2000
    private lateinit var imageUri: Uri
    private lateinit var timestamp: LocalDateTime


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
        userViewModel.message.observe(this) { it ->
            it.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        // Actions taken when home portrait changes
        userViewModel.inputPortrait.observe(this) { uri ->
            uri?.let {
                Log.d(TAG, "Loading image using Coil")
                loadPortraitImage(uri)
            }
        }

        // Registers a photo picker activity launcher in single-select mode.
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                userViewModel.inputPortrait.value = uri.toString() // Set photo uri from photo picker

            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
        binding.ivImageView.setOnClickListener { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }


        // Registers camera activity
        val takeImageResult = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            Log.d(TAG, "Binding $imageUri - captured photos")
            userViewModel.inputPortrait.value = imageUri.toString() // Set photo uri from camera
        }

        binding.fabPhotoPicker.setOnClickListener {
            Log.d(TAG, "Taking photo")
            imageUri = createImageUri()
            takeImageResult.launch(imageUri)
        }
    }

    // Instead of creating a new adapter object for every new update,
    // reuse the initially created adapter object
    private fun displayUsersList() {
        userViewModel.users.observe(this) {
            Log.i(TAG, it.toString())
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        }
    }

    private fun initRv() {
        binding.rvUserList.layoutManager = LinearLayoutManager(this)
        adapter = UserListAdapter { selectedItem: User ->
            listItemClicked(selectedItem)
        }
        binding.rvUserList.adapter = adapter
        displayUsersList()
    }

    private fun listItemClicked(user: User) {
        userViewModel.initUpdateAndDelete(user)
    }

    private fun loadPortraitImage(uri: String?) {
        val request = ImageRequest.Builder(this)
            .data(uri)
            .crossfade(true)
            .placeholder(R.drawable.ic_image_placeholder)
            .transformations(CircleCropTransformation())
            .target(binding.ivImageView)
            .build()
        imageLoader.enqueue(request)
//        binding.ivImageView.load(uri) {
//            crossfade(true)
//            crossfade(400)
//            placeholder(R.drawable.ic_image_placeholder)
//            transformations(CircleCropTransformation())
//        }
    }
    private fun createImageUri() : Uri {
        timestamp = LocalDateTime.now()
        Log.d(TAG, "Timestamp: $timestamp")
        val image = File(filesDir, "camera_photo_$timestamp.png")
        val uri = FileProvider.getUriForFile(this, "com.example.roomdbdemo.FileProvider", image)
        Log.d(TAG, "Uri: $uri")
        return uri
    }
}