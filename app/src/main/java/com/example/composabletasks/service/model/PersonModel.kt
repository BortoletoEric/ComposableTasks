package com.example.composabletasks.service.model

import com.google.gson.annotations.SerializedName

data class PersonModel (
    @SerializedName("token")
    val token: String,

    @SerializedName("personKey")
    val personKey: String,

    @SerializedName("name")
    val name: String
)