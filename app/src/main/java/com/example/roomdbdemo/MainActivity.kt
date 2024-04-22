package com.example.roomdbdemo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.RoundedCorner
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
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
import java.io.File

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var adapter: UserListAdapter
    private val CAMERA_REQUEST_CODE = 2000
    private lateinit var imageUrl: Uri



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
        val cameraPicker = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            Log.d(TAG, "Binding $imageUrl - captured photos")
//            loadPortraitImage(imageUrl)
//            binding.ivImageView.setImageURI(null)
//            binding.ivImageView.setImageURI(imageUrl)
            userViewModel.inputPortrait.value = imageUrl.toString() // Set photo uri from camera
        }

        binding.fabPhotoPicker.setOnClickListener {
            Log.d(TAG, "Taking photo")
            imageUrl = createImageUri()
            Log.d(TAG, "$imageUrl created")
            cameraPicker.launch(imageUrl)
        }
    }

    // Instead of creating a new adapter object for every new update,
    // reuse the initially created adapter object
    private fun displayUsersList() {
        userViewModel.users.observe(this, Observer {
            Log.i(TAG, it.toString())
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })
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
        binding.ivImageView.load(uri) {
            crossfade(true)
            crossfade(400)
            placeholder(R.drawable.ic_image_placeholder)
            transformations(CircleCropTransformation())
        }
    }

    private fun createImageUri() : Uri {
        val image = File(filesDir, "camera_photos.png")
        val uri = FileProvider.getUriForFile(this, "com.example.roomdbdemo.FileProvider", image)
        Log.d(TAG, "Uri: $uri")
        return uri
    }
}