package com.example.roomdbdemo.viewmodel

import android.health.connect.datatypes.units.Length
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomdbdemo.database.entity.User
import com.example.roomdbdemo.database.repository.UserRepository
import com.example.roomdbdemo.event.UserEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    val users = userRepository.users // get the users live data
    private var isUpdateOrDelete = false
    private lateinit var userToUpdateOrDelete: User

    val inputFirstName = MutableLiveData<String?>()
    val inputLastName = MutableLiveData<String?>()
    val inputUserName = MutableLiveData<String?>()
    val inputPhoneNum = MutableLiveData<String?>()
    val inputEmail = MutableLiveData<String?>()
    val listLiveData = MutableLiveData<List<User>>()

    val btnSaveOrUpdateText = MutableLiveData<String?>()
    val btnClearOrDeleteText = MutableLiveData<String?>()

    private val statusMessage = MutableLiveData<UserEvent<String>>()
    val message: LiveData<UserEvent<String>>
        get() = statusMessage

    init {
        btnSaveOrUpdateText.value = "Save"
        btnClearOrDeleteText.value = "Clear All"
    }

    fun saveOrUpdate() {
        if (isUpdateOrDelete) {
            userToUpdateOrDelete.firstName = inputFirstName.value!!
            userToUpdateOrDelete.lastName = inputLastName.value!!
            userToUpdateOrDelete.userName = inputUserName.value!!
            userToUpdateOrDelete.phone = inputPhoneNum.value!!
            userToUpdateOrDelete.email = inputEmail.value!!

            update(userToUpdateOrDelete)
        } else {
            val fName = inputFirstName.value!!
            val lName = inputLastName.value!!
            val uName = inputUserName.value!!
            val phone = inputPhoneNum.value!!
            val email = inputEmail.value!!

            add(User(0, fName, lName, uName, phone, email))

            inputFirstName.value = ""
            inputLastName.value = ""
            inputUserName.value = ""
            inputPhoneNum.value = ""
            inputEmail.value = ""
        }
    }

    fun clearOrDelete() {
        if (isUpdateOrDelete) {
            delete(userToUpdateOrDelete)
        } else clearAll()
    }

    private fun add(user: User) = viewModelScope.launch(Dispatchers.IO){
        val newRowId = userRepository.add(user)

        withContext(Dispatchers.Main) {
            if (newRowId > -1) {
                statusMessage.value = UserEvent("User Id added: $newRowId")
            } else {
                statusMessage.value = UserEvent("An error occurred!")
            }
        }
    }

    fun addList(users: List<User>) = viewModelScope.launch(Dispatchers.IO) {
        userRepository.add(users)
    }

    private fun update(user: User) = viewModelScope.launch(Dispatchers.IO) {
        val numOfRows = userRepository.update(user)

        // Since this is an UI update, change the thread back to Main thread
        withContext(Dispatchers.Main) {
            if (numOfRows > 0) {
                inputFirstName.value = ""
                inputLastName.value = ""
                inputUserName.value = ""
                inputPhoneNum.value = ""
                inputEmail.value = ""

                isUpdateOrDelete = false
                userToUpdateOrDelete = user
                btnSaveOrUpdateText.value = "Save"
                btnClearOrDeleteText.value = "Clear All"
                statusMessage.value = UserEvent("$numOfRows row(s) updated")
            } else {
                statusMessage.value = UserEvent("An error occurred!")
            }
        }
    }

    private fun delete(user: User) = viewModelScope.launch(Dispatchers.IO) {
        val numOfUserDeleted = userRepository.delete(user)

        // Since this is an UI update, change the thread back to Main thread
        withContext(Dispatchers.Main) {
            if (numOfUserDeleted > 0) {
                inputFirstName.value = ""
                inputLastName.value = ""
                inputUserName.value = ""
                inputPhoneNum.value = ""
                inputEmail.value = ""

                isUpdateOrDelete = false
                userToUpdateOrDelete = user
                btnSaveOrUpdateText.value = "Save"
                btnClearOrDeleteText.value = "Clear All"
                statusMessage.value = UserEvent("$numOfUserDeleted user deleted")
            } else {
                statusMessage.value = UserEvent("An error occurred!")
            }
        }
    }

    private fun clearAll() = viewModelScope.launch(Dispatchers.IO) {
        val numOfRowsDeleted = userRepository.deleteAll()

        withContext(Dispatchers.Main) {
            if (numOfRowsDeleted > 0) {
                statusMessage.value = UserEvent("$numOfRowsDeleted row(s) deleted")
            } else {
                statusMessage.value = UserEvent("An error occurred!")
            }

        }

    }

    fun initUpdateAndDelete(user: User) {
        inputFirstName.value = user.firstName
        inputLastName.value = user.lastName
        inputUserName.value = user.userName
        inputPhoneNum.value = user.phone
        inputEmail.value = user.email

        isUpdateOrDelete = true
        userToUpdateOrDelete = user
        btnSaveOrUpdateText.value = "Update"
        btnClearOrDeleteText.value = "Delete"
    }
}