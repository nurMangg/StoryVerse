package com.dicoding.mangg.storyapp.view.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.mangg.storyapp.data.model.User
import com.dicoding.mangg.storyapp.data.repo.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryViewModel(private val repository: StoryRepository) : ViewModel() {

    fun addStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Double?,
        long: Double?
    ) = repository.addStory(token, file, description, lat, long)

    fun getUser(): LiveData<User> {
        return repository.getUserData()
    }
}