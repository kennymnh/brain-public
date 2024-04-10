package net.ykenny.brain.service

import net.ykenny.brain.model.request.BodyToSend
import net.ykenny.brain.model.response.GeneratedAnswer
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenAiService {
    @POST("chat/completions")
    suspend fun getMessages(@Body post: BodyToSend): GeneratedAnswer
}