package net.ykenny.brain.repository

import net.ykenny.brain.model.request.BodyToSend
import net.ykenny.brain.model.response.GeneratedAnswer
import net.ykenny.brain.service.RetrofitInstance

class OpenAiRepository {
    private val openAiService = RetrofitInstance.openAiService

    suspend fun getChatFromOpenAi(bodyToSend: BodyToSend): GeneratedAnswer {
        return openAiService.getMessages(bodyToSend)
    }
}
