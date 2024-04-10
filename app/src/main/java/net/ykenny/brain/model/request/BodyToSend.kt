package net.ykenny.brain.model.request

import com.google.gson.annotations.SerializedName
import net.ykenny.brain.model.OpenAiMessageBody

data class BodyToSend(
    @SerializedName("messages") val messages: List<OpenAiMessageBody>,
    @SerializedName("model") val model: String = "gpt-3.5-turbo"
)