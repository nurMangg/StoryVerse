package com.dicoding.mangg.storyapp.data.network.response

import com.dicoding.mangg.storyapp.data.model.UserLogin
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("loginResult")
    val loginResult: UserLogin?,
)