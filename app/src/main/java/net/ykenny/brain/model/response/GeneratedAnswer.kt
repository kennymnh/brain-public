package net.ykenny.brain.model.response

import com.google.gson.annotations.SerializedName

data class GeneratedAnswer(
    @SerializedName("choices")
    val choices: List<Choice>,

    @SerializedName("created")
    val created: Int,

    @SerializedName("id")
    val id: String,

    @SerializedName("model")
    val model: String
)