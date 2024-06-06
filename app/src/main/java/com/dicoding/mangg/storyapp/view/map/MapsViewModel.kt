package com.dicoding.mangg.storyapp.view.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.mangg.storyapp.data.model.User
import com.dicoding.mangg.storyapp.data.repo.StoryRepository

class MapsViewModel(private val repository: StoryRepository) : ViewModel() {

    fun getStoryLocation(token: String) =
        repository.getStoryLocation(token)

    fun getUser(): LiveData<User> {
        return repository.getUserData()
    }
}