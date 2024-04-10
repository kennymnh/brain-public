package net.ykenny.brain.model.response

import com.google.gson.annotations.SerializedName
import net.ykenny.brain.model.OpenAiMessageBody

data class Choice(
    @SerializedName("message")
    val message: OpenAiMessageBody
)