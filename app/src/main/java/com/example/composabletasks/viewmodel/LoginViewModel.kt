package com.example.composabletasks.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.composabletasks.service.constants.TaskConstants
import com.example.composabletasks.service.helper.BiometricHelper
import com.example.composabletasks.service.model.ValidationModel
import com.example.composabletasks.service.repository.PersonRepository
import com.example.composabletasks.service.repository.PriorityRepository
import com.example.composabletasks.service.repository.local.PreferencesManager
import com.example.composabletasks.service.repository.remote.RetrofitClient
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : BaseAndroidViewModel(application) {
    private val preferencesManager = PreferencesManager(application.applicationContext)
    private val personRepository: PersonRepository =
        PersonRepository(application.applicationContext)
    private val priorityRepository = PriorityRepository(application.applicationContext)

    private val _login = MutableLiveData<ValidationModel>()
    val login: LiveData<ValidationModel> = _login

    private val _loggedUser = MutableLiveData<Boolean>()
    val loggedUser: LiveData<Boolean> = _loggedUser

    var email by mutableStateOf("")
        private set // Garante que só o VM pode alterar o valor

    fun onEmailChange(newValue: String) {
        email = newValue
    }

    var password by mutableStateOf("")
        private set

    fun onPasswordChange(newValue: String) {
        password = newValue
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = personRepository.login(email, password)
                if (response.isSuccessful && response.body() != null) {
                    val personModel = response.body()!!

                    RetrofitClient.addHeaders(personModel.personKey, personModel.token)

                    super.saveUserAuthentication(personModel)

                    val responsePriority = priorityRepository.getList()
                    if (responsePriority.isSuccessful && responsePriority.body() != null) {
                        priorityRepository.save(responsePriority.body()!!)
                    }

                    _login.value = ValidationModel()
                } else {
                    _login.value = parseErrorMessage(response)
                }
            } catch (e: Exception) {
                _login.value = handleException(e)
            }
        }
    }

    fun verifyAuthentication() {
        viewModelScope.launch {
            val token = preferencesManager.get(TaskConstants.SHARED.TOKEN_KEY)
            val personKey = preferencesManager.get(TaskConstants.SHARED.PERSON_KEY)

            RetrofitClient.addHeaders(personKey, token)

            val logged = (token != "" && personKey != "")
            _loggedUser.value = logged && BiometricHelper.isBiometricAvaible(getApplication())

            if (!logged) {
                try {
                    val response = priorityRepository.getList()
                    if (response.isSuccessful && response.body() != null) {
                        priorityRepository.save(response.body()!!)
                    }
                } catch (e: Exception) {
                    Log.e("LoginViewModel", e.message.toString())
                }
            }
        }

    }

}