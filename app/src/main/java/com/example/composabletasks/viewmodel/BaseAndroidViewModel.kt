package com.example.composabletasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.composabletasks.R
import com.example.composabletasks.service.constants.TaskConstants
import com.example.composabletasks.service.exception.NoInternetException
import com.example.composabletasks.service.model.PersonModel
import com.example.composabletasks.service.model.ValidationModel
import com.example.composabletasks.service.repository.local.PreferencesManager
import com.google.gson.Gson
import retrofit2.Response

open class BaseAndroidViewModel(private val application: Application) : AndroidViewModel(application) {
    private val preferencesManager = PreferencesManager(application.applicationContext)

    fun <T> parseErrorMessage(response: Response<T>): ValidationModel {
        return ValidationModel(
            Gson().fromJson(
                response.errorBody()?.string().toString(),
                String::class.java
            )
        )
    }

    suspend fun saveUserAuthentication(personModel: PersonModel) {
        preferencesManager.store(TaskConstants.SHARED.TOKEN_KEY, personModel.token)
        preferencesManager.store(TaskConstants.SHARED.PERSON_KEY, personModel.personKey)
        preferencesManager.store(TaskConstants.SHARED.PERSON_NAME, personModel.name)
    }

    fun handleException(e: Exception): ValidationModel {
        return if (e is NoInternetException) {
            ValidationModel(e.errorMessage)
        } else {
            ValidationModel(application.getString(R.string.error_unexpected))
        }
    }
}