package com.dicoding.mangg.storyapp.utils

import com.dicoding.mangg.storyapp.data.model.Story
import com.dicoding.mangg.storyapp.data.model.UserLogin
import com.dicoding.mangg.storyapp.data.network.response.BaseResponse
import com.dicoding.mangg.storyapp.data.network.response.LoginResponse
import com.dicoding.mangg.storyapp.data.network.response.StoryResponse

object DataDummy {

    fun generateDummyLoginResponse(): LoginResponse {
        return LoginResponse(
            false,
            "token",
            UserLogin(
                "userId",
                "name",
                "token"
            )
        )
    }

    fun generateDummyRegisterResponse(): BaseResponse {
        return BaseResponse(
            false,
            "success"
        )
    }

    fun generateDummyStory(): List<Story> {
        val item = arrayListOf<Story>()

        for (i in 0 until 10) {
            val story = Story(
                "story-asd3eqfafcasd",
                "mangg",
                "Ini gambarku mana gambarmu",
                "https://story-api.dicoding.dev/images/stories/nur-rohman.jpg",
                "2022-10-12T06:20:49.720Z",
                -1.1123235,
                121.123434
            )
            item.add(story)
        }
        return item
    }

    fun generateDummyStoryLocation(): StoryResponse {
        val item: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val story = Story(
                "story-asd3eqfafcasd",
                "mangg",
                "Ini gambarku mana gambarmu",
                "https://story-api.dicoding.dev/images/stories/nur-rohman.jpg",
                "2022-10-12T06:20:49.720Z",
                -1.1123235,
                121.123434
            )
            item.add(story)
        }
        return StoryResponse(
            false,
            "success",
            item
        )
    }

    fun generateDummyAddStoryResponse(): BaseResponse {
        return BaseResponse(
            false,
            "success"
        )
    }
}