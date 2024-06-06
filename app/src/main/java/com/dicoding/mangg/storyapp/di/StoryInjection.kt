package com.dicoding.mangg.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dicoding.mangg.storyapp.data.network.api.ApiConfig
import com.dicoding.mangg.storyapp.data.pref.UserPreference
import com.dicoding.mangg.storyapp.data.repo.StoryRepository


val Context.dataStore: DataStore<Preferences> by preferencesDataStore("storyapp")

object StoryInjection {
    fun provideRepository(context: Context): StoryRepository {
        val preferences = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiClient()
        return StoryRepository.getInstance(preferences, apiService)
    }
}