package com.dicoding.mangg.storyapp.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.mangg.storyapp.data.network.response.BaseResponse
import com.dicoding.mangg.storyapp.data.repo.StoryRepository
import com.dicoding.mangg.storyapp.data.result.Result
import com.dicoding.mangg.storyapp.utils.DataDummy
import com.dicoding.mangg.storyapp.utils.getOrAwaitValue
import com.dicoding.mangg.storyapp.view.story.StoryViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
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
import java.io.File

@RunWith(MockitoJUnitRunner::class)
class AddStoryViewModelTest {

    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var addStoryViewModel: StoryViewModel
    private val dummyAddStory = DataDummy.generateDummyAddStoryResponse()
    private val token = "TOKEN"
    private val dummylat = 0.1
    private val dummylon = 0.1

    @Before
    fun setUp() {
        addStoryViewModel = StoryViewModel(storyRepository)
    }

    @Test
    fun `when Add Story is Success`() {
        val description = "description".toRequestBody("text/plain".toMediaType())
        val file = Mockito.mock(File::class.java)
        val requestImageFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )

        val expectedStory = MutableLiveData<Result<BaseResponse>>()
        expectedStory.value = Result.Success(dummyAddStory)
        `when`(
            storyRepository.addStory(
                token,
                imageMultipart,
                description,
                dummylat,
                dummylon
            )
        ).thenReturn(expectedStory)

        val actualStory =
            addStoryViewModel.addStory(token, imageMultipart, description, dummylat, dummylon)
                .getOrAwaitValue()

        Mockito.verify(storyRepository)
            .addStory(token, imageMultipart, description, dummylat, dummylon)
        assertNotNull(actualStory)
        assertTrue(actualStory is Result.Success)
    }
}