package com.example.composabletasks.service.repository.remote

import com.example.composabletasks.service.model.PersonModel
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface PersonService {

    @POST("Authentication/Login")
    @FormUrlEncoded
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<PersonModel>

    @POST("Authentication/Create")
    @FormUrlEncoded
    suspend fun create(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("receiveNews") receiveNews: String
    ): Response<PersonModel>
}
