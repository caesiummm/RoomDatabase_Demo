package com.example.roomdbdemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomdbdemo.InputValidator
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
    val inputPortrait = MutableLiveData<String?>()

    val btnSaveOrUpdateText = MutableLiveData<String?>()
    val btnClearOrDeleteText = MutableLiveData<String?>()

    private val statusMessage = MutableLiveData<UserEvent<String>>()
    val message: LiveData<UserEvent<String>>
        get() = statusMessage

    init {
        btnSaveOrUpdateText.value = "Save"
        btnClearOrDeleteText.value = "Clear All"
    }

    // When user clicks on Save/Update button
    fun saveOrUpdate() {
        val userFirstName = inputFirstName.value?.trim()
        val userLastName = inputLastName.value?.trim()
        val userName = inputUserName.value?.trim()
        val userPhoneNum = inputPhoneNum.value?.trim()
        val userEmail = inputEmail.value?.trim()
        val userPortrait = inputPortrait.value

        // Input Validation
        val (isValid, inputValidatorMessage) = InputValidator.validateInput(userFirstName, userLastName, userName, userPhoneNum, userEmail)
        if (!isValid) {
            statusMessage.value = UserEvent(inputValidatorMessage!!)
            return
        }
        //checkUserName(userName!!)

        if (isUpdateOrDelete) {
            userToUpdateOrDelete.apply {
                this.firstName = userFirstName
                this.lastName = userLastName
                this.userName = userName
                this.phone = userPhoneNum
                this.email = userEmail
                this.portrait = userPortrait
            }
            update(userToUpdateOrDelete)
        } else {
            add(User(0, userFirstName, userLastName, userName, userPhoneNum, userEmail, userPortrait))
            inputFirstName.value = ""
            inputLastName.value = ""
            inputUserName.value = ""
            inputPhoneNum.value = ""
            inputEmail.value = ""
            inputPortrait.value = ""
        }
    }

    // When user clicks on Clear/Delete button
    fun clearOrDelete() {
        if (isUpdateOrDelete) {
            delete(userToUpdateOrDelete)
        } else clearAll()
    }

    // Insert User
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

    // Update User
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
                inputPortrait.value = ""

                isUpdateOrDelete = false
                userToUpdateOrDelete = user
                btnSaveOrUpdateText.value = "Save"
                btnClearOrDeleteText.value = "Clear All"
                statusMessage.value = UserEvent("${user.userName}'s info updated")
            } else {
                statusMessage.value = UserEvent("An error occurred!")
            }
        }
    }

    // Delete User
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
                inputPortrait.value = ""

                isUpdateOrDelete = false
                userToUpdateOrDelete = user
                btnSaveOrUpdateText.value = "Save"
                btnClearOrDeleteText.value = "Clear All"
                statusMessage.value = UserEvent("$numOfUserDeleted user deleted")
            } else {
                statusMessage.value = UserEvent("No users registered")
            }
        }
    }

    // Delete All Users
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

//    private fun checkUserName(userName: String) {
//        viewModelScope.launch {
//            val isExists = userRepository.isUserNameExists(userName)
//            if (isExists) {
//                withContext(Dispatchers.Main) {
//                    statusMessage.value = UserEvent("Username already existed")
//                    return@withContext
//                }
//            }
//        }
//    }

    fun initUpdateAndDelete(user: User) {
        inputFirstName.value = user.firstName
        inputLastName.value = user.lastName
        inputUserName.value = user.userName
        inputPhoneNum.value = user.phone
        inputEmail.value = user.email
        inputPortrait.value = user.portrait

        isUpdateOrDelete = true
        userToUpdateOrDelete = user
        btnSaveOrUpdateText.value = "Update"
        btnClearOrDeleteText.value = "Delete"
    }
}