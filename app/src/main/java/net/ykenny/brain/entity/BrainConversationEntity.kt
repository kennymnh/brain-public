package net.ykenny.brain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "brain_conversations")
data class BrainConversationEntity(
    @PrimaryKey(autoGenerate = true)
    val conversationId: Long,
    val conversationName: String,
)