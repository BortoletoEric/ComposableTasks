package com.example.composabletasks.service.repository

import android.content.Context
import com.example.composabletasks.service.model.PersonModel
import com.example.composabletasks.service.repository.remote.PersonService
import com.example.composabletasks.service.repository.remote.RetrofitClient
import retrofit2.Response

class PersonRepository(val context: Context) : BaseRepository(context) {

    private val remote = RetrofitClient.getService(PersonService::class.java)

    suspend fun login(email: String, password: String): Response<PersonModel> {
        return safeApiCall { remote.login(email, password) }
    }

    suspend fun create(
        name: String,
        email: String,
        password: String,
        receiveNews: String
    ): Response<PersonModel> {
        return safeApiCall { remote.create(name, email, password, receiveNews) }
    }


}