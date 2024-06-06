package com.dicoding.mangg.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.mangg.storyapp.data.model.Story
import com.dicoding.mangg.storyapp.data.model.User
import com.dicoding.mangg.storyapp.data.repo.StoryRepository

class MainViewModel(private val repository: StoryRepository) : ViewModel() {

    fun getStory(): LiveData<PagingData<Story>> {
        return repository.getStory().cachedIn(viewModelScope)
    }

    fun getUser(): LiveData<User> {
        return repository.getUserData()
    }

}