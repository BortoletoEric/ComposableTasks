package com.example.composabletasks.service.repository.remote

import com.example.composabletasks.service.model.PriorityModel
import retrofit2.Response
import retrofit2.http.GET

interface PriorityService {

    @GET("Priority")
    suspend fun getList(): Response<List<PriorityModel>>

}