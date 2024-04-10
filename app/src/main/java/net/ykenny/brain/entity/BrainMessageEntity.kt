package net.ykenny.brain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "brain_messages")
data class BrainMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val messageId: Long,
    val conversationId: Long,
    val role: String,
    val message: String,
)