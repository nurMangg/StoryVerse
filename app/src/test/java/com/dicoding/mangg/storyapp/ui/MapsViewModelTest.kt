package com.dicoding.mangg.storyapp.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.mangg.storyapp.data.network.response.StoryResponse
import com.dicoding.mangg.storyapp.data.repo.StoryRepository
import com.dicoding.mangg.storyapp.data.result.Result
import com.dicoding.mangg.storyapp.utils.DataDummy
import com.dicoding.mangg.storyapp.utils.getOrAwaitValue
import com.dicoding.mangg.storyapp.view.map.MapsViewModel
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var mapsViewModel: MapsViewModel
    private val dummyStoryLocation = DataDummy.generateDummyStoryLocation()
    private val token = "TOKEN"

    @Before
    fun setUp() {
        mapsViewModel = MapsViewModel(storyRepository)
    }

    @Test
    fun `when get Story Location is success`() {
        val expectedStory = MutableLiveData<Result<StoryResponse>>()
        expectedStory.value = Result.Success(dummyStoryLocation)

        `when`(storyRepository.getStoryLocation(token)).thenReturn(expectedStory)

        val actualStory = mapsViewModel.getStoryLocation(token).getOrAwaitValue()
        Mockito.verify(storyRepository).getStoryLocation(token)
        assertNotNull(actualStory)
        assertTrue(actualStory is Result.Success)
    }
}