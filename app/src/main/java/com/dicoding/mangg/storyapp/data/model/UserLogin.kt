package com.dicoding.mangg.storyapp.data.model

import com.google.gson.annotations.SerializedName

class UserLogin(s: String, s1: String, s2: String) {
    @SerializedName("userId")
    val userId: String? = null

    @SerializedName("name")
    val name: String? = null

    @SerializedName("token")
    val token: String? = null
}